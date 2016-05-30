/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.FacetPanelHeader", {
    extend: "Ext.Container",

    layout:  'vbox',

    cls: 'labkey-facet-summary-header labkey-facet-header',

    bubbleEvents: ["clearAllFilters", "finderObjectChanged"],

    // TODO need to hook up the participant group functionality to the menus
    showParticipantGroups: false,

    isGuest : false,

    initComponent: function() {

        this.cubeConfigs = Ext4.getStore("CubeConfigs");

        this.items = [
            this.getHeader()
        ];

        if (this.showParticipantGroups)
        {
            this.items.push(this.getParticipantGroupMenus());
        }

        this.callParent();

    },

    getHeader: function()
    {
        if (!this.header)
        {
            this.header = Ext4.create("Ext.panel.Panel", {
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                width: "100%",
                items: [
                    this.getSummaryLabel(),
                    {
                        // spacer
                        xtype: 'box',
                        autoEl: {
                            tag: 'div'
                        },
                        flex: 10
                    },
                    Ext4.create("Ext.button.Button", {
                        text: 'clear all',
                        cls: 'labkey-clear-all inactive',
                        scope: this,
                        handler: function() {
                            this.fireEvent("clearAllFilters", false);
                        }
                    })
                ]
            });

        }
        return this.header;
    },

    getSummaryLabel: function()
    {
        if (!this.summaryLabel)
        {
            this.summaryLabel = Ext4.create("Ext4.Component", {
                html: "Summary",
                cls: 'labkey-facet-panel-summary'
            });
        }
        return this.summaryLabel;
    },

    getParticipantGroupMenus: function()
    {
        this.currentGroup = {
            id: null,
            label: "Unsaved group"
        };

        this.saveOptions = [
            {
                id: "save",
                label : "Save",
                isActive : true
            },
            {
                id: "saveAs",
                label : "Save As",
                isActive : false
            }
        ];
        if (!this.participantGroupMenus) {
            this.participantGroupMenus = Ext4.create("Ext.Container", {
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                }
            });

            this.participantGroupMenus.items = [];
            this.participantGroupMenus.items.push(
                    {
                        xtype: 'component',
                        html: (this.currentGroup.id != null ? 'Saved Group: ' : '') + this.currentGroup.label
                    }
            );

            this.participantGroupMenus.items.push(
                    {
                        xtype: 'component',
                        html: '<span id="manageMenu"><i class="fa fa-cog"></i></span>'
                    }
            );

            this.participantGroupMenus.items.push(
                    {
                        xtype: 'component',
                        html: '<span id="loadMenu">Load</span>'
                    }
            );

            this.participantGroupMenus.items.push(
                    {
                        xtype: 'component',
                        html: '<span id="saveMenu">Save</span>'
                    }
            )

        }
        return this.participantGroupMenus;
    },


    // TODO to hook up the menus, we'll want to use a mechanism like this
    //getLoadMenuTitle : function () {
    //    if (!this.loadMenuTitle)
    //    {
    //        this.loadMenu = Ext.create('Ext.menu.Menu', {
    //            // NOTE: consider replacing the action class here with a id based-method
    //            cls: 'labkey-dropdown',
    //            showSeparator: false
    //        });
    //
    //        this.groupingMenuTitle = Ext.create('Ext.Component', {
    //            data: {},
    //            tpl: new Ext.XTemplate(
    //                    '<tpl if="showMenu == true">',
    //                    '<span class="menutitle showgroupingmenu">',
    //                    '<span class="label">{text:htmlEncode}</span>',
    //                    '<img class="down-arrow" src="' + LABKEY.contextPath + '/argos/images/argos_dropdown_arrow.png" />',
    //                    '</span>',
    //                    '<tpl else>',
    //                    '<span class="menutitle">',
    //                    '<span class="label">{text:htmlEncode}</span>',
    //                    '</span>',
    //                    '</tpl>'
    //            )
    //        });
    //
    //        this.groupingMenu.on('afterrender', function() {
    //            Ext.iterate(this.availableGroupings, function (group) {
    //                this.groupingMenu.add({value: group, text: this.groupingLookupMap[group].label});
    //            }, this);
    //        }, this);
    //
    //        this.groupingMenu.on('click', function(menu, item) {
    //            this.selectedGrouping = item.value;
    //            this.dirty = true;
    //            this._refresh();
    //        }, this);
    //    }
    //
    //    return this.groupingMenuTitle;
    //},
    //
    //showGroupingMenu : function () {
    //    var boxPos = this.getGroupingMenuTitle().getBox();
    //    this.groupingMenu.showAt([boxPos.x, boxPos.y + boxPos.height + 6]);
    //},
    //
    //getGroupingMenuPanel : function () {
    //    if (!this.groupingMenuPanel)
    //    {
    //        this.groupingMenuPanel = Ext.create('Ext.panel.Panel', {
    //            minWidth : 400,
    //            border : false,
    //            layout : {type: 'hbox', align: 'stretch'},
    //            items :[
    //                this.getChartCategoryBox(),
    //                {
    //                    xtype : 'container',
    //                    minWidth : 200,
    //                    cls : 'groupingmenupanel',
    //                    items : [this.getGroupingMenuTitle()]
    //                }]
    //        });
    //    }
    //
    //    return this.groupingMenuPanel;
    //},
    //
    //updateGroupingMenuTitle : function() {
    //    var showMenu = this.availableGroupings.length > 1;
    //
    //    this.getGroupingMenuTitle().update({
    //        value: this.selectedGrouping,
    //        text: this.selectedGrouping ? this.groupingLookupMap[this.selectedGrouping].label : null,
    //        showMenu: showMenu
    //    });
    //
    //    if (showMenu) {
    //        this.getGroupingMenuTitle().getEl().down('.menutitle').on('click', this.showGroupingMenu, this);
    //    }
    //}
});