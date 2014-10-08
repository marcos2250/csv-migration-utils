package marcos2250.csvmigrationutils.domain;

import marcos2250.csvmigrationutils.misc.GenericDAO;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Repository
public class StatusMigracaoIdentidadeRepository {

    @Autowired
    private GenericDAO GenericDAO;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public StatusMigracaoIdentidade salvar(StatusMigracaoIdentidade status) {
        return GenericDAO.merge(status);
    }

    /**
     * Null-safe getStatusMigracao. Se nao existir, cria uma com a identidade informada.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public StatusMigracaoIdentidade getStatusMigracaoIdentidade(final IdentidadeIntersistemas identidadeIntersistemas) {

        StatusMigracaoIdentidade resultado = getStatusMigracao(identidadeIntersistemas);

        if (resultado == null) {
            resultado = new StatusMigracaoIdentidade(identidadeIntersistemas);
        }

        return resultado;
    }

    private StatusMigracaoIdentidade getStatusMigracao(final IdentidadeIntersistemas identidadeIntersistemas) {

        Preconditions.checkNotNull(identidadeIntersistemas, "A identidade intersistemas nao pode ser nula!");

        Criteria criteria = GenericDAO.createCriteria(StatusMigracaoIdentidade.class);

        criteria.add(Restrictions.eq("identidadeIntersistemas", identidadeIntersistemas));

        return StatusMigracaoIdentidade.class.cast(criteria.uniqueResult());
    }
}
