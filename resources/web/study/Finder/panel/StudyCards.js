Ext4.define("LABKEY.study.panel.StudyCards", {

    extend: 'Ext.view.View',

    alias: 'widget.labkey-studyCards-panel',

    cls: 'labkey-study-cards',

    layout: 'hbox',

    width: "100%",

    itemSelector: 'div.labkey-study-card',

    autoScroll: true,

    store : Ext4.create('Ext.data.Store', {
        model: 'LABKEY.study.data.StudyCard',
        autoLoad: true,
        proxy : {
            type: "ajax",
            url:  LABKEY.ActionURL.buildURL("trialshare", "getStudies", LABKEY.containerPath),
            reader: {
                type: 'json',
                root: 'data'
            }
        }
    }),

    tpl: new Ext4.XTemplate(
        '<div id="studypanel">',
        '   <tpl for=".">',
        '   <tpl if="isLoaded">',
        '   <div class="labkey-study-card loaded">',
        '   <tpl else>',
        '   <div class="labkey-study-card">',
        '   </tpl>',
        '       <span class="labkey-study-card-highlight labkey-study-card-accession">{accession}</span>',
        '       <span class="labkey-study-card-highlight labkey-study-card-pi">{investigator}</span>',
        '       <hr class="labkey-study-card-divider">',
        '       <div>',
        '           <a class="labkey-text-link labkey-study-card-summary" title="click for more details">view summary</a>',
        '           <tpl if="isLoaded && url">',
        '           <a class="labkey-text-link labkey-study-card-goto" href="{url}">go to study</a>',
        '           </tpl>',
        '       </div>',
                <!-- TODO this should be labkey-study-card-title -->
        '       <div class="labkey-study-card-description">{title}</div>',
        '   </div>',
        '   </tpl>',
        '</div>'
    ),

    listeners: {
        itemClick: function(view, record, item, index, e, eOpts) {
            console.log("Show study popup for record " , record);
            this.showPopup(record.get("accession"));
        }
    },

    showPopup : function(studyId)
    {
        this.hidePopup();

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
                url: 'trialshare-studyDetail.view?_frame=none&studyId=' + studyId
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

    hidePopup: function()
    {
        if (this.detailShowing)
        {
            this.detailShowing.hide();
            this.detailShowing.destroy();
            this.detailShowing = null;
        }
    }
});