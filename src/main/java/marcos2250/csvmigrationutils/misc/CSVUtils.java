package marcos2250.csvmigrationutils.misc;

import java.util.regex.Pattern;

public class CSVUtils {

    public static String[] split(String csv, String delimitador) {
        Pattern p = Pattern.compile(delimitador + "(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        if (csv.endsWith(delimitador)) {
            return p.split(csv + " ");
        }
        return p.split(csv);
    }

}
