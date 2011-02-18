<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "curso.edita.titulo",request.getAttribute("curso"),false) %></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="actualiza"/>
    </portlet:actionURL>

    <form:form name="cursoForm" commandName="curso" method="post" action="${actionUrl}" >
        <form:hidden path="id" />
        <form:hidden path="version" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="codigo"><liferay-ui:message key="curso.codigo" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="codigo" maxlength="32"/><br/>
                            <form:errors cssClass="errors" path="codigo" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nombre"><liferay-ui:message key="curso.nombre" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="nombre" maxlength="128"/><br/>
                            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nombre"><liferay-ui:message key="curso.comunidad" /></label>
                        </td>
                        <td valign="top" class="value">${curso.comunidadNombre}</td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='curso.actualiza' />"/></span>
            <span class="button"><a class="cancel" href="<portlet:renderURL portletMode="view"/>"><liferay-ui:message key="curso.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.cursoForm.codigo.focus();
    </script>
</div>

