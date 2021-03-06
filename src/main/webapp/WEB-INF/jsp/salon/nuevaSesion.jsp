<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><liferay-ui:message key="salon.nuevaSesion" /></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="creaSesion"/>
    </portlet:actionURL>
    <portlet:renderURL var="verSalon" >
        <portlet:param name="action" value="ver" />
        <portlet:param name="salonId" value="${sesion.salon.id}" />
    </portlet:renderURL>
    
    <form:form name="sesionForm" commandName="sesion" method="post" action="${actionUrl}" >
        <form:hidden path="salon.id" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />dia"><liferay-ui:message key="sesion.dia" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:select path="dia" items="${dias}"/>
                            <form:errors cssClass="errors" path="dia" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />horaInicial"><liferay-ui:message key="sesion.horaInicial" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="horaInicial" />
                            <form:errors cssClass="errors" path="horaInicial" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="<portlet:namespace />horaFinal"><liferay-ui:message key="sesion.horaFinal" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="horaFinal" />
                            <form:errors cssClass="errors" path="horaFinal" cssStyle="color:red;" />
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="nav">
            <span class="menuButton"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='sesion.crea' />"/></span>
            <span class="menuButton"><a class="cancel" href='${verSalon}'><liferay-ui:message key="sesion.cancela" /></a></span>
        </div>
    </form:form>
</div>
