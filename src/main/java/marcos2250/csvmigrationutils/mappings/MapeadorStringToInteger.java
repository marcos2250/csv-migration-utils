package marcos2250.csvmigrationutils.mappings;

import org.apache.commons.lang.StringUtils;

public class MapeadorStringToInteger {

    private MapeadorStringToInteger() {
        // service class
    }

    public static Integer converter(String valorEmString) {

        if (StringUtils.isBlank(valorEmString)) {
            return null;
        }

        return Integer.valueOf(valorEmString);

    }
}
