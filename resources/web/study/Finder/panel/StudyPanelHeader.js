Ext4.define("LABKEY.study.panel.StudyPanelHeader", {

    extend: 'Ext.Container',

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    showHelpLinks: true,

    searchMessage: "",

    dataModuleName: 'study',

    bubbleEvents: [
        "studySubsetChanged",
        "searchTermsChanged"
    ],


    initComponent: function() {

        this.studySubsets = Ext4.create('LABKEY.study.store.StudySubsets', {
            dataModuleName : this.dataModuleName,
            objectName: this.objectName
        });
        this.items = [];
        var searchBox = this.getSearchBox();
        if (searchBox)
            this.items.push(searchBox);
        if (this.getStudySubsetMenu())
            this.items.push(this.getStudySubsetMenu());
        if (this.showHelpLinks)
            this.items.push(this.getHelpLinks());

        this.studySubsets.on(
                'load', function(store) {
                    this.getStudySubsetMenu().setValue(store.defaultValue);
                    var objectStore = Ext4.getStore(this.objectName);
                    if (objectStore && store.defaultValue)
                        objectStore.selectedSubset = store.defaultValue.data.id;
                    if (store.count() > 1)
                        this.getStudySubsetMenu().show();
                },
                this
        );

        this.callParent();
    },

    getSearchBox : function() {
        if (!this.searchBox && this.showSearch) {
            this.searchBox = Ext4.create('Ext.form.field.Text', {
                emptyText:'Studies',
                cls: 'labkey-search-box',
                fieldLabel: '<i class="fa fa-search"></i>',
                labelWidth: "10px",
                labelSeparator: '',
                disabled: !this.showSearch,
                hidden: !this.showSearch,
                fieldCls: 'labkey-search-box',
                id: 'searchTerms',
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

    getStudySubsetMenu: function() {
        if (!this.studySubsetMenu) {
            this.studySubsetMenu = Ext4.create('Ext.form.ComboBox', {
                store: this.studySubsets,
                name : 'studySubsetSelect',
                queryMode: 'local',
                valueField: 'id',
                displayField: 'name',
                hidden: true,
                value: this.studySubsets.defaultValue,
                cls: 'labkey-study-search',
                multiSelect: false,
                listeners: {
                    scope: this,
                    'select': function(field, newValue, oldValue, eOpts) {
                        this.onSubsetChanged(newValue[0])
                    },
                    'render': function(eOpts) {
                        if (this.studySubsets.defaultValue)
                            this.onSubsetChanged(this.studySubsets.defaultValue)
                    }
                }
            })
        }
        return this.studySubsetMenu;
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

    onSearchTermsChanged: function(value) {
        console.log("Search terms changed to ", value);
        this.fireEvent("searchTermsChanged", value);
    },

    onSubsetChanged: function(value) {
        this.fireEvent("studySubsetChanged", value.data.id);
    },

    startTutorial: function() {
        this.registerTutorial();
        LABKEY.help.Tour.show("LABKEY.tour.dataFinder." + this.objectName);
        return false;
    },

    // TODO this is not very extensible.  Consider other options
    registerTutorial: function() {
        var $=$||jQuery;

        var index = this.objectName == "Study" ? 0 : 1;
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
                    target: $('.labkey-facet-summary')[index],
                    title: "Summary",
                    content: "This summary area indicates how many " + (this.objectName == "Study" ? "subjects and studies" : "publications and studies") + " match the selected criteria.",
                    placement: "right",
                    showNextButton: true,
                    showPrevButton: true
                },{
                    target: $('.labkey-study-facets')[index],
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