package marcos2250.csvmigrationutils.misc;

import org.apache.commons.lang.StringUtils;

public class StringAppender {

    public static void appendIfNotNull(StringBuilder sb, String rotulo, String dado) {
        if (StringUtils.isNotBlank(dado)) {
            sb.append(rotulo);
            sb.append(": ");
            sb.append(dado);
        }
    }

}
