Ext4.define("LABKEY.study.panel.SelectionSummary", {
    extend: 'Ext.Component',

    alias : 'widget.facet-selection-summary',

    tpl: new Ext4.XTemplate(
            '<div id="selectionPanel" style="background-color: white; border:none">',
            '       <div id="summaryArea" class="facet-summary" >',
            '           <div class="facet-header"><span class="facet-caption">Summary</span></div>',
            '           <ul>',
            '               <li class="member">',
            '                   <span class="member-name">Studies</span>',
            '                   <span id="memberCount" class="member-count">{studyCount:this.getStudyCount}</span>',
            '               </li>',
            '               <li class="member">',
            '                   <span class="member-name">Subjects</span>',
            '                   <span id="participantCount" class="member-count">{participantCount:this.getParticipantCount}</span>',
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
        console.log("in SelectionSummary with filterChange");
        this.update();
    }
});