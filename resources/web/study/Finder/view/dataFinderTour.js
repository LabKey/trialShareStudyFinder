var $=$||jQuery;

LABKEY.help.Tour.register({
    id: "LABKEY.tour.dataFinder",
    steps: [
        {
            target: $('.labkey-wp')[0],
            title: "Data Finder",
            content: "Welcome to the Data Finder. A tool for searching, accessing and combining data across studies.",
            placement: "top",
            showNextButton: true
        },{
            target: "studypanel",
            title: "Study Panel",
            content: "This area contains short descriptions of the studies/datasets that match the selected criteria.",
            placement: "top",
            showNextButton: true,
            showPrevButton: true
        },{
            target: "summaryArea",
            title: "Summary",
            content: "This summary area indicates how many subjects and studies match the selected criteria.",
            placement: "right",
            showNextButton: true,
            showPrevButton: true
        },{
            target: "facetPanel",
            title: "Filters",
            content: "This is where filters are selected and applied. The numbers (also represented as the length of the gray bars) represent how many subjects will match the search if this filter is added.",
            placement: "right",
            showNextButton: true,
            showPrevButton: true
        },{
            target: "searchTerms",
            title: "Quick Search",
            content: "Enter terms of interest to search study and data descriptions. This will find matches within the selection of filtered studies/datasets.",
            placement: "right",
            yOffset: -25,
            showPrevButton: true
        }
        //{
        //    target: 'group_Condition',
        //    title: "Study Attributes",
        //    content: "Select items in this area to find studies of interest.  The gray bars show the number of selected participants.<p/>Try " + (Ext4.isMac ? "Command" : "Ctrl") + "-click to multi-select.",
        //    placement: "right"
        //},
        //{
        //    target: 'searchTerms',
        //    title: "Quick Search",
        //    content: "Enter terms of interest to search study descriptions.",
        //    placement: "right"
        //},
        //{
        //    target: 'summaryArea',
        //    title: "Summary",
        //    content: "Here is a summary of the data in the selected studies. Studies represents the number of studies that contain some participants that match the criteria. Subjects is the number of subjects across all selected studies (including subjects that did not match all attributes).",
        //    placement: "right"
        //},
        //{
        //    target: 'filterArea',
        //    title: "Filter Area",
        //    content: "See and manage your active filters.",
        //    placement: "right"
        //}
    ]
});

