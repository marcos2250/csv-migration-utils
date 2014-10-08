package marcos2250.csvmigrationutils.carga;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import marcos2250.csvmigrationutils.domain.IObjetoPersistente;
import marcos2250.csvmigrationutils.misc.CheckUtil;
import marcos2250.csvmigrationutils.misc.LeitorCSV;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public abstract class LeitorBaseDados<T extends IObjetoPersistente<?>> implements ProcessadorCSV {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeitorBaseDados.class);

    private Session sessao;

    protected LeitorBaseDados(Session sessao) {
        this.sessao = Preconditions.checkNotNull(sessao);
    }

    public void popularBaseDados() {
        BufferedReader reader = getDescritorArquivo();

        new LeitorCSV(reader, this).processarArquivo();
    }

    private BufferedReader getDescritorArquivo() {
        return new BufferedReader(new InputStreamReader(getCaminhoArquivo()));
    }

    protected abstract InputStream getCaminhoArquivo();

    @Override
    public void processarLinha(String[] lineTokens, Map<String, Integer> headerMap) {
        salvarDadoMigrado(transformarDadoMigrado(lineTokens, headerMap));
    }

    private T salvarDadoMigrado(T dadoNaoSaldo) {
        Object merge = null;
        try {
            merge = sessao.merge(dadoNaoSaldo);
            // CHECKSTYLE:OFF
        } catch (Exception e) { // NOPMD
            // CHECKSTYLE:ON
            LOGGER.error(
                    "ERRO ao persistir: " + dadoNaoSaldo.getClass().getSimpleName() + " pk =" + dadoNaoSaldo.getPk(), e);
        }

        return CheckUtil.uncheckedCast(merge);
    }

    protected abstract T transformarDadoMigrado(String[] lineTokens, Map<String, Integer> headerMap);
}
