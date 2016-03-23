/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.store.Facets', {
    extend: 'Ext.data.Store',
    model: 'LABKEY.study.data.Facet',
    autoLoad: true,

    mdx: null,
    isLoaded: false,

    proxy : {
        type: "ajax",
        //url:  // set in constructor
        reader: {
            type: 'json',
            root: 'data'
        }
    },
    listeners: {
        'load' : {
            fn : function(store, records, options) {
                store.isLoaded = true;
                store.loadFromCube();
            },
            scope: this
        }
    },

    clearAllSelectedMembers: function() {
        for (var f = 0; f < this.count(); f++) {
            var facet = this.getAt(f);
            if (facet.get("name") != this.cubeConfig.objectName)
            {
                this.getAt(f).data.selectedMembers = [];
            }
        }
    },

    selectMembers : function(members) {
        for (var i = 0; i < members.length; i++)
        {
            this.getById(members[i].data.facetName).data.selectedMembers.push(members[i]);
        }
    },

    loadFromCube : function() {
        if (this.isLoaded && this.mdx)
        {
            var cube = this.mdx._cube;
            var facetMembersStore = Ext4.getStore(this.cubeConfig.objectName + "FacetMembers");
            for (var f = 0; f < this.count(); f++)
            {
                var facet = this.getAt(f);
                facet.data.hierarchy = cube.hierarchyMap[facet.get("hierarchyName")];
                facet.data.level = facet.data.hierarchy.levelMap[facet.get("levelName")];
                facet.data.members = [];
                var defaultFilter = this.getDefaultFilterOption(facet.filterOptionsStore);
                facet.data.currentFilterType = defaultFilter.get("type");
                facet.data.currentFilterCaption = defaultFilter.get("caption");
                for (var m = 0; m < facet.data.level.members.length; m++)
                {
                    var src = facet.data.level.members[m];
                    if (src.name == "#notnull")
                        continue;
                    var member = {
                        name: src.name,
                        uniqueName: src.uniqueName,
                        level: src.level.uniqueName,
                        count: 0,
                        percent: 0,
                        facetName: facet.get("name"),
                        facet : facet
                    };
                    if (facet.get("name") != this.cubeConfig.objectName)
                        facetMembersStore.add(member);
                    facet.data.members.push(member);
                }
                if (facet.get("name") == this.cubeConfig.objectName) {
                    facet.data.selectedMembers = facet.data.members;
                }
            }
            facetMembersStore.filter(
                [
                    {filterFn: function(item) {
                        return item.get("facet").get("displayFacet");
                    }}
                ]
            );
            facetMembersStore.sort();
            this.updateCountsAsync(false);
        }
    },

    getDefaultFilterOption : function(optionsStore) {
        for (var i = 0; i < optionsStore.count(); i++) {
            if (optionsStore.getAt(i).data.isDefault)
                return optionsStore.getAt(i);
        }
        return optionsStore.getAt(0);
    },

    getStudySubsetFilter: function() {
        var store = Ext4.getStore(this.cubeConfig.objectName);

        if (!store || !store.selectedSubset)
            return null;
        else
            return {level: this.cubeConfig.subsetLevelName, members: [ store.selectedSubset ]};

    },

    getCountDistinctFilters: function(filters) {
        // TODO I think we need to check for an existing filter for the filterByLevel
        var newFilters = this.getStudySubsetFilter();
        if (newFilters != null)
            filters.push(newFilters);
        newFilters = this.getSearchFilter();
        if (newFilters != null)
            filters.push(newFilters);
        newFilters = this.getCustomFilters();
        if (newFilters != null)
            filters.push(newFilters);
        return filters;
    },

    getSearchFilter : function()
    {
        var store = Ext4.getStore(this.cubeConfig.objectName);
        if (store.searchSelectedMembers != null)
        {
            var selection = [];
            for (var key in store.searchSelectedMembers)
            {
                selection.push(store.searchSelectedMembers[key])
            }
            return {level: this.cubeConfig.filterByLevel, members: selection};
        }
    },

    getCustomFilters: function()
    {
        return null;
    },

    getValidFacetMemberSubset : function(possibleMembers)
    {
        var validMembers = [];
        var facetMemberStore = Ext4.getStore(this.cubeConfig.objectName + "FacetMembers");
        for (var i = 0; i < possibleMembers.length; i++)
        {
            if (facetMemberStore.getById(possibleMembers[i]))
                validMembers.push(possibleMembers[i]);
        }
        return validMembers;
    },

    updateCountsAsync: function (isSavedGroup)
    {
        if (!this.isLoaded || !this.mdx)
        {
            console.log("Store not ready for count update.  Please try again later.");
            return;
        }

        var url = LABKEY.ActionURL.buildURL(this.dataModuleName, "accessibleMembers.api", null, {
            "objectName": this.cubeConfig.objectName
        });
        Ext4.Ajax.request({
            url: url,
            success: function (response)
            {
                var o = Ext4.decode(response.responseText);
                if (o.success)
                {
                    var filters = [];
                    for (var level in o.data)
                    {
                        if (o.data[level].length)
                        {
                            filters.push({
                                level: level,
                                members: o.data[level]
                            })
                        }
                    }
                    this.makeCountDistinctQuery(filters);
                }

            },
            scope: this
        });
    },

    makeCountDistinctQuery: function(intersectFilters)
    {
        this.getCountDistinctFilters(intersectFilters);
        var i, f, facet;
        for (f = 0; f < this.count(); f++)
        {
            facet = this.getAt(f);
            var selectedMembers = facet.get("selectedMembers");
            if (facet.get("name") != this.cubeConfig.objectName)
            {
                if (!selectedMembers || selectedMembers.length == 0)
                    continue;
                if (facet.get("currentFilterType") === "OR")
                {
                    var names = [];
                    selectedMembers.forEach(function (m)
                    {
                        names.push(m.data.uniqueName)
                    });
                    intersectFilters.push({
                        level: this.cubeConfig.filterByLevel,
                        membersQuery: {level: selectedMembers[0].data.level, members: names}
                    });
                }
                else
                {
                    for (i = 0; i < selectedMembers.length; i++)
                    {
                        var filterMember = selectedMembers[i];
                        intersectFilters.push({
                            level: this.cubeConfig.filterByLevel,
                            membersQuery: {level: filterMember.data.level, members: [filterMember.data.uniqueName]}
                        });
                    }
                }
            }
        }

        var filters = intersectFilters;

        var onRows = { operator: "UNION", arguments: [] };
        onRows.arguments.push({level: this.cubeConfig.filterByLevel});
        for (f = 0; f < this.count(); f++)
        {
            facet = this.getAt(f);
            if (facet.get("name") == "Subject")
                onRows.arguments.push({level: facet.data.hierarchy.levels[0].uniqueName});
            else if (facet.get("name") == this.cubeConfig.objectName )
                continue;
            else
                onRows.arguments.push({level: facet.data.level.uniqueName});
        }


        var config =
        {
            "sql": true,
            configId: this.cubeConfig.configId,
            schemaName: this.cubeConfig.schemaName,
            container: this.cubeConfig.cubeContainerId,
            containerPath : this.cubeConfig.cubeContainerPath,
            name: this.cubeConfig.name,
            success: function (cellSet, mdx, config)
            {
                this.updateCountsUnion(cellSet);
                //this.fireEvent("cubeReady");
            },
            scope: this,

            // query
            onRows: onRows,
            countFilter: filters,
            includeNullMemberInCount: false,
            countDistinctLevel: this.cubeConfig.countDistinctLevel
        };
        this.mdx.query(config);
    },

    updateCountsZero : function ()
    {
        var facetMembersStore = Ext4.getStore(this.cubeConfig.objectName + "FacetMembers");
        facetMembersStore.zeroCounts();
        //this.saveFilterState();
        //this.updateContainerFilter();
        //this.changeSubjectGroup();
        //this.doneRendering();
    },

    /* handle query response to update all the member counts with all filters applied */
    updateCountsUnion : function (cellSet)
    {
        var facet, member, f, m;
        // map from hierarchyName to facet
        var map = {};
        var facetStore = this;
        var facetMembersStore = Ext4.getStore(this.cubeConfig.objectName + "FacetMembers");
        facetMembersStore.suspendEvents(false);

        for (f = 0; f < facetStore.count(); f++)
        {
            facet = this.getAt(f);
            map[facet.data.hierarchy.uniqueName] = facet;
            if (facet.get("name") == this.cubeConfig.objectName) {
                facet.data.selectedMembers = [];
            }
        }

        this.updateCountsZero();
        var positions = this.getRowPositionsOneLevel(cellSet);
        var data = this.getDataOneColumn(cellSet, 0);
        var max = 0;
        var selectedMembers = {};
        for (var i = 0; i < positions.length; i++)
        {
            var resultMember = positions[i];
            var hierarchyName = resultMember.level.hierarchy.uniqueName;

            facet = map[hierarchyName];
            var count = data[i];
            member = facetMembersStore.getById(resultMember.uniqueName);
            if (facet.get("name") == this.cubeConfig.objectName)
            {
                selectedMembers[resultMember.name] = resultMember;
                facet.data.selectedMembers.push(resultMember);
            }
            else if (!member)
            {
                // might be an all member
                //if (facet.data.allMemberName == resultMember.uniqueName)
                //    facet.data.allMemberCount = count;
                //else
                if (-1 == resultMember.uniqueName.indexOf("#") && "(All)" != resultMember.name)
                    console.log("member not found: " + resultMember.uniqueName);
            }
            else if (facet.get("displayFacet"))
            {
                member.set("count", count);
                if (count > max)
                    max = count;
            }

        }

        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            if (facet.data.hierarchy.uniqueName !== this.cubeConfig.filterByFacetUniqueName)
            {
                for (m = 0; m < facet.data.members.length; m++)
                {
                    member = facetMembersStore.getById(facet.data.members[m].uniqueName);
                    member.set("percent", max == 0 ? 0 : (100.0 * member.data.count) / max);
                }
            }
        }

        facetMembersStore.resumeEvents();
        facetMembersStore.fireEvent("refresh");

        this.updateMemberFilter(selectedMembers);

        //this.saveFilterState();
        //this.updateContainerFilter();
        //if (!isSavedGroup)
        //    this.changeSubjectGroup();


        LABKEY.Utils.signalWebDriverTest('dataFinderCountsUpdated');
    },

    updateMemberFilter : function(selectedMembers) {
        var store = Ext4.getStore(this.cubeConfig.objectName);
        store.updateFacetFilters(selectedMembers);
    },

    getRowPositions : function(cellSet)
    {
        return cellSet.axes[1].positions;
    },

    getRowPositionsOneLevel : function(cellSet)
    {
        var positions = cellSet.axes[1].positions;
        if (positions.length > 0 && positions[0].length > 1)
        {
            console.log("warning: rows have nested members");
            throw "illegal state";
        }
        return positions.map(function(inner){return inner[0]});
    },

    getData : function(cellSet,defaultValue)
    {
        var cells = cellSet.cells;
        return cells.map(function(row)
        {
            return row.map(function(col){return col.value ? col.value : defaultValue;});
        });
    },

    getDataOneColumn : function(cellSet,defaultValue)
    {
        var cells = cellSet.cells;
        if (cells.length > 0 && cells[0].length > 1)
        {
            console.log("warning cellSet has more than one column");
            throw "illegal state";
        }
        return cells.map(function(row)
        {
            return row[0].value ? row[0].value : defaultValue;
        });
    },

    constructor: function(config)
    {
        this.proxy.url = LABKEY.ActionURL.buildURL(config.dataModuleName, "Facets.api", LABKEY.containerPath, {objectName: config.cubeConfig.objectName});
        this.callParent([config]);
    }

});