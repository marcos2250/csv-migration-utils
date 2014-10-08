package marcos2250.csvmigrationutils.engine;

import java.util.Set;

import javax.annotation.Resource;

import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemas;
import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemasRepository;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDTO;
import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDecorator;
import marcos2250.csvmigrationutils.domain.StatusMigracaoIdentidade;
import marcos2250.csvmigrationutils.domain.StatusMigracaoIdentidadeRepository;
import marcos2250.csvmigrationutils.misc.Calendario;
import marcos2250.csvmigrationutils.misc.DataUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

@Component
public class ProcessadorIndividualIdentidadeIntersistemasLadoOrigem {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProcessadorIndividualIdentidadeIntersistemasLadoOrigem.class);

    @Autowired
    private IdentidadeIntersistemasRepository identidadeIntersistemasRepository;
    @Autowired
    private StatusMigracaoIdentidadeRepository statusMigracaoIdentidadeRepository;
    @Resource
    private Calendario calendario;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
    public Set<IdentidadeIntersistemas> criarIdentidadesIntersistemasLadoOrigem(
            final IdentidadeProcessoMigradaDTO identidadeMigrada) {
        Set<IdentidadeIntersistemas> conjuntoIdentidadesCriadas = Sets.newHashSet();

        IdentidadeProcessoMigradaDecorator decorator = new IdentidadeProcessoMigradaDecorator(identidadeMigrada);

        // Lado do sistema original...
        criar(conjuntoIdentidadesCriadas, decorator);

        return conjuntoIdentidadesCriadas;
    }

    private void criar(Set<IdentidadeIntersistemas> conjuntoIdentidadesCriadas,
            IdentidadeProcessoMigradaDecorator decorator) {
        IdentidadeIntersistemas identidadeIntersistemas = new IdentidadeIntersistemas();
        identidadeIntersistemas.setPkOriginal(decorator.getPkOriginal());
        conjuntoIdentidadesCriadas.add(persistirIdentidade(identidadeIntersistemas));
    }

    private IdentidadeIntersistemas persistirIdentidade(IdentidadeIntersistemas identidadeIntersistemas) {
        IdentidadeIntersistemas identidadePersistida = identidadeIntersistemasRepository
                .alterar(identidadeIntersistemas);

        StatusMigracaoIdentidade status = statusMigracaoIdentidadeRepository
                .getStatusMigracaoIdentidade(identidadePersistida);

        final String mensagemSucesso = "[FASE 0-A - Lado sistema original: SUCESSO em "
                + DataUtil.toString(calendario.getDataHoraAtual()) + "] Criacao das Identidades "
                + identidadePersistida;
        status.setResumoUltimaAcao(mensagemSucesso);

        statusMigracaoIdentidadeRepository.salvar(status);

        LOGGER.info(mensagemSucesso);

        return identidadePersistida;
    }

}
