Ext4.define('LABKEY.study.store.FacetMembers', {
    extend: 'Ext.data.Store',
    model: 'LABKEY.study.data.FacetMember',
    storeId: 'facetMembers',
    autoLoad: false,

    groupers: [
        {
            property: 'facetName',
            sorterFn: function(o1, o2){
                rank1 = o1.get('facet').get("ordinal");
                rank2 = o2.get('facet').get("ordinal");
                if (rank1 === rank2)
                    if (o1.get("name") === o2.get("name"))
                        return 0;
                    else
                        return o1.get("name") < o2.get("name") ? -1 : 1;
                else
                    return rank1 < rank2 ? -1 : 1;

            }
        }
    ],

    zeroCounts : function() {
        this.each(function(record) {
            record.set("count", 0);
            record.set("percent", 0);
        });
    }


});