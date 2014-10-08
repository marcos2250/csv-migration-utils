package marcos2250.csvmigrationutils.engine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import marcos2250.csvmigrationutils.domain.IdentidadeIntersistemas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class RelatorioMigracaoExporter {

    private static final String STRING_VAZIA = "";

    private static final String MASCARA_CSV = "%s;# %s;# %s;%s;\r\n";

    private static final Logger LOGGER = LoggerFactory.getLogger(RelatorioMigracaoExporter.class);

    private static RelatorioMigracaoExporter instance;

    private StringBuilder buffer;

    private String nomeFaseMigracao;

    public static RelatorioMigracaoExporter getInstance() {
        if (instance == null) {
            instance = new RelatorioMigracaoExporter();
        }
        return instance;
    }

    public void init(String nomeFaseMigracao) {
        this.nomeFaseMigracao = nomeFaseMigracao;
        buffer = new StringBuilder();
    }

    public void registrar(String descricaoFase, IdentidadeIntersistemas identidadeIntersistemas, Exception excecao) {

        buffer.append(String.format(MASCARA_CSV, //
                descricaoFase, //
                trataDescrucaoSistemaOrigem(identidadeIntersistemas), //
                trataDescrucaoSistemaDestino(identidadeIntersistemas), //
                trataErroNegocial(excecao)));

    }

    public void registrar(String descricaoFase, String pkProcesso, Exception excecao) {

        buffer.append(String.format(MASCARA_CSV, //
                descricaoFase, //
                pkProcesso, //
                STRING_VAZIA, //
                trataErroNegocial(excecao)));
    }

    private String trataErroNegocial(Exception excecao) {
        if (excecao.getMessage() == null) {
            return excecao.getClass().getSimpleName();
        }
        return excecao.getMessage();
    }

    private String trataDescrucaoSistemaOrigem(IdentidadeIntersistemas identidadeIntersistemas) {
        return "PK Origem: " + identidadeIntersistemas.getPkOriginal();
    }

    private String trataDescrucaoSistemaDestino(IdentidadeIntersistemas identidadeIntersistemas) {
        return "PK Destino: " + identidadeIntersistemas.getIdentidadeProcesso().getPk();
    }

    public void write() {
        PrintWriter output;
        File file;

        try {

            int tentativa = 0;
            do {
                String outputFileName = trataNomeArquivo(nomeFaseMigracao, tentativa);
                file = new File(outputFileName);
                tentativa++;
            } while (file.exists());

            Files.touch(file);

            output = new PrintWriter(file);

            output.println("Fase;Id Sistema 1;Id Sistema 2;Mensagem de erro");

            output.print(buffer.toString());

            output.close();
            LOGGER.info("\nArquivo CSV gerado: " + file.getAbsolutePath());

        } catch (IOException e) {
            LOGGER.error("Erro gerador relatario de migracao Excel", e);
        }
    }

    private String trataNomeArquivo(String nomeFaseMigracao, int tentativa) {
        StringBuilder sb = new StringBuilder();
        
        //sb.append("d:/"); //for windows
        
        sb.append(nomeFaseMigracao.replaceAll(":", ""));
        if (tentativa != 0) {
            sb.append(" ");
            sb.append(tentativa);
        }
        sb.append(".csv");
        return sb.toString();
    }

}
