package marcos2250.csvmigrationutils.carga;

import java.io.InputStream;
import java.util.Map;

import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDTO;
import marcos2250.csvmigrationutils.mappings.MigracaoIdentidadeUtils;

import org.hibernate.Session;

public class LeitorIdentidadeProcessoMigrada extends LeitorBaseDados<IdentidadeProcessoMigradaDTO> {

    private static final String IDENTIDADE_INTERSISTEMAS_CSV = "DadosIdentidade.csv";

    LeitorIdentidadeProcessoMigrada(Session session) {
        super(session);
    }

    @Override
    protected InputStream getCaminhoArquivo() {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(IDENTIDADE_INTERSISTEMAS_CSV);
    }

    @Override
    public String getDelimitador() {
        return DELIMITADOR_VIRGULA;
    }

    @Override
    protected IdentidadeProcessoMigradaDTO transformarDadoMigrado(String[] lineTokens, Map<String, Integer> headerMap) {
        return MigracaoIdentidadeUtils.gerar(lineTokens, headerMap);
    }
}
