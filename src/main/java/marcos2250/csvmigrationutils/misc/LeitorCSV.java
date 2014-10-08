package marcos2250.csvmigrationutils.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import marcos2250.csvmigrationutils.carga.ProcessadorCSV;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class LeitorCSV {
    private static final String DELIMITADOR_ASPA_DUPLA = "\"";

    private static final Logger LOGGER = LoggerFactory.getLogger(LeitorCSV.class);

    private BufferedReader reader;
    private ProcessadorCSV processadorCSV;

    public LeitorCSV(BufferedReader reader, ProcessadorCSV processadorCSV) {
        this.reader = reader;
        this.processadorCSV = processadorCSV;
    }

    public void processarArquivo() {
        String header = readLine(reader);

        String[] headerTokens = extrairTokens(header);

        Map<String, Integer> headerMap = gerarHeaderMap(headerTokens);

        int numeroColunas = headerMap.size();
        int numeroTokens;
        int extracoesComSucesso = 0;

        while (true) {
            String line = readLine(reader);
            if (line == null) {
                break;
            }

            if (StringUtils.isEmpty(line)) {
                continue;
            }

            String[] lineTokens = formatarTokens(extrairTokens(line));
            numeroTokens = lineTokens.length;

            if (numeroTokens != numeroColunas) {
                LOGGER.error("A linha " + line + " apresenta " + numeroTokens + " tokens, em vez de " + numeroColunas
                        + " tokens!");
                continue;
            }

            processadorCSV.processarLinha(lineTokens, headerMap);

            ++extracoesComSucesso;
        }

        LOGGER.info("Foram extraidos do arquivo, com sucesso, " + extracoesComSucesso + " registros!");
    }

    private String readLine(BufferedReader reader) {
        try {
            String line = reader.readLine();
            if (line == null) {
                return null;
            }
            return line;
        } catch (IOException e) {
            LOGGER.error("Leitura com problema da linha do arquivo", e);
        }

        throw new IllegalStateException("Algum erro ao ler a linha do arquivo informado!");
    }

    private String[] extrairTokens(String untokenizedLine) {
        return CSVUtils.split(untokenizedLine, processadorCSV.getDelimitador());
    }

    private Map<String, Integer> gerarHeaderMap(String[] headerTokens) {
        Map<String, Integer> headerMap = Maps.newHashMap();

        int coluna = 0;
        for (String headerToken : headerTokens) {
            headerMap.put(headerToken.replace(DELIMITADOR_ASPA_DUPLA, "").trim(), coluna++);

        }

        return headerMap;
    }

    private String[] formatarTokens(String[] tokens) {
        String[] tokenFormatado = new String[tokens.length];

        for (int i = 0; i < tokenFormatado.length; i++) {
            tokenFormatado[i] = tokens[i].replace(DELIMITADOR_ASPA_DUPLA, "").trim();
        }

        return tokenFormatado;
    }
}
