package marcos2250.csvmigrationutils.engine;

import java.util.Set;

import marcos2250.csvmigrationutils.domain.EntidadeMigravel;
import marcos2250.csvmigrationutils.domain.StatusProcessamentoMigracao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class MotorMigracao<T extends EntidadeMigravel> {

    protected static final int TAMANHO_PAGINA = 200;
    private static final Logger LOGGER = LoggerFactory.getLogger(MotorMigracao.class);

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public abstract String getFaseMigracao();

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public abstract int getQuantidadeTotalRegistros();

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public abstract Set<T> getRegistros(int offset, int tamanhoPagina);

    public abstract StatusProcessamentoMigracao acao(T registro);

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public void processar() {

        int totalRegistros = getQuantidadeTotalRegistros();
        LOGGER.info("Registros a serem processados nesta fase: " + totalRegistros);

        int offset = 0;
        int registrosProcessados = 0;
        int processadosComSucesso = 0;

        RelatorioMigracaoExporter.getInstance().init(getFaseMigracao());

        while (true) {

            Set<T> registros = getRegistros(offset, TAMANHO_PAGINA);
            if (registros.isEmpty()) {
                break;
            }

            for (final T registro : registros) {

                LOGGER.info(String.format("[%s --- %d/%d] Migracao do registro com PK: %s", //
                        getFaseMigracao(), //
                        ++registrosProcessados, //
                        totalRegistros, //
                        registro.getIdentificador()));

                try {
                    StatusProcessamentoMigracao status = acao(registro);

                    if (StatusProcessamentoMigracao.SUCESSO.equals(status)) {
                        ++processadosComSucesso;
                    } else {
                        ++offset;
                    }

                    // CHECKSTYLE:OFF
                } catch (Exception e) {
                    // CHECKSTYLE:ON
                    LOGGER.error(
                            "Erro ao gravar o fracasso do processamento individual do " + registro.getIdentificador(),
                            e);
                    ++offset;
                }

            }

        }

        loggarSumarioProcessamento(totalRegistros, processadosComSucesso);
    }

    private void loggarSumarioProcessamento(int totalRegistros, int processadosComSucesso) {

        String msg = String.format(">>>> [ --- SUMARIO: %s --- ] --> Total registros: %d (Sucesso = %d, Falhas = %d)", //
                getFaseMigracao(), //
                totalRegistros, //
                processadosComSucesso, //
                (totalRegistros - processadosComSucesso));

        LOGGER.info(msg);

        RelatorioMigracaoExporter.getInstance().write();
    }
}
