package marcos2250.csvmigrationutils.domain;

import java.io.Serializable;

public interface IEntidadeMigracaoMonitoravel extends Serializable, IObjetoPersistente<Long> {

    int TAMANHO_MAXIMO_ERRO = 2000;
    int TAMANHO_MAXIMO_STACKTRACE = 20000;

    IdentidadeIntersistemas getIdentidadeIntersistemas();

    String getResumoUltimaAcao();

    String getStackTrace();

}
