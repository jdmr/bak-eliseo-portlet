package mx.edu.um.portlets.eliseo.web;

import mx.edu.um.portlets.eliseo.dao.CursoDao;
import mx.edu.um.portlets.eliseo.dao.Curso;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.servlet.ImageServletTokenUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.imagegallery.model.IGImage;
import com.liferay.portlet.imagegallery.service.IGImageLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;
import com.liferay.portlet.journalcontent.util.JournalContentUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageServiceUtil;
import com.liferay.util.portlet.PortletRequestUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import mx.edu.um.portlets.eliseo.dao.Examen;
import mx.edu.um.portlets.eliseo.dao.ExamenDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping("VIEW")
public class CursoPortlet {

    private static final Logger log = LoggerFactory.getLogger(CursoPortlet.class);
    @Autowired
    private CursoDao cursoDao;
    private Curso curso;
    @Autowired
    private CursoValidator cursoValidator;
    @Autowired
    private ExamenDao examenDao;
    private Examen examen;

    public CursoPortlet() {
        log.debug("Nueva instancia del portlet de cursos");
    }

    @InitBinder
    public void inicializar(PortletRequestDataBinder binder) {
        if (binder.getTarget() instanceof Curso) {
            binder.setValidator(cursoValidator);
        }
    }

    @ModelAttribute("curso")
    public Curso getCommandObject() {
        log.info("Creando el curso");
        if (curso == null) {
            curso = new Curso();
        }
        return curso;
    }
    
