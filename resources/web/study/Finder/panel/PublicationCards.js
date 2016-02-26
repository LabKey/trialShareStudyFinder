/*
 * Copyright (c) 2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.PublicationCards", {

    extend: 'Ext.view.View',

    alias: 'widget.labkey-publicationCards-panel',

    cls: 'labkey-publication-cards',

    layout: 'hbox',

    width: "100%",

    itemSelector: 'div.labkey-publication-card',

    autoScroll: true,

    store : Ext4.create('LABKEY.study.store.Publications', {
        dataModuleName: this.dataModuleName
    }),

    tpl: new Ext4.XTemplate(
            '<div id="publicationpanel">',
            '   <tpl for=".">',
            '   <tpl if="isHighlighted">',
            '   <div class="labkey-publication-card labkey-publication-highlight collapsed">',
            '   <tpl else>',
            '   <div class="labkey-publication-card collapsed">',
            '   </tpl>',
            '       <div class="labkey-publication">',
            '       <span>',
            '           <span id="morePublicationDetails"><i class="fa fa-plus-square"></i></span>',
            '           <span id="lessPublicationDetails"><i class="fa fa-minus-square"></i></span>',
            '       </span>',
            '       <div>',
            '           <div class="labkey-publication-title">{title:htmlEncode}</div>',
            '           <div id="abbreviatedAuthorList" class="labkey-publication-author">{authorAbbrev:htmlEncode}</div>',
            '           <div id="fullAuthorList" class="labkey-publication-author">{author:htmlEncode}</div>',
            '           <div class="labkey-publication-citation">{citation:htmlEncode}</div>',
            '           <div>',
            '           <tpl if="url">',
            '               <a class="labkey-text-link labkey-publication-goto" target="_blank" href="{url}">view document</a>',
            '           </tpl>',
            '           <tpl if="dataUrl">',
            '               <a class="labkey-text-link labkey-study-card-goto" target="_blank" href="{dataUrl}">view data</a>',
            '           </tpl>',
            '           </div>',
            '           <div id="publicationDetails_{id}" class="labkey-publication-detail collapsed"></div>',
            '       </div>',
            '       </div>',
            '   </div>',
            '   </tpl>',
            '</div>'
    ),

    listeners: {
        itemClick: function(view, record, item, index, event, eOpts)
        {
            if (event.target.className.indexOf("fa-plus-square") >= 0)
            {
                this.toggleDetails(record.get("id"), item, true);
            }
            else if (event.target.className.indexOf("fa-minus-square") >= 0)
            {
                this.toggleDetails(record.get("id"), item, false);
            }
        }
    },

    toggleDetails : function(publicationId, item, expand)
    {
        if (expand)
        {
            if (!this.detailsOnPage[publicationId])
            {
                var url = LABKEY.ActionURL.buildURL(this.dataModuleName, "publicationDetails.api", null, {
                    "id": publicationId
                });
                Ext4.Ajax.request({
                    url: url,
                    success: function (response)
                    {
                        var o = Ext4.decode(response.responseText);
                        if (o.success)
                        {
                            this.detailsOnPage[publicationId] = true;
                            Ext4.create("LABKEY.study.panel.PublicationDetails", {
                                data: o.data,
                                renderTo: "publicationDetails_" + publicationId
                            });
                            Ext4.get(Ext4.DomQuery.select('#publicationDetails_' + publicationId)[0]).replaceCls('collapsed', 'expanded');
                            item.className = item.className.replace("collapsed", "expanded");
                        }

                        LABKEY.Utils.signalWebDriverTest('publicationDetailsLoaded');
                    },
                    scope: this
                });
            }
            else
            {
                Ext4.get(Ext4.DomQuery.select('#publicationDetails_' + publicationId)[0]).replaceCls('collapsed', 'expanded');
                item.className = item.className.replace("collapsed", "expanded");
                LABKEY.Utils.signalWebDriverTest('publicationDetailsLoaded');
            }
        }
        else
        {
            Ext4.get(Ext4.DomQuery.select('#publicationDetails_' + publicationId)[0]).replaceCls('expanded', 'collapsed');
            item.className = item.className.replace("expanded", "collapsed");
        }

    },

    showDetailPopup : function(objectId)
    {
        this.hidePopup(this.detailShowing);

        var url = LABKEY.ActionURL.buildURL(this.dataModuleName, 'publicationDetail.view', this.cubeContainerPath, {
            _frame: 'none',
            detailType: 'publication',
            id : objectId
        });
        var detailWindow = Ext4.create('Ext.window.Window', {
            width: 800,
            maxHeight: 600,
            resizable: true,
            layout: 'fit',
            border: false,
            cls: 'labkey-publication-detail',
            autoScroll: true,
            loader: {
                autoLoad: true,
                url: url
            }
        });
        var viewScroll = Ext4.getBody().getScroll();
        var viewSize = Ext4.getBody().getViewSize();
        var region = [viewScroll.left, viewScroll.top, viewScroll.left + viewSize.width, viewScroll.top + viewSize.height];
        var proposedXY = [region[0] + viewSize.width / 2 - 400, region[1] + viewSize.height / 2 - 300];
        proposedXY[1] = Math.max(region[1], Math.min(region[3] - 400, proposedXY[1]));
        detailWindow.setPosition(proposedXY);
        this.detailShowing = detailWindow;
        this.detailShowing.show();
    },


    hidePopup: function(popup)
    {
        if (popup)
        {
            popup.hide();
            popup.destroy();
            popup = null;
        }
    },

    initComponent: function(config)
    {
        this.getStore().proxy.url = LABKEY.ActionURL.buildURL(this.dataModuleName, "publications.api", this.cubeContainerPath);
        this.getStore().load();
        this.detailsOnPage = {}; // map between publication id and boolean to indicate which publications have had details retrieved

        this.callParent();

        this.store.addListener('filterChange',this.onFilterSelectionChanged, this);
    },

    onFilterSelectionChanged: function()
    {
        this.detailsOnPage = {};
    },

    constructor: function(config)
    {
        this.dataModuleName = config.dataModuleName;
        this.cubeContainerPath = config.cubeContainerPath;
        this.callParent(config);
    }
});