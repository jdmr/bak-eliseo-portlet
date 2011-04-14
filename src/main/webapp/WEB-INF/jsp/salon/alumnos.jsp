<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "salon.ver.titulo",request.getAttribute("salon"),false) %></h1>

    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <liferay-ui:message key="salon.nombre" />
                    </td>
                    <td valign="top" class="value">${salon.nombre}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <liferay-ui:message key="salon.curso" />
                    </td>
                    <td valign="top" class="value">${salon.curso}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <liferay-ui:message key="salon.maestro" />
                    </td>
                    <td valign="top" class="value">${salon.maestroNombre}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <liferay-ui:message key="salon.inicia" />
                    </td>
                    <td valign="top" class="value">${salon.inicia}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <liferay-ui:message key="salon.termina" />
                    </td>
                    <td valign="top" class="value">${salon.termina}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <liferay-ui:message key="salon.agregaAlumno" />
                    </td>
                    <td valign="top" class="value">
                        <portlet:actionURL var="actionUrl">
                            <portlet:param name="action" value="actualiza"/>
                        </portlet:actionURL>

                        <form:form name="salonForm" commandName="salon" method="post" action="${actionUrl}" >
                            <form:hidden path="id" />
                            <input type="text" name="alumnoNombre" id="<portlet:namespace />alumnoNombre" value="" style="width:350px;"/>
                        </form:form>
                    </td>
                </tr>

            </tbody>
        </table>
        <div id="<portlet:namespace />alumnosDiv" class="list">
            <table id="<portlet:namespace />sesiones">
                <thead>
                    <tr>

                        <th><liferay-ui:message key="usuario.nombre" /></th>

                        <th><liferay-ui:message key="usuario.usuario" /></th>

                        <th><liferay-ui:message key="usuario.correo" /></th>

                        <th style="width:100px;"><liferay-ui:message key="acciones" /></th>

                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${alumnos}" var="alumno" varStatus="status">
                        <portlet:actionURL var="eliminaAlumno" >
                            <portlet:param name="action" value="eliminaAlumno" />
                            <portlet:param name="alumnoId" value="${alumno.id}" />
                            <portlet:param name="salonId" value="${salon.id}" />
                        </portlet:actionURL>
                        <tr class="${(status.count % 2) == 0 ? 'odd' : 'even'}">

                            <td>${alumno.nombreCompleto}</td>

                            <td>${alumno.usuario}</td>

                            <td>${alumno.correo}</td>

                            <td><a href="${eliminaAlumno}"><liferay-ui:message key="salon.eliminaAlumno" /></a></td>
                            
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="nav">
        <portlet:renderURL var="editaSalon" >
            <portlet:param name="action" value="edita" />
            <portlet:param name="salonId" value="${salon.id}" />
        </portlet:renderURL>
        <portlet:actionURL var="eliminaSalon" >
            <portlet:param name="action" value="elimina" />
            <portlet:param name="salonId" value="${salon.id}" />
        </portlet:actionURL>
        <portlet:renderURL var="sesiones" >
            <portlet:param name="action" value="ver" />
            <portlet:param name="salonId" value="${salon.id}" />
        </portlet:renderURL>
        <span class="menuButton"><a class="edit" href="${editaSalon}"><liferay-ui:message key="salon.edita" /></a></span>
        <span class="menuButton"><a class="list" href="${sesiones}"><liferay-ui:message key="salon.sesiones" /></a></span>
        <span class="menuButton"><a class="delete" href="${eliminaSalon}"><liferay-ui:message key="salon.elimina" /></a></span>
        <span class="menuButton"><a class="back" href='<portlet:renderURL portletMode="view"/>'><liferay-ui:message key="salon.regresa" /></a></span>
    </div>
</div>
<link type="text/css" href="<%= request.getContextPath() %>/css/custom-theme/jquery-ui-1.8.10.custom.css" rel="Stylesheet" />
<!-- Grab Google CDN's jQuery. fall back to local if necessary -->
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.5.1/jquery.min.js"></script>
<script>!window.jQuery && document.write(unescape('%3Cscript src="<%= request.getContextPath() %>/js/jquery-1.5.1.min.js"%3E%3C/script%3E'))</script>
<script src="<%= request.getContextPath() %>/js/jquery-ui-1.8.10.custom.min.js"></script>
<script type="text/javascript">
    <portlet:actionURL var="eliminaAlumno2" >
        <portlet:param name="action" value="eliminaAlumno" />
        <portlet:param name="salonId" value="${salon.id}" />
    </portlet:actionURL>
    $(document).ready(function() {
        $("input#<portlet:namespace />alumnoNombre")
        .autocomplete({
            source: "<portlet:resourceURL id='buscaAlumno'/>",
            select: function(event, ui) {
                $("#<portlet:namespace />alumnosDiv").load('<portlet:resourceURL id="asignaAlumno" />',{salonId:${salon.id},alumnoId:ui.item.id,url:'${eliminaAlumno2}'},function() {
                    $("input#<portlet:namespace />alumnoNombre").val("");
                    $("input#<portlet:namespace />alumnoNombre").focus();
                });
                return false;
            }
        });
    });

</script>