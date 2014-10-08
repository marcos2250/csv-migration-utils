package marcos2250.csvmigrationutils.carga;

import org.hibernate.Session;

/**
 * Objetos que implementam esta interface sao injetados pelo Spring em CarregadorDados conforme referencia em
 * applicationContext-web-desktop.xml
 * 
 * @see CarregadorDados
 */
public interface ICarga {

    /**
     * Carrega os dados.
     */
    void carregar(Session sessao);

    /**
     * Identifica univocamente cada carga - PK da tabela de controle de cargas.
     */

    String getIdentificadorUnivoco();
}
