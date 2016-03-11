/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.StudySummary", {
    extend: 'Ext.Component',

    alias : 'widget.facet-selection-summary',

    tpl: new Ext4.XTemplate(
            '<div id="studySelectionPanel">',
            '       <div id="summaryArea" class="labkey-facet-summary" >',
            '           <ul>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Studies</span>',
            '                   <span id="memberCount" class="labkey-facet-member-count">{studyCount:this.formatNumber}</span>',
            '               </li>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Subjects</span>',
            '                   <span id="participantCount" class="labkey-facet-member-count">{participantCount:this.formatNumber}</span>',
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
        participantCount: 0
    },

    initComponent: function() {
        Ext4.getStore(this.objectName).addListener('filterChange',this.onFilterSelectionChanged, this);
        Ext4.getStore(this.objectName).addListener('load', this.onFilterSelectionChanged, this);
        this.callParent();
    },

    onFilterSelectionChanged : function() {
        this.update( {
            studyCount : Ext4.getStore("Study").count(),
            participantCount :  Ext4.getStore("Study").sum("participantCount")
        });
    }
});