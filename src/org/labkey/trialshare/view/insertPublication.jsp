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
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
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
        resources.add(ClientDependency.fromPath("study/Finder/panel/PublicationEdit.js"));

        return resources;
    }
%>
<%

//    JspView<QueryUpdateForm> me = (JspView<QueryUpdateForm>) HttpView.currentView();
//    TrialShareController.CubeObjectTypeForm bean = me.getModelBean();
//    TrialShareController.ObjectName thisObjectName = bean.getObjectName();
//    ModelAndView manageObjectsView = me.getView("manageObjectsView");

    String renderId = "insert-publication-" + UniqueID.getRequestScopedUID(HttpView.currentRequest());
%>
<labkey:errors/>
<div id="<%= h(renderId)%>" class="requests-editor"></div>

<script type="text/javascript">
    Ext4.onReady(function(){

        //create a formpanel using a store config object
        Ext4.create('LABKEY.study.panel.PublicationEdit', {
            store: {
                schemaName: 'lists',
                queryName: 'manuscriptsAndAbstracts',
                viewName: 'dataFinderDetails',
                autoLoad: true,
            },
//            store: Ext4.create('LABKEY.study.store.CubeObject', {
//
//                url : LABKEY.ActionURL.buildURL(this.dataModuleName, "publication.api", this.cubeContainerPath),
//                storeId: 'Publication',
//                model: 'LABKEY.study.data.Publication',
//                autoLoad: true,
//                facetSelectedMembers : {}, // initially we indicate that none of the members is selected by facets
//                searchSelectedMembers : null, // initially we have no search terms so everything is selected
//                selectedSubset : null
//
//            }),
            renderTo: <%=q(renderId)%>,
            bindConfig: {
                autoCreateRecordOnChange: true,
                autoBindFirstRecord: true
            },
            //this config will be applied to the Ext fields created in this FormPanel only.
            metadata: {
                Title: {
                    width: 1000
                },
                Author: {
                    width: 1000,
                    height: 50,
                    xtype: 'textarea'
                },
                PermissionsContainer : {
                    containerFilter: "AllFolders"
                }
            }
         });
    });
</script>