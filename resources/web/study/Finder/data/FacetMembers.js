Ext4.define('LABKEY.study.store.FacetMembers', {
    extend: 'Ext.data.Store',
    model: 'LABKEY.study.data.FacetMember2',
    storeId: 'facetMembers',
    autoLoad: true,
    dataModuleName: 'trialShare', // TODO figure out how to use this config property so we don't have to hard-code 'trialshare'
    proxy : {
        type: "ajax",
        url:  LABKEY.ActionURL.buildURL('trialShare', "studyFacetMembers.api", LABKEY.containerPath),
        reader: {
            type: 'json',
            root: 'data'
        }
    },
    listeners: {
        'load' : {
            fn : function(store, records, options) {
                console.log('FacetMember store loaded');
                var facetsStore = Ext4.getStore("facets");
                for (var i = 0; i < records.length; i++) {
                    if (!facetsStore.getById(records[i].data.facetName))
                    {
                        var facet = {
                            name: records[i].data.facetName,
                            uniqueName: records[i].data.facetUniqueName,
                            filterOptions: records[i].filterOptionsStore ? records[i].filterOptionsStore.data.items : [],
                            selectedMembers : []
                        };
                        if (facet.filterOptions.length) {
                            facet.currentFilterCaption = facet.filterOptions[0].get("caption");
                            facet.currentFilterType = facet.filterOptions[0].get("type");
                        }
                        facetsStore.add(facet);
                    }
                }
            },
            scope: this
        }
    },
    groupField : 'facetName',

    updateCountsAsync: function (isSavedGroup)
    {
        var facetStore = Ext4.getStore("facets");
        //var intersectFilters = [];
        //var d, i, dim;
        //for (d in dataspace.dimensions)
        //{
        //    if (!dataspace.dimensions.hasOwnProperty(d))
        //        continue;
        //    dim = dataspace.dimensions[d];
        //    var filterMembers = dim.filters;
        //    if (d == 'Study')
        //    {
        //        if (!filterMembers || filterMembers.length == dim.members.length)
        //            continue;
        //        if (filterMembers.length == 0)
        //        {
        //            // in the case of study filter, this means no matches, rather than no filter!
        //            this.updateCountsZero();
        //            return;
        //        }
        //        var uniqueNames = filterMembers.map(function(m){return m.uniqueName;});
        //        if (this.filterByLevel != "[Study].[Name]")
        //            intersectFilters.push({
        //                level: this.filterByLevel,
        //                membersQuery: {level: "[Study].[Name]", members: uniqueNames}
        //            });
        //        else
        //            intersectFilters.push({level: "[Study].[Name]", members: uniqueNames});
        //    }
        //    else
        //    {
        //        if (!filterMembers || filterMembers.length == 0)
        //            continue;
        //        if (dim.filterType === "OR")
        //        {
        //            var names = [];
        //            filterMembers.forEach(function (m)
        //            {
        //                names.push(m.uniqueName)
        //            });
        //            intersectFilters.push({
        //                level: this.filterByLevel,
        //                membersQuery: {level: filterMembers[0].level, members: names}
        //            });
        //        }
        //        else
        //        {
        //            for (i = 0; i < filterMembers.length; i++)
        //            {
        //                var filterMember = filterMembers[i];
        //                intersectFilters.push({
        //                    level: this.filterByLevel,
        //                    membersQuery: {level: filterMember.level, members: [filterMember.uniqueName]}
        //                });
        //            }
        //        }
        //    }
        //}
        //
        //var filters = intersectFilters;
        //if (intersectFilters.length && this.filterByLevel != "[Subject].[Subject]")
        //{
        //    filters = [{
        //        level: "[Subject].[Subject]",
        //        membersQuery: {operator: "INTERSECT", arguments: intersectFilters}
        //    }]
        //}
        //
        //// CONSIDER: Don't fetch subject IDs every time a filter is changed.
        //var includeSubjectIds = true;
        //
        //var onRows = { operator: "UNION", arguments: [] };
        //for (d in dataspace.dimensions)
        //{
        //    if (!dataspace.dimensions.hasOwnProperty(d))
        //        continue;
        //    dim = dataspace.dimensions[d];
        //    if (dim.name == "Subject")
        //        onRows.arguments.push({level: dim.hierarchy.levels[0].uniqueName});
        //    else if (dim.name == "Study" && this.filterByLevel == "[Study].[Name]")
        //        continue;
        //    else
        //        onRows.arguments.push({level: dim.level.uniqueName});
        //}
        //
        //if (includeSubjectIds)
        //    onRows.arguments.push({level: "[Subject].[Subject]", members: "members"});
        //
        //var config =
        //{
        //    "sql": true,
        //    configId: 'ImmPort:/StudyCube',
        //    schemaName: 'ImmPort',
        //    name: 'StudyCube',
        //    success: function (cellSet, mdx, config)
        //    {
        //        // use angular timeout() for its implicit $scope.$apply()
        //        //                config.scope.timeout(function(){config.scope.updateCounts(config.dim, cellSet);},1);
        //        config.scope.timeout(function ()
        //        {
        //            config.scope.updateCountsUnion(cellSet, isSavedGroup);
        //            $scope.$broadcast("cubeReady");
        //        }, 1);
        //    },
        //    scope: this,
        //
        //    // query
        //    onRows: onRows,
        //    countFilter: filters,
        //    countDistinctLevel: '[Subject].[Subject]'
        //};
        //this.mdx.query(config);
    },

    updateCountsZero : function ()
    {
        for (d in dataspace.dimensions)
        {
            if (!dataspace.dimensions.hasOwnProperty(d))
                continue;
            var dim = dataspace.dimensions[d];
            dim.summaryCount = 0;
            for (var m = 0; m < dim.members.length; m++)
            {
                dim.members[m].count = 0;
                dim.members[m].percent = 0;
            }
            dim.summaryCount = 0;
        }

        this.saveFilterState();
        this.updateContainerFilter();
        this.changeSubjectGroup();
        this.doneRendering();
    },

    /* handle query response to update all the member counts with all filters applied */
    updateCountsUnion : function (cellSet, isSavedGroup)
    {
        var dim, member, d, m;
        // map from hierarchyName to dataspace dimension
        var map = {};

        // clear old subjects and counts (to be safe)
        //this.subjects.length = 0;
        for (d = 0; d < this.count(); d++)
        {
            dim = this.getAt(d);
            map[dim.data.hierarchy.uniqueName] = dim;
            dim.data.summaryCount = 0;
            dim.data.allMemberCount = 0;
        }
        for (var i = 0; i < this.count(); i++) {
            this.getAt(i).data.count = 0;
            this.getAt(i).data.percent = 0;
        }

        var positions = cellSetHelper.getRowPositionsOneLevel(cellSet);
        var data = cellSetHelper.getDataOneColumn(cellSet, 0);
        var max = 0;
        for (var i = 0; i < positions.length; i++)
        {
            var resultMember = positions[i];
            if (resultMember.level.uniqueName == "[Subject].[Subject]")
            {
                this.subjects.push(resultMember.name);
            }
            else
            {
                var hierarchyName = resultMember.level.hierarchy.uniqueName;
                dim = map[hierarchyName];
                var count = data[i];
                member = dim.memberMap[resultMember.uniqueName];
                if (!member)
                {
                    // might be an all member
                    if (dim.allMemberName == resultMember.uniqueName)
                        dim.allMemberCount = count;
                    else if (-1 == resultMember.uniqueName.indexOf("#") && "(All)" != resultMember.name)
                        console.log("member not found: " + resultMember.uniqueName);
                }
                else
                {
                    member.count = count;
                    if (count)
                        dim.summaryCount += 1;
                    if (count > max)
                        max = count;
                }
            }
        }

        for (d in dataspace.dimensions)
        {
            dim = dataspace.dimensions[d];
            map[dim.hierarchy.uniqueName] = dim;
            for (m = 0; m < dim.members.length; m++)
            {
                member = dim.members[m];
                member.percent = max == 0 ? 0 : (100.0 * member.count) / max;
            }
        }

        //this.saveFilterState();
        //this.updateContainerFilter();
        //if (!isSavedGroup)
        //    this.changeSubjectGroup();
        //this.doneRendering();
    }

});