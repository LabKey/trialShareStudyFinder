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

    autoScroll: true,
    
    initComponent : function() {
        this.items = [
            this.getCardPanelHeader(),
            this.getCardsContainer()
        ];
        this.callParent();

        this.on({
            'subsetChanged': this.onSubsetChanged,
            'searchTermsChanged': this.onSearchTermsChanged
        });
    },

    onSubsetChanged : function(selectedSubset) {
        if (!selectedSubset)
            selectedSubset = this.getCardPanelHeader().getSubsetMenu().getValue();
        this.getCards().store.updateFacetFilters(null, selectedSubset);
    },
    
    // TODO should this be displayed?
    getSearchMessage : function() {
        if (this.getCards().store.count() == 0 && this.searchTerms != "")
            return "No " + this.cubeConfig.objectNamePlural.toLowerCase() + " match your search criteria";
        else
            return "";
    },

    // TODO make this a delayed action?
    onSearchTermsChanged : function(searchTerms) {
        this.searchTerms = searchTerms;
        if (!searchTerms)
        {
            this.updateSearchFilters(null);
            return;
        }

        var url = LABKEY.ActionURL.buildURL("search", "json", this.dataModuleName, {
            "category": this.cubeConfig.searchCategory,
            "q": searchTerms
        });
        Ext4.Ajax.request({
            url: url,
            scope: this,
            success: function (response)
            {
                var data = Ext4.decode(response.responseText);
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
                console.log("found " + Object.keys(searchHits).length + " objects matching terms " + searchTerms, searchHits);
                this.updateSearchFilters(searchHits);
                // this.up('labkey-data-finder-panel').onSearchTermsChanged();
            }
        });
        
    },

    updateSearchFilters: function(searchHits)
    {
        this.getCards().store.updateSearchFilters(searchHits);
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

    getCardsContainer : function() {
        if (!this.facetsContainer) {
            this.facetsContainer = {
                xtype: 'container',
                itemId: this.cubeConfig.objectName.toLowerCase() + 'CardsContainer',
                flex: 10,
                autoScroll: true,
                layout: {
                    type: 'vbox',
                    align: 'stretch',
                    pack: 'start'
                },
                items: [
                    this.getCards()
                ]
            };
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