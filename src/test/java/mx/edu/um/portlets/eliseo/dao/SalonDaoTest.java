package mx.edu.um.portlets.eliseo.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author jdmr
 */
@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"/context/applicationContext.xml"})
@Transactional
public class SalonDaoTest {

    private static final Logger log = LoggerFactory.getLogger(SalonDaoTest.class);
    private Long salonId;
    private Curso curso;
    
    @Autowired
    private CursoDao cursoDao;
    
    @Autowired
    private SalonDao salonDao;
    
    public SalonDaoTest () {
        log.debug("Nueva instancia de las pruebas del dao de salones");
    }
    
    @Before
    public void inicializa() {
        log.debug("Inicializando pruebas");
        curso = new Curso();
        curso.setCodigo("test001");
        curso.setNombre("TEST 001");
        curso.setComunidadId(new Long(1));
        curso.setComunidadNombre("TEST");
        curso = cursoDao.crea(curso);
        assertNotNull(curso.getId());
        
        Salon salon = new Salon();
        salon.setNombre("Salon001");
        salon.setCurso(curso);
        salon = salonDao.crea(salon);
        salonId = salon.getId();
    }
    
    @Test(expected=org.springframework.dao.DataIntegrityViolationException.class)
    public void noDebieraCrearSalonDuplicado() {
        log.debug("No debiera crear salon duplicado");
        Salon salon = new Salon();
        salon.setNombre("Salon001");
        salon.setCurso(curso);
        salon = salonDao.crea(salon);
        fail("Debe lanzar una excepcion de salon duplicado");
    }
    
    @Test
    public void debieraEncontrarUnSalon() {
        Salon salon = new Salon();
        salon.setNombre("Salon002");
        salon.setCurso(curso);
        salon = salonDao.crea(salon);
        
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("filtro", "salon");
        Set<Long> comunidades = new HashSet<Long>();
        comunidades.add(1L);
        params.put("comunidades",comunidades);
        List<Salon> salones = salonDao.busca(params);
        assertNotNull(salones);
        log.debug("Salones {}",salones);
    }
    
    @Test
    public void debieraModificarSalon() {
        log.debug("Debiera modificar salon");
        Salon salon = salonDao.obtiene(salonId);
        assertNotNull(salon);
        salon.setNombre("PRUEBA");
        salonDao.actualiza(salon);
        
        salon = salonDao.obtiene(salonId);
        assertTrue("Debe ser el mismo nombre","PRUEBA".equals(salon.getNombre()));
    }
    
    @Test(expected=RuntimeException.class)
    public void debieraEliminarSalon() {
        log.debug("Debiera eliminar salon");
        salonDao.elimina(salonId);
        
        Salon salon = salonDao.obtiene(salonId);
        fail("Debe lanzar una excepcion de examen no encontrado");
    }
}
