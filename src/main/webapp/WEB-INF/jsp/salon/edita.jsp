<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><liferay-ui:message key="salon.edita" /></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="actualiza"/>
    </portlet:actionURL>

    <form:form name="salonForm" commandName="salon" method="post" action="${actionUrl}" >
        <form:hidden path="id" />
        <form:hidden path="version" />
        <input type="hidden" id="<portlet:namespace />cursoId" name="<portlet:namespace />cursoId" value="<c:if test='${salon.curso != null}'><c:out value='${salon.curso.id}'/></c:if>"/>
        <input type="hidden" id="<portlet:namespace />maestroId" name="<portlet:namespace />maestroId" value="${salon.maestroId}"/>
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />nombre"><liferay-ui:message key="salon.nombre" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input id="<portlet:namespace />nombre" path="nombre" maxlength="128"/>
                            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />cursoNombre"><liferay-ui:message key="salon.curso" /></label>
                        </td>
                        <td valign="top" class="value">
                            <div id="<portlet:namespace />cursoDiv">
                                <c:if test="${salon.curso != null}">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th><liferay-ui:message key="curso.codigo" /></th>
                                                <th><liferay-ui:message key="curso.nombre" /></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>${salon.curso.codigo}</td>
                                                <td>${salon.curso.nombre}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </c:if>
                            </div>
                            <input type="text" name="<portlet:namespace />cursoNombre" id="<portlet:namespace />cursoNombre" value="" />
                            <form:errors cssClass="errors" path="curso" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />maestroNombre"><liferay-ui:message key="salon.maestro" /></label>
                        </td>
                        <td valign="top" class="value">
                            <div id="<portlet:namespace />maestroDiv">
                                <c:if test="${salon.maestroNombre != null && salon.maestroNombre != ''}">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th><liferay-ui:message key="usuario.nombre" /></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>${salon.maestroNombre}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </c:if>
                            </div>
                            <input type="text" name="maestroNombre" id="<portlet:namespace />maestroNombre" value="" />
                            <form:errors cssClass="errors" path="maestroNombre" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />inicia"><liferay-ui:message key="salon.inicia" /></label>
                        </td>
                        <td valign="top" class="value">
                            <input type="text" name="inicia" id="<portlet:namespace />inicia" value="${salon.inicia}" />
                            <form:errors cssClass="errors" path="inicia" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />termina"><liferay-ui:message key="salon.termina" /></label>
                        </td>
                        <td valign="top" class="value">
                            <input type="text" name="termina" id="<portlet:namespace />termina" value="${salon.termina}" />
                            <form:errors cssClass="errors" path="termina" cssStyle="color:red;" />
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="nav">
            <span class="menuButton"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='salon.actualiza' />"/></span>
            <span class="menuButton"><a class="cancel" href="<portlet:renderURL portletMode="view"/>"><liferay-ui:message key="salon.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.salonForm.nombre.focus();
    </script>
</div>
<link type="text/css" href="<%= request.getContextPath() %>/css/custom-theme/jquery-ui-1.8.10.custom.css" rel="Stylesheet" />
<!-- Grab Google CDN's jQuery. fall back to local if necessary -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
<script>!window.jQuery && document.write(unescape('%3Cscript src="<%= request.getContextPath() %>/js/jquery-1.5.1.min.js"%3E%3C/script%3E'))</script>
<script src="<%= request.getContextPath() %>/js/jquery-ui-1.8.10.custom.min.js"></script>
<script type="text/javascript">
    $(document).ready(function() {
        $("input#<portlet:namespace />cursoNombre")
        .autocomplete({
            source: "<portlet:resourceURL id='buscaCurso'/>",
            select: function(event, ui) {
                $("input#<portlet:namespace />cursoId").val(ui.item.id);
                $("#<portlet:namespace />cursoDiv").load('<portlet:resourceURL id="asignaCurso" />',{id:ui.item.id},function() {
                    $("input#<portlet:namespace />cursoNombre").val("");
                    $("input#<portlet:namespace />maestroNombre").focus();
                });
                return false;
            }
        });
        $("input#<portlet:namespace />maestroNombre")
        .autocomplete({
            source: "<portlet:resourceURL id='buscaMaestro'/>",
            select: function(event, ui) {
                $("input#<portlet:namespace />maestroId").val(ui.item.id);
                $("#<portlet:namespace />maestroDiv").load('<portlet:resourceURL id="asignaMaestro" />',{id:ui.item.id},function() {
                    $("input#<portlet:namespace />maestroNombre").val(ui.item.nombre);
                    $("input#<portlet:namespace />inicia").focus();
                });
                return false;
            }
        });
        $("input#<portlet:namespace />inicia").datepicker();
        $("input#<portlet:namespace />termina").datepicker();
    });

</script>
