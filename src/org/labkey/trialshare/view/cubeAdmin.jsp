<%
    /*
     * Copyright (c) 2016-2019 LabKey Corporation
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *     http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
%>
<%@ taglib prefix="labkey" uri="http://www.labkey.org/taglib" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.labkey.api.data.ContainerManager" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%!
    @Override
    public void addClientDependencies(ClientDependencies dependencies)
    {
        dependencies.add("Ext4");
    }
%>
<%
    TrialShareController.CubeDefinitionBean bean = ((JspView<TrialShareController.CubeDefinitionBean>) HttpView.currentView()).getModelBean();
    Map<String, List<String>> cubeDefinitions = bean.getCubeDefinitionMap();
%>
<labkey:errors/>
<p>
Data cube definitions and contents are cached on the server for efficiency.  When data are updated, you will need to
clear the cache in order for the new data to be presented to users.  Also, the search index for the data cube data is refreshed
    periodically, but you may want to manually reindex if you are making changes to the cube data.
</p>
<p>
Cube definitions are managed per container.  The following listing shows the definitions currently registered.
</p>
<labkey:form method="post" id="cubeAdminForm">
    <input type="hidden" id="containerPath" name="path"/>
    <input type="hidden" id="adminMethod" name="method"/>
    <table>
<%
    for (String path : cubeDefinitions.keySet())
    {
        List<String> definitionIds = cubeDefinitions.get(path);
        if (definitionIds.size() > 0)
        {

%>
        <tr>
            <th>Action</th>
            <th>Container</th>
            <th>Cube Definitions</th>
        </tr>
        <tr>
            <td><%= button("Clear Cache").onClick("confirmClear(" + qh(path) + ")") %>&nbsp;<%= button("Reindex").onClick("confirmReindex(" + qh(path) + ")")%></td>
            <td><%=link(path, ContainerManager.getForPath(path).getStartURL(getUser()))%></td>
            <td><%= h(StringUtils.join(definitionIds, ", ")) %></td>
        </tr>
<%
        }
    }
%>
    </table>

</labkey:form>
<script type="text/javascript">

    function confirmClear(path)
    {
        Ext4.Msg.show({
            title: 'Confirm',
            buttons: Ext4.MessageBox.OKCANCEL,
            msg: 'Clear the cached contents of the cube(s) in container ' + path + '?',
            fn: function (btn)
            {
                if (btn == 'ok')
                {
                    document.getElementById("containerPath").setAttribute("value", path);
                    document.getElementById("adminMethod").setAttribute("value", "clearCache");
                    document.getElementById("cubeAdminForm").submit();
                }
            }
        });
    }

    function confirmReindex(path)
    {
        Ext4.Msg.show({
            title: 'Confirm',
            buttons: Ext4.MessageBox.OKCANCEL,
            msg: 'Reindex the data in the cube ' + path + '?',
            fn: function (btn)
            {
                if (btn == 'ok')
                {
                    document.getElementById("containerPath").setAttribute("value", path);
                    document.getElementById("adminMethod").setAttribute("value", "reindex");
                    document.getElementById("cubeAdminForm").submit();
                }
            }
        });
    }

</script>
