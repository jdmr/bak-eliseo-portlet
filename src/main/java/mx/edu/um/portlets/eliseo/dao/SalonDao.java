package mx.edu.um.portlets.eliseo.dao;

import com.liferay.portal.model.User;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Criteria;
import org.hibernate.Query;
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
public class SalonDao {
    
    private static final Logger log = LoggerFactory.getLogger(SalonDao.class);
    private HibernateTemplate hibernateTemplate;

    public SalonDao() {
        log.debug("Nueva instancia del dao de salones");
    }

    @Autowired
    protected void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Transactional(readOnly = true)
    public Long cantidad(Set<Long> comunidades) {
        log.debug("Obteniendo cantidad de salones");
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Salon.class);
        if (comunidades != null) {
            Criteria cursoCriteria = criteria.createCriteria("curso");
            cursoCriteria.add(Restrictions.in("comunidadId",comunidades));
            cursoCriteria.setProjection(Projections.rowCount());
            return ((Long) cursoCriteria.list().get(0));
        }
        criteria.setProjection(Projections.rowCount());
        return ((Long) criteria.list().get(0));
    }

    @Transactional(readOnly = true)
    public List<Salon> busca(Map<String, Object> params) {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Salon.class);
        if (params != null && params.containsKey("filtro") && ((String)params.get("filtro")).trim().length() > 0) {
            String filtro = "%" + ((String)params.get("filtro")).trim() + "%";
            log.debug("Buscando salones por {}",filtro);
            Disjunction propiedades = Restrictions.disjunction();
            propiedades.add(Restrictions.ilike("nombre", filtro));
            criteria.add(propiedades);
        }
        if (params != null && params.containsKey("comunidades")) {
            Criteria cursoCriteria = criteria.createCriteria("curso");
            cursoCriteria.add(Restrictions.in("comunidadId",(Set<Long>)params.get("comunidades")));
            return cursoCriteria.list();
        }
        return criteria.list();
    }

    public Salon crea(Salon salon) {
        log.info("Creando el salon {}", salon);
        Long id = (Long) hibernateTemplate.save(salon);
        salon.setId(id);
        return salon;
    }

    public Salon actualiza(Salon salon) {
        log.info("Actualizando el salon {}", salon);
        hibernateTemplate.update(salon);
        return salon;
    }

    @Transactional(readOnly = true)
    public Salon obtiene(Long id) {
        log.debug("Buscando el salon {}", id);
        Salon salon = hibernateTemplate.get(Salon.class, id);
        if (salon == null) {
            throw new RuntimeException("Salon no encontrado");
        }
        return salon;
    }

    public void elimina(Long id) {
        log.info("Eliminando el salon {}", id);
        hibernateTemplate.delete(hibernateTemplate.load(Salon.class, id));
    }

    public Map lista(Map<String, Object> params) {
        if (params.get("offset") == null) {
            params.put("offset", new Integer(0));
        }
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Salon.class);
        if (params != null && params.containsKey("comunidades")) {
            criteria = criteria.createCriteria("curso");
            criteria.add(Restrictions.in("comunidadId",(Set<Long>)params.get("comunidades")));
        }
        criteria.setMaxResults((Integer) params.get("max"));
        criteria.setFirstResult((Integer) params.get("offset"));
        params.put("salones", criteria.list());
        return params;
    }
    
    public Sesion creaSesion(Sesion sesion) {
        log.info("Creando la sesion {}", sesion);
        Long id = (Long) hibernateTemplate.save(sesion);
        sesion.setId(id);
        return sesion;
    }
    
    public void eliminaSesion(Long id) {
        log.info("Eliminando la sesion {}", id);
        hibernateTemplate.delete(hibernateTemplate.load(Sesion.class, id));
    }
    
    public List<Sesion> obtieneSesiones(Salon salon) {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Query query = session.createQuery("select sesion from Sesion sesion where sesion.salon.id = :salonId");
        query.setParameter("salonId", salon.getId());
        return query.list();
    }
    
    public void agregaAlumno(Salon salon, User alumno) {
        log.info("Agregando al alumno {} al salon {}", alumno, salon);
        AlumnoInscrito alumnoInscrito = new AlumnoInscrito(alumno, salon);
        hibernateTemplate.save(alumnoInscrito);
    }
    
    public List<AlumnoInscrito> getAlumnos(Salon salon) {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Query query = session.createQuery("select alumno from AlumnoInscrito alumno where alumno.salon.id = :salonId");
        query.setParameter("salonId", salon.getId());
        return query.list();
    }

    public void eliminaAlumno(Salon salon, Long alumnoId) {
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Query query = session.createQuery("select alumno from AlumnoInscrito alumno where alumno.salon.id = :salonId and alumno.alumnoId = :alumnoId");
        query.setParameter("salonId", salon.getId());
        query.setParameter("alumnoId", alumnoId);
        hibernateTemplate.delete(query.uniqueResult());
    }

    public Long eliminaAlumno(Long alumnoId) {
        AlumnoInscrito alumno = hibernateTemplate.get(AlumnoInscrito.class, alumnoId);
        Long salonId = alumno.getSalon().getId();
        hibernateTemplate.delete(alumno);
        return salonId;
    }

    public Boolean estaInscrito(Long salonId, Long alumnoId) {
        boolean resultado = false;
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Query query = session.createQuery("select alumno from AlumnoInscrito alumno where alumno.salon.id = :salonId and alumno.alumnoId = :alumnoId");
        query.setParameter("salonId", salonId);
        query.setParameter("alumnoId", alumnoId);
        AlumnoInscrito alumno = (AlumnoInscrito)query.uniqueResult();
        if (alumno != null) {
            resultado = true;
        }
        return resultado;
    }

    public Boolean existeSesionActiva(Long salonId, Integer dia, Date hora) {
        boolean resultado = false;
        Session session = hibernateTemplate.getSessionFactory().openSession();
        Query query = session.createQuery("select sesion from Sesion sesion where sesion.salon.id = :salonId and sesion.dia = :dia and sesion.horaInicial <= :hora and sesion.horaFinal >= :hora");
        query.setParameter("salonId", salonId);
        query.setParameter("dia", dia);
        query.setParameter("hora", hora);
        Sesion sesion = (Sesion) query.uniqueResult();
        if (sesion != null) {
            resultado = true;
        }
        return resultado;
    }
    
}
