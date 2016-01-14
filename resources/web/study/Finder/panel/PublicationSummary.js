Ext4.define("LABKEY.study.panel.PublicationSummary", {
    extend: 'Ext.Component',

    alias : 'widget.facet-selection-summary',

    tpl: new Ext4.XTemplate(
            '<div id="selectionPanel">',
            '       <div id="summaryArea" class="labkey-facet-summary" >',
            '           <div class="labkey-facet-header"><span class="labkey-facet-caption">Summary</span></div>',
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

                getPublicationCount : function(defaultValue) {
                    var store = Ext4.getStore("Publication");
                    if (!store)
                        return this.formatNumber(defaultValue);
                    return this.formatNumber(store.count());
                },

                getStudyCount: function(defaultValue) {
                    //var store = Ext4.getStore("Publication");
                    var store = Ext4.getStore("facetMembers");
                    if (!store)
                        return this.formatNumber(defaultValue);
                    return this.formatNumber(store.sum("count", true).Study);
                }
            }
    ),

    data : {
        studyCount: 0,
        publicationCount: 0
    },

    initComponent: function() {
        Ext4.getStore(this.objectName).addListener('filterChange',this.onFilterSelectionChanged, this);
        this.callParent();
    },

    onFilterSelectionChanged : function() {
        this.update();
    }
});