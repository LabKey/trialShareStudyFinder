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
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%!
    public LinkedHashSet<ClientDependency> getClientDependencies()
    {
        LinkedHashSet<ClientDependency> resources = new LinkedHashSet<>();
        resources.add(ClientDependency.fromPath("internal/jQuery")); // this is for the Help tour defined below
        resources.add(ClientDependency.fromPath("Ext4"));
        resources.add(ClientDependency.fromPath("clientapi/ext4"));
        resources.add(ClientDependency.fromPath("query/olap.js"));
        resources.add(ClientDependency.fromPath("study/Finder/dataFinder.css"));
        resources.add(ClientDependency.fromPath("study/Finder/trialShare.css"));
        resources.add(ClientDependency.fromPath("study/Finder/data/Facet.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/FacetFilter.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/FacetMember.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/StudyCard.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/Studies.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/StudySubset.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/StudySubsets.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/Facets.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/FacetMembers.js"));

        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetPanelHeader.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetsGrid.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/SelectionSummary.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetSelection.js"));

        resources.add(ClientDependency.fromPath("study/Finder/panel/Studies.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/StudyPanelHeader.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/StudyCards.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/Finder.js"));
        resources.add(ClientDependency.fromPath("study/Finder/view/DataFinderTour.js"));
        return resources;
    }
%>


<script type="text/javascript">
    var DataFinder = {};
    Ext4.onReady(function ()
    {
        DataFinder.finderView = Ext4.create('LABKEY.study.panel.Finder', {
            renderTo    : 'dataFinderWrapper',
            dataModuleName: 'trialshare',
            olapConfig : {
                configId: 'TrialShare:/StudyCube',
                schemaName: 'lists',
                name: 'StudyCube',
            }
        });
    });
</script>

<div id="dataFinderWrapper" class="labkey-data-finder-outer">
</div>
