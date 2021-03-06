package mx.edu.um.portlets.eliseo.web;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.eliseo.dao.CursoDao;
import mx.edu.um.portlets.eliseo.dao.Salon;
import mx.edu.um.portlets.eliseo.dao.SalonDao;
import mx.edu.um.portlets.eliseo.dao.Sesion;
import mx.edu.um.portlets.eliseo.utils.ZonaHorariaUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class CursosActivosPortlet {

    private static final Logger log = LoggerFactory.getLogger(CursosActivosPortlet.class);
    @Autowired
    private CursoDao cursoDao;
    @Autowired
    private SalonDao salonDao;
    private Salon salon;

    public CursosActivosPortlet() {
        log.info("Nueva instancia del portlet de cursos activos");
    }

    @RequestMapping
    public String lista(RenderRequest request,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "max", required = false) Integer max,
            @RequestParam(value = "direccion", required = false) String direccion,
            Model modelo) throws PortalException, SystemException, ParseException {
        log.debug("Lista de cursos");
        Map<Long, String> comunidades = obtieneComunidades(request);
        TimeZone tz = null;
        DateTimeZone zone = null;
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            tz = themeDisplay.getTimeZone();
            zone = DateTimeZone.forID(tz.getID());
        } catch (IllegalArgumentException e) {
            zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
        }
        DateTime hoy = new DateTime(zone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Long total = cursoDao.cantidadActiva(comunidades.keySet(), sdf.parse(hoy.toString("yyyy-MM-dd HH:mm")));
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
        params.put("comunidades", comunidades.keySet());

        params = cursoDao.listaActivos(params, hoy.toDate());
        modelo.addAttribute("salones", params.get("salones"));
        modelo.addAttribute("max", max);
        modelo.addAttribute("offset", offset);

        return "cursosActivos/lista";
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam("salonId") Long id, Model model) throws PortalException, SystemException, ParseException {
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

        User user = PortalUtil.getUser(request);
        if (user != null) {
            log.debug("Usuario {}", user);
            Boolean estaInscrito = salonDao.estaInscrito(id, user.getPrimaryKey());
            if (estaInscrito) {
                model.addAttribute("estaInscrito", true);
                // validar si es hora de entrar a alguna sesion en vivo
                TimeZone tz = null;
                DateTimeZone zone = null;
                try {
                    tz = themeDisplay.getTimeZone();
                    zone = DateTimeZone.forID(tz.getID());
                } catch (IllegalArgumentException e) {
                    zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
                }
                DateTime hoy = new DateTime(zone);

                sdf = new SimpleDateFormat("HH:mm");
                Boolean existeSesionActiva = salonDao.existeSesionActiva(id, hoy.getDayOfWeek(), sdf.parse(hoy.toString("HH:mm")));
                if (existeSesionActiva) {
                    model.addAttribute("salonUrl", salon.getUrl());
                }
            }
        }

        return "cursosActivos/ver";
    }

    @RequestMapping(params = "action=inscribirse")
    public String inscribirse(RenderRequest request, RenderResponse response, @RequestParam("salonId") Long id, Model model) throws PortalException, SystemException, ParseException {
        log.debug("Inscribirse a curso");
        User user = PortalUtil.getUser(request);
        String resultado;
        if (user != null) {
            Boolean estaInscrito = salonDao.estaInscrito(id, user.getPrimaryKey());
            if (estaInscrito) {
                resultado = ver(request, id, model);
            } else {
                log.debug("Iniciando proceso de inscripcion");
                TimeZone tz = null;
                DateTimeZone zone = null;
                ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
                try {
                    tz = themeDisplay.getTimeZone();
                    zone = DateTimeZone.forID(tz.getID());
                } catch (IllegalArgumentException e) {
                    zone = DateTimeZone.forID(ZonaHorariaUtil.getConvertedId(tz.getID()));
                }
                try {
                    long scopeGroupId = themeDisplay.getScopeGroupId();

                    AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

                    DateTime hoy = (DateTime) request.getPortletSession().getAttribute("hoy", PortletSession.APPLICATION_SCOPE);
                    if (hoy == null) {
                        hoy = new DateTime(zone);
                        log.debug("Subiendo atributo hoy({}) a la sesion", hoy);
                        request.getPortletSession().setAttribute("hoy", hoy, PortletSession.APPLICATION_SCOPE);
                    }
                    
                    salon = salonDao.obtiene(id);

                    // Busca el contenido del dia
                    String[] tags = new String[] {salon.getNombre().toLowerCase(),"inscripcion"};

                    long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, tags);

                    assetEntryQuery.setAllTagIds(assetTagIds);

                    List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                    for (AssetEntry asset : results) {
                        if (asset.getClassName().equals(JournalArticle.class.getName())) {
                            JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(asset.getClassPK());
                            String contenido = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("contenido", contenido);
                        }
                    }

                } catch (Exception e) {
                    log.error("No se pudo cargar el contenido", e);
                    throw new RuntimeException("No se pudo cargar el contenido", e);
                }
                resultado = "cursosActivos/inscribirse";
            }
        } else {
            log.debug("Explicandole al usuario que necesita firmarse");
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            model.addAttribute("signInUrl", themeDisplay.getURLSignIn());
            resultado = "cursosActivos/login";
        }
        return resultado;
    }

    private Map<Long, String> obtieneComunidades(RenderRequest request) throws SystemException {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
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
