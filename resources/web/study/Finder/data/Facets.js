/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define('LABKEY.study.store.Facets', {
    extend: 'Ext.data.Store',
    model: 'LABKEY.study.data.Facet',
    autoLoad: false,

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

    getObjectSubsetFilter: function() {
        var store = Ext4.getStore(this.cubeConfig.objectName);

        if (!store || !store.selectedSubset)
            return null;
        else
            return {level: this.cubeConfig.subsetLevelName, members: [ store.selectedSubset ]};

    },

    getFiltersForCountDistinct: function(filtersMap) {
        this.addFilterMapData(filtersMap, this.getObjectSubsetFilter());
        this.addFilterMapData(filtersMap, this.getSearchFilter());
        this.addFilterMapData(filtersMap, this.getCustomFilters());
        var filters = [];
        for (var level in filtersMap) {
            if (!filtersMap.hasOwnProperty(level))
                    continue;
            if (Ext4.isObject(filtersMap[level]))
            {
                for (var level2 in filtersMap[level])
                {
                    filters.push({
                        level: this.cubeConfig.filterByLevel,
                        membersQuery: {level: level2, members: filtersMap[level][level2]}
                    });
                }
            }
            else
                filters.push({level: level, members: filtersMap[level]});
        }
        this.addSelectedMembersFilters(filters);
        return filters;
    },

    addFilterMapData : function(filtersMap, newFilters)
    {
        if (newFilters != null)
        {
            if (filtersMap[newFilters.level])
                filtersMap[newFilters.level].concat(newFilters.members);
            else
                filtersMap[newFilters.level] = newFilters.members;
        }
        return filtersMap;
    },

    getSearchFilter : function()
    {
        var store = Ext4.getStore(this.cubeConfig.objectName);
        if (store.searchSelectedMembers != null)
        {
            var selection = [];
            for (var key in store.searchSelectedMembers)
            {
                selection = selection.concat(store.searchSelectedMembers[key]);
            }
            return {level: this.cubeConfig.filterByLevel, members: selection};
        }
        return null;
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

        var store = Ext4.getStore(this.cubeConfig.objectName);
        if (store.searchSelectedMembers != null)
            this.makeCountDistinctQueries({}); // search has already filtered results to the ones the user has access to
        else
        {
            // console.log("updateCountsAsync called");
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
                        var filters = {};
                        for (var level in o.data)
                        {
                            if (!o.data.hasOwnProperty(level))
                                continue;
                            if (Ext4.isObject(o.data[level]) || (Ext4.isArray(o.data[level]) && o.data[level].length))
                            {
                                var includedMembers = this.removeMembersNotInCube(level, o.data[level]);
                                if (!filters[level])
                                    filters[level] = includedMembers;
                                else
                                    filters[level] = filters[level].concat(includedMembers);
                            }
                        }
                        this.makeCountDistinctQueries(filters);
                    }
                    else
                    {
                        console.log("Problem making request for accessible members", o);
                    }

                },
                scope: this
            });
        }
    },

    removeMembersNotInCube : function(level, filterData)
    {
        if (!filterData)
            return filterData;
        if (Ext4.isArray(filterData))
        {
            // split the level name into its parts to use for accessing the cube members
            var levelParts = level.replace(/[\[\]]/g, "").split(".");
            if (levelParts && levelParts.length > 1)
            {
                // find the uniqueNames of the current members of the cube for the given level
                var uniqueNames = this.mdx._cube.hierarchyMap[levelParts[0]].levelMap[levelParts[1]].members.map(function (m)
                {
                    return m.uniqueName
                });
                // filter out the accessible members that are not currently part of the cube
                var includedMembers = filterData.filter(function (m)
                {
                    return uniqueNames.indexOf(m) >= 0;
                });
                return includedMembers;
            }

        }
        else if (Ext4.isObject(filterData))
        {
            for (var nextLevel in filterData)
            {
                if (filterData.hasOwnProperty(nextLevel))
                    filterData[nextLevel] = this.removeMembersNotInCube(nextLevel, filterData[nextLevel]);
            }
            return filterData;
        }
    },

    makeCountDistinctQueries : function(filtersMap)
    {
        this.cellSetPositions = null;
        this.cellSetCells = null;
        this.cellSets = {};
        if (this.cubeConfig.objectName == "Publication")
            this.makeCountDistinctQuery(filtersMap);
        else
        {
            this.makeVisibilityCountDistinctQuery(filtersMap);
            this.makeCountDistinctQuery(filtersMap);
        }

    },

    makeCountDistinctQuery: function(filtersMap)
    {
        var filters = this.getFiltersForCountDistinct(filtersMap);
        
        var multiColumnCount = this.cubeConfig.countField && this.cubeConfig.countField != this.cubeConfig.objectName;

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
                this.updateCounts(cellSet, "Default", multiColumnCount);
            },
            scope: this,

            // query
            onRows: this.getOnRowsData(),
            countFilter: filters,
            includeNullMemberInCount: false,
            countDistinctLevel: this.cubeConfig.countDistinctLevel
        };
        if (multiColumnCount)
        {
            config.onCols = { operator: "UNION", arguments: [{level: this.cubeConfig.filterByLevel}] };
        }
        // console.log("Making count distinct query with config", config);
        this.mdx.query(config);

    },

    addSelectedMembersFilters: function(filters)
    {
        var facet;
        for (var f = 0; f < this.count(); f++)
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
                    filters.push({
                        level: this.cubeConfig.filterByLevel,
                        membersQuery: {level: selectedMembers[0].data.level, members: names}
                    });
                }
                else
                {
                    for (var i = 0; i < selectedMembers.length; i++)
                    {
                        var filterMember = selectedMembers[i];
                        filters.push({
                            level: this.cubeConfig.filterByLevel,
                            membersQuery: {level: filterMember.data.level, members: [filterMember.data.uniqueName]}
                        });
                    }
                }
            }
        }
    },

    getOnRowsData: function()
    {
        var onRows = { operator: "UNION", arguments: [] };
        onRows.arguments.push({level: this.cubeConfig.filterByLevel});
        for (f = 0; f < this.count(); f++)
        {
            facet = this.getAt(f);
            if (facet.get("name") == "Subject")
                onRows.arguments.push({level: facet.data.hierarchy.levels[0].uniqueName});
            else if (facet.get("name") == this.cubeConfig.objectName || (this.cubeConfig.objectName == "Study" && facet.get("name") == "Visibility"))
                continue;
            else
                onRows.arguments.push({level: facet.data.level.uniqueName});
        }
        return onRows;
    },

    mergeCellSets: function(newCellSet)
    {
        if (!this.cellSetPositions)
        {
            this.cellSetPositions = newCellSet.axes[1].positions;
            this.cellSetCells = newCellSet.cells;
        }
        else
        {
            // console.log("before", this.cellSet);
            this.cellSetPositions = this.cellSetPositions.concat(newCellSet.axes[1].positions);
            this.cellSetCells = this.cellSetCells.concat(newCellSet.cells);
            // console.log("after", this.cellSet);
        }
    },

    makeVisibilityCountDistinctQuery : function(filtersMap)
    {
        var filters = this.getFiltersForCountDistinct(filtersMap);

        var onRows = { operator: "UNION", arguments: [] };
        onRows.arguments.push({level: "[Study.Visibility].[Visibility]"});

        var multiColumnCount = this.cubeConfig.countField && this.cubeConfig.countField != this.cubeConfig.objectName;

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
                this.updateCounts(cellSet, "Visibility", multiColumnCount);
            },
            scope: this,

            // query
            onRows: onRows,
            countFilter: filters,
            includeNullMemberInCount: false,
            countDistinctLevel: "[Study].[Container]"
        };
        if (multiColumnCount)
        {
            config.onCols = { operator: "UNION", arguments: [{level: this.cubeConfig.filterByLevel}] };
        }
        // console.log("Making count distinct query with config", config);
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

    updateCounts: function(cellSet, cellSetType, multiColumnCount)
    {
        this.mergeCellSets(cellSet);
        if (this.cubeConfig.objectName == "Publication")
            this.updateCountsUnion(multiColumnCount);
        else
        {
            this.cellSets[cellSetType] = true;
            if (this.cellSets.Visibility && this.cellSets.Default)
                this.updateCountsUnion(multiColumnCount);
        }
    },

    /* handle query response to update all the member counts with all filters applied */
    updateCountsUnion : function (multiColumnCount)
    {
        var cellSet = {cells: this.cellSetCells};
        var facet, member, f, m;
        // map from hierarchyName to facet
        var map = {};
        var facetStore = this;
        var facetMembersStore = Ext4.getStore(this.cubeConfig.objectName + "FacetMembers");
        var objectStore = Ext4.getStore(this.cubeConfig.objectName);
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
        // var positions = this.getAxisPositions(cellSet, 1);
        var positions = this.cellSetPositions.map(function(inner){return inner[0]});
        var data;
        if (!objectStore.unfilteredCount)
            objectStore.setUnfilteredCount();
        if (multiColumnCount)
            data = this.getMultiColumnData(cellSet);
        else
            data = this.getDataOneColumn(cellSet, 0);

        var max = 0;
        var selectedMembers = {};
        for (var i = 0; i < positions.length; i++)
        {
            var resultMember = positions[i];
            var hierarchyName = resultMember.level.hierarchy.uniqueName;

            facet = map[hierarchyName];

            var isCubeObjectCount = facet.get("name") == this.cubeConfig.objectName;
            // a bit of hackery here because cube objects that are present in the counts should become selected members
            // even if the countField value is 0.  We return -1 in the data to indicate selected but with a 0 count.
            // So if this member is a cube object, we make a negative count positive (1) and otherwise we make the count 0.
            var count = (data[i] >= 0 ? data[i] : isCubeObjectCount ? 1 : 0);
            member = facetMembersStore.getById(resultMember.uniqueName);
            if (isCubeObjectCount)
            {
                if (count > 0)
                {
                    selectedMembers[resultMember.name] = resultMember;
                    facet.data.selectedMembers.push(resultMember);
                }
            }
            else if (!member)
            {
                if (-1 == resultMember.uniqueName.indexOf("#") && "(All)" != resultMember.name)
                    console.log("member not found: " + resultMember.uniqueName);
            }
            else if (facet.get("displayFacet"))
            {
                member.set("count", count);
                if (count > max)
                    max = count;
                if (!member.data.unfilteredCount)
                    member.set("unfilteredCount", count);
            }
        }

        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            if (facet.data.hierarchy.uniqueName !== this.cubeConfig.filterByFacetUniqueName)
            {
                var facetTotal = 0;
                for (m = 0; m < facet.data.members.length; m++)
                {
                    member = facetMembersStore.getById(facet.data.members[m].uniqueName);
                    if (facet.get("displayFacet"))
                    {
                        if (objectStore)
                        {
                            member.set("unfilteredPercent", objectStore.unfilteredCount == 0 ? 0 : 100 * member.data.unfilteredCount / objectStore.unfilteredCount);
                            member.set("percent", objectStore.unfilteredCount == 0 ? 0 : (100.0 * member.data.count) / objectStore.unfilteredCount);
                        }
                        else // not sure this is necessary
                        {
                            member.set("unfilteredPercent", 100 * member.data.unfilteredCount / max);
                            member.set("percent", max == 0 ? 0 : (100.0 * member.data.count) / max);
                        }
                    }
                }
            }
            if (!facet.data.allMemberCount)
                facet.set("allMemberCount", facetTotal);
        }

        facetMembersStore.resumeEvents();
        facetMembersStore.fireEvent("refresh");

        this.updateMemberFilter(selectedMembers);

        //this.saveFilterState();
        //this.updateContainerFilter();
        //if (!isSavedGroup)
        //    this.changeSubjectGroup();

        this.fireEvent("countsUpdated");
        LABKEY.Utils.signalWebDriverTest('dataFinder' + this.cubeConfig.objectName + 'CountsUpdated');
    },

    updateMemberFilter : function(selectedMembers) {
        var store = Ext4.getStore(this.cubeConfig.objectName);
        store.updateFacetFilters(selectedMembers);
    },

    getAxisPositions : function(cellSet, axisIndex)
    {
        var positions = cellSet.axes[axisIndex].positions;
        if (positions.length > 0 && positions[0].length > 1)
        {
            console.log("warning: axis has nested members");
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

    getMultiColumnData : function(cellSet)
    {
        var columnPositions = this.getAxisPositions(cellSet, 0);
        var cells = cellSet.cells;
        var objectStore = Ext4.getStore(this.cubeConfig.objectName);
        return cells.map(function(row)
        {
            var isSelected = false;
            var sum = 0;
            for (var i = 0; i < row.length; i++)
            {
                var object = objectStore.getById(columnPositions[i].name);
                if (object && object.get(this.cubeConfig.countField) !== undefined)
                {
                    isSelected = isSelected || row[i].value > 0; // we want members to be selected from the filter even if their countField is 0
                    sum += row[i].value * object.get(this.cubeConfig.countField)
                }
                else
                {
                    console.log("no object in store with id " + columnPositions[i].name);
                }
            }
            // return -1 to indicate that the member was selected, even though the sum of count
            // field was 0 (because, for example, there may be operational studies (cube objects) that have 0 
            // participants (count field) currently)
            return sum > 0 ? sum : (isSelected ? -1 : 0);
        }, this);
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