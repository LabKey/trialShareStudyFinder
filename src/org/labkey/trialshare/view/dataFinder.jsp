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
        resources.add(ClientDependency.fromPath("study/Finder/data/Facet.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/FacetFilter.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/FacetMember.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/StudyCard.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/StudySubset.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/Facets.js"));
        resources.add(ClientDependency.fromPath("study/Finder/data/FacetMembers.js"));

//        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetPanelHeaderTpl.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetPanelHeader.js"));
//        resources.add(ClientDependency.fromPath("study/Finder/panel/Facets.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetsGrid.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/SelectionSummary.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/FacetSelection.js"));

        resources.add(ClientDependency.fromPath("study/Finder/panel/StudyCards.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/StudyPanelHeader.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/Studies.js"));
        resources.add(ClientDependency.fromPath("study/Finder/panel/Finder.js"));
        return resources;
    }
%>


<script type="text/javascript">
    var DataFinder = {};
    Ext4.onReady(function ()
    {
        DataFinder.finderView = Ext4.create('LABKEY.study.panel.Finder', {
            renderTo    : 'dataFinderWrapper',
            dataModuleName: 'trialshare'
        });
    });
</script>

<div id="dataFinderWrapper" class="labkey-data-finder-outer">
</div>


<script type="text/javascript">

var $=$||jQuery;

LABKEY.help.Tour.register({
    id: "LABKEY.tour.dataFinder",
    steps: [
        {
            target: $('.labkey-wp')[0],
            title: "Data Finder",
            content: "Welcome to the Data Finder. A tool for searching, accessing and combining data across studies.",
            placement: "top",
            showNextButton: true
        },{
            target: "studypanel",
            title: "Study Panel",
            content: "This area contains short descriptions of the studies/datasets that match the selected criteria.",
            placement: "top",
            showNextButton: true,
            showPrevButton: true
        },{
            target: "summaryArea",
            title: "Summary",
            content: "This summary area indicates how many subjects and studies match the selected criteria.", 
            placement: "right",
            showNextButton: true,
            showPrevButton: true
        },{
            target: "facetPanel",
            title: "Filters",
            content: "This is where filters are selected and applied. The numbers (also represented as the length of the gray bars) represent how many subjects will match the search if this filter is added.",
            placement: "right",
            showNextButton: true,
            showPrevButton: true
        },{
            target: "searchTerms",
            title: "Quick Search",
            content: "Enter terms of interest to search study and data descriptions. This will find matches within the selection of filtered studies/datasets.",
            placement: "right",
            yOffset: -25,
            showPrevButton: true
        }
        //{
        //    target: 'group_Condition',
        //    title: "Study Attributes",
        //    content: "Select items in this area to find studies of interest.  The gray bars show the number of selected participants.<p/>Try " + (Ext4.isMac ? "Command" : "Ctrl") + "-click to multi-select.",
        //    placement: "right"
        //},
        //{
        //    target: 'searchTerms',
        //    title: "Quick Search",
        //    content: "Enter terms of interest to search study descriptions.",
        //    placement: "right"
        //},
        //{
        //    target: 'summaryArea',
        //    title: "Summary",
        //    content: "Here is a summary of the data in the selected studies. Studies represents the number of studies that contain some participants that match the criteria. Subjects is the number of subjects across all selected studies (including subjects that did not match all attributes).",
        //    placement: "right"
        //},
        //{
        //    target: 'filterArea',
        //    title: "Filter Area",
        //    content: "See and manage your active filters.",
        //    placement: "right"
        //}
    ]
});

</script>

