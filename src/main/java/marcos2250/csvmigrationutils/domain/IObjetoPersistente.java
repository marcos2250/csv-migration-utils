package marcos2250.csvmigrationutils.domain;

import java.io.Serializable;

/**
 * Interface para todas as entidades que serao persistidas em banco de dados.
 * 
 * @param <PK> Tipo da PK.
 */
public interface IObjetoPersistente<PK extends Serializable> {

    /**
     * Retorna a chave primaria deste objeto persistente.
     * 
     * @return chave primaria
     */
    PK getPk();

    /**
     * Atribui um valor a chave primaria deste objeto persistente.
     * 
     * @param pk - chave primaria
     */
    void setPk(PK pk);
}