    @RequestMapping
    public String lista(RenderRequest request,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "max", required = false) Integer max,
            @RequestParam(value = "direccion", required = false) String direccion,
            Model modelo) throws PortalException, SystemException {
        log.debug("Lista de cursos");
        curso = null;
        User user = PortalUtil.getUser(request);
        Map<Long, String> comunidades = obtieneComunidades(request);
        Long total = cursoDao.cantidad(comunidades.keySet());
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

        params = cursoDao.lista(params);
        modelo.addAttribute("cursos", params.get("cursos"));
        modelo.addAttribute("max", max);
        modelo.addAttribute("offset", offset);

        return "curso/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model model) throws SystemException {
        log.debug("Nuevo curso");
        curso = new Curso();
        model.addAttribute("curso", curso);
        model.addAttribute("comunidades", obtieneComunidades(request));
        return "curso/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model model) throws SystemException {
        log.debug("Hubo algun error y regresamos a editar el nuevo curso");
        model.addAttribute("comunidades", obtieneComunidades(request));
        return "curso/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        log.debug("Guardando el curso");
        curso.setComunidadNombre(GroupLocalServiceUtil.getGroup(curso.getComunidadId()).getDescriptiveName());
        cursoValidator.validate(curso, result);
        if (!result.hasErrors()) {
            curso = cursoDao.crea(curso);
            sessionStatus.setComplete();
        } else {
            log.error("No se pudo guardar el curso");
            response.setRenderParameter("action", "nuevoError");
        }
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, @RequestParam("cursoId") Long id, Model model) throws SystemException {
        log.debug("Edita curso");
        model.addAttribute("curso", cursoDao.obtiene(id));
        return "curso/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, @RequestParam("cursoId") Long id, Model model) throws SystemException {
        log.debug("Regresando a edicion debido a un error");
        return "curso/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus) {
        log.debug("Guardando el curso");
        cursoValidator.validate(curso, result);
        if (!result.hasErrors()) {
            cursoDao.actualiza(curso);
            sessionStatus.setComplete();
        } else {
            log.error("No se pudo actualizar el curso");
            response.setRenderParameter("action", "editaError");
            response.setRenderParameter("cursoId", curso.getId().toString());
        }
    }

    @RequestMapping(params = "action=busca")
    public String busca(RenderRequest request, Model modelo, @RequestParam("filtro") String filtro) throws PortalException, SystemException {
        log.debug("Buscando curso");
        curso = null;
        User user = PortalUtil.getUser(request);
        Map<Long, String> comunidades = obtieneComunidades(request);
        modelo.addAttribute("cantidad", cursoDao.cantidad(comunidades.keySet()));
        log.debug(filtro);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("filtro", filtro);
        List<Curso> cursos = cursoDao.busca(params);
        if (cursos != null && cursos.size() > 0) {
            modelo.addAttribute("cursos", cursos);
        }
        return "curso/lista";
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam("cursoId") Long id, Model model) throws PortalException, SystemException {
        log.debug("Ver curso");
        curso = cursoDao.obtiene(id);
        model.addAttribute("curso", curso);
        List contenidos = new ArrayList();
        String[] lista = StringUtil.split(curso.getContenidos());
        if (lista != null && lista.length > 0) {
            contenidos = new ArrayList();
        }
        for (String contenidoId : lista) {
            if (contenidoId.startsWith("E")) {
                contenidos.add(examenDao.obtiene(new Long(contenidoId.substring(1))));
            } else {
                contenidos.add(AssetEntryServiceUtil.getEntry(new Long(contenidoId)));
            }
        }
        model.addAttribute("contenidos", contenidos);

        return "curso/ver";
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("cursoId") Long id) {
        log.debug("Elimina curso " + id);
        cursoDao.elimina(id);
        sessionStatus.setComplete();
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

    @RequestMapping(params = "action=contenido")
    public String contenido(RenderRequest request, RenderResponse response, @RequestParam("cursoId") Long id, Model model) throws SystemException {
        log.debug("Edita contenido");
        curso = cursoDao.obtiene(id);
        model.addAttribute("curso", curso);

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        PortletPreferences preferences = request.getPreferences();
        String portletResource = ParamUtil.getString(request, "portletResource");

        if (Validator.isNotNull(portletResource)) {
            preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
        }

        long scopeGroupId = themeDisplay.getScopeGroupId();

        long[] availableClassNameIds = AssetRendererFactoryRegistryUtil.getClassNameIds();

        boolean anyAssetType = GetterUtil.getBoolean(preferences.getValue("any-asset-type", Boolean.TRUE.toString()));

        // Metodo copiado del AssetPublisherUtil
        long[] classNameIds = getClassNameIds(preferences, availableClassNameIds);

        try {
            AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

            String[] allAssetTagNames = new String[]{curso.getCodigo().toLowerCase()};
            assetEntryQuery = getAssetEntryQuery(preferences, scopeGroupId);

            long[] assetTagIds = AssetTagLocalServiceUtil.getTagIds(scopeGroupId, allAssetTagNames);

            assetEntryQuery.setAllTagIds(assetTagIds);

            List disponibles = new ArrayList();
            List seleccionados = new ArrayList();

            for (long classNameId : classNameIds) {
                long[] groupClassNameIds = {classNameId};

                assetEntryQuery.setClassNameIds(groupClassNameIds);

                List<AssetEntry> results = AssetEntryServiceUtil.getEntries(assetEntryQuery);

                for (AssetEntry asset : results) {
                    log.debug("Asset: " + asset.getTitle() + " : " + asset.getDescription() + " : " + asset.getMimeType() + " : " + asset.getClassName());
                    disponibles.add(new KeyValuePair(new Long(asset.getPrimaryKey()).toString(), asset.getTitle()));
                }

            }
            
            Map<String, Object> params = new HashMap<String, Object>();
            List<Long> communities = new ArrayList<Long>();
            communities.add(scopeGroupId);
            params.put("communities", communities);
            List<Examen> examenes = examenDao.busca(params);
            for(Examen examen : examenes) {
                disponibles.add(new KeyValuePair("E"+examen.getId(), examen.getCodigo()));
            }

            model.addAttribute("disponibles", disponibles);
            model.addAttribute("seleccionados", seleccionados);

        } catch (Exception e) {
            log.error("No se pudo cargar el contenido", e);
            throw new RuntimeException("No se pudo cargar el contenido", e);
        }

        return "curso/contenido";
    }

    @RequestMapping(params = "action=actualizaContenido")
    public void actualizaContenido(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("cursoId") Long id, @RequestParam("seleccionados") String seleccionados) {
        log.debug("Actualizando contenido");
        log.debug("CursoId: {} | Contenidos: ", new Object[]{id, seleccionados});
        if (seleccionados.length() == 0) {
            Map params = request.getParameterMap();
            String[] contenidoSeleccionado = (String[]) params.get("contenidoSeleccionado");
            seleccionados = StringUtil.merge(contenidoSeleccionado);
        }

        curso = cursoDao.obtiene(id);

        curso.setContenidos(seleccionados);

        cursoDao.actualiza(curso);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("cursoId", id.toString());
    }

    @RequestMapping(params = "action=verContenido")
    public String verContenido(RenderRequest request, RenderResponse response, @RequestParam("cursoId") Long cursoId, @RequestParam("contenidoId") Long contenidoId, Model model) {
        log.debug("Ver contenido");
        curso = cursoDao.obtiene(cursoId);
        model.addAttribute("curso", curso);
        model.addAttribute("contenidoId", contenidoId);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            AssetEntry contenido = AssetEntryServiceUtil.getEntry(new Long(contenidoId));
            log.debug("Contenido: " + contenido);
            if (contenido.getClassName().equals(JournalArticle.class.getName())) {
                JournalArticleResource articleResource = JournalArticleResourceLocalServiceUtil.getArticleResource(contenido.getClassPK());
                String templateId = (String) request.getAttribute("JOURNAL_TEMPLATE_ID");
                String languageId = LanguageUtil.getLanguageId(request);
                int articlePage = ParamUtil.getInteger(request, "page", 1);
                String xmlRequest = PortletRequestUtil.toXML(request, response);
                model.addAttribute("currentURL", themeDisplay.getURLCurrent());

                JournalArticleDisplay articleDisplay = JournalContentUtil.getDisplay(articleResource.getGroupId(), articleResource.getArticleId(), templateId, null, languageId, themeDisplay, articlePage, xmlRequest);

                if (articleDisplay != null) {
                    AssetEntryServiceUtil.incrementViewCounter(contenido.getClassName(), articleDisplay.getResourcePrimKey());
                    model.addAttribute("articleDisplay", articleDisplay);

                    String[] availableLocales = articleDisplay.getAvailableLocales();
                    if (availableLocales.length > 0) {
                        model.addAttribute("availableLocales", availableLocales);
                    }
                    int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(JournalArticle.class.getName()), articleDisplay.getResourcePrimKey(), WorkflowConstants.STATUS_APPROVED);
                    if (discussionMessagesCount > 0) {
                        model.addAttribute("discussionMessages", true);
                    }
                }
            } else if (contenido.getClassName().equals(IGImage.class.getName())) {
                IGImage image = IGImageLocalServiceUtil.getImage(contenido.getClassPK());
                AssetEntryServiceUtil.incrementViewCounter(contenido.getClassName(), image.getImageId());
                model.addAttribute("contenido", contenido);
                model.addAttribute("image", image);
                model.addAttribute("imageURL", themeDisplay.getPathImage() + "/image_gallery?img_id=" + image.getLargeImageId() + "&t=" + ImageServletTokenUtil.getToken(image.getLargeImageId()));
                int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(IGImage.class.getName()),
                        image.getPrimaryKey(),
                        WorkflowConstants.STATUS_APPROVED);
                if (discussionMessagesCount > 0) {
                    model.addAttribute("discussionMessages", true);
                }
            } else if (contenido.getClassName().equals(DLFileEntry.class.getName())) {
                DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getFileEntry(contenido.getClassPK());

                model.addAttribute("document", fileEntry);
                String fileUrl = themeDisplay.getPortalURL() + themeDisplay.getPathContext() + "/documents/" + themeDisplay.getScopeGroupId() + StringPool.SLASH + fileEntry.getFolderId() + StringPool.SLASH + HttpUtil.encodeURL(HtmlUtil.unescape(fileEntry.getTitle()));
                //model.addAttribute("documentURL", themeDisplay.getPathMain() + "/document_library/get_file?p_l_id=" + themeDisplay.getPlid() + "&folderId=" + fileEntry.getFolderId() + "&name=" + HttpUtil.encodeURL(fileEntry.getName()));
                model.addAttribute("documentURL", fileUrl);

                log.debug("NAME: {}", fileEntry.getTitle());
                if (fileEntry.getTitle().endsWith("flv")) {
                    model.addAttribute("video", true);
                }
                int discussionMessagesCount = MBMessageLocalServiceUtil.getDiscussionMessagesCount(PortalUtil.getClassNameId(DLFileEntry.class.getName()),
                        fileEntry.getPrimaryKey(),
                        WorkflowConstants.STATUS_APPROVED);
                if (discussionMessagesCount > 0) {
                    model.addAttribute("discussionMessages", true);
                }
            }
        } catch (Exception e) {
            log.error("Error al traer el contenido", e);
        }

        return "curso/verContenido";
    }

    public static AssetEntryQuery getAssetEntryQuery(
            PortletPreferences preferences, long scopeGroupId)
            throws Exception {

        AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

        long[] allAssetCategoryIds = new long[0];
        long[] anyAssetCategoryIds = new long[0];
        long[] notAllAssetCategoryIds = new long[0];
        long[] notAnyAssetCategoryIds = new long[0];

        String[] allAssetTagNames = new String[0];
        String[] anyAssetTagNames = new String[0];
        String[] notAllAssetTagNames = new String[0];
        String[] notAnyAssetTagNames = new String[0];

        for (int i = 0; true; i++) {
            String[] queryValues = preferences.getValues(
                    "queryValues" + i, null);

            if ((queryValues == null) || (queryValues.length == 0)) {
                break;
            }

            boolean queryContains = GetterUtil.getBoolean(
                    preferences.getValue("queryContains" + i, StringPool.BLANK));
            boolean queryAndOperator = GetterUtil.getBoolean(
                    preferences.getValue("queryAndOperator" + i, StringPool.BLANK));
            String queryName = preferences.getValue(
                    "queryName" + i, StringPool.BLANK);

            if (Validator.equals(queryName, "assetCategories")) {
                long[] assetCategoryIds = GetterUtil.getLongValues(queryValues);

                if (queryContains && queryAndOperator) {
                    allAssetCategoryIds = assetCategoryIds;
                } else if (queryContains && !queryAndOperator) {
                    anyAssetCategoryIds = assetCategoryIds;
                } else if (!queryContains && queryAndOperator) {
                    notAllAssetCategoryIds = assetCategoryIds;
                } else {
                    notAnyAssetCategoryIds = assetCategoryIds;
                }
            } else {
                if (queryContains && queryAndOperator) {
                    allAssetTagNames = queryValues;
                } else if (queryContains && !queryAndOperator) {
                    anyAssetTagNames = queryValues;
                } else if (!queryContains && queryAndOperator) {
                    notAllAssetTagNames = queryValues;
                } else {
                    notAnyAssetTagNames = queryValues;
                }
            }
        }

        long[] allAssetTagIds = AssetTagLocalServiceUtil.getTagIds(
                scopeGroupId, allAssetTagNames);
        long[] anyAssetTagIds = AssetTagLocalServiceUtil.getTagIds(
                scopeGroupId, anyAssetTagNames);
        long[] notAllAssetTagIds = AssetTagLocalServiceUtil.getTagIds(
                scopeGroupId, notAllAssetTagNames);
        long[] notAnyAssetTagIds = AssetTagLocalServiceUtil.getTagIds(
                scopeGroupId, notAnyAssetTagNames);

        assetEntryQuery.setAllCategoryIds(allAssetCategoryIds);
        assetEntryQuery.setAllTagIds(allAssetTagIds);
        assetEntryQuery.setAnyCategoryIds(anyAssetCategoryIds);
        assetEntryQuery.setAnyTagIds(anyAssetTagIds);
        assetEntryQuery.setNotAllCategoryIds(notAllAssetCategoryIds);
        assetEntryQuery.setNotAllTagIds(notAllAssetTagIds);
        assetEntryQuery.setNotAnyCategoryIds(notAnyAssetCategoryIds);
        assetEntryQuery.setNotAnyTagIds(notAnyAssetTagIds);

        return assetEntryQuery;
    }

    public static String[] getAssetTagNames(
            PortletPreferences preferences, long scopeGroupId)
            throws Exception {

        String[] allAssetTagNames = new String[0];

        for (int i = 0; true; i++) {
            String[] queryValues = preferences.getValues(
                    "queryValues" + i, null);

            if ((queryValues == null) || (queryValues.length == 0)) {
                break;
            }

            boolean queryContains = GetterUtil.getBoolean(
                    preferences.getValue("queryContains" + i, StringPool.BLANK));
            boolean queryAndOperator = GetterUtil.getBoolean(
                    preferences.getValue("queryAndOperator" + i, StringPool.BLANK));
            String queryName = preferences.getValue(
                    "queryName" + i, StringPool.BLANK);

            if (!Validator.equals(queryName, "assetCategories")
                    && queryContains && queryAndOperator) {

                allAssetTagNames = queryValues;
            }
        }

        return allAssetTagNames;
    }

    public static long[] getClassNameIds(
            PortletPreferences preferences, long[] availableClassNameIds) {

        boolean anyAssetType = GetterUtil.getBoolean(
                preferences.getValue("any-asset-type", Boolean.TRUE.toString()));

        long[] classNameIds = null;

        if (!anyAssetType
                && (preferences.getValues("class-name-ids", null) != null)) {

            classNameIds = GetterUtil.getLongValues(
                    preferences.getValues("class-name-ids", null));
        } else {
            classNameIds = availableClassNameIds;
        }

        return classNameIds;
    }

    public static long[] getGroupIds(
            PortletPreferences preferences, long scopeGroupId, Layout layout) {

        long[] groupIds = new long[]{scopeGroupId};

        boolean defaultScope = GetterUtil.getBoolean(
                preferences.getValue("default-scope", null), true);

        if (!defaultScope) {
            String[] scopeIds = preferences.getValues(
                    "scope-ids",
                    new String[]{"group" + StringPool.UNDERLINE + scopeGroupId});

            groupIds = new long[scopeIds.length];

            for (int i = 0; i < scopeIds.length; i++) {
                try {
                    String[] scopeIdFragments = StringUtil.split(
                            scopeIds[i], StringPool.UNDERLINE);

                    if (scopeIdFragments[0].equals("Layout")) {
                        long scopeIdLayoutId = GetterUtil.getLong(
                                scopeIdFragments[1]);

                        Layout scopeIdLayout =
                                LayoutLocalServiceUtil.getLayout(
                                scopeGroupId, layout.isPrivateLayout(),
                                scopeIdLayoutId);

                        Group scopeIdGroup = scopeIdLayout.getScopeGroup();

                        groupIds[i] = scopeIdGroup.getGroupId();
                    } else {
                        if (scopeIdFragments[1].equals(
                                GroupConstants.DEFAULT)) {

                            groupIds[i] = scopeGroupId;
                        } else {
                            long scopeIdGroupId = GetterUtil.getLong(
                                    scopeIdFragments[1]);

                            groupIds[i] = scopeIdGroupId;
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        return groupIds;
    }

    @RequestMapping(params = "action=discusion")
    public void discusion(ActionRequest request, ActionResponse response,
            @ModelAttribute("curso") Curso curso, BindingResult result,
            Model model, SessionStatus sessionStatus, @RequestParam("cursoId") Long id, @RequestParam("contenidoId") Long contenidoId) {
        log.debug("Ver discusion");
        log.debug("CursoId: " + id);

        try {
            String cmd = ParamUtil.getString(request, Constants.CMD);
            if (cmd.equals(Constants.ADD) || cmd.equals(Constants.UPDATE)) {
                MBMessage message = updateMessage(request);
            } else if (cmd.equals(Constants.DELETE)) {
                deleteMessage(request);
            }
        } catch (Exception e) {
            log.error("Error al intentar actualizar el mensaje", e);
        }

        response.setRenderParameter("action", "verContenido");
        response.setRenderParameter("cursoId", id.toString());
        response.setRenderParameter("contenidoId", contenidoId.toString());
    }

    protected void deleteMessage(ActionRequest actionRequest) throws Exception {
        long groupId = PortalUtil.getScopeGroupId(actionRequest);

        String className = ParamUtil.getString(actionRequest, "className");
        long classPK = ParamUtil.getLong(actionRequest, "classPK");

        String permissionClassName = ParamUtil.getString(
                actionRequest, "permissionClassName");

        long permissionClassPK = ParamUtil.getLong(
                actionRequest, "permissionClassPK");

        long messageId = ParamUtil.getLong(actionRequest, "messageId");


        MBMessageServiceUtil.deleteDiscussionMessage(
                groupId, className, classPK, permissionClassName, permissionClassPK,
                messageId);
    }

    protected MBMessage updateMessage(ActionRequest actionRequest)
            throws Exception {

        String className = ParamUtil.getString(actionRequest, "className");
        long classPK = ParamUtil.getLong(actionRequest, "classPK");
        String permissionClassName = ParamUtil.getString(
                actionRequest, "permissionClassName");
        long permissionClassPK = ParamUtil.getLong(
                actionRequest, "permissionClassPK");

        long messageId = ParamUtil.getLong(actionRequest, "messageId");

        long threadId = ParamUtil.getLong(actionRequest, "threadId");
        long parentMessageId = ParamUtil.getLong(
                actionRequest, "parentMessageId");
        String subject = ParamUtil.getString(actionRequest, "subject");
        String body = ParamUtil.getString(actionRequest, "body");

        ServiceContext serviceContext = ServiceContextFactory.getInstance(
                MBMessage.class.getName(), actionRequest);

        MBMessage message = null;

        if (messageId <= 0) {

            // Add message

            message = MBMessageServiceUtil.addDiscussionMessage(
                    serviceContext.getScopeGroupId(), className, classPK,
                    permissionClassName, permissionClassPK, threadId,
                    parentMessageId, subject, body, serviceContext);
        } else {

            // Update message

            message = MBMessageServiceUtil.updateDiscussionMessage(
                    className, classPK, permissionClassName, permissionClassPK,
                    messageId, subject, body, serviceContext);
        }

        return message;
    }
    
    
    @RequestMapping(params = "action=nuevoExamen")
    public String nuevoExamen(
            RenderRequest request, 
            RenderResponse response, 
            @RequestParam("cursoId") Long cursoId, 
            Model model) {
        
        curso = cursoDao.obtiene(cursoId);
        examen = new Examen();
        examen.setCurso(curso);
        model.addAttribute("curso", curso);
        model.addAttribute("examen", examen);
        
        return "examen/nuevo";
    }
    
    @RequestMapping(params = "action=creaExamen")
    public void creaExamen(ActionRequest request, ActionResponse response,
            @ModelAttribute("examen") Examen examen, BindingResult result,
            Model model, SessionStatus sessionStatus) throws PortalException, SystemException {
        
        log.debug("Creando el examen");
        examen.setCurso(cursoDao.obtiene(examen.getCurso().getId()));
        examen = examenDao.crea(examen);
        
        response.setRenderParameter("action", "ver");
        response.setRenderParameter("cursoId", examen.getCurso().getId().toString());
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }
}
