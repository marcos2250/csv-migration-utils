package marcos2250.csvmigrationutils.domain;

import java.util.Set;

import marcos2250.csvmigrationutils.misc.CheckUtil;
import marcos2250.csvmigrationutils.misc.GenericDAO;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class IdentidadeProcessoMigradaRepository {

    @Autowired
    private GenericDAO GenericDAO;

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public Set<IdentidadeProcessoMigradaDTO> buscarIdentidadesAindaNaoFinalizadas(int offset, int tamanhoPagina) {

        Criteria criteria = GenericDAO.createCriteria(IdentidadeProcessoMigradaDTO.class, offset, tamanhoPagina);

        criarFiltroParaIdentidadesComFaseCriacaoIdentidadesNaoFinalizada(criteria);

        return CheckUtil.toCheckedSet(criteria.list(), IdentidadeProcessoMigradaDTO.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.SUPPORTS, readOnly = true)
    public int buscarQuantidadeIdentidades() {

        Criteria criteria = GenericDAO.createCriteria(IdentidadeProcessoMigradaDTO.class);
        criteria.setProjection(Projections.rowCount());

        criarFiltroParaIdentidadesComFaseCriacaoIdentidadesNaoFinalizada(criteria);

        return Long.class.cast(criteria.uniqueResult()).intValue();
    }

    private void criarFiltroParaIdentidadesComFaseCriacaoIdentidadesNaoFinalizada(Criteria criteria) {

        DetachedCriteria dcConhecimento = criarRestricaoPorTipoEmIdentidadeIntersistemasCompletas();

        // ... tal que nao esteja no conjunto-resposta
        criteria.add(Subqueries.propertyNotIn("pkOriginal", dcConhecimento));

    }

    private DetachedCriteria criarRestricaoPorTipoEmIdentidadeIntersistemasCompletas() {

        DetachedCriteria dc = DetachedCriteria.forClass(IdentidadeIntersistemas.class);
        dc.setProjection(Projections.property("pkOriginal"));

        // qualquer tupla que ja esteja com a fase 1 completa...
        dc.add(Restrictions.isNotNull("identidadeProcesso"));

        return dc;
    }

}
