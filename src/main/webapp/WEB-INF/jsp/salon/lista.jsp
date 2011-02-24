<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="Curso">
    <h1><liferay-ui:message key="javax.portlet.title" /> (<%= LanguageUtil.format(pageContext, "salon.cantidad",request.getAttribute("cantidad"),false) %>)</h1>
        <portlet:renderURL var="actionUrl" >
        <portlet:param name="action" value="busca" />
    </portlet:renderURL>

    <form name="<portlet:namespace />fm" method="post" action="${actionUrl}" >
        <div class="search">
            <table>
                <tbody>
                    <tr class="prop">
                        <td>
                            <input type="text" name="<portlet:namespace />filtro" id="<portlet:namespace />filtro" value="" />
                            <input type="submit" name="<portlet:namespace />_busca" value='<liferay-ui:message key="salon.buscar" />'/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </form>
    <c:if test="${salones != null}">
        <div class="list">
            <table id="<portlet:namespace />salones">
                <thead>
                    <tr>

                        <th><liferay-ui:message key="salon.nombre" /></th>

                        <th><liferay-ui:message key="salon.curso" /></th>

                        <th><liferay-ui:message key="salon.maestro" /></th>

                        <th><liferay-ui:message key="salon.inicia" /></th>

                        <th><liferay-ui:message key="salon.termina" /></th>

                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${salones}" var="salon" varStatus="status">
                        <portlet:renderURL var="verSalon" >
                            <portlet:param name="action" value="ver" />
                            <portlet:param name="salonId" value="${salon.id}" />
                        </portlet:renderURL>
                        <tr class="${(status.count % 2) == 0 ? 'odd' : 'even'}">

                            <td><a href="${verSalon}">${salon.nombre}</a></td>

                            <td>${salon.curso.nombre}</td>

                            <td>${salon.maestroNombre}</td>

                            <td>${salon.inicia}</td>

                            <td>${salon.termina}</td>

                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <portlet:renderURL var="anterior" >
            <portlet:param name="max" value="${max}" />
            <portlet:param name="offset" value="${offset}" />
            <portlet:param name="direccion" value="anterior" />
        </portlet:renderURL>

        <portlet:renderURL var="siguiente" >
            <portlet:param name="max" value="${max}" />
            <portlet:param name="offset" value="${offset}" />
            <portlet:param name="direccion" value="siguiente" />
        </portlet:renderURL>

        <div class="paginateButtons">
            <c:if test="${offset > 0}">
                <a href="${anterior}" class="prevLink"><liferay-ui:message key="salon.anterior" /></a>
            </c:if>
            <c:if test="${cantidad > 5}">
                <a href="${siguiente}" class="nextLink"><liferay-ui:message key="salon.siguiente" /></a>
            </c:if>
        </div>

    </c:if>
    <div class="nav">
        <span class="menuButton"><a class="create" href='<portlet:renderURL><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><liferay-ui:message key="salon.nuevo" /></a></span>
    </div>
    <script type="text/javascript">
        <c:if test="${salones != null}">
            highlightTableRows("<portlet:namespace />salones")
        </c:if>
    </script>

</div>
