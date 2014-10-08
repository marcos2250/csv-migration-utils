package marcos2250.csvmigrationutils.domain;

import java.util.Set;

import marcos2250.csvmigrationutils.misc.CheckUtil;
import marcos2250.csvmigrationutils.misc.GenericDAO;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

@Repository
public class IdentidadeIntersistemasRepository {

    @Autowired
    private GenericDAO GenericDAO;

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS)
    public IdentidadeIntersistemas getIdentidadeIntersistemas(Long pkSistemaA) {

        Preconditions.checkNotNull(pkSistemaA, "A pk do sistema de origem nao pode ser nula!");

        Criteria criteria = GenericDAO.createCriteria(IdentidadeIntersistemas.class);

        criteria.add(Restrictions.eq("pkOriginal", pkSistemaA.toString()));

        return IdentidadeIntersistemas.class.cast(criteria.uniqueResult());

    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public IdentidadeIntersistemas alterar(IdentidadeIntersistemas identidadeIntersistemas) {
        return GenericDAO.merge(identidadeIntersistemas);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS)
    public Set<IdentidadeIntersistemas> getIdentidadeIntersistemasIncompletas(//
            IdentidadeProcessoMigradaDTO identidadeMigrada) {

        Criteria criteria = GenericDAO.createCriteria(IdentidadeIntersistemas.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        criteria.add(Restrictions.isNull("identidadeProcesso"));

        return Sets.newHashSet(CheckUtil.checkedList(criteria.list(), IdentidadeIntersistemas.class));

    }

}
