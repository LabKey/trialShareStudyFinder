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
            target: ["studypanel"],
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

