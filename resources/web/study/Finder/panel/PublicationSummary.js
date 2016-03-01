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
            '                   <span id="memberCount" class="labkey-facet-member-count">{publicationCount:this.formatNumber}</span>',
            '               </li>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Studies</span>',
            '                   <span id="studyCount" class="labkey-facet-member-count">{studyCount:this.formatNumber}</span>',
            '               </li>',
            '           </ul>',
            '       </div>',
            '</div>',
            {
                formatNumber :  Ext4.util.Format.numberRenderer('0,000'),
            }
    ),

    data : {
        studyCount: 0,
        publicationCount: 0
    },

    initComponent: function() {
        Ext4.getStore(this.objectName).addListener('filterChange',this.onFilterSelectionChanged, this);
        Ext4.getStore(this.objectName).addListener('load', this.onFilterSelectionChanged, this);
        Ext4.getStore("PublicationFacetMembers").addListener('load', this.onFilterSelectionChanged, this);
        this.callParent();
    },

    onFilterSelectionChanged : function() {
        var store = Ext4.getStore("PublicationFacetMembers");
        var count = 0;
        for (var i = 0; i < store.count(); i++)
        {
            var member = store.getAt(i);
            if (member.data.facet.data.name == "Study" && member.data.count > 0)
                count++;
        }
        this.update( {
            studyCount : count,
            publicationCount :  Ext4.getStore("Publication").count()
        });
    }
});