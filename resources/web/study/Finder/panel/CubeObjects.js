/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.CubeObjects", {
    extend: 'Ext.panel.Panel',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    border: false,

    initComponent : function() {
        this.items = [
            this.getCardPanelHeader(),
            this.getCardsContainer()
        ];
        this.callParent();

        var searchTermsChangeTask = new Ext4.util.DelayedTask();

        this.on({
            'resize' : this.updateCardContainerHeight,
            'subsetChanged': this.onSubsetChanged,
            'searchTermsChanged': function(searchTerms){
                searchTermsChangeTask.delay(350, this.onSearchTermsChanged, this, [searchTerms]);
            }
        });
    },

    onClearAllFilters : function() {
        this.getCardPanelHeader().onClearAllFilters();
    },

    onSubsetChanged : function(selectedSubset) {
        if (!selectedSubset)
            selectedSubset = this.getCardPanelHeader().getSubsetMenu().getValue();
        this.getCards().store.updateFacetFilters(null, selectedSubset);
    },

    onSearchTermsChanged : function(searchTerms) {
        // console.log("search terms changed to " + searchTerms);
        this.searchTerms = searchTerms;
        if (!searchTerms)
        {
            this.updateSearchFilters(null);
            return;
        }

        var url = LABKEY.ActionURL.buildURL("search", "json", this.cubeConfig.cubeContainerPath, {
            "category": this.cubeConfig.searchCategory,
            "q": searchTerms,
            "scope" : this.cubeConfig.searchScope
        });
        Ext4.Ajax.request({
            url: url,
            scope: this,
            success: function (response)
            {
                var data = Ext4.decode(response.responseText);
                if (data.q != this.searchTerms) // search terms changed since query was made
                {
                    // console.log("Ignoring results.  Search terms have since changed to " + this.searchTerms, data);
                    return;
                }
                var hits = data.hits;
                var searchHits = {};
                var count = 0;
                for (var h = 0; h < hits.length; h++)
                {
                    var id = hits[h].id;
                    var accession = id.substring(id.lastIndexOf(':') + 1);
                    if (searchHits[accession])
                        continue;
                    if (this.cubeConfig.hasContainerFilter)
                        searchHits[accession] = "[" + this.cubeConfig.objectName + "].[" + hits[h].container + "].[" + accession + "]";
                    else
                        searchHits[accession] = "[" + this.cubeConfig.objectName + "].[" + accession + "]";
                }
                // console.log("found " + Object.keys(searchHits).length + " objects matching terms " + searchTerms, searchHits);
                this.updateSearchFilters(searchHits);
            },
            failure: function(response)
            {
                var data = Ext4.decode(response.responseText);
                Ext4.Msg.show({
                    title: 'Error',
                    buttons: Ext4.MessageBox.OK,
                    msg: data.exception
                });
            }
        });
        
    },

    updateSearchFilters: function(searchHits)
    {
        this.getCards().store.setSearchFilters(searchHits);
        var facetStore = Ext4.getStore(this.cubeConfig.objectName + "Facets");
        if (facetStore)
            facetStore.updateCountsAsync();
    },

    getCardPanelHeader : function() {
        if (!this.cardPanelHeader) {
            this.cardPanelHeader = Ext4.create("LABKEY.study.panel.FinderCardPanelHeader", {
                dataModuleName: this.dataModuleName,
                cubeContainerPath: this.cubeConfig.cubeContainerPath,
                showSearch : this.cubeConfig.showSearch,
                objectName: this.cubeConfig.objectName,
                objectNamePlural: this.cubeConfig.objectNamePlural
            });
        }
        return this.cardPanelHeader;
    },

    updateCardContainerHeight: function() {
        this.getCardsContainer().setHeight(this.getHeight() - this.getCardPanelHeader().getHeight());
    },

    getCardsContainer : function() {
        if (!this.facetsContainer) {
            this.facetsContainer = Ext4.create('Ext.panel.Panel', {
                itemId: this.cubeConfig.objectName.toLowerCase() + 'CardsContainer',
                autoScroll: true,
                border: false,
                items: [
                    this.getCards()
                ]
            });
            this.facetsContainer.on("afterlayout", this.updateCardContainerHeight, this, {single: true});

        }
        return this.facetsContainer;
    },

    getCards : function() {
        if (!this.cards) {
            this.cards = Ext4.create("LABKEY.study.panel." + this.cubeConfig.objectName + "Cards", {
                dataModuleName: this.dataModuleName,
                cubeContainerPath : this.cubeConfig.cubeContainerPath
            });
        }
        return this.cards;
    }
});