package mx.edu.um.portlets.eliseo.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ExamenDaoTest {

    private static final Logger log = LoggerFactory.getLogger(ExamenDaoTest.class);
    private Long examenId;
    private Curso curso;
    
    @Autowired
    private CursoDao cursoDao;
    
    @Autowired
    private ExamenDao examenDao;
    
    public ExamenDaoTest () {
        log.debug("Nueva instancia de las pruebas del dao de examenes");
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
        
        Examen examen = new Examen();
        examen.setCodigo("examen001");
        examen.setNombre("Examen 001");
        examen.setCurso(curso);
        examen = examenDao.crea(examen);
        examenId = examen.getId();
    }
    
    @Test(expected=org.springframework.dao.DataIntegrityViolationException.class)
    public void noDebieraCrearExamenDuplicado() {
        log.debug("No debiera crear examen duplicado");
        Examen examen = new Examen();
        examen.setCodigo("examen001");
        examen.setNombre("Examen 001");
        examen.setCurso(curso);
        examen = examenDao.crea(examen);
        fail("Debe lanzar una excepcion de examen duplicado");
    }
    
    @Test
    public void debieraEncontrarUnExamen() {
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("filtro", "examen");
        List<Examen> examenes = examenDao.busca(params);
        assertNotNull(examenes);
    }
    
    @Test
    public void debieraModificarExamen() {
        log.debug("Debiera modificar examen");
        Examen examen = examenDao.obtiene(examenId);
        assertNotNull(examen);
        examen.setNombre("PRUEBA");
        examenDao.actualiza(examen);
        
        examen = examenDao.obtiene(examenId);
        assertTrue("Debe ser el mismo nombre","PRUEBA".equals(examen.getNombre()));
    }
    
    @Test(expected=RuntimeException.class)
    public void debieraEliminarExamen() {
        log.debug("Debiera eliminar examen");
        examenDao.elimina(examenId);
        
        Examen examen = examenDao.obtiene(examenId);
        fail("Debe lanzar una excepcion de examen no encontrado");
    }
                
}
