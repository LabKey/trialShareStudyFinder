Ext4.define("LABKEY.study.view.GridTable", {

    extend: 'Ext.view.Table',

    /**
     * @Override
     * Issue 26148: Collapsing a section cause click in sections that follow to be off by 1.
     * Sencha Issue: https://www.sencha.com/forum/showthread.php?265078-Broken-contracts-of-getAt-indexOf-methods-of-the-Ext.grid.feature.Grouping
     * Version: 4.2.1
     */
    getRecord: function (node)
    {
        node = this.getNode(node);
        if (node)
        {
            return this.dataSource.data.get(node.getAttribute('data-recordId'));
        }
    },


    // in addition to updating getRecord, update indexInStore
    indexInStore: function (node) {
        node = this.getNode(node, true);
        if (!node && node !== 0) {
            return -1;
        }
        return this.dataSource.indexOf(this.getRecord(node));
    }
});