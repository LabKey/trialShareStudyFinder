/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.FinderCardPanelHeader", {

    extend: 'Ext.Container',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    cls: 'labkey-finder-card-header',

    searchMessage: "",

    bubbleEvents: [
        "subsetChanged",
        "searchTermsChanged"
    ],

    initComponent: function() {

        this.subsets = Ext4.create('LABKEY.study.store.Subsets', {
            dataModuleName : this.dataModuleName,
            cubeContainerPath: this.cubeContainerPath,
            objectName: this.objectName
        });
        this.items = [];
        var searchBox = this.getSearchBox();
        if (searchBox)
            this.items.push(searchBox);
        this.items.push(this.getSubsetMenu());

        this.items.push({
            // spacer
            xtype: 'box',
            autoEl: {
                tag: 'div'
            },
            flex: 10
        });
        this.addManageDataLink();
        this.items.push(this.getHelpLinks());

        this.subsets.on(
                'load', function(store) {
                    this.getSubsetMenu().setValue(store.defaultValue);
                    var objectStore = Ext4.getStore(this.objectName);
                    if (objectStore && store.defaultValue)
                        objectStore.selectedSubset = store.defaultValue.data.id;
                    if (store.count() > 1)
                        this.getSubsetMenu().show();
                },
                this
        );
        
        this.callParent();
    },

    addManageDataLink: function()
    {
        LABKEY.Security.getUserPermissions({
            containerPath: this.cubeContainerPath,
            success: function (userPerms, resp) {
                if (LABKEY.Security.hasPermission(userPerms.container.permissions, LABKEY.Security.permissions.insert))
                {
                    if (this.objectName == "Publication")
                        this.items.items.push(this.getInsertNewLink());
                    this.items.items.push(this.getManageDataLink());
                }
            },
            scope: this
        });
    },

    getSearchBox : function() {
        if (!this.searchBox && this.showSearch) {
            this.searchBox = Ext4.create('Ext.form.field.Text', {
                emptyText: this.objectNamePlural,
                cls: 'labkey-search-box',
                fieldLabel: '<i class="fa fa-search"></i>',
                labelWidth: "10px",
                labelSeparator: '',
                disabled: !this.showSearch,
                hidden: !this.showSearch,
                fieldCls: 'labkey-search-box',
                name: this.objectName + 'searchTerms',
                listeners: {
                    scope: this,
                    'change': function(field,newValue,oldValue,eOpts) {
                        this.onSearchTermsChanged(newValue)
                    }
                }
            })
        }
        return this.searchBox;

    },

    getSubsetMenu: function() {
        if (!this.subsetMenu) {
            this.subsetMenu = Ext4.create('Ext.form.ComboBox', {
                store: this.subsets,
                queryMode: 'local',
                name: 'subsetSelect',
                valueField: 'id',
                displayField: 'name',
                hidden: true,
                value: this.subsets.defaultValue,
                cls: 'labkey-study-search',
                multiSelect: false,
                listeners: {
                    scope: this,
                    'select': function(field, newValue, oldValue, eOpts) {
                        this.onSubsetChanged(newValue[0])
                    },
                    'render': function(eOpts) {
                        if (this.subsets.defaultValue)
                            this.onSubsetChanged(this.subsets.defaultValue)
                    }
                }
            })
        }
        return this.subsetMenu;
    },

    getManageDataLink: function() {
        if (!this.manageDataLink) {
            this.manageDataLink = Ext4.create("Ext.button.Button", {
                text: 'manage data',
                cls: 'labkey-text-link labkey-finder-manage-data',
                componentCls: 'labkey-finder-manage-data',
                scope: this,
                // renderTo: this.objectName + '-manage-data-link',
                handler: function() {
                    window.open(LABKEY.ActionURL.buildURL(this.dataModuleName, "manageData.view", this.cubeContainerPath, {
                        objectName : this.objectName,
                        'query.viewName' : 'manageData'
                    }), '_blank');
                }
            });
        }
        return this.manageDataLink;
    },

    getInsertNewLink: function() {
        if (!this.insertNewLink) {
            this.insertNewLink = Ext4.create("Ext.button.Button", {
                text: 'Insert New',
                cls: 'labkey-text-link labkey-finder-insert-new',
                componentCls: 'labkey-finder-insert-new',
                scope: this,
                handler: function() {
                    window.open(LABKEY.ActionURL.buildURL(this.dataModuleName, "insertDataForm.view", this.cubeContainerPath, {
                        objectName : this.objectName,
                        'query.viewName' : 'manageData'
                    }), '_blank');
                }
            });
        }
        return this.insertNewLink;
    },

    getHelpLinks: function() {
        if (!this.helpLinks) {
            this.helpLinks = Ext4.create("Ext.button.Button", {
                text: 'quick help',
                cls: 'labkey-text-link labkey-finder-help',
                componentCls: 'labkey-finder-help',
                scope: this,
                handler: function() {
                    this.startTutorial();
                }
            });
        }
        return this.helpLinks;
    },

    onClearAllFilters: function() {
        this.getSearchBox().setValue("");
    },

    onSearchTermsChanged: function(value) {
        this.fireEvent("searchTermsChanged", value);
    },

    onSubsetChanged: function(value) {
        this.fireEvent("subsetChanged", value.data.id);
    },

    startTutorial: function() {
        this.registerTutorial();
        LABKEY.help.Tour.show("LABKEY.tour.dataFinder." + this.objectName);
        return false;
    },

    // TODO this is not very extensible.  Consider other options
    registerTutorial: function() {
        var $=$||jQuery;

        LABKEY.help.Tour.register({
            id: "LABKEY.tour.dataFinder." + this.objectName,
            steps: [
                {
                    target: $('.labkey-wp-body')[0],
                    title: "Data Finder",
                    content: "Welcome to the Data Finder. A tool for searching, accessing and combining data across studies.",
                    placement: "top",
                    showNextButton: true
                },{
                    target: this.objectName.toLowerCase() + "panel",
                    title: this.objectName + " Panel",
                    content: "This area contains short descriptions of the " + (this.objectName == "Study" ? "studies/datasets" : "publications") + " that match the selected criteria.",
                    placement: "top",
                    showNextButton: true,
                    showPrevButton: true
                },{
                    target: this.objectName.toLowerCase() + "SelectionPanel",
                    title: "Summary",
                    content: "This summary area indicates how many " + (this.objectName == "Study" ? "subjects and studies" : "publications and studies") + " match the selected criteria.",
                    placement: "right",
                    showNextButton: true,
                    showPrevButton: true
                },{
                    target: $('.labkey-' + this.objectName.toLowerCase() + '-facets')[0],
                    title: "Filters",
                    content: "This is where filters are selected and applied. The numbers (also represented as the length of the gray bars) represent how many " + (this.objectName == "Study" ? "subjects" : "publications") + " will match the search if this filter is added.",
                    placement: "right",
                    showNextButton: this.showSearch,
                    showPrevButton: true
                },
                {
                    target: "searchTerms",
                    title: "Quick Search",
                    content: "Enter terms of interest to search study and data descriptions. This will find matches within the selection of filtered studies/datasets.",
                    placement: "right",
                    yOffset: -25,
                    showPrevButton: true
                }
            ]
        });
    }

});