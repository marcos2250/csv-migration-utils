package marcos2250.csvmigrationutils.carga;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarregadorDadosSQL {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarregadorDadosSQL.class);

    public static void cargaArquivoSQL(String arquivo, Session session) {
        BufferedReader br;
        int contador = 0;

        try {

            File file = new File(arquivo);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));

            String linha = br.readLine();

            while (linha != null) {

                if (linha.startsWith("--")) {
                    continue;
                }

                Query query = session.createSQLQuery(linha);
                query.executeUpdate();

                contador++;

                linha = br.readLine();
            }

            br.close();

            LOGGER.info("Foram executados com sucesso " + contador + " registros!");

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Erro ao carregar arquivo (arquivo nao encontrado): " + arquivo, e);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar arquivo (erro de leitura): " + arquivo, e);
        }
    }

}
