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
        resources.add(ClientDependency.fromPath("study/Finder/datafinder"));
        resources.add(ClientDependency.fromPath("clientapi"));
        resources.add(ClientDependency.fromPath("Ext4ClientApi"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/JunctionEditFormPanel.js"));

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

        Ext4.create('LABKEY.study.panel.JunctionEditFormPanel', {
            mode: "<%=h(bean.getMode())%>",
            objectName : 'Publication',
            joinTableFields : ["StudyIds", "Conditions", "TherapeuticAreas"],
            store: {
                schemaName: 'lists',
                queryName: 'manuscriptsAndAbstracts',
                viewName: 'dataFinderDetails',
                autoLoad: true,
                <%
                if (bean.getId() != null)
                {
                %>
                filterArray: [LABKEY.Filter.create('<%=h(bean.getIdField())%>', <%=h(bean.getId())%>, LABKEY.Filter.Types.EQUAL)]
                <%
                }
                %>
            },
            renderTo: <%=q(renderId)%>,
            bindConfig: {
                autoCreateRecordOnChange: true,
                autoBindFirstRecord: <%= bean.getId() != null %>
            },
            metadata: {
                Title: {
                    width: 1000,
                    isRequired: true
                },
                Author: {
                    width: 1000,
                    height: 50,
                    xtype: 'textarea',
                    stripNewLines : true
                },
                Journal: {
                    width: 800
                },
                Status : {
                    isRequired: true
                },
                PublicationType : {
                    isRequired: true
                },
                ManuscriptContainer : {
                    containerFilter: "AllFolders",
                    width: 500
                },
                PermissionsContainer : {
                    containerFilter: "AllFolders",
                    width: 500
                },
                Citation : {
                    width: 1000,
                    height: 30,
                    xtype: 'textarea'
                },
                StudyIds : {
                    width: 800
                },
                Conditions : {
                    width: 800
                },
                TherapeuticAreas : {
                    width: 800
                },
                DOI : {
                    name: 'doi'
                },
                PMID : {
                    name: 'pmid',
                    xtype: 'textfield'
                },
                PMCID : {
                    name: 'pmcid'
                },
                AbstractText : {
                    width: 1000,
                    height: 100,
                    xtype: 'htmleditor'
                },
                Link1 : {
                    width: 1000
                },
                Description1 : {
                    width: 1000
                },
                Link2 : {
                    width: 1000
                },
                Description2 : {
                    width: 1000
                },
                Link3 : {
                    width: 1000
                },
                Description3 : {
                    width: 1000
                },
                Keywords: {
                    width: 800,
                    height: 50,
                    xtype: 'textarea',
                    stripNewLines : true
                }
            }
         });
    });
</script>