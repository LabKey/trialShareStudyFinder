/*
 * Copyright (c) 2015-2016 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
Ext4.define("LABKEY.study.panel.StudyCards", {

    extend: 'Ext.view.View',

    alias: 'widget.labkey-studyCards-panel',

    cls: 'labkey-study-cards',

    layout: 'hbox',

    width: "100%",

    itemSelector: 'div.labkey-study-card',

    autoScroll: true,
    
    loadMask: false,

    dataModuleName: 'study',

    store : Ext4.create('LABKEY.study.store.CubeObjects', {
        storeId: 'Study',
        model: 'LABKEY.study.data.Study',
        autoLoad: false,
        facetSelectedMembers : {}, // initially we indicate that none of the members are selected by facets
        searchSelectedMembers : null, // initially we have no search terms so everything is selected
        selectedSubset : null,
        sorters: [{
            property: 'shortName',
            direction: 'ASC'
        }]
    }),


    tpl: new Ext4.XTemplate(
        '<div id="studypanel">',
        '   <tpl for=".">',
        '      {[this.displayCardHeader(values)]}',
        '       <span class="labkey-study-card-header labkey-study-card-accession">{studyId:htmlEncode}</span>',
        '       <tpl if="shortName">',
        '           <span class="labkey-study-card-short-name">{shortName:htmlEncode}</span>',
        '       </tpl>',
            '<br>',
        '       <span class="labkey-study-card-header labkey-study-card-pi">{investigator:htmlEncode}</span>',
        '       <hr class="labkey-study-card-divider">',
        '       <div>',
        '           <a class="labkey-text-link labkey-study-card-summary" title="click for more details">view summary</a>',
        '           <tpl if="studyAccessList.length &lt; 2">',
        '               <tpl if="url">',
        '           <a class="labkey-text-link labkey-study-card-goto" href="{url}" target="_blank">go to study</a>',
        '               </tpl>',
        '           <tpl elseif="studyAccessList.length &gt; 1">',
        '           <a class="labkey-text-link labkey-study-card-goto labkey-study-card-goto-menu">go to study</a>',
        '           </tpl>',
        '       </div>',
        '       <div class="labkey-study-card-title">{title:htmlEncode}</div>',
        '       <div class="labkey-study-card-publications">',
        '       {manuscriptCount:this.displayManuscriptCount}',
        '       {abstractCount:this.displayAbstractCount}',
        '       </div>',
        '   </div>',
        '   </tpl>',
        '</div>',
            {
                displayCardHeader : function(values) {
                    var cssClass = "labkey-study-card";
                    if (values.isHighlighted)
                        cssClass += " labkey-publication-highlight";
                    if (values.isBorderHighlighted)
                        cssClass += " labkey-study-border-highlight";
                    return '<div class="' + cssClass + '">';
                },

                displayManuscriptCount : function(count) {
                    if (count == 0)
                        return "";
                    else {
                        var text = '<a class="labkey-text-link labkey-study-card-manuscript-count">';
                        if (count == 1)
                            text += "1 manuscript available";
                        else
                            text += count + " manuscripts available";
                        text += '</a>';
                        return text;
                    }
                },
                displayAbstractCount : function(count) {
                    if (count == 0)
                        return "";
                    else {
                        var text = '<a class="labkey-text-link labkey-study-card-abstract-count">';
                        if (count == 1)
                            text += "1 abstract available";
                        else
                            text += count + " abstracts available";
                        text += '</a>';
                        return text;
                    }
                }
            }
    ),

    listeners: {
        itemClick: function(view, record, item, index, event, eOpts) {
            if (event.target.className.indexOf("labkey-study-card-goto-menu") >= 0)
            {
                this.displayStudyLinkChoice(record.get("studyId"), event);
            }
            if (event.target.className.indexOf("labkey-study-card-summary") >= 0)
            {
                this.showStudyDetailPopup(record.get("studyId"));
            }
            else if (event.target.className.indexOf("labkey-study-card-manuscript-count") >= 0)
            {
                this.showStudyManuscriptsPopup(record.get("studyId"), "manuscripts");
            }
            else if (event.target.className.indexOf("labkey-study-card-abstract-count") >= 0)
            {
                this.showStudyManuscriptsPopup(record.get("studyId"), "abstracts");
            }
        }
    },

    showStudyDetailPopup : function(studyId)
    {
        this.hidePopup(this.detailShowing);

        var url = LABKEY.ActionURL.buildURL(this.dataModuleName, 'studyDetail.view', this.cubeContainerPath, {
            _frame: 'none',
            detailType: 'study',
            studyId : studyId
        });
        var detailWindow = Ext4.create('Ext.window.Window', {
            width: 800,
            maxHeight: 600,
            resizable: true,
            layout: 'fit',
            border: false,
            cls: 'labkey-study-detail',
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

    displayStudyLinkChoice : function (studyId, event)
    {
        var study = this.getStore().getById(studyId);

        if (!study)
        {
            console.error("could not find study " + studyId);
            return;
        }
        var studyLinks = study.get("studyAccessList");
        if (studyLinks.length < 1)
            return;

        var studyLinksMenu = Ext4.create('Ext.menu.Menu', {
            cls: 'labkey-study-goto-menu',
            showSeparator: false
        });

        studyLinksMenu.on('click', function(menu, item) {
                   window.open(LABKEY.ActionURL.buildURL("project", 'begin.view', item.value));
                },
                this
        );

        for (var i = 0; i < studyLinks.length; i++) {
            studyLinksMenu.add({
                text: studyLinks[i].displayName ? studyLinks[i].displayName : studyLinks[i].studyContainerPath,
                value: studyLinks[i].studyContainerPath
            });
        }
        studyLinksMenu.showAt(event.xy);
    },

    showStudyManuscriptsPopup : function(studyId, publicationType)
    {
        this.hidePopup(this.manuscriptsShowing);

        var url = LABKEY.ActionURL.buildURL(this.dataModuleName, 'studyDetail.view', this.cubeContainerPath, {
            _frame: 'none',
            detailType: publicationType,
            studyId : studyId
        });
        var detailWindow = Ext4.create('Ext.window.Window', {
            width: 800,
            maxHeight: 600,
            resizable: true,
            layout: 'fit',
            border: false,
            cls: 'labkey-study-detail-manuscripts',
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
        this.manuscriptsShowing = detailWindow;
        this.manuscriptsShowing.show();
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
        this.getStore().proxy.url = LABKEY.ActionURL.buildURL(this.dataModuleName, "studies.api", this.cubeContainerPath);
        this.getStore().load();
        this.callParent();
    }
});