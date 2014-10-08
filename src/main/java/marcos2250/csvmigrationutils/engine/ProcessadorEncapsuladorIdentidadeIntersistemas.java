package marcos2250.csvmigrationutils.engine;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemas;
import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemasRepository;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDTO;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDecorator;
import marcos2250.csvmigrationutils.domain.StatusMigracaoIdentidade;
import marcos2250.csvmigrationutils.domain.StatusMigracaoIdentidadeRepository;
import marcos2250.csvmigrationutils.domain.StatusProcessamentoMigracao;
import marcos2250.csvmigrationutils.misc.Calendario;
import marcos2250.csvmigrationutils.misc.DataUtil;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.core.IsNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

@Component
public class ProcessadorEncapsuladorIdentidadeIntersistemas {

    private static final String IDENTIDADE = "identidade";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessadorEncapsuladorIdentidadeIntersistemas.class);

    @Autowired
    private ProcessadorIndividualIdentidadeIntersistemasLadoOrigem processadorSistemaA;
    @Autowired
    private ProcessadorIndividualIdentidadeIntersistemasLadoDestino processadorSistemaB;
    @Autowired
    private IdentidadeIntersistemasRepository identidadeIntersistemasRepository;
    @Autowired
    private StatusMigracaoIdentidadeRepository statusMigracaoIdentidadeRepository;
    @Autowired
    private Calendario calendario;

    /**
     * <p>
     * Essa servico, o qual gera uma nova transacao, responsavel por agrupar as transacoes de criacao da identidade
     * intersistemas em duas fases, quais sejam:
     * </p>
     * 
     * <ol>
     * <li>Criacao da identidade intersistemas somente com os dados provenientes do sistema original; e</li>
     * <li>Associacao da identidade do processo do sistema destino a identidade intersistemas.</li>
     * </ol>
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public StatusProcessamentoMigracao criarIdentidadeIntersistemas(IdentidadeProcessoMigradaDTO identidadeMigrada) {
        try {

            // Foram criadas duas transacoes para ser possivel corrigir eventuais erros
            // que porventura ocorram em funcao da criacao da identidade do processo sistema 2
            Set<IdentidadeIntersistemas> identidadesIncompletas = identidadeIntersistemasRepository
                    .getIdentidadeIntersistemasIncompletas(identidadeMigrada);

            // se nao retornou ninguem, significa que nem comecou essa fase ainda
            if (identidadesIncompletas.isEmpty()) {
                // cria as identidades intersistemas de FORMA INCOMPLETA, com dados somente do sistema 1
                identidadesIncompletas = processadorSistemaA.criarIdentidadesIntersistemasLadoOrigem(identidadeMigrada);
            }

            validarEstadoIdentidades(identidadesIncompletas);

            // cria a identidade intersistemas COMPLETAMENTE, associando uma identidade do sistema 2
            processadorSistemaB.criarIdentidadeIntersistemasLadoDestino(identidadeMigrada);

            return StatusProcessamentoMigracao.SUCESSO;

            // CHECKSTYLE:OFF
        } catch (Exception e) {
            // CHECKSTYLE:ON
            gravarFracasso(identidadeMigrada, e);
        }

        return StatusProcessamentoMigracao.FRACASSO;
    }

    private void validarEstadoIdentidades(Set<IdentidadeIntersistemas> identidades) {
        List<IdentidadeIntersistemas> identidadesJaCompletas = Lambda.select(identidades,
                Lambda.having(Lambda.on(IdentidadeIntersistemas.class).getIdentidadeProcesso(), IsNull.notNullValue()));

        if (!identidadesJaCompletas.isEmpty()) {
            throw new IllegalStateException(
                    "Ja existem identidades completas sendo reprocessadas na fase de criacao de identidades!");
        }
    }

    private void gravarFracasso(IdentidadeProcessoMigradaDTO identidadeMigrada, Exception excecao) {
        IdentidadeProcessoMigradaDecorator decorator = new IdentidadeProcessoMigradaDecorator(identidadeMigrada);

        StringBuilderWriter stackTraceErrorWriter = new StringBuilderWriter();
        excecao.printStackTrace(new PrintWriter(stackTraceErrorWriter));
        String stackTraceError = stackTraceErrorWriter.getBuilder().toString();

        try {

            gravarErro(excecao, decorator.getPkOriginal(), stackTraceError);

            // CHECKSTYLE:OFF
        } catch (Exception e) {
            // CHECKSTYLE:ON
            LOGGER.error("Erro ao salvar status de fracasso...", e);
        }
    }

    private void gravarErro(Exception excecao, String pkProcesso, String stackTraceError) {
        if (StringUtils.isBlank(pkProcesso)) {
            return;
        }

        IdentidadeIntersistemas identidadeIntersistemas = identidadeIntersistemasRepository
                .getIdentidadeIntersistemas(Long.valueOf(pkProcesso));
        if (identidadeIntersistemas == null) {
            LOGGER.error(
                    "Ocorreu erro na fase 1 da criacao da identidade intersistemas (apenas com dados do sistema origem)",
                    excecao);
            RelatorioMigracaoExporter.getInstance().registrar(IDENTIDADE, pkProcesso, excecao);
            return;
        }

        gerarMensagemErro(excecao, stackTraceError, identidadeIntersistemas);
    }

    private void gerarMensagemErro(Exception excecao, String stackTraceError,
            IdentidadeIntersistemas identidadeIntersistemas) {
        StatusMigracaoIdentidade status = statusMigracaoIdentidadeRepository
                .getStatusMigracaoIdentidade(identidadeIntersistemas);

        String mensagemFracasso = //
        "[FASE 0: FRACASSO em " + DataUtil.toString(calendario.getDataHoraAtual()) //
                + "] Criacao das Identidades " + identidadeIntersistemas + //
                ", STACKTRACE: " + excecao.getMessage();

        status.setResumoUltimaAcao(mensagemFracasso);
        status.setStackTrace(stackTraceError);

        LOGGER.error(mensagemFracasso);

        RelatorioMigracaoExporter.getInstance().registrar(IDENTIDADE, identidadeIntersistemas, excecao);
    }
}
