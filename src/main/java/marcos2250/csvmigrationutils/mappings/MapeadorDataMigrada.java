package marcos2250.csvmigrationutils.mappings;

import java.util.regex.Pattern;

import marcos2250.csvmigrationutils.misc.DataUtil;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapeadorDataMigrada {

    public static final String STR_DATA_PADRAO = "1900-01-01";
    public static final LocalDate DATA_PADRAO = LocalDate.parse(STR_DATA_PADRAO);

    private static final Logger LOGGER = LoggerFactory.getLogger(MapeadorDataMigrada.class);

    public static LocalDate converter(String valorNaoCovertido) {

        if ("1111-11-11".equals(valorNaoCovertido)) {
            return DATA_PADRAO;
        }

        if (Pattern.matches("[0-9]+/[0-9]+/[0-9]{2,4}", valorNaoCovertido)) {
            return DataUtil.toLocalDate(valorNaoCovertido);
        }
        if (Pattern.matches("[0-9]{2,4}[-][0-9]+[-][0-9]+", valorNaoCovertido)) {
            return LocalDate.parse(valorNaoCovertido);
        }

        LOGGER.trace("Mapeador de Data Migrada encontrou a data <" + valorNaoCovertido
                + "> e nao conseguiu transforma-la no formato correto de data. "
                + "Assumindo data padrao (01-01-1900)...");

        return LocalDate.parse(STR_DATA_PADRAO);
    }
}
