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
<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.labkey.api.util.JavaScriptFragment" %>
<%@ page import="org.labkey.api.util.UniqueID" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page import="org.labkey.trialshare.data.StudyAccess" %>
<%@ page import="org.labkey.trialshare.data.StudyBean" %>
<%@ page import="java.util.List" %>
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
    JspView<TrialShareController.CubeObjectDetailForm> me = (JspView<TrialShareController.CubeObjectDetailForm>) HttpView.currentView();
    TrialShareController.CubeObjectDetailForm bean = me.getModelBean();

    String renderId = "study-details-" + UniqueID.getRequestScopedUID(HttpView.currentRequest());
    JavaScriptFragment cubeObjectJson = bean.getCubeObject() == null ? JavaScriptFragment.NULL : new JSONObject(bean.getCubeObject()).getJavaScriptFragment(2);
    List<StudyAccess> accessList = bean.getCubeObject() == null ? null :((StudyBean) bean.getCubeObject()).getStudyAccessList();

    JSONArray jsonArray = new JSONArray();
    if (accessList != null)
    {
        for (StudyAccess access : accessList)
        {
            jsonArray.put(new JSONObject(access));
        }
    }

    JavaScriptFragment studyaccesslist = jsonArray.getJavaScriptFragment(2);
%>
<labkey:errors/>
<div id="<%= h(renderId)%>" class="requests-editor"></div>

<script type="text/javascript">
    Ext4.onReady(function(){

        Ext4.create('LABKEY.study.panel.StudyDetailsFormPanel', {
            mode: "<%=h(bean.getMode())%>",
            objectName : 'Study',
            renderTo: <%=q(renderId)%>,
            accessListId : <%= bean.getAccessListId() %>,
            cubeObject : <%= cubeObjectJson %>,
            studyaccesslist: <%= studyaccesslist %>,
            cubeContainerPath: "<%=h(bean.getCubeContainerPath())%>"
        });
    });
</script>