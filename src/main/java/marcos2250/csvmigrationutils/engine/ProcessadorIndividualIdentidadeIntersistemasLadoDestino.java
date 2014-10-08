package marcos2250.csvmigrationutils.engine;

import javax.annotation.Resource;

import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemas;
import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemasService;
import marcos2250.csvmigrationutils.domain.IdentidadeProcesso;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDTO;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDecorator;
import marcos2250.csvmigrationutils.domain.StatusMigracaoIdentidade;
import marcos2250.csvmigrationutils.domain.StatusMigracaoIdentidadeRepository;
import marcos2250.csvmigrationutils.misc.Calendario;
import marcos2250.csvmigrationutils.misc.DataUtil;
import marcos2250.csvmigrationutils.misc.GenericDAO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProcessadorIndividualIdentidadeIntersistemasLadoDestino {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProcessadorIndividualIdentidadeIntersistemasLadoDestino.class);

    @Autowired
    private IdentidadeIntersistemasService identidadeIntersistemasService;
    @Autowired
    private StatusMigracaoIdentidadeRepository statusMigracaoIdentidadeRepository;
    @Autowired
    private GenericDAO genericDAO;
    @Resource
    private Calendario calendario;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public void criarIdentidadeIntersistemasLadoDestino(final IdentidadeProcessoMigradaDTO identidadeMigrada) {

        final IdentidadeProcessoMigradaDecorator decorator = new IdentidadeProcessoMigradaDecorator(identidadeMigrada);

        final IdentidadeProcesso novaIdentidade = criarIdentidadeProcessoSistemaDestino(decorator);

        associarIdentidadeSistemaDestinoAoProcesso(decorator.getPkOriginal(), novaIdentidade);
    }

    private void associarIdentidadeSistemaDestinoAoProcesso(final String pkProcesso,
            final IdentidadeProcesso novaIdentidade) {
        if (StringUtils.isBlank(pkProcesso)) {
            return;
        }

        final IdentidadeIntersistemas identidadeIntersistemas = buscarIdentidadeIntersistemasIncompleta(pkProcesso);

        // completar com o dado passivel de erro
        identidadeIntersistemas.setIdentidadeProcesso(novaIdentidade);

        persistirIdentidadeIntersistemas(identidadeIntersistemas);
    }

    private IdentidadeIntersistemas buscarIdentidadeIntersistemasIncompleta(String pkSistemaOriginal) {
        return identidadeIntersistemasService.getIdentidadeIntersistemas(pkSistemaOriginal);
    }

    private void persistirIdentidadeIntersistemas(IdentidadeIntersistemas identidadeIntersistemas) {
        StatusMigracaoIdentidade status = statusMigracaoIdentidadeRepository
                .getStatusMigracaoIdentidade(identidadeIntersistemas);

        final String mensagemSucesso = "[FASE 0-B - Lado sistema destino: SUCESSO em "
                + DataUtil.toString(calendario.getDataHoraAtual()) + "] Criacao das Identidades "
                + identidadeIntersistemas;
        status.setResumoUltimaAcao(mensagemSucesso);

        statusMigracaoIdentidadeRepository.salvar(status);

        LOGGER.info(mensagemSucesso);
    }

    private IdentidadeProcesso criarIdentidadeProcessoSistemaDestino(final IdentidadeProcessoMigradaDecorator decorator) {

        // TODO HERE IS THE ENDPOINT!

        IdentidadeProcesso identidadeProcesso = new IdentidadeProcesso();

        genericDAO.saveOrUpdate(identidadeProcesso);

        return identidadeProcesso;
    }

}
