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
        configId: 'Study:/StudyCube',
        schemaName: 'lists',
        name: 'StudyCube'
    },
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
                        facetName: facet.get("name"),
                        facet : facet
                    };
                    if (facet.get("name") != "Study")
                        facetMembersStore.add(member);
                    facet.data.members.push(member);
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
            if (optionsStore.getAt(i).data.isDefault)
                return optionsStore.getAt(i);
        }
        return optionsStore.getAt(0);
    },

    getStudySubsetFilter: function() {
        var studiesStore = Ext4.getStore("studies");
        if (studiesStore.selectedSubset == "operational")
            return {level: "[Study.Public].[Public]", members: ["[Study.Public].[false]"]};
        else
            return {level: "[Study.Public].[Public]", members: ["[Study.Public].[true]"]};
    },

    updateCountsAsync: function (isSavedGroup)
    {
        if (!this.isLoaded || !this.mdx)
        {
            console.log("Store not ready for count update.  Please try again later.");
            return;
        }

        var facetStore = this;
        var intersectFilters = [];
        var i, f, facet;

        intersectFilters.push(this.getStudySubsetFilter());
        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            var selectedMembers = facet.get("selectedMembers");
            if (facet.get("name") == 'Study')
            {
                //if (!selectedMembers || selectedMembers.length == facet.data.members.length)
                //    continue;
                //if (selectedMembers.length == 0)
                //{
                //    // in the case of study filter, this means no matches, rather than no filter!
                //    this.updateCountsZero();
                //    return;
                //}
                // TODO seems unnecessary if we always pass in all of the names.
                //var uniqueNames = facet.data.members.map(function(m){return m.uniqueName;});
                //if (this.filterByLevel != "[Study].[Study]")
                //    intersectFilters.push({
                //        level: this.filterByLevel,
                //        membersQuery: {level: "[Study].[Study]", members: uniqueNames}
                //    });
                //else
                //    intersectFilters.push({level: "[Study].[Study]", members: uniqueNames});
            }
            else
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
                        level: this.filterByLevel,
                        membersQuery: {level: selectedMembers[0].data.level, members: names}
                    });
                }
                else
                {
                    for (i = 0; i < selectedMembers.length; i++)
                    {
                        var filterMember = selectedMembers[i];
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
        onRows.arguments.push({level: this.filterByLevel});
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
        var facetMembersStore = Ext4.getStore("facetMembers");
        facetMembersStore.zeroCounts();
        //this.saveFilterState();
        //this.updateContainerFilter();
        //this.changeSubjectGroup();
        //this.doneRendering();
    },

    /* handle query response to update all the member counts with all filters applied */
    updateCountsUnion : function (cellSet, isSavedGroup)
    {
        var facet, member, f, m;
        // map from hierarchyName to facet
        var map = {};
        var facetStore = this;
        var facetMembersStore = Ext4.getStore("facetMembers");
        facetMembersStore.suspendEvents(false);

        // clear old subjects and counts (to be safe)
        //this.subjects.length = 0;
        for (f = 0; f < facetStore.count(); f++)
        {
            facet = this.getAt(f);
            map[facet.data.hierarchy.uniqueName] = facet;
            if (facet.get("name") == "Study") {
                facet.data.selectedMembers = [];
            }
        }

        this.updateCountsZero();
        var positions = this.getRowPositionsOneLevel(cellSet);
        var data = this.getDataOneColumn(cellSet, 0);
        var max = 0;
        var selectedStudies = {};
        for (var i = 0; i < positions.length; i++)
        {
            var resultMember = positions[i];
            var hierarchyName = resultMember.level.hierarchy.uniqueName;
            //if (resultMember.data.level.uniqueName == "[Subject].[Subject]")
            //{
            //    this.subjects.push(resultMember.data.name);
            //}
            //else
            {
                facet = map[hierarchyName];
                var count = data[i];
                member = facetMembersStore.getById(resultMember.uniqueName);
                if (facet.get("name") == "Study")
                {
                    selectedStudies[resultMember.name] = resultMember;
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
                else
                {
                    member.set("count", count);
                    if (count > max)
                        max = count;
                }
            }
        }

        for (f = 0; f < facetStore.count(); f++)
        {
            facet = facetStore.getAt(f);
            if (facet.data.hierarchy.uniqueName !== this.filterByFacetUniqueName)
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

        this.updateStudyFilter(selectedStudies);

        //this.saveFilterState();
        //this.updateContainerFilter();
        //if (!isSavedGroup)
        //    this.changeSubjectGroup();


        LABKEY.Utils.signalWebDriverTest('dataFinderCountsUpdated');
    },

    updateStudyFilter : function(selectedStudies) {
        var studiesStore = Ext4.getStore("studies");
        studiesStore.selectedStudies = selectedStudies;
        studiesStore.updateFilters(selectedStudies);
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
        this.proxy.url = LABKEY.ActionURL.buildURL(config.dataModuleName, "studyFacets.api", LABKEY.containerPath);
        this.olapConfig = config.olapConfig;
        this.callParent(config);
    }

});