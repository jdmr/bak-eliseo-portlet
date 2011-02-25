<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><liferay-ui:message key="salon.nuevo" /></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="crea"/>
    </portlet:actionURL>

    <form:form name="salonForm" commandName="salon" method="post" action="${actionUrl}" >
        <form:hidden id="<portlet:namespace />cursoId" path="curso.id" />
        <form:hidden id="<portlet:namespace />maestroId" path="maestroId" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nombre"><liferay-ui:message key="salon.nombre" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="nombre" maxlength="128"/>
                            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cursoNombre"><liferay-ui:message key="salon.curso" /></label>
                        </td>
                        <td valign="top" class="value">
                            <div id="<portlet:namespace />cursoNombre"></div>
                            <form:errors cssClass="errors" path="curso" cssStyle="color:red;" />
                            <aui:script use="aui-autocomplete">

                                var dataSource = new A.DataSource.IO(
                                    {
                                        source: '<portlet:resourceURL id="buscaCurso"/>'
                                    }
                                );

                                var autocomplete = new A.AutoComplete(
                                    {
                                        dataSource: dataSource,
                                        delimChar: ',',
                                        contentBox: '#<portlet:namespace />cursoNombre',
                                        matchKey: 'name',
                                        schema: {
                                            resultListLocator: 'response',
                                            resultFields: ['key', 'name', 'description']
                                        },
                                        schemaType:'json',
                                        typeAhead: true,
                                        input: '#<portlet:namespace />cursoId'
                                    }
                                );

                                autocomplete.generateRequest = function(query) {
                                    return {
                                        request: '&cursoNombre=' + query
                                    };
                                }

                                autocomplete.render();

                            </aui:script>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maestroNombre"><liferay-ui:message key="salon.maestro" /></label>
                        </td>
                        <td valign="top" class="value">
                            <input type="text" name="maestroNombre" id="maestroNombre" value="${salon.maestroNombre}"
                            <form:errors cssClass="errors" path="maestroId" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="inicia"><liferay-ui:message key="salon.inicia" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="inicia" maxlength="128"/>
                            <form:errors cssClass="errors" path="inicia" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="termina"><liferay-ui:message key="salon.termina" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="termina" maxlength="128"/>
                            <form:errors cssClass="errors" path="termina" cssStyle="color:red;" />
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='salon.crea' />"/></span>
            <span class="button"><a class="cancel" href="<portlet:renderURL portletMode="view"/>"><liferay-ui:message key="salon.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.salonForm.nombre.focus();
    </script>
</div>

