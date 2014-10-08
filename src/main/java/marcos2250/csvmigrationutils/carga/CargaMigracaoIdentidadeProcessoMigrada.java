package marcos2250.csvmigrationutils.carga;

import org.hibernate.Session;

public class CargaMigracaoIdentidadeProcessoMigrada implements ICargaBase {

    @Override
    public void carregar(Session sessao) {

        new LeitorIdentidadeProcessoMigrada(sessao).popularBaseDados();
    }

    @Override
    public String getIdentificadorUnivoco() {
        return CargaMigracaoIdentidadeProcessoMigrada.class.getSimpleName();
    }

}
