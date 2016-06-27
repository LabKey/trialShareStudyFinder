<%
    /*
     * Copyright (c) 2016 LabKey Corporation
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
<%@ page import="org.labkey.api.util.UniqueID" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ taglib prefix="labkey" uri="http://www.labkey.org/taglib" %>
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%!
    public LinkedHashSet<ClientDependency> getClientDependencies()
    {
        LinkedHashSet<ClientDependency> resources = new LinkedHashSet<>();
        resources.add(ClientDependency.fromPath("study/Finder/dataFinderEditor"));

        return resources;
    }
%>
<%
    TrialShareController.CubeObjectDetailBean bean = ((JspView<TrialShareController.CubeObjectDetailBean>) HttpView.currentView()).getModelBean();

    String renderId = "publication-details-" + UniqueID.getRequestScopedUID(HttpView.currentRequest());
%>
<labkey:errors/>
<div id="<%= h(renderId)%>" class="requests-editor"></div>

<script type="text/javascript">
    Ext4.onReady(function(){

        Ext4.create('LABKEY.study.panel.PublicationDetailsFormPanel', {
            mode: "<%=h(bean.getMode())%>",
            objectName : 'Publication',
            renderTo: <%=q(renderId)%>
        });
    });
</script>