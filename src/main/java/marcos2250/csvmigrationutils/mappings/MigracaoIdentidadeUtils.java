package marcos2250.csvmigrationutils.mappings;

import java.util.List;
import java.util.Map;

import marcos2250.csvmigrationutils.domain.IdentidadeProcessoMigradaDTO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class MigracaoIdentidadeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MigracaoIdentidadeUtils.class);

    public static IdentidadeProcessoMigradaDTO gerar(String[] lineTokens, Map<String, Integer> headerMap) {
        IdentidadeProcessoMigradaDTO identidade = new IdentidadeProcessoMigradaDTO();

        identidade.setPkOriginal(lineTokens[headerMap.get("PK_ORIGINAL")]);
        identidade.setComment(lineTokens[headerMap.get("COMMENT")]);

        return identidade;
    }

    public static List<String> splitTokensValidos(String stringMigracao) {
        List<String> tokens = Lists.newArrayList();

        String[] split = stringMigracao.split(" ");

        int tokensValidosEncontrados = 0;

        String stringTratada;
        for (String string : split) {
            stringTratada = limpaStringSomenteNumeros(string);
            if (StringUtils.isBlank(stringTratada)) {
                continue;
            }
            tokensValidosEncontrados++;
            tokens.add(stringTratada);
        }

        // if (tokensValidosEncontrados > 2) {
        // throw new IllegalArgumentException
        LOGGER.info("Foram encontrados " + tokensValidosEncontrados + " tokens na string: " + stringMigracao);
        // }

        return tokens;
    }

    public static String limpaStringSomenteNumeros(String string) {
        return string.replaceAll("(-|[.]|\\D|\\s)", "");
    }
}
