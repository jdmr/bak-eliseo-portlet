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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import mx.edu.um.portlets.eliseo.dao.Curso;
import mx.edu.um.portlets.eliseo.dao.CursoDao;
import mx.edu.um.portlets.eliseo.dao.Salon;
import mx.edu.um.portlets.eliseo.dao.SalonDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
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

    public SalonPortlet() {
        log.debug("Nueva instancia del portlet de salones");
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
            @ModelAttribute("salon") Salon salon, 
            BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        log.debug("Creando el salon");
        log.debug("Salon: {} {} {} {} {} {}",new Object[] {salon.getNombre(), cursoId, salon.getMaestroId(), salon.getMaestroNombre(), salon.getInicia(), salon.getTermina()});
        
        salon.setCurso(cursoDao.obtiene(cursoId));
        salon = salonDao.crea(salon);
        sessionStatus.setComplete();
    }

    @ResourceMapping(value="buscaCurso")
    public void buscaCursos(@RequestParam("term") String cursoNombre, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Buscando cursos que contengan {}", cursoNombre);
        JSONArray results = JSONFactoryUtil.createJSONArray();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("comunidades", obtieneComunidades((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY)).keySet());
        params.put("filtro", cursoNombre);
        List<Curso> cursos = cursoDao.busca(params);
        for(Curso curso : cursos) {
            JSONObject listEntry = JSONFactoryUtil.createJSONObject();

            listEntry.put("id", curso.getId());
            listEntry.put("value", curso.getCodigo() + " | " + curso.getNombre());

            results.put(listEntry);
        }
        
        PrintWriter writer = response.getWriter();
        writer.println(results.toString());
    }
    
    @ResourceMapping(value="asignaCurso")
    public void asignaCurso(@RequestParam("id") Long cursoId, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Asignando curso {}", cursoId);
        StringBuilder sb = new StringBuilder();
        Curso curso = cursoDao.obtiene(cursoId);
        sb.append("<table><thead><tr>");
        sb.append("<th>Codigo</th><th>Nombre</th>");
        sb.append("</tr></thead>");
        sb.append("<tbody><tr><td>");
        sb.append(curso.getCodigo());
        sb.append("</td><td>");
        sb.append(curso.getNombre());
        sb.append("</td></tbody></table>");
        
        PrintWriter writer = response.getWriter();
        writer.println(sb.toString());
    }
    
    @ResourceMapping(value="buscaMaestro")
    public void buscaMaestros(@RequestParam("term") String maestroNombre, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Buscando maestros que contengan {}", maestroNombre);
        JSONArray results = JSONFactoryUtil.createJSONArray();
        
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        List<User> usuarios = UserLocalServiceUtil.search(themeDisplay.getCompanyId(), maestroNombre, true, null,  QueryUtil.ALL_POS,  QueryUtil.ALL_POS, (OrderByComparator)null);
        for(User usuario : usuarios) {
            JSONObject listEntry = JSONFactoryUtil.createJSONObject();

            listEntry.put("id", usuario.getPrimaryKey());
            listEntry.put("value", usuario.getFullName() +" | "+usuario.getScreenName() + " | " + usuario.getEmailAddress());
            listEntry.put("nombre", usuario.getFullName());

            results.put(listEntry);
        }
        
        PrintWriter writer = response.getWriter();
        writer.println(results.toString());
    }
    
    @ResourceMapping(value="asignaMaestro")
    public void asignaMaestro(@RequestParam("id") Long maestroId, ResourceRequest request, ResourceResponse response) throws IOException, SystemException, PortalException {
        log.debug("Asignando maestro {}", maestroId);
        StringBuilder sb = new StringBuilder();
        User user = UserLocalServiceUtil.getUser(maestroId);
        sb.append("<table><thead><tr>");
        sb.append("<th>Nombre</th><th>Usuario</th><th>Correo</th>");
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
}
