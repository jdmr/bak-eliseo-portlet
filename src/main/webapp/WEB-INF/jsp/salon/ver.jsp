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

                        <th><liferay-ui:message key="acciones" /></th>

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

                            <td><a href="${eliminaSesion}"><liferay-ui:message key="sesion.elimina" /></a></td>
                            
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
        <portlet:renderURL var="nuevaSesion" >
            <portlet:param name="action" value="nuevaSesion" />
            <portlet:param name="salonId" value="${salon.id}" />
        </portlet:renderURL>
        <span class="menuButton"><a class="edit" href="${editaSalon}"><liferay-ui:message key="salon.edita" /></a></span>
        <span class="menuButton"><a class="create" href="${nuevaSesion}"><liferay-ui:message key="salon.nuevaSesion" /></a></span>
        <span class="menuButton"><a class="delete" href="${eliminaSalon}"><liferay-ui:message key="salon.elimina" /></a></span>
        <span class="menuButton"><a class="back" href='<portlet:renderURL portletMode="view"/>'><liferay-ui:message key="salon.regresa" /></a></span>
    </div>
</div>

