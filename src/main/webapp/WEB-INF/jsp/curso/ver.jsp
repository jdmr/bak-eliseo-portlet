<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "curso.ver.titulo",request.getAttribute("curso"),false) %></h1>
    <portlet:renderURL var="editaCurso" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="cursoId" value="${curso.id}" />
    </portlet:renderURL>
    <portlet:renderURL var="editaContenido" >
        <portlet:param name="action" value="contenido" />
        <portlet:param name="cursoId" value="${curso.id}" />
    </portlet:renderURL>
    <portlet:actionURL var="eliminaCurso" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="cursoId" value="${curso.id}" />
    </portlet:actionURL>
    <portlet:renderURL var="nuevoExamen" >
        <portlet:param name="action" value="nuevoExamen" />
        <portlet:param name="cursoId" value="${curso.id}" />
    </portlet:renderURL>

    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="codigo"><liferay-ui:message key="curso.codigo" /></label>
                    </td>
                    <td valign="top" class="value">${curso.codigo}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="nombre"><liferay-ui:message key="curso.nombre" /></label>
                    </td>
                    <td valign="top" class="value">${curso.nombre}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="nombre"><liferay-ui:message key="curso.comunidad" /></label>
                    </td>
                    <td valign="top" class="value">${curso.comunidadNombre}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="nombre"><liferay-ui:message key="curso.contenido" /></label>
                    </td>
                    <td valign="top" class="value">
                        <c:if test="${contenidos != null}">
                            <div class="list">
                                <table id="<portlet:namespace />contenidos">
                                    <thead>
                                        <tr>

                                            <th><liferay-ui:message key="curso.nombre" /></th>

                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${contenidos}" var="contenido" varStatus="status">
                                            <portlet:renderURL var="verContenido" >
                                                <portlet:param name="action" value="verContenido" />
                                                <portlet:param name="cursoId" value="${curso.id}" />
                                                <portlet:param name="contenidoId" value="${contenido.primaryKey}" />
                                            </portlet:renderURL>
                                            <tr class="${(status.count % 2) == 0 ? 'odd' : 'even'}">
                                                <td><a href="${verContenido}">${contenido.title}</a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:if>
                    </td>
                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <span class="button"><a class="edit" href="${editaCurso}"><liferay-ui:message key="curso.edita" /></a></span>
        <span class="button"><a class="edit" href="${editaContenido}"><liferay-ui:message key="curso.contenido" /></a></span>
        <span class="button"><a class="delete" href="${eliminaCurso}"><liferay-ui:message key="curso.elimina" /></a></span>
        <span class="button"><a class="edit" href="${nuevoExamen}"><liferay-ui:message key="examen.nuevo" /></a></span>
        <span class="button"><a class="back" href="<portlet:renderURL portletMode="view"/>"><liferay-ui:message key="curso.regresa" /></a></span>
    </div>
</div>

