Ext4.define("LABKEY.study.panel.StudyPanelHeader", {

    extend: 'Ext.view.View',

    studySubsets: [],
    selectedSubset: null,
    searchMessage: "",
    searchTerms : "",

    tpl: new Ext4.XTemplate(
        '<div class="labkey-study-finder-header">',
        '   <span class="search-box">',
        '       <i class="fa fa-search"></i>&nbsp;',
        '       <input placeholder="Studies" id="searchTerms" name="q" class="search-box" value="{searchTerms}" type="search">',
        '   </span>',
        '   <tpl if="studySubsets.length &gt; 1">',
        '   <span class="labkey-study-search">',
        '       <select name="studySubsetSelect">',
        '       <tpl for="{studySubsets}">',
        '           <tpl if="value==selectedSubset">',
        '           <option value="{value}" selected=true>{name}</option>',
        '           <tpl else>',
        '           <option value="{value}">{name}</option>',
        '           </tpl>',
        '       </tpl>',
        '       </select>',
        '   </span>',
        '   </tpl>',
        '   <span class="study-search">{searchMessage}</span>',
        '</div>'
        //'<div class="help-links">',
        //'   <%=textLink("quick help", "#", "start_tutorial()", "showTutorial")%>',
        //'   <%=textLink("Export Study Datasets", ImmPortController.ExportStudyDatasetsAction.class)%><br>',
        //'</div>'
    ),

    listeners : {
        change : function(field, newValue, oldValue) {
            if (field = "searchTerms")
                onSearchTermsChanged();
            else if (field == "studySubsetSelect")
                onStudySubsetChanged();
        }
    }
});