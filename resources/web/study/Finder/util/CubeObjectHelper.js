Ext4.define('LABKEY.study.util.CubeObjectHelper', {
    singleton: true,

    getModelProxy: function(queryName, cubeContainerPath, schemaName)
    {
        var modelProxy = {
            type : 'ajax',
            url    : LABKEY.ActionURL.buildURL('query', 'selectRows.api', cubeContainerPath),
            extraParams : {
                schemaName  : schemaName ? schemaName : 'lists',
                queryName   : queryName
            },
            reader : {
                type : 'json',
                root : 'rows'
            }
        };
        return modelProxy;
    },

    studyShortNameNaturalSortFn: function(o1, o2)
    {
        if (o1 == null && o2 == null)
            return 0;
        else if (o1 == null)
            return -1;
        else if (o2 == null)
            return 1;
        return LABKEY.study.util.CubeObjectHelper.naturalSortFn(o1.get('shortName'), o2.get('shortName'));
    },

    naturalSortFn: function (aso, bso)
    {
        // http://stackoverflow.com/questions/19247495/alphanumeric-sorting-an-array-in-javascript
        var a, b, a1, b1, i = 0, n, L,
                rx = /(\.\d+)|(\d+(\.\d+)?)|([^\d.]+)|(\.\D+)|(\.$)/g;

        if (aso === bso) return 0;
        a = aso.toLowerCase().match(rx);
        b = bso.toLowerCase().match(rx);

        //var _empty = LABKEY.app.model.Filter.sorters.handleEmptySort(a, b);
        //if (_empty !== undefined) {
        //    return _empty;
        //}

        L = a.length;
        while (i < L)
        {
            if (!b[i]) return 1;
            a1 = a[i];
            b1 = b[i++];
            if (a1 !== b1)
            {
                n = a1 - b1;
                if (!isNaN(n)) return n;
                return a1 > b1 ? 1 : -1;
            }
        }
        return b[i] ? -1 : 0;
    }

});