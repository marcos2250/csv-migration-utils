package marcos2250.csvmigrationutils.mappings;

import org.apache.commons.lang.StringUtils;

public class MapeadorStringToLong {

    private MapeadorStringToLong() {
        // service class
    }

    public static Long converter(String valorEmString) {

        if (StringUtils.isBlank(valorEmString)) {
            return null;
        }

        return Long.valueOf(valorEmString);

    }
}
