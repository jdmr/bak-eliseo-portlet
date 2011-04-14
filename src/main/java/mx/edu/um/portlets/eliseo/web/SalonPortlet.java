package mx.edu.um.portlets.eliseo.web;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import mx.edu.um.portlets.eliseo.dao.AlumnoInscrito;
import mx.edu.um.portlets.eliseo.dao.Curso;
import mx.edu.um.portlets.eliseo.dao.CursoDao;
import mx.edu.um.portlets.eliseo.dao.Salon;
import mx.edu.um.portlets.eliseo.dao.SalonDao;
import mx.edu.um.portlets.eliseo.dao.Sesion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class SalonPortlet {

    private static final Logger log = LoggerFactory.getLogger(SalonPortlet.class);
    @Autowired
    private SalonDao salonDao;
    @Autowired
    private CursoDao cursoDao;
    private Salon salon;
    private Sesion sesion;
    @Autowired
    private SalonValidator salonValidator;
    @Autowired
    private SesionValidator sesionValidator;
    @Autowired
    private ResourceBundleMessageSource messageSource;
    private Map<Integer, String> dias;

    public SalonPortlet() {
        log.debug("Nueva instancia del portlet de salones");
    }

    @InitBinder
    public void inicializar(PortletRequestDataBinder binder) {
        if (binder.getTarget() instanceof Salon) {
            binder.setValidator(salonValidator);
            binder.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), false));
        }
    }

    @RequestMapping
    public String lista(RenderRequest request,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "max", required = false) Integer max,
            @RequestParam(value = "direccion", required = false) String direccion,
            Model modelo) throws SystemException {

        log.debug("Mostrando lista de salones");
        Map<Long, String> comunidades = obtieneComunidades((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));
        Long total = salonDao.cantidad(comunidades.keySet());
        modelo.addAttribute("cantidad", total);

        if (max == null) {
            max = new Integer(5);
        }
        if (offset == null) {
            offset = new Integer(0);
        } else if (direccion.equals("siguiente") && (offset + max) <= total) {
            offset = offset + max;
        } else if (direccion.equals("anterior") && offset > 0) {
            offset = offset - max;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("max", max);
        params.put("offset", offset);

        params = salonDao.lista(params);
        modelo.addAttribute("salones", params.get("salones"));
        modelo.addAttribute("max", max);
        modelo.addAttribute("offset", offset);

        return "salon/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model model) throws SystemException {
        log.debug("Nuevo salon");
        salon = new Salon();
        model.addAttribute("salon", salon);
        model.addAttribute("comunidades", obtieneComunidades((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)));
        return "salon/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model model) throws SystemException {
        log.debug("Hubo algun error y regresamos a editar el nuevo salon");
        model.addAttribute("comunidades", obtieneComunidades((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)));
        return "salon/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @RequestParam Long cursoId,
            @RequestParam Long maestroId,
            @ModelAttribute("salon") Salon salon,
            BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        log.debug("Creando el salon");
        log.debug("Salon: {} {} {} {} {} {}", new Object[]{salon.getNombre(), cursoId, salon.getMaestroId(), salon.getMaestroNombre(), salon.getInicia(), salon.getTermina()});

        if (cursoId != null && cursoId > 0) {
            salon.setCurso(cursoDao.obtiene(cursoId));
        }

        salonValidator.validate(salon, result);
        if (!result.hasErrors()) {
            salon = salonDao.crea(salon);
            response.setRenderParameter("action", "ver");
            response.setRenderParameter("salonId", salon.getId().toString());
            sessionStatus.setComplete();
        } else {
            log.error("No se pudo guardar el salon");
            response.setRenderParameter("action", "nuevoError");
        }
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam("salonId") Long id, Model model) throws PortalException, SystemException {
        log.debug("Ver salon");
        salon = salonDao.obtiene(id);
        model.addAttribute("salon", salon);

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm z");
        sdf.setTimeZone(themeDisplay.getTimeZone());
        List<Sesion> sesionesList = salonDao.obtieneSesiones(salon);
        List<SesionVO> sesiones = new ArrayList<SesionVO>();
        for (Sesion sesionLocal : sesionesList) {
            sesiones.add(new SesionVO(sesionLocal, sdf));
        }
        model.addAttribute("sesiones", sesiones);

        return "salon/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, @RequestParam("salonId") Long id, Model model) throws SystemException {
        log.debug("Edita salon");
        salon = salonDao.obtiene(id);
        log.debug("Salon: {} {} {} {} {} {}", new Object[]{salon.getNombre(), salon.getCurso().getId(), salon.getMaestroId(), salon.getMaestroNombre(), salon.getInicia(), salon.getTermina()});

        model.addAttribute("salon", salon);
        return "salon/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, @RequestParam("salonId") Long id, Model model) throws SystemException {
        log.debug("Regresando a edicion debido a un error");
        return "salon/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @RequestParam Long cursoId,
            @RequestParam Long maestroId,
            @ModelAttribute("salon") Salon salon, BindingResult result,
            Model model, SessionStatus sessionStatus) {
        log.debug("Actualizando el salon");

        log.debug("Salon: {}-{}-{}-{}-{}-{}-{}", new Object[]{salon.getNombre(), cursoId, maestroId, salon.getMaestroNombre(), salon.getInicia(), salon.getTermina(), maestroId});

        if (cursoId != null && cursoId > 0) {
            salon.setCurso(cursoDao.obtiene(cursoId));
        }

        if (maestroId != null & maestroId > 0) {
            salon.setMaestroId(maestroId);
            try {
                User user = UserLocalServiceUtil.getUser(maestroId);
                salon.setMaestroNombre(user.getFullName());
            } catch (Exception e) {
                log.error("Error al obtener al maestro", e);
            }
        }

        salonValidator.validate(salon, result);
        if (!result.hasErrors()) {
            salon = salonDao.actualiza(salon);
            response.setRenderParameter("action", "ver");
            response.setRenderParameter("salonId", salon.getId().toString());
            sessionStatus.setComplete();
        } else {
            log.error("No se pudo actualizar el salon");
            response.setRenderParameter("action", "editaError");
            response.setRenderParameter("salonId", salon.getId().toString());
        }
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, ActionResponse response,
            @ModelAttribute("salon") Salon salon, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("salonId") Long id) {
        log.debug("Eliminando salon {}", id);
        salonDao.elimina(id);
        sessionStatus.setComplete();
    }

    @ResourceMapping(value = "buscaCurso")
    public void buscaCursos(@RequestParam("term") String cursoNombre, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Buscando cursos que contengan {}", cursoNombre);
        JSONArray results = JSONFactoryUtil.createJSONArray();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("comunidades", obtieneComunidades((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).keySet());
        params.put("filtro", cursoNombre);
        List<Curso> cursos = cursoDao.busca(params);
        for (Curso curso : cursos) {
            JSONObject listEntry = JSONFactoryUtil.createJSONObject();

            listEntry.put("id", curso.getId());
            listEntry.put("value", curso.getCodigo() + " | " + curso.getNombre());

            results.put(listEntry);
        }

        PrintWriter writer = response.getWriter();
        writer.println(results.toString());
    }

    @ResourceMapping(value = "asignaCurso")
    public void asignaCurso(@RequestParam("id") Long cursoId, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Asignando curso {}", cursoId);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        StringBuilder sb = new StringBuilder();
        Curso curso = cursoDao.obtiene(cursoId);
        sb.append("<table><thead><tr>");
        sb.append("<th>");
        sb.append(messageSource.getMessage("curso.codigo", null, themeDisplay.getLocale()));
        sb.append("</th><th>").append(messageSource.getMessage("curso.nombre", null, themeDisplay.getLocale())).append("</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody><tr><td>");
        sb.append(curso.getCodigo());
        sb.append("</td><td>");
        sb.append(curso.getNombre());
        sb.append("</td></tbody></table>");

        PrintWriter writer = response.getWriter();
        writer.println(sb.toString());
    }

    @ResourceMapping(value = "buscaMaestro")
    public void buscaMaestros(@RequestParam("term") String maestroNombre, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Buscando maestros que contengan {}", maestroNombre);
        JSONArray results = JSONFactoryUtil.createJSONArray();

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        List<User> usuarios = UserLocalServiceUtil.search(themeDisplay.getCompanyId(), maestroNombre, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator) null);
        for (User usuario : usuarios) {
            JSONObject listEntry = JSONFactoryUtil.createJSONObject();

            listEntry.put("id", usuario.getPrimaryKey());
            listEntry.put("value", usuario.getFullName() + " | " + usuario.getScreenName() + " | " + usuario.getEmailAddress());
            listEntry.put("nombre", usuario.getFullName());

            results.put(listEntry);
        }

        PrintWriter writer = response.getWriter();
        writer.println(results.toString());
    }

    @ResourceMapping(value = "asignaMaestro")
    public void asignaMaestro(@RequestParam("id") Long maestroId, ResourceRequest request, ResourceResponse response) throws IOException, SystemException, PortalException {
        log.debug("Asignando maestro {}", maestroId);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        StringBuilder sb = new StringBuilder();
        User user = UserLocalServiceUtil.getUser(maestroId);
        sb.append("<table><thead><tr>");
        sb.append("<th>");
        sb.append(messageSource.getMessage("usuario.nombre", null, themeDisplay.getLocale()));
        sb.append("</th><th>").append(messageSource.getMessage("usuario.usuario", null, themeDisplay.getLocale())).append("</th>");
        sb.append("</th><th>").append(messageSource.getMessage("usuario.correo", null, themeDisplay.getLocale())).append("</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody><tr><td>");
        sb.append(user.getFullName());
        sb.append("</td><td>");
        sb.append(user.getScreenName());
        sb.append("</td><td>");
        sb.append(user.getEmailAddress());
        sb.append("</td></tbody></table>");

        PrintWriter writer = response.getWriter();
        writer.println(sb.toString());
    }

    private Map<Long, String> obtieneComunidades(ThemeDisplay themeDisplay) throws SystemException {
        List types = new ArrayList();

        types.add(new Integer(GroupConstants.TYPE_COMMUNITY_OPEN));
        types.add(new Integer(GroupConstants.TYPE_COMMUNITY_RESTRICTED));
        types.add(new Integer(GroupConstants.TYPE_COMMUNITY_PRIVATE));

        LinkedHashMap groupParams = new LinkedHashMap();
        groupParams.put("types", types);
        groupParams.put("active", Boolean.TRUE);

        List<Group> comunidadesList = GroupLocalServiceUtil.search(themeDisplay.getCompanyId(), null, null, groupParams, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
        Map<Long, String> comunidades = new LinkedHashMap<Long, String>();
        for (Group group : comunidadesList) {
            comunidades.put(group.getGroupId(), group.getName());
        }

        return comunidades;
    }

    @RequestMapping(params = "action=nuevaSesion")
    public String nuevaSesion(RenderRequest request, @RequestParam("salonId") Long id, Model model) {
        log.debug("Nueva Sesion");
        salon = salonDao.obtiene(id);

        sesion = new Sesion();
        sesion.setSalon(salon);

        model.addAttribute("sesion", sesion);
        model.addAttribute("dias", getDias(request));

        return "salon/nuevaSesion";
    }

    public Map<Integer, String> getDias(RenderRequest request) {
        if (dias == null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

            dias = new LinkedHashMap<Integer, String>();
            for (int i = 1; i <= 7; i++) {
                dias.put(i, messageSource.getMessage("dia" + i, null, themeDisplay.getLocale()));
            }
        }
        return dias;
    }

    @RequestMapping(params = "action=creaSesion")
    public void creaSesion(ActionRequest request, ActionResponse response,
            @RequestParam String horaInicial,
            @RequestParam String horaFinal,
            @ModelAttribute("sesion") Sesion sesion,
            BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        log.debug("Creando la sesion");

        log.debug("Sesion {} {} {} {}", new Object[]{sesion.getDia(), sesion.getSalon().getId(), horaInicial, horaFinal});

        if (sesion.getSalon().getId() != null && sesion.getSalon().getId() > 0) {
            sesion.setSalon(salonDao.obtiene(sesion.getSalon().getId()));
        }

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(themeDisplay.getTimeZone());
        try {
            sesion.setHoraInicial(sdf.parse(horaInicial));
            sesion.setHoraFinal(sdf.parse(horaFinal));
        } catch (ParseException ex) {
            log.error("No se pudo leer la hora inicial y/o la hora final", ex);
        }

        sesion = salonDao.creaSesion(sesion);
        sessionStatus.setComplete();
        response.setRenderParameter("action", "ver");
        response.setRenderParameter("salonId", salon.getId().toString());

    }

    @RequestMapping(params = "action=nuevaSesionError")
    public String nuevaSesionError(RenderRequest request, Model model) throws SystemException {
        log.debug("Hubo algun error y regresamos a editar la nueva sesion");
        return "salon/nuevaSesion";
    }

    @RequestMapping(params = "action=eliminaSesion")
    public void eliminaSesion(ActionRequest request, ActionResponse response,
            @RequestParam Long sesionId,
            @RequestParam Long salonId,
            @ModelAttribute("sesion") Sesion sesion,
            BindingResult result,
            Model model, SessionStatus sessionStatus) {
        log.debug("Eliminando sesion {}", sesionId);
        salonDao.eliminaSesion(sesionId);
        sessionStatus.setComplete();

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("salonId", salonId.toString());
    }

    @RequestMapping(params = "action=alumnos")
    public String alumnos(RenderRequest request, @RequestParam Long salonId, Model model) {
        log.debug("Mostrando alumnos de salon {}",salonId);
        salon = salonDao.obtiene(salonId);
        
        List<AlumnoInscrito> alumnos = salonDao.getAlumnos(salon);
        
        model.addAttribute("salon",salon);
        model.addAttribute("alumnos",alumnos);
        
        return "salon/alumnos";
    }
    
    @ResourceMapping(value = "buscaAlumno")
    public void buscaAlumno(@RequestParam("term") String alumnoNombre, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Buscando alumnos que contengan {}", alumnoNombre);
        JSONArray results = JSONFactoryUtil.createJSONArray();

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        List<User> usuarios = UserLocalServiceUtil.search(themeDisplay.getCompanyId(), alumnoNombre, true, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, (OrderByComparator) null);
        for (User usuario : usuarios) {
            JSONObject listEntry = JSONFactoryUtil.createJSONObject();

            listEntry.put("id", usuario.getPrimaryKey());
            listEntry.put("value", usuario.getFullName() + " | " + usuario.getScreenName() + " | " + usuario.getEmailAddress());
            listEntry.put("nombre", usuario.getFullName());

            results.put(listEntry);
        }

        PrintWriter writer = response.getWriter();
        writer.println(results.toString());
    }

    @ResourceMapping(value = "asignaAlumno")
    public void asignaAlumno(@RequestParam Long alumnoId, 
                             @RequestParam Long salonId, 
                             @RequestParam String url,
                             ResourceRequest request, ResourceResponse response) throws IOException, SystemException, PortalException {
        log.debug("Asignando alumno {}", alumnoId);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        StringBuilder sb = new StringBuilder();
        User user = UserLocalServiceUtil.getUser(alumnoId);
        salon = salonDao.obtiene(salonId);
        salonDao.agregaAlumno(salon, user);
        List<AlumnoInscrito> alumnos = salonDao.getAlumnos(salon);
        
        sb.append("<table><thead><tr>");
        sb.append("<th>");
        sb.append(messageSource.getMessage("usuario.nombre", null, themeDisplay.getLocale()));
        sb.append("</th><th>").append(messageSource.getMessage("usuario.usuario", null, themeDisplay.getLocale())).append("</th>");
        sb.append("</th><th>").append(messageSource.getMessage("usuario.correo", null, themeDisplay.getLocale())).append("</th>");
        sb.append("</th><th>").append(messageSource.getMessage("acciones", null, themeDisplay.getLocale())).append("</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody>");
        for(AlumnoInscrito alumno : alumnos) {
            String nuevaUrl = url + "&_Salones_WAR_eliseoportlet_alumnoId="+alumno.getId();
            sb.append("<tr><td>");
            sb.append(alumno.getNombreCompleto());
            sb.append("</td><td>");
            sb.append(alumno.getUsuario());
            sb.append("</td><td>");
            sb.append(alumno.getCorreo());
            sb.append("</td><td><a href='").append(nuevaUrl).append("'>");
            sb.append(messageSource.getMessage("salon.eliminaAlumno", null, themeDisplay.getLocale())).append("</a>");
            sb.append("</td></tr>");
        }
        sb.append("</tbody></table>");

        PrintWriter writer = response.getWriter();
        writer.println(sb.toString());
    }

    @RequestMapping(params = "action=eliminaAlumno")
    public void eliminaAlumno(ActionRequest request, ActionResponse response,
            @RequestParam Long alumnoId,
            @ModelAttribute("salon") Salon salon,
            BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        log.debug("Dando de baja a alumno inscrito {} ", alumnoId);

        Long salonId = salonDao.eliminaAlumno(alumnoId);
        
        sessionStatus.setComplete();
        response.setRenderParameter("action", "alumnos");
        response.setRenderParameter("salonId", salonId.toString());

    }

    
}
