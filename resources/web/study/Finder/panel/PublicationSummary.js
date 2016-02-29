/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.PublicationSummary", {
    extend: 'Ext.Component',

    alias : 'widget.facet-publication-summary',

    objectName: null,

    tpl: new Ext4.XTemplate(
            '<div id="publicationSelectionPanel">',
            '       <div id="summaryArea" class="labkey-facet-summary" >',
            '           <ul>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Publications</span>',
            '                   <span id="memberCount" class="labkey-facet-member-count">{publicationCount:this.getPublicationCount}</span>',
            '               </li>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Studies</span>',
            '                   <span id="studyCount" class="labkey-facet-member-count">{studyCount:this.getStudyCount}</span>',
            '               </li>',
            '           </ul>',
            '       </div>',
            '</div>',
            {
                formatNumber :  Ext4.util.Format.numberRenderer('0,000'),

                getPublicationCount : function(defaultValue)
                {
                    var store = Ext4.getStore("Publication");
                    if (!store)
                    {
                        console.log("Publication store not available.  Using " + defaultValue);
                        return this.formatNumber(defaultValue);
                    }
                    return this.formatNumber(store.count());
                },

                getStudyCount: function(defaultValue)
                {
                    var store = Ext4.getStore("PublicationFacetMembers");
                    if (!store)
                    {
                        console.log("PublicationFacetMembers store not available. Using " + defaultValue);
                        return this.formatNumber(defaultValue);
                    }
                    var count = 0;
                    for (var i = 0; i < store.count(); i++)
                    {
                        var member = store.getAt(i);
                        if (member.data.facet.data.name == "Study" && member.data.count > 0)
                            count++;
                    }
                    return this.formatNumber(count);
                }
            }
    ),

    data : {
        studyCount: 0,
        publicationCount: 0
    },

    initComponent: function() {
        this.callParent();
        Ext4.getStore(this.objectName).addListener('filterChange',this.onFilterSelectionChanged, this);
    },

    onFilterSelectionChanged : function() {
        this.update();
    }
});