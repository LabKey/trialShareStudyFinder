<%
    /*
     * Copyright (c) 2015 LabKey Corporation
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
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%!
    public LinkedHashSet<ClientDependency> getClientDependencies()
    {
        LinkedHashSet<ClientDependency> resources = new LinkedHashSet<>();
        resources.add(ClientDependency.fromPath("study/Finder/datafinder"));
        return resources;
    }
%>

<%
    JspView<TrialShareController.StudyCubeBean> me = (JspView) HttpView.currentView();
    TrialShareController.StudyCubeBean studyCube = me.getModelBean();
%>

<script type="text/javascript">
    var DataFinder = {};
    Ext4.onReady(function ()
    {
        DataFinder.finderView = Ext4.create('LABKEY.study.panel.Finder', {
            renderTo    : 'dataFinderWrapper',
            dataModuleName: '<%=h(studyCube.getDataModuleName())%>',
            olapConfig : {
                configId: '<%=h(studyCube.getConfigId())%>',
                schemaName: '<%=h(studyCube.getSchemaName())%>',
                name: '<%=h(studyCube.getCubeName())%>',
                filterByLevel : '<%=h(studyCube.getFilterByLevel())%>',
                countDistinctLevel : '<%=h(studyCube.getCountDistinctLevel())%>',
                filterByFacetUniqueName : '<%=h(studyCube.getFilterByFacetUniqueName())%>',
                objectName : '<%=h(studyCube.getObjectName())%>'
            },
            showSearch: false
        });
    });
</script>

<div id="dataFinderWrapper" class="labkey-data-finder-outer">
</div>
