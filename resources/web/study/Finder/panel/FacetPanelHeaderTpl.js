Ext4.define("LABKEY.study.panel.FacetPanelHeaderTpl", {
    extend: "Ext.Component",

    padding: "0 0 5 0",
    data : {
        loadedStudiesShown: true,
        isGuest: false,
        hasFilters: false,
        showParticipantGroups:false,
        currentGroup: {
            id: 1,
            label: "My group"
        },
        saveOptions: [
            {
                id: "save",
                label : "Save",
                isActive : true
            },
            {
                id: "saveAs",
                label : "Save As",
                isActive : true
            }
        ],

        groups: [{
            label: "my first group"
        }]
    },
    tpl: new Ext4.XTemplate(
        '<div id="filterArea" class="facet-selection-header">&nbsp;',
        '<tpl if="showParticipantGroups">',
        '   <div class="labkey-group-label"><tpl if="currentGroup.id != null">Saved group: </tpl>{currentGroup.label}</div>',
        '</tpl>',
        '   <div class="navbar navbar-default ">',
        '   <tpl if="showParticipantGroups">',
        '   <ul class="nav navbar-nav">',
        '   <tpl if="!isGuest">',
        '       <li id="manageMenu" class="labkey-dropdown" ng-mouseover="openMenu($event, true)">',
        '       <a href="#"><i class="fa fa-cog"></i></a>',
        '       <ul class="labkey-dropdown-menu">',
        '           <li class="x4-menu-item-text"><a class="menu-item-link" href="<%=new ActionURL("study", "manageParticipantCategories", getContainer()).toHString()%>">Manage Groups</a></li>',
        '       </ul>',
        '       </li>',
        '   </tpl>',
        '       <li id="loadMenu" class="labkey-dropdown" >',
        '       <tpl if="loadedStudiesShown">',
        '           <a class="labkey-text-link no-arrow" style="margin-right: 0.8em" href="#" ng-mouseover="openMenu(event, false)">Load <i class="fa fa-caret-down"></i></a>',
        '           <tpl if="groups.length &gt; 0">',
        '           <ul class="labkey-dropdown-menu" >',
        '           <tpl for="groups">',
        '               <li class="x4-menu-item-text">',
        '                   <a class="menu-item-link" onclick="applySubjectGroupFilter()">{label}</a>',
        '               </li>',
        '           </tpl>',
        '           </ul>',
        '           </tpl>',
        '       <tpl else>',
        '           <a class="labkey-disabled-text-link no-arrow" style="margin-right: 0.8em" href="#" ng-mouseover="openMenu(event, false)">Load <i class="fa fa-caret-down"></i></a>',
        '       </tpl>',
        '       </li>',
        '       <li id="saveMenu" class="labkey-dropdown">',
        '       <tpl if="loadedStudiesShown">',
        '           <a class="labkey-text-link no-arrow" style="margin-right: 0.8em" href="#" ng-mouseover="openMenu(event, false)" ng-mouseleave="closeMenu(event)">Save <i class="fa fa-caret-down"></i> </a>',
        '           <tpl if="!isGuest">',
        '           <ul class="labkey-dropdown-menu">',
        '               <tpl for="saveOptions">',
        '                   <tpl if="isActive">',
        '               <li class="x4-menu-item-text">',
        '                   <a class="menu-item-link" onclick="saveSubjectGroup(id, $event)">{label}</a>',
        '               </li>',
        '                   <tpl else>',
        '               <li class="x4-menu-item-text inactive">',
        '                   <a class="menu-item-link inactive" onclick="saveSubjectGroup(id, $event)">{label}</a>',
        '               </li>',
        '                   </tpl>',
        '               </tpl>',
        '           </ul>',
        '           </tpl>',
        '       <tpl else>',
        '           <a class="labkey-disabled-text-link no-arrow" style="margin-right: 0.8em" href="#" ng-mouseover="openMenu($event, false)" ng-mouseleave="closeMenu($event)">Save <i class="fa fa-caret-down"></i> </a>',
        '       </tpl>',
        '       <tpl if="isGuest">',
        '           <ul class="labkey-dropdown-menu">',
        '               <li class="x4-menu-item-text">',
        '                   <span class="menu-item-link">You must be logged in to save a group.</span>',
        '               </li>',
        '           </ul>',
        '       </tpl>',
        '       </li>',
        '   </ul>',
        '   </tpl>',
        //'   <tpl if="hasFilters">',
        '   <span class="clear-filter inactive">[clear all]</span>',
        //'   <tpl else>',
        //'   <span class="clear-filter inactive">[clear all]</span>',
        //'   </tpl>',
        '   </div>',
        '</div>'
    ),


});