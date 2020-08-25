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
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.labkey.api.util.HtmlString" %>
<%@ page import="org.labkey.api.util.UniqueID" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ taglib prefix="labkey" uri="http://www.labkey.org/taglib" %>
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%!
    @Override
    public void addClientDependencies(ClientDependencies dependencies)
    {
        dependencies.add("study/Finder/dataFinderEditor");
    }
%>
<%
    TrialShareController.CubeObjectDetailForm bean = ((JspView<TrialShareController.CubeObjectDetailForm>) HttpView.currentView()).getModelBean();

    String renderId = "publication-details-" + UniqueID.getRequestScopedUID(HttpView.currentRequest());

    HtmlString cubeObjectJson = bean.getCubeObject() == null ? HtmlString.of("null") : new JSONObject(bean.getCubeObject()).getHtmlString(2);
%>

<labkey:errors/>
<div id="<%= h(renderId)%>" class="requests-editor"></div>

<script type="text/javascript">
    Ext4.onReady(function(){

        Ext4.create('LABKEY.study.panel.PublicationDetailsFormPanel', {
            mode: "<%=h(bean.getMode())%>",
            objectName : 'Publication',
            renderTo: <%=q(renderId)%>,
            cubeObject : <%=cubeObjectJson%>,
            cubeContainerPath: "<%=h(bean.getCubeContainerPath())%>"
        });
    });
</script>