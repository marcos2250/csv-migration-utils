package marcos2250.csvmigrationutils.domain;

import com.google.common.base.Preconditions;

public class IdentidadeProcessoMigradaDecorator {

    private IdentidadeProcessoMigradaDTO identidadeMigrada;

    public IdentidadeProcessoMigradaDecorator(IdentidadeProcessoMigradaDTO identidadeMigrada) {
        this.identidadeMigrada = Preconditions.checkNotNull(identidadeMigrada);
    }

    public String getPkOriginal() {
        return identidadeMigrada.getPkOriginal();
    }

}
