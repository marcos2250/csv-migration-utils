package marcos2250.csvmigrationutils.misc;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import marcos2250.csvmigrationutils.domain.IObjetoPersistente;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Repository
public class GenericDAO {

    private static final String PK = "pk";
    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public <O extends IObjetoPersistente<?>> void persist(O obj) {
        getCurrentSession().persist(obj);
    }

    @Transactional
    public <O extends IObjetoPersistente<?>> void saveOrUpdate(O obj) {
        getCurrentSession().saveOrUpdate(obj);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public <O extends IObjetoPersistente<?>> O merge(O obj) {
        return (O) getCurrentSession().merge(obj);
    }

    @Transactional
    public <O extends IObjetoPersistente<?>> void delete(O obj) {
        getCurrentSession().delete(obj);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public boolean hasRecord(Class<?> clazz, Long id) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        return ((Long) criteria.add(Restrictions.eq(PK, id)).setProjection(Projections.rowCount()).uniqueResult()) > 0;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public <O extends IObjetoPersistente<?>> O getRecord(Class<O> clazz, Long id) {
        return clazz.cast(getCurrentSession().get(clazz, id));
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public <O extends IObjetoPersistente<?>> Set<O> getRecords(Class<O> clazz, Set<Long> ids) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.add(Restrictions.in(PK, ids));
        return Sets.newHashSet(criteria.list());
    }

    public <O extends IObjetoPersistente<?>> O attach(O entidade) {
        if ((entidade.getPk() != null) && !getCurrentSession().contains(entidade)) {
            getCurrentSession().buildLockRequest(LockOptions.NONE).lock(entidade);
        }
        return entidade;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public <O extends IObjetoPersistente<?>> void refresh(O entidade) {
        getCurrentSession().refresh(entidade);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @SuppressWarnings("unchecked")
    public <T> List<T> findAll(Class<T> classe) {
        return getCurrentSession().createCriteria(classe).list();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public <O extends IObjetoPersistente<?>> List<O> findAllComOrder(Class<O> clazz, String property) {
        Criteria criteria = getCurrentSession().createCriteria(clazz);
        criteria.addOrder(order(property, true));
        return CheckUtil.checkedList(criteria.list(), clazz);
    }

    public <O extends IObjetoPersistente<?>> Criteria createCriteria(Class<O> persistentClass) {
        return getCurrentSession().createCriteria(persistentClass);
    }

    public <O extends IObjetoPersistente<?>> Criteria createCriteria(Class<O> persistentClass, String alias) {
        return getCurrentSession().createCriteria(persistentClass, alias);
    }

    public <O extends IObjetoPersistente<?>> Criteria createCriteria(Class<O> persistentClass, int offsetRegistro,
            int numeroMaximoRegistros) {
        Criteria criteria = getCurrentSession().createCriteria(persistentClass);
        criteria.setFirstResult(offsetRegistro);
        criteria.setMaxResults(numeroMaximoRegistros);
        return criteria;
    }

    public List<Object> getResultadoHql(String consulta, Map<String, Object> parametros) {
        return getResultadoHql(consulta, parametros, null, null);
    }

    public List<Object> getResultadoHql(String consulta, Map<String, Object> parametros, Integer max, Integer offset) {
        Query query = getCurrentSession().createQuery(consulta);
        if (max != null && offset != null) {
            query.setMaxResults(max);
            query.setFirstResult(offset);
        }
        for (Entry<String, Object> entry : parametros.entrySet()) {
            if (entry.getValue() instanceof Collection) {
                Collection<?> value = (Collection<?>) entry.getValue();
                if (value.isEmpty()) {
                    return newArrayList();
                }
                query.setParameterList(entry.getKey(), value);
            } else {
                if (entry.getValue() == null) {
                    return newArrayList();
                }
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        return CheckUtil.checkedList(query.list(), Object.class);
    }

    public static <O extends Comparable<O>> SortedSet<O> paginar(Collection<O> colecao, int offsetRegistro,
            int numeroMaximoRegistros) {
        int indexFinal = Math.min(offsetRegistro + numeroMaximoRegistros, colecao.size());
        return CheckUtil.toSortedSet(Lists.newArrayList(colecao).subList(offsetRegistro, indexFinal));
    }

    public static Order order(String property, boolean asc) {
        if (asc) {
            return Order.asc(property);
        }
        return Order.desc(property);
    }

    public SQLQuery createSQLQuery(String sql) {
        return getCurrentSession().createSQLQuery(sql);
    }

    public Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

    public Session getNewSession() {
        return SessionFactoryUtils.getNewSession(getSessionFactory());
    }

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public <O extends IObjetoPersistente<?>> void evict(O objeto) {
        getCurrentSession().evict(objeto);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public <O extends IObjetoPersistente<?>> void flush() {
        getCurrentSession().flush();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void clear() {
        getCurrentSession().clear();
    }

}
