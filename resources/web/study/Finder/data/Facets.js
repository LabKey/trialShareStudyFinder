Ext4.define('LABKEY.study.store.Facets', {
    extend: 'Ext.data.Store',
    model: 'LABKEY.study.data.Facet',
    storeId: 'facets',
    autoLoad: true,
    dataModuleName: 'study',
    filterByLevel : '[Study].[Study]',
    countDistinctLevel : '[Study].[Study]',
    filterByFacetUniqueName : '[Study]',
    olapConfig : {
        configId: 'TrialShare:/StudyCube',
        schemaName: 'lists',
        name: 'StudyCube'
    },
    mdx: null,
    isLoaded: false,

    proxy : {
        type: "ajax",
        url:  LABKEY.ActionURL.buildURL("trialshare", "studyFacets.api", LABKEY.containerPath),
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
            if (facet.get("name") != "Study")
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
            console.log("cube is ready now!");
            var facetMembersStore = Ext4.getStore("facetMembers");
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
                        //filteredCount: -1,
                        //selectedCount: -1,
                        facetName: facet.get("name"),
                        facet : facet
                    };
                    if (facet.get("name") != "Study")
                        facetMembersStore.add(member);
                    facet.data.members.push(member);
                    facet.data.memberMap[member.uniqueName] = member;
                }
                if (facet.get("name") == "Study") {
                    facet.data.selectedMembers = facet.data.members;
                }
            }
            facetMembersStore.sort();
            this.updateCountsAsync(false);
        }
    },

    getDefaultFilterOption : function(optionsStore) {
        for (var i = 0; i < optionsStore.count(); i++) {
            if (optionsStore.getAt(i).data.default)
                return optionsStore.getAt(i);
        }
        return optionsStore.getAt(0);
    },

    updateCountsAsync: function (isSavedGroup)
    {
        var facetStore = this;
        var intersectFilters = [];
        var i, f, facet;
        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            var filterMembers = facet.get("selectedMembers");
            if (facet.get("name") == 'Study')
            {
                if (!filterMembers || filterMembers.length == facet.data.members.length)
                    continue;
                if (filterMembers.length == 0)
                {
                    // in the case of study filter, this means no matches, rather than no filter!
                    this.updateCountsZero();
                    return;
                }
                var uniqueNames = filterMembers.map(function(m){return m.data.uniqueName;});
                if (this.filterByLevel != "[Study].[Study]")
                    intersectFilters.push({
                        level: this.filterByLevel,
                        membersQuery: {level: "[Study].[Study]", members: uniqueNames}
                    });
                else
                    intersectFilters.push({level: "[Study].[Study]", members: uniqueNames});
            }
            else
            {
                if (!filterMembers || filterMembers.length == 0)
                    continue;
                if (facet.get("currentFilterType") === "OR")
                {
                    var names = [];
                    filterMembers.forEach(function (m)
                    {
                        names.push(m.data.uniqueName)
                    });
                    intersectFilters.push({
                        level: this.filterByLevel,
                        membersQuery: {level: filterMembers[0].data.level, members: names}
                    });
                }
                else
                {
                    for (i = 0; i < filterMembers.length; i++)
                    {
                        var filterMember = filterMembers[i];
                        intersectFilters.push({
                            level: this.filterByLevel,
                            membersQuery: {level: filterMember.data.level, members: [filterMember.data.uniqueName]}
                        });
                    }
                }
            }
        }

        var filters = intersectFilters;
        //if (intersectFilters.length && this.filterByLevel != "[Subject].[Subject]")
        //{
        //    filters = [{
        //        level: "[Subject].[Subject]",
        //        membersQuery: {operator: "INTERSECT", arguments: intersectFilters}
        //    }]
        //}
        //
        // CONSIDER: Don't fetch subject IDs every time a filter is changed.
        var includeSubjectIds = false;

        var onRows = { operator: "UNION", arguments: [] };
        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            if (facet.get("name") == "Subject")
                onRows.arguments.push({level: facet.data.hierarchy.levels[0].uniqueName});
            else if (facet.get("name") == "Study" && this.filterByLevel == "[Study].[Study]")
                continue;
            else
                onRows.arguments.push({level: facet.data.level.uniqueName});
        }

        if (includeSubjectIds)
            onRows.arguments.push({level: "[Subject].[Subject]", members: "members"});

        var config =
        {
            "sql": true,
            configId: this.olapConfig.configId,
            schemaName: this.olapConfig.schemaName,
            name: this.olapConfig.name,
            success: function (cellSet, mdx, config)
            {
                this.updateCountsUnion(cellSet, isSavedGroup);
                //this.fireEvent("cubeReady");
            },
            scope: this,

            // query
            onRows: onRows,
            countFilter: filters,
            countDistinctLevel: this.countDistinctLevel
        };
        this.mdx.query(config);
    },

    updateCountsZero : function ()
    {
        var facetStore = Ext4.getStore("facets");
        var facetMembersStore = Ext4.getStore("facetMembers");
        var f, member, facet;
        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            facet.data.summaryCount = 0;
            facet.data.allMemberCount = 0;
            for (var m = 0; m < facetMembersStore.count(); m++)
            {
                member = facetMembersStore.getAt(m);
                member.data.count = 0;
                member.data.percent = 0;
            }
        }

        //this.saveFilterState();
        //this.updateContainerFilter();
        //this.changeSubjectGroup();
        //this.doneRendering();
    },

    /* handle query response to update all the member counts with all filters applied */
    updateCountsUnion : function (cellSet, isSavedGroup)
    {
        var facet, member, f, m;
        // map from hierarchyName to dataspace dimension
        var map = {};
        var facetStore = this;
        var facetMembersStore = Ext4.getStore("facetMembers");

        // clear old subjects and counts (to be safe)
        //this.subjects.length = 0;
        for (f = 0; f < facetStore.count(); f++)
        {
            facet = this.getAt(f);
            map[facet.data.hierarchy.uniqueName] = facet;
            facet.data.summaryCount = 0;
            facet.data.allMemberCount = 0;
        }
        for (m = 0; m < facetMembersStore.count(); m++) {
            facetMembersStore.getAt(m).data.count = 0;
            facetMembersStore.getAt(m).data.percent = 0;
        }

        var positions = this.getRowPositionsOneLevel(cellSet);
        var data = this.getDataOneColumn(cellSet, 0);
        var max = 0;
        for (var i = 0; i < positions.length; i++)
        {
            var resultMember = positions[i];
            //if (resultMember.data.level.uniqueName == "[Subject].[Subject]")
            //{
            //    this.subjects.push(resultMember.data.name);
            //}
            //else
            {
                var hierarchyName = resultMember.level.hierarchy.uniqueName;
                facet = map[hierarchyName]; // todo can't this come from the store if the key is uniqueName?
                var count = data[i];
                member = facetMembersStore.getById(resultMember.uniqueName);
                if (!member)
                {
                    // might be an all member
                    if (facet.data.allMemberName == resultMember.uniqueName)
                        facet.data.allMemberCount = count;
                    else if (-1 == resultMember.uniqueName.indexOf("#") && "(All)" != resultMember.name)
                        console.log("member not found: " + resultMember.uniqueName);
                }
                else
                {
                    member.set("count", count);
                    //member.data.count = count;
                    if (count)
                        facet.data.summaryCount += 1;
                    if (count > max)
                        max = count;
                }
            }
        }

        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            map[facet.data.hierarchy.uniqueName] = facet;
            if (facet.data.hierarchy.uniqueName !== this.filterByFacetUniqueName)
            {
                for (m = 0; m < facet.data.members.length; m++)
                {
                    member = facetMembersStore.getById(facet.data.members[m].uniqueName);
                    member.set("percent", max == 0 ? 0 : (100.0 * member.data.count) / max);
                    //member.data.percent = max == 0 ? 0 : (100.0 * member.data.count) / max;
                }
            }
        }

        //this.fireEvent("countUpdate");
        //this.saveFilterState();
        //this.updateContainerFilter();
        //if (!isSavedGroup)
        //    this.changeSubjectGroup();
        //this.doneRendering();
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
        var ret = cells.map(function(row)
        {
            return row.map(function(col){return col.value ? col.value : defaultValue;});
        });
        return ret;
    },

    getDataOneColumn : function(cellSet,defaultValue)
    {
        var cells = cellSet.cells;
        if (cells.length > 0 && cells[0].length > 1)
        {
            console.log("warning cellSet has more than one column");
            throw "illegal state";
        }
        var ret = cells.map(function(row)
        {
            return row[0].value ? row[0].value : defaultValue;
        });
        return ret;
    }

});