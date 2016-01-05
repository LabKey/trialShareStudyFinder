Ext4.define("LABKEY.study.panel.StudyCards", {

    extend: 'Ext.view.View',

    alias: 'widget.labkey-studyCards-panel',

    cls: 'labkey-study-cards',

    layout: 'hbox',

    width: "100%",

    itemSelector: 'div.labkey-study-card',

    autoScroll: true,

    dataModuleName: 'study',

    store : Ext4.create('LABKEY.study.store.Studies', {
        dataModuleName: this.dataModuleName
    }),

    tpl: new Ext4.XTemplate(
        '<div id="studypanel">',
        '   <tpl for=".">',
        '   <tpl if="manuscriptCount + abstractCount &gt; 0">',
        '   <div class="labkey-study-card labkey-publication-highlight">',
        '   <tpl else>',
        '   <div class="labkey-study-card">',
        '   </tpl>',
        '       <span class="labkey-study-card-highlight labkey-study-card-accession">{studyId}</span>',
        '       <tpl if="shortName">',
        '           <span class="labkey-study-card-short-name">{shortName}</span>',
        '       </tpl>',
            '<br>',
        '       <span class="labkey-study-card-highlight labkey-study-card-pi">{investigator}</span>',
        '       <hr class="labkey-study-card-divider">',
        '       <div>',
        '           <a class="labkey-text-link labkey-study-card-summary" title="click for more details">view summary</a>',
        '           <tpl if="url">',
        '           <a class="labkey-text-link labkey-study-card-goto" href="{url}">go to study</a>',
        '           </tpl>',
        '       </div>',
        '       <div class="labkey-study-card-title">{title}</div>',
        '       <div class="labkey-study-card-publications">',
        '       {manuscriptCount:this.displayManuscriptCount}',
        '       {abstractCount:this.displayAbstractCount}',
        '       </div>',
        '   </div>',
        '   </tpl>',
        '</div>',
            {
                displayManuscriptCount : function(count) {
                    if (count == 0)
                        return "";
                    else {
                        var text = '<a class="labkey-text-link labkey-study-card-pub-count">';
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
                        var text = '<a class="labkey-text-link labkey-study-card-pub-count">';
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
            if (event.target.className.includes("labkey-study-card-summary"))
            {
                this.showStudyDetailPopup(record.get("studyId"));
            }
            else if (event.target.className.includes("labkey-study-card-pub-count"))
            {
                this.showStudyManuscriptsPopup(record.get("studyId"));
            }
        }
    },

    showStudyDetailPopup : function(studyId)
    {
        this.hidePopup(this.detailShowing);

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
                url: this.dataModuleName + '-studyDetail.view?_frame=none&detailType=study&studyId=' + studyId
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

    showStudyManuscriptsPopup : function(studyId)
    {
        this.hidePopup(this.manuscriptsShowing);

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
                url: this.dataModuleName + '-studyDetail.view?_frame=none&detailType=publications&studyId=' + studyId
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
        this.getStore().proxy.url = LABKEY.ActionURL.buildURL(this.dataModuleName, "studies.api", LABKEY.containerPath);
        this.getStore().load();
        this.callParent();
    },

    constructor: function(config)
    {
        this.dataModuleName = config.dataModuleName;
        this.callParent(config);
    }
});