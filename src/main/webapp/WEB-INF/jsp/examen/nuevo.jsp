<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="Curso">
    <h1><liferay-ui:message key="examen.nuevo.titulo" /></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="creaExamen"/>
    </portlet:actionURL>

    <form:form name="examenForm" commandName="examen" method="post" action="${actionUrl}" >
        <form:hidden path="curso.id" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="codigo"><liferay-ui:message key="examen.codigo" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="codigo" maxlength="32"/>
                            <form:errors cssClass="errors" path="codigo" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nombre"><liferay-ui:message key="examen.nombre" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="nombre" maxlength="128"/>
                            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='examen.crea' />"/></span>
            <span class="button"><a class="cancel" href='<portlet:renderURL portletMode="view"/>'><liferay-ui:message key="examen.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.examenForm.codigo.focus();
    </script>
</div>

