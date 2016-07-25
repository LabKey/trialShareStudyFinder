<%
/*
 * Copyright (c) 2015-2016 LabKey Corporation
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
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.template.ClientDependencies" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%@ taglib prefix="labkey" uri="http://www.labkey.org/taglib" %>
<%!
    @Override
    public void addClientDependencies(ClientDependencies dependencies)
    {
        dependencies.add("study/Finder/datafinder");
    }
%>
<%
    JspView<TrialShareController.FinderBean> me = (JspView) HttpView.currentView();
    TrialShareController.FinderBean bean = me.getModelBean();

    ObjectMapper jsonMapper = new ObjectMapper();
%>

<labkey:errors></labkey:errors>
<script type="text/javascript">
    var DataFinder = {};
    Ext4.onReady(function ()
    {
        DataFinder.finderView = Ext4.create('LABKEY.study.panel.Finder', {
            renderTo    : 'dataFinderWrapper',
            dataModuleName: '<%=h(bean.getDataModuleName())%>',
            cubeConfigs : <%=text(jsonMapper.writeValueAsString(bean.getCubeConfigs()))%>
        });
    });

</script>

<div id="dataFinderWrapper" class="labkey-data-finder-outer">
</div>
