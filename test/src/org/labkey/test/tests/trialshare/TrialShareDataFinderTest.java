/*
 * Copyright (c) 2015 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.labkey.test.tests.trialshare;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.TestFileUtils;
import org.labkey.test.TestTimeoutException;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.components.study.StudyOverviewWebPart;
import org.labkey.test.components.trialshare.PublicationDetailPanel;
import org.labkey.test.components.trialshare.StudySummaryWindow;
import org.labkey.test.pages.study.ManageParticipantGroupsPage;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.util.APIContainerHelper;
import org.labkey.test.util.AbstractContainerHelper;
import org.labkey.test.util.DataRegionTable;
import org.labkey.test.util.ListHelper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.PortalHelper;
import org.labkey.test.util.ReadOnlyTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@Category({Git.class})
public class TrialShareDataFinderTest extends BaseWebDriverTest implements ReadOnlyTest
{
    private static final String MODULE_NAME = "TrialShare";
    private static final String WEB_PART_NAME = "TrialShare Data Finder";
    private static File listArchive = TestFileUtils.getSampleData("DataFinder.lists.zip");

    private static final Map<String, Set<String>> studySubsets = new HashMap<>();
    static {

        Set<String> operationalSet = new HashSet<>();
        studySubsets.put("Operational", operationalSet);
        operationalSet.add("SACHS"); operationalSet.add("IL2-RAPA"); operationalSet.add("RAVE"); operationalSet.add("TILT");
        operationalSet.add("AbATE"); operationalSet.add("WISP-R"); operationalSet.add("AWISH"); operationalSet.add("LEAP");
        operationalSet.add("HALT-MS"); operationalSet.add("ACCESS"); operationalSet.add("ACCLAIM"); operationalSet.add("Mixed Chimerism");
        operationalSet.add("RESTARRT"); operationalSet.add("EXIIST"); operationalSet.add("RETAIN"); operationalSet.add("GRASS");
        operationalSet.add("T1DAL"); operationalSet.add("TAKE"); operationalSet.add("LEAP-ON"); operationalSet.add("IMPACT");
        operationalSet.add("AVATARS"); operationalSet.add("ACCEPTOR"); operationalSet.add("CATNIP"); operationalSet.add("PAUSE");
        operationalSet.add("FACTOR"); operationalSet.add("ARTIST"); operationalSet.add("iWITH");

        Set<String> publicSet = new HashSet<>();
        studySubsets.put("Public", publicSet);
        publicSet.add("DIAMOND"); publicSet.add("Shapiro"); publicSet.add("Khoury"); publicSet.add("Herold II");
        publicSet.add("Orban"); publicSet.add("Knechtle"); publicSet.add("Casale"); publicSet.add("STAYCIS");
        publicSet.add("Miller-Burke"); publicSet.add("Vincenti"); publicSet.add("GPAC"); publicSet.add("START");
    };

    private static Set<String> loadedStudies = new HashSet<>();
    static {
        loadedStudies.add("Casale");
        loadedStudies.add("WISP-R");
    }

    @Override
    protected void doCleanup(boolean afterTest) throws TestTimeoutException
    {
        _containerHelper.deleteProject(getProjectName(), afterTest);
    }

    @BeforeClass
    public static void initTest()
    {
        TrialShareDataFinderTest init = (TrialShareDataFinderTest)getCurrentTest();

        if (init.needsSetup())
            init.setUpProject();
    }

    @Override
    protected BrowserType bestBrowser()
    {
        return BrowserType.CHROME;
    }

    @Override
    protected String getProjectName()
    {
        return "TrialShareDataFinderTest Project";
    }

    @Override
    public List<String> getAssociatedModules()
    {
        return Collections.singletonList("TrialShare");
    }

    @Override
    public boolean needsSetup()
    {
        try
        {
            return HttpStatus.SC_NOT_FOUND == WebTestHelper.getHttpGetResponse(WebTestHelper.getBaseURL() + WebTestHelper.buildURL("project", getProjectName(), "begin"));
        }
        catch (IOException fail)
        {
            return true;
        }
    }

    private void setUpProject()
    {
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);

        containerHelper.createProject(getProjectName(), "Custom");
        containerHelper.enableModule(MODULE_NAME);
        ListHelper listHelper = new ListHelper(this);
        listHelper.importListArchive(listArchive);
        goToProjectHome();
        new PortalHelper(this).addWebPart(WEB_PART_NAME);

        for (String studyAccession : loadedStudies)
        {
            File studyArchive = TestFileUtils.getSampleData(studyAccession + ".folder.zip");
            containerHelper.createSubfolder(getProjectName(), studyAccession, "Study");
            importStudyFromZip(studyArchive, true, true);
        }
    }

    @Before
    public void preTest()
    {
        goToProjectHome();
        DataFinderPage finder = new DataFinderPage(this, true);
        finder.clearSearch();
        try
        {
            finder.clearAllFilters();
        }
        catch (NoSuchElementException ignore) {}
        finder.dismissTour();
    }

    @Test
    public void testCounts()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        assertCountsSynced(finder);

        Map<DataFinderPage.Dimension, Integer> studyCounts = finder.getSummaryCounts();

        for (Map.Entry<DataFinderPage.Dimension, Integer> count : studyCounts.entrySet())
        {
            if (count.getKey().getCaption() != null)
                assertNotEquals("No " + count.getKey().getCaption(), 0, count.getValue().intValue());
        }
    }

    @Test
    public void testStudyCards()
    {
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), true);

        List<DataFinderPage.DataCard> studyCards = finder.getDataCards();

        studyCards.get(0).viewStudySummary();
    }

    @Test
    public void testStudySubset()
    {
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), true);

        Set<String> linkedStudyNames = new HashSet<>();
        for (String subset : studySubsets.keySet())
        {
            finder.selectStudySubset(subset);
            List<DataFinderPage.DataCard> studyCards = finder.getDataCards();
            Set<String> studies = new HashSet<>();
            for (DataFinderPage.DataCard studyCard : studyCards)
            {
                studies.add(studyCard.getStudyShortName());
            }
            assertEquals("Wrong study cards for studies", studySubsets.get(subset), studies);
            linkedStudyNames.addAll(getTexts(Locator.tagWithClass("div", "labkey-study-card").withPredicate(Locator.linkWithText("go to study"))
                    .append(Locator.tagWithClass("span", "labkey-study-card-short-name")).findElements(getDriver())));
        }

        assertEquals("Wrong studies have LabKey study links", loadedStudies, linkedStudyNames);
    }

    @Test
    public void testSelection()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        finder.selectStudySubset("Public");

        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        facets.toggleFacet(DataFinderPage.Dimension.AGE_GROUP, "Adult");
        facets.toggleFacet(DataFinderPage.Dimension.PHASE, "Phase 1");

        assertCountsSynced(finder);

        facets.clearFilter(DataFinderPage.Dimension.PHASE);

        assertEquals("Clearing Phase filters did not remove selection",  Collections.emptyList(), facets.getSelectedMembers(DataFinderPage.Dimension.PHASE));

        // re-select
        facets.toggleFacet(DataFinderPage.Dimension.PHASE, "Phase 1");
        // deselect
        facets.toggleFacet(DataFinderPage.Dimension.AGE_GROUP, "Adult");
        assertEquals("Clearing selection did not remove selection", Collections.emptyList(), facets.getSelectedMembers(DataFinderPage.Dimension.AGE_GROUP));
        assertEquals("Clearing selection removed other filter", Collections.singletonList("Phase 1"), facets.getSelectedMembers(DataFinderPage.Dimension.PHASE));

        finder.clearAllFilters();
        assertEquals("Clearing all filters didn't clear selection", Collections.emptyList(), facets.getSelectedMembers(DataFinderPage.Dimension.PHASE));

        assertCountsSynced(finder);
    }

    @Test
    public void testSelectingEmptyMeasure()
    {
        Map<DataFinderPage.Dimension, Integer> expectedCounts = new HashMap<>();
        expectedCounts.put(DataFinderPage.Dimension.STUDIES, 0);
        expectedCounts.put(DataFinderPage.Dimension.SUBJECTS, 0);

        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), true);
        finder.selectStudySubset("Operational");

        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        facets.toggleFacet(DataFinderPage.Dimension.ASSAY, "ELISA");


        List<DataFinderPage.DataCard> filteredStudyCards = finder.getDataCards();
        assertEquals("Study cards visible after selection", 0, filteredStudyCards.size());

        Map<DataFinderPage.Dimension, Integer> filteredSummaryCounts = finder.getSummaryCounts();
        assertEquals("Wrong counts after selecting empty measure", expectedCounts, filteredSummaryCounts);

        for (DataFinderPage.Dimension dimension : DataFinderPage.Dimension.values())
        {
            if (dimension.getHierarchyName() != null)
            {
                Map<String, Integer> memberCounts = facets.getMemberCounts(dimension);
                for (Map.Entry<String, Integer> memberCount : memberCounts.entrySet())
                {
                    assertEquals("Wrong counts for member " + memberCount.getKey() + " of dimension " + dimension + " after selecting empty measure", 0, memberCount.getValue().intValue());
                }
            }
        }
    }

    @Test
    @Ignore("Search not implemented")
    public void testSearch()
    {
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), true);
        finder.selectStudySubset("Operational");

        List<DataFinderPage.DataCard> studyCards = finder.getDataCards();
        String searchString = studyCards.get(0).getStudyAccession();

        finder.studySearch(searchString);

        shortWait().until(ExpectedConditions.stalenessOf(studyCards.get(1).getCardElement()));
        studyCards = finder.getDataCards();

        assertEquals("Wrong number of studies after search", 1, studyCards.size());

        assertCountsSynced(finder);
    }

    @Test
    public void testStudySummaryWindow()
    {
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), true);

        DataFinderPage.DataCard studyCard = finder.getDataCards().get(0);

        StudySummaryWindow summaryWindow = studyCard.viewStudySummary();

        assertEquals("Study card does not match summary (Accession)", studyCard.getStudyAccession().toLowerCase(), summaryWindow.getAccession().toLowerCase());
        assertEquals("Study card does not match summary (Short Name)", studyCard.getStudyShortName().toLowerCase(), summaryWindow.getShortName().toLowerCase());
        assertEquals("Study card does not match summary (Title)", studyCard.getTitle().toUpperCase(), summaryWindow.getTitle().toUpperCase());
//        String cardPI = studyCard.getStudyPI();
//        String summaryPI = summaryWindow.getStudyPI();
//        assertTrue("Study card does not match summary (studyPI)", summaryPI.contains(cardPI));

        summaryWindow.closeWindow();
    }

    @Test
    @Ignore("Participant counts are not expected to match for this data")
    public void testStudyParticipantCounts()
    {
        Map<String, Integer> finderParticipantCounts = new HashMap<>();
        Map<String, Integer> studyParticipantCounts = new HashMap<>();

        DataFinderPage finder = new DataFinderPage(this, true);
        for (String studyShortName : loadedStudies)
        {
            finder.studySearch(studyShortName);
            finderParticipantCounts.put(studyShortName, finder.getSummaryCounts().get(DataFinderPage.Dimension.SUBJECTS));
        }

        for (String studyShortName : loadedStudies)
        {
            clickFolder(studyShortName);
            StudyOverviewWebPart studyOverview = new StudyOverviewWebPart(this);
            studyParticipantCounts.put(studyShortName, studyOverview.getParticipantCount());
        }

        assertEquals("Participant counts in study finder don't match LabKey studies", finderParticipantCounts, studyParticipantCounts);
    }

    @Test
    public void testStudyCardStudyLinks()
    {
        Set<String> foundNames = new HashSet<>();
        for (String name :  loadedStudies)
        {
            DataFinderPage finder = new DataFinderPage(this, true);
            if (studySubsets.get("Operational").contains(name))
                finder.selectStudySubset("Operational");
            else
                finder.selectStudySubset("Public");
            for (DataFinderPage.DataCard studyCard : finder.getDataCards())
            {
                if (studyCard.getStudyShortName().equals(name))
                {
                    foundNames.add(name);
                    studyCard.clickGoToStudy();
                    WebElement title = Locator.css(".labkey-folder-title > a").waitForElement(shortWait());
                    Assert.assertTrue("Study card " + name + " linked to wrong study", title.getText().contains(name));
                    goBack();
                    break; // we've found it so we don't need to look further
                }
            }
        }
        assertEquals("Didn't find all studies", loadedStudies, foundNames);
    }

    @Test
    @Ignore("Session storage not yet in use")
    public void testNavigationDoesNotRemoveFinderFilter()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetsGrid = finder.getFacetsGrid();
        facetsGrid.toggleFacet(DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy");


        Map<DataFinderPage.Dimension, List<String>> selections = finder.getFacetsGrid().getSelectedMembers();
        clickTab("Manage");
        clickTab("Overview");
        assertEquals("Navigation cleared study finder filter", selections, finder.getFacetsGrid().getSelectedMembers());
    }

    @Test
    @Ignore("Session storage not yet in use")
    public void testRefreshDoesNotRemoveFinderFilter()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetsGrid = finder.getFacetsGrid();
        facetsGrid.toggleFacet(DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy");

        Map<DataFinderPage.Dimension, List<String>> selections = finder.getFacetsGrid().getSelectedMembers();
        refresh();
        assertEquals("'Refresh' cleared study finder filter", selections, finder.getFacetsGrid().getSelectedMembers());
    }

    @Test
    @Ignore("Session storage not yet in use")
    public void testBackDoesNotRemoveFinderFilter()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        facetGrid.toggleFacet(DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy");

        Map<DataFinderPage.Dimension, List<String>> selections = finder.getFacetsGrid().getSelectedMembers();
        clickTab("Manage");
        goBack();
        assertEquals("'Back' cleared study finder filter", selections, finder.getFacetsGrid().getSelectedMembers());
    }

    @Test
    @Ignore("Session storage not yet in use")
    public void testFinderWebPartAndActionShareFilter()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        facetGrid.toggleFacet(DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy");

        Map<DataFinderPage.Dimension, List<String>> selections = finder.getFacetsGrid().getSelectedMembers();
        DataFinderPage.goDirectlyToPage(this, getProjectName(), true);
        assertEquals("WebPart study finder filter didn't get applied", selections, finder.getFacetsGrid().getSelectedMembers());
    }



    @Test
    @Ignore("Not yet implemented")
    public void testGroupSaveAndLoad()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        finder.selectStudySubset("Operational");
        finder.clearAllFilters();
        assertEquals("Group label not as expected", "Unsaved Group", finder.getGroupLabel());

        Map<DataFinderPage.Dimension, List<String>> selections = new HashMap<>();
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        facetGrid.toggleFacet(DataFinderPage.Dimension.AGE_GROUP, "Adult");
        facetGrid.toggleFacet(DataFinderPage.Dimension.CONDITION, "Acute Kidney Injury");

        selections.put(DataFinderPage.Dimension.AGE_GROUP, Collections.singletonList("Adult"));
        selections.put(DataFinderPage.Dimension.CONDITION, Collections.singletonList("Acute Kidney Injury"));

        Map<DataFinderPage.Dimension, Integer> summaryCounts = finder.getSummaryCounts();

        // click on "Save" menu and assert "Save" is not active then assert "Save as" is active
        DataFinderPage.GroupMenu saveMenu = finder.getMenu(DataFinderPage.Locators.saveMenu);
        saveMenu.toggleMenu();
        Assert.assertEquals("Unexpected number of inactive options", 1, saveMenu.getInactiveOptions().size());
        Assert.assertTrue("'Save' option is not an inactive menu option but should be", saveMenu.getInactiveOptions().contains("Save"));

        Assert.assertEquals("Unexpected number of active options", 1, saveMenu.getActiveOptions().size());
        Assert.assertTrue("'Save as' option is not active but should be", saveMenu.getActiveOptions().contains("Save As"));

        String filterName = "testGroupSaveAndLoad" + System.currentTimeMillis();
        saveMenu.chooseOption("Save As", false);
        // assert that popup has the proper number of Selected Studies and Subjects
        DataRegionTable subjectData = new DataRegionTable("demoDataRegion", this);
        Assert.assertEquals("Subject counts on save group window differ from those on data finder", summaryCounts.get(DataFinderPage.Dimension.SUBJECTS).intValue(), subjectData.getDataRowCount());
        finder.saveGroup(filterName);

        assertEquals("Group label not as expected", "Saved group: " + filterName, finder.getGroupLabel());

        finder.clearAllFilters();
        //load group with test name
        DataFinderPage.GroupMenu loadMenu = finder.getMenu(DataFinderPage.Locators.loadMenu);
        loadMenu.toggleMenu();
        Assert.assertTrue("Saved group does not appear in load menu", loadMenu.getActiveOptions().contains(filterName));
        loadMenu.chooseOption(filterName, false);
        assertEquals("Group label not as expected", "Saved group: " + filterName, finder.getGroupLabel());

        // assert the selected items are the same and the counts are the same as before.
        assertEquals("Summary counts not as expected after load", summaryCounts, finder.getSummaryCounts());
        assertEquals("Selected items not as expected after load", selections, finder.getFacetsGrid().getSelectedMembers());
        // assert that "Save" is now active in the menu
        saveMenu = finder.getMenu(DataFinderPage.Locators.saveMenu);
        saveMenu.toggleMenu();
        Assert.assertTrue("'Save' option is not an active menu option but should be", saveMenu.getActiveOptions().contains("Save"));
        saveMenu.toggleMenu(); // close the menu

        // Choose another dimension and save the summary counts
        log("selecting an Assay filter");
        facetGrid.toggleFacet(DataFinderPage.Dimension.ASSAY, "FCM");
        selections.put(DataFinderPage.Dimension.ASSAY, Collections.singletonList("FCM"));
        summaryCounts = finder.getSummaryCounts();
        log("Selections is now " + selections);
        assertEquals("Selected items not as expected after assay selection", selections, finder.getFacetsGrid().getSelectedMembers());

        // Save the filter
        saveMenu = finder.getMenu(DataFinderPage.Locators.saveMenu);
        saveMenu.toggleMenu();
        saveMenu.chooseOption("Save", true);
        sleep(1000); // Hack!  This seems necessary to give time for saving the filter before loading it again.  Waiting for signals doesn't seem to work...

        finder.clearAllFilters();

        // Load the filter
        loadMenu = finder.getMenu(DataFinderPage.Locators.loadMenu);
        loadMenu.toggleMenu();
        Assert.assertTrue("Saved filter does not appear in menu", loadMenu.getActiveOptions().contains(filterName));
        loadMenu.chooseOption(filterName, true);

        // assert that the selections are as expected.
        assertEquals("Summary counts not as expected after load", summaryCounts, finder.getSummaryCounts());
        assertEquals("Selected items not as expected after load", selections, finder.getFacetsGrid().getSelectedMembers());

        // manage group and delete the group that was created
        DataFinderPage.GroupMenu manageMenu = finder.getMenu(DataFinderPage.Locators.manageMenu);
        manageMenu.toggleMenu();
        manageMenu.chooseOption("Manage Groups", false);
        waitForText("Manage Participant Groups");
        ManageParticipantGroupsPage managePage = new ManageParticipantGroupsPage(this);
        managePage.selectGroup(filterName);
        Assert.assertTrue("Delete should be enabled for group created through data finder", managePage.isDeleteEnabled());
        Assert.assertFalse("Edit should not be enabled for group created through data finder", managePage.isEditEnabled());
        managePage.deleteGroup(filterName);
    }

    @Test
    public void testSwitchBetweenStudyAndPublication()
    {
        DataFinderPage finder = new DataFinderPage(this, true);
        log("Start at home.");
        goToProjectHome();
        assertElementVisible(DataFinderPage.Locators.studyFinder);
        log("Click the 'Publications' button.");
        finder.navigateToPublications();
        log("Go back by clicking the 'Studies' button");
        finder.navigateToStudies();
    }

    @Test
    public void testFilterOnStatus()
    {
        String cardTitle = "Circulating markers of vascular injury";
        String cardAuthors = "Monach PA, Tomasson G, Specks U, et al.";
        String cardText;
        Map<String, Integer> counts;

        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), false);

        log("Go to publications and clear any filters that may have been set.");
        finder.navigateToPublications();
        finder.clearAllFilters();

        log("Filter for 'In Progress' only publications.");
        DataFinderPage.FacetGrid fg = finder.getFacetsGrid();
        fg.toggleFacet(DataFinderPage.Dimension.STATUS, "In Progress");

        log("Validate that the number, content and style of the cards is as expected.");
        counts = fg.getMemberCounts(DataFinderPage.Dimension.IN_PROGRESS);
        assertEquals("Expected count after filtering for 'In Progress' was not as expected.", 1, counts.get("In Progress").intValue());

        // I have no idea why assertTextPresent returned false for these strings. The below tests appear to be more reliable.
        cardText = getText(DataFinderPage.Locators.pubCardHighlight);
        assertTrue("Could not find '" + cardTitle + "' on card.", cardText.contains(cardTitle));
        assertTrue("Could not find '" + cardAuthors + "' on card.", cardText.contains(cardAuthors));

        log("Validate that there is only one publication card present and has the correct style.");
        assertElementPresent(DataFinderPage.Locators.pubCard, 1);
        assertElementVisible(DataFinderPage.Locators.pubCardHighlight);
        assertElementPresent(DataFinderPage.Locators.pubCardHighlight, 1);

        log("Remove the 'In Progress' filter, and apply the 'Complete' filter.");
        fg.toggleFacet(DataFinderPage.Dimension.STATUS, "In Progress");
        fg.toggleFacet(DataFinderPage.Dimension.STATUS, "Complete");

        log("Validate counts for 'Complete' publications.");
        counts = fg.getMemberCounts(DataFinderPage.Dimension.COMPLETE);
        assertEquals("Expected count after filtering for 'Complete' was not as expected.", 127, counts.get("Complete").intValue());

        log("Validate that there are no 'In Progress' cards visible.");
        assertElementNotPresent("There is a card with the 'In Progress' style, there should not be.", DataFinderPage.Locators.pubCardHighlight);

    }

    @Test
    public void testPublicationDetail()
    {
        DataFinderPage.FacetGrid fg;
        Map<DataFinderPage.Dimension, Integer> summaryCount;

        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName(), false);

        log("Go to publications and clear any filters that may have been set.");
        finder.navigateToPublications();
        finder.clearAllFilters();

        log("Filter for a publication that has DOI, PMDI and PMCID values.");
        fg = finder.getFacetsGrid();
        fg.toggleFacet(DataFinderPage.Dimension.YEAR, "2011");
        fg.toggleFacet(DataFinderPage.Dimension.PUBLICATION_JOURNAL, "Arthritis Rheum.");

        summaryCount = finder.getSummaryCounts();
        assertTrue("Number of publication cards returned does not match dimension count. Number of cards: " + finder.getDataCards().size() + " Count in dimension: " + summaryCount.get(DataFinderPage.Dimension.PUBLICATIONS), summaryCount.get(DataFinderPage.Dimension.PUBLICATIONS) == finder.getDataCards().size());

        log("Click the 'More Details' link and validate that the detail content is as expected.");
        PublicationDetailPanel detailPanel = finder.getDataCards().get(0).viewDetail();

        assertTrue("Author value not as expected on detail page.", detailPanel.getAuthor().contains("Monach PA, Tomasson G, Specks U, Stone JH, Cuthbertson D"));
        assertTrue("Title value not as expected on detail page.", detailPanel.getTitle().contains("Circulating markers of vascular injury and angiogenesis in Antineutrophil Cytoplasmic Antibody-Associated Vasculitis."));
        assertTrue("Citation value not as expected on detail page.", detailPanel.getCitation().contains("Arthritis Rheum 63: 3988-3997, 2011"));
        assertTrue("PMID value not as expected on detail page.", detailPanel.getPMID().contains("21953143"));
        assertTrue("PMCID value not as expected on detail page.", detailPanel.getPMCID().contains("PMC3227746"));
        assertTrue("DOI value not as expected on detail page.", detailPanel.getDOI().contains("10.1002/ART.30615"));
        assertTrue("Studies value not as expected on detail page.", detailPanel.getStudyShortName().contains("RAVE"));

        log("Validate that the links actually go someplace.");
        detailPanel.clickDOI();
        switchToWindow(1);
        assertTrue("URL of opened windows does not go where expected.", getURL().getHost().contains("onlinelibrary.wiley.com"));
        // Close the external window.

        getDriver().close();
        switchToMainWindow();

        detailPanel.closeWindow();

        log("Go to another publication that doesn't have the same type of detail.");
        finder.clearAllFilters();
        fg.toggleFacet(DataFinderPage.Dimension.PUB_CONDITION, "Microscopic Polyangiitis");
        fg.toggleFacet(DataFinderPage.Dimension.PUB_ASSAY, "FCM");
        fg.toggleFacet(DataFinderPage.Dimension.PUB_STUDY, "RAVE");
        fg.toggleFacet(DataFinderPage.Dimension.PUB_THERAPEUTIC_AREA, "Autoimmune");
        fg.toggleFacet(DataFinderPage.Dimension.PUBLICATION_TYPE, "Manuscript");

        summaryCount = finder.getSummaryCounts();
        assertEquals("Number of studies count not as expected.", 2, summaryCount.get(DataFinderPage.Dimension.STUDIES).intValue());

        log("Click the 'More Details' link again, this time validate that the missing values are rendered as expected.");
        detailPanel = finder.getDataCards().get(0).viewDetail();

        assertTrue("Author value not as expected on detail page.", detailPanel.getAuthor().contains("Ytterberg SR, Mueller M, Sejismundo LP, Mieras K, Stone JH."));
        assertTrue("Title value not as expected on detail page.", detailPanel.getTitle().contains("Efficacy of Remission-Induction Regimens for ANCA-Associated Vasculitis"));
        assertTrue("Citation value not as expected on detail page.", detailPanel.getCitation().contains("New Eng J Med 2013; 369:417-427"));
        assertTrue("PMID value not as expected on detail page.", detailPanel.getPMID().contains("23902481"));
        assertTrue("PMCID value not as expected on detail page.", detailPanel.getPMCID().contains(""));
        assertTrue("DOI value not as expected on detail page.", detailPanel.getDOI().contains(""));
        assertTrue("Studies value not as expected on detail page.", detailPanel.getStudyShortName().contains("RAVE"));

        detailPanel.closeWindow();

    }

    @LogMethod(quiet = true)
    private void assertCountsSynced(DataFinderPage finder)
    {
        List<DataFinderPage.DataCard> studyCards = finder.getDataCards();
        Map<DataFinderPage.Dimension, Integer> studyCounts = finder.getSummaryCounts();

        assertEquals("Study count mismatch", studyCards.size(), studyCounts.get(DataFinderPage.Dimension.STUDIES).intValue());
    }

}