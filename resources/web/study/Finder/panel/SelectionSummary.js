Ext4.define("LABKEY.study.panel.SelectionSummary", {
    extend: 'Ext.Component',

    alias : 'widget.facet-selection-summary',

    tpl: new Ext4.XTemplate(
            '<div id="selectionPanel">',
            '       <div id="summaryArea" class="labkey-facet-summary" >',
            '           <div class="labkey-facet-header"><span class="labkey-facet-caption">Summary</span></div>',
            '           <ul>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Studies</span>',
            '                   <span id="memberCount" class="labkey-facet-member-count">{studyCount:this.getStudyCount}</span>',
            '               </li>',
            '               <li class="labkey-facet-member">',
            '                   <span class="labkey-facet-member-name">Subjects</span>',
            '                   <span id="participantCount" class="labkey-facet-member-count">{participantCount:this.getParticipantCount}</span>',
            '               </li>',
            '           </ul>',
            '       </div>',
            '</div>',
            {
                formatNumber :  Ext4.util.Format.numberRenderer('0,000'),

                getStudyCount : function(defaultValue) {
                    var studyStore = Ext4.getStore("studies");
                    if (!studyStore)
                        return this.formatNumber(defaultValue);
                    return this.formatNumber(studyStore.count());
                },

                getParticipantCount: function(defaultValue) {
                    var studyStore = Ext4.getStore("studies");
                    if (!studyStore)
                        return this.formatNumber(defaultValue);
                    return this.formatNumber(studyStore.sum("participantCount"));
                }
            }
    ),

    data : {
        studyCount: 0,
        participantCount: 0
    },

    initComponent: function() {
        Ext4.getStore('studies').addListener('filterChange',this.onFilterSelectionChanged, this);
    },

    onFilterSelectionChanged : function() {
        this.update();
    }
});