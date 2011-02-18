package mx.edu.um.portlets.cursos;

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
public class CursoDao {

    private static final Logger log = LoggerFactory.getLogger(CursoDao.class);
    private HibernateTemplate hibernateTemplate;

    public CursoDao() {
        log.debug("Nueva instancia del dao de cursos");
    }

    @Autowired
    protected void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly = true)
    public Long cantidad(Set<Long> comunidades) {
        log.debug("Obteniendo cantidad de cursos");
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Curso.class);
        if (comunidades != null) {
            criteria.add(Restrictions.in("comunidadId", comunidades));
        }
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.list().get(0));
    }

    @Transactional(readOnly = true)
    public List<Curso> busca(Map<String, Object> params) {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Curso.class);
        if (params != null && params.containsKey("filtro") && ((String)params.get("filtro")).trim().length() > 0) {
            String filtro = "%" + ((String)params.get("filtro")).trim() + "%";
            log.debug("Buscando cursos por {}",filtro);
            Disjunction propiedades = Restrictions.disjunction();
            propiedades.add(Restrictions.ilike("codigo", filtro));
            propiedades.add(Restrictions.ilike("nombre", filtro));
            propiedades.add(Restrictions.ilike("comunidadNombre", filtro));
            criteria.add(propiedades);
        }
        if (params != null && params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId",(Set<Long>)params.get("comunidades")));
        }
        return criteria.list();
    }

    public Curso crea(Curso curso) {
        log.info("Creando el curso {}", curso);
        Long id = (Long) hibernateTemplate.save(curso);
        curso.setId(id);
        return curso;
    }

    public Curso actualiza(Curso curso) {
        log.info("Actualizando el curso {}", curso);
        hibernateTemplate.update(curso);
        return curso;
    }

    @Transactional(readOnly = true)
    public Curso obtiene(Long id) {
        log.debug("Buscando el curso {}", id);
        Curso curso = hibernateTemplate.get(Curso.class, id);
        if (curso == null) {
            throw new RuntimeException("Curso no encontrado");
        }
        return curso;
    }

    public void elimina(Long id) {
        log.info("Eliminando el curso {}", id);
        hibernateTemplate.delete(hibernateTemplate.load(Curso.class, id));
    }

    public Map lista(Map<String, Object> params) {
        if (params.get("offset") == null) {
            params.put("offset", new Integer(0));
        }
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Curso.class);
        if (params != null && params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId",(Set<Long>)params.get("comunidades")));
        }
        criteria.setMaxResults((Integer) params.get("max"));
        criteria.setFirstResult((Integer) params.get("offset"));
        params.put("cursos", criteria.list());
        return params;
    }
}
