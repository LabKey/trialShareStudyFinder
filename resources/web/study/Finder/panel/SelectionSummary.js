Ext4.define("LABKEY.study.panel.SelectionSummary", {
    extend: 'Ext.Component',

    alias : 'widget.facet-selection-summary',

    renderTpl: new Ext4.XTemplate(
            '<div id="selectionPanel" style="background-color: white; border:none">',
            '       <div id="summaryArea" class="facet-summary" >',
            '           <div class="facet-header"><span class="facet-caption">Summary</span></div>',
            '           <ul>',
            '               <li class="member">',
            '                   <span class="member-name">Studies</span>',
            '                   <span id="memberCount" class="member-count">{studyCount:this.formatNumber}</span>',
            '               </li>',
            '               <li class="member">',
            '                   <span class="member-name">Subjects</span>',
            '                   <span id="participantCount" class="member-count">{participantCount:this.formatNumber}</span>',
            '               </li>',
            '           </ul>',
            '       </div>',
            '</div>',
            {
                formatNumber :  Ext4.util.Format.numberRenderer('0,000')
            }
    ),

    renderSelectors: {
        studyCountEl: 'span#memberCount',
        participantCountEl: 'span#participantCount'
    },

    renderData : {
        studyCount: 4,
        participantCount: 10000
    }
});