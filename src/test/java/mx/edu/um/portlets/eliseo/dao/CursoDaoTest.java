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
public class CursoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(CursoDaoTest.class);
    private Long cursoId;
    
    @Autowired
    private CursoDao cursoDao;
    
    public CursoDaoTest () {
        log.debug("Nueva instancia de las pruebas del dao de cursos");
    }
    
    @Before
    public void inicializa() {
        log.debug("Inicializando pruebas");
        Curso curso = new Curso();
        curso.setCodigo("test001");
        curso.setNombre("TEST 001");
        curso.setComunidadId(new Long(1));
        curso.setComunidadNombre("TEST");
        curso = cursoDao.crea(curso);
        assertNotNull(curso.getId());
        cursoId = curso.getId();
    }
    
//    @Test
//    public void debieraMostrarListaDeResultados() {
//        log.debug("Debiera mostrar lista de resultados");
//        Set<Long> comunidades = new HashSet<Long>();
//        comunidades.add(new Long(1));
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("comunidades", comunidades);
//        List<Curso> cursos = cursoDao.busca(params);
//        log.debug("Resultados: {}",cursos);
//        assertNotNull(cursos);
//        assertTrue(cursos.size() >= 1);
//    }

    @Test(expected=org.springframework.dao.DataIntegrityViolationException.class)
    public void noDebieraCrearCursoDuplicado() {
        log.debug("No debiera crear curso duplicado");
        Curso curso = new Curso();
        curso.setCodigo("test001");
        curso.setNombre("TEST-001");
        curso.setComunidadId(new Long(1));
        curso.setComunidadNombre("TEST");
        curso = cursoDao.crea(curso);
        fail("Debe lanzar una excepcion de curso duplicado");
    }
    
    @Test
    public void debieraEncontrarUnCurso() {
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("filtro", "test");
        List<Curso> cursos = cursoDao.busca(params);
    }
    
    @Test
    public void debieraModificarCurso() {
        log.debug("Debiera modificar curso");
        Curso curso = cursoDao.obtiene(cursoId);
        assertNotNull(curso);
        curso.setNombre("PRUEBA");
        cursoDao.actualiza(curso);
        
        curso = cursoDao.obtiene(cursoId);
        assertTrue("Debe ser el mismo nombre","PRUEBA".equals(curso.getNombre()));
    }
    
    @Test(expected=RuntimeException.class)
    public void debieraEliminarCurso() {
        log.debug("Debiera eliminar curso");
        cursoDao.elimina(cursoId);
        
        Curso curso = cursoDao.obtiene(cursoId);
        fail("Debe lanzar una excepcion de curso no encontrado");
    }
                
}
