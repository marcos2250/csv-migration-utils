package marcos2250.csvmigrationutils.carga;

import java.util.Map;

public interface ProcessadorCSV {

    String STRING_VAZIA = "";
    String DELIMITADOR_ASPA_DUPLA = "\"";
    String DELIMITADOR_PONTO_VIRGULA = ";";
    String DELIMITADOR_VIRGULA = ",";

    String getDelimitador();

    void processarLinha(String[] lineTokens, Map<String, Integer> headerMap);
}
