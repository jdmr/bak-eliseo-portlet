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

            </tbody>
        </table>
        <div class="list">
            <table id="<portlet:namespace />sesiones">
                <thead>
                    <tr>

                        <th><liferay-ui:message key="sesion.dia" /></th>

                        <th><liferay-ui:message key="sesion.horaInicial" /></th>

                        <th><liferay-ui:message key="sesion.horaFinal" /></th>

                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${sesiones}" var="sesion" varStatus="status">
                        <portlet:actionURL var="eliminaSesion" >
                            <portlet:param name="action" value="eliminaSesion" />
                            <portlet:param name="sesionId" value="${sesion.id}" />
                            <portlet:param name="salonId" value="${salon.id}" />
                        </portlet:actionURL>
                        <tr class="${(status.count % 2) == 0 ? 'odd' : 'even'}">

                            <td><liferay-ui:message key="dia${sesion.dia}" /></td>

                            <td>${sesion.horaInicial}</td>

                            <td>${sesion.horaFinal}</td>

                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="nav">
        <c:choose>
            <c:when test="${estaInscrito}">
                <c:if test="${salonUrl}">
                    <span class="menuButton"><a class="edit" href="${salonUrl}"><liferay-ui:message key="salon.entrar" /></a></span>
                </c:if>
                <portlet:renderURL var="contenidoUrl" >
                    <portlet:param name="action" value="contenido" />
                    <portlet:param name="salonId" value="${salon.id}" />
                </portlet:renderURL>
                <span class="menuButton"><a class="edit" href="${contenidoUrl}"><liferay-ui:message key="salon.contenido" /></a></span>
            </c:when>
            <c:otherwise>
                <portlet:renderURL var="inscribirseUrl" >
                    <portlet:param name="action" value="inscribirse" />
                    <portlet:param name="salonId" value="${salon.id}" />
                </portlet:renderURL>
                <span class="menuButton"><a class="edit" href="${inscribirseUrl}"><liferay-ui:message key="salon.registro" /></a></span>
            </c:otherwise>
        </c:choose>
        <span class="menuButton"><a class="back" href='<portlet:renderURL portletMode="view"/>'><liferay-ui:message key="salon.regresa" /></a></span>
    </div>
</div>

