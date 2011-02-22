package mx.edu.um.portlets.eliseo.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@Repository
@Transactional
public class ExamenDao {

    private static final Logger log = LoggerFactory.getLogger(ExamenDao.class);
    private HibernateTemplate hibernateTemplate;

    public ExamenDao() {
        log.debug("Nueva instancia del dao de examenes");
    }

    @Autowired
    protected void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly = true)
    public Long cantidad(Set<Long> comunidades) {
        log.debug("Obteniendo cantidad de examenes");
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Examen.class);
        if (comunidades != null) {
            criteria.add(Restrictions.in("comunidadId", comunidades));
        }
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.list().get(0));
    }

    @Transactional(readOnly = true)
    public List<Examen> busca(Map<String, Object> params) {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Examen.class);
        if (params != null && params.containsKey("filtro") && ((String)params.get("filtro")).trim().length() > 0) {
            String filtro = "%" + ((String)params.get("filtro")).trim() + "%";
            log.debug("Buscando examenes por {}",filtro);
            Disjunction propiedades = Restrictions.disjunction();
            propiedades.add(Restrictions.ilike("codigo", filtro));
            propiedades.add(Restrictions.ilike("nombre", filtro));
            criteria.add(propiedades);
        }
        if (params != null && params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId",(Set<Long>)params.get("comunidades")));
        }
        return criteria.list();
    }

    public Examen crea(Examen examen) {
        log.info("Creando el examen {}", examen);
        Long id = (Long) hibernateTemplate.save(examen);
        examen.setId(id);
        return examen;
    }

    public Examen actualiza(Examen examen) {
        log.info("Actualizando el examen {}", examen);
        hibernateTemplate.update(examen);
        return examen;
    }

    @Transactional(readOnly = true)
    public Examen obtiene(Long id) {
        log.debug("Buscando el examen {}", id);
        Examen examen = hibernateTemplate.get(Examen.class, id);
        if (examen == null) {
            throw new RuntimeException("Examen no encontrado");
        }
        return examen;
    }

    public void elimina(Long id) {
        log.info("Eliminando el examen {}", id);
        hibernateTemplate.delete(hibernateTemplate.load(Examen.class, id));
    }

    public Map lista(Map<String, Object> params) {
        if (params.get("offset") == null) {
            params.put("offset", new Integer(0));
        }
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Examen.class);
        if (params != null && params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId",(Set<Long>)params.get("comunidades")));
        }
        criteria.setMaxResults((Integer) params.get("max"));
        criteria.setFirstResult((Integer) params.get("offset"));
        params.put("examenes", criteria.list());
        return params;
    }
}
