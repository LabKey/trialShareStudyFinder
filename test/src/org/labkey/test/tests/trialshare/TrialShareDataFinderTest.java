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
import org.labkey.test.ModulePropertyValue;
import org.labkey.test.TestFileUtils;
import org.labkey.test.TestTimeoutException;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.components.study.StudyOverviewWebPart;
import org.labkey.test.components.trialshare.StudySummaryWindow;
import org.labkey.test.pages.PermissionsEditor;
import org.labkey.test.pages.study.ManageParticipantGroupsPage;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.pages.trialshare.StudyPropertiesQueryUpdatePage;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@Category({Git.class})
public class TrialShareDataFinderTest extends BaseWebDriverTest implements ReadOnlyTest
{
    private static final String MODULE_NAME = "TrialShare";
    private static final String WEB_PART_NAME = "TrialShare Data Finder";
    private static final String OPERATIONAL_STUDY_NAME = "DataFinderTestOperationalStudy";
    private static final String PUBLIC_STUDY_NAME = "DataFinderTestPublicStudy";
    private static final String EMAIL_EXTENSION = "@datafinder.test";
    private static final String PUBLIC_READER_DISPLAY_NAME = "public_reader";
    private static final String PUBLIC_READER = PUBLIC_READER_DISPLAY_NAME + EMAIL_EXTENSION;
    private static final String CASALE_READER_DISPLAY_NAME = "casale_reader";
    private static final String CASALE_READER = CASALE_READER_DISPLAY_NAME + EMAIL_EXTENSION;
    private static final String WISPR_READER_DISPLAY_NAME = "wispr_reader";
    private static final String WISPR_READER = WISPR_READER_DISPLAY_NAME + EMAIL_EXTENSION;
    private static File listArchive = TestFileUtils.getSampleData("DataFinder.lists.zip");

    private static final String RELOCATED_DATA_FINDER_PROJECT = "RelocatedDataFinder";

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
        loadedStudies.add("DataFinderTestPublicCasale");
        loadedStudies.add("DataFinderTestOperationalWISP-R");
    }

    @Override
    protected void doCleanup(boolean afterTest) throws TestTimeoutException
    {
        _containerHelper.deleteProject(getProjectName(), afterTest);
        _containerHelper.deleteProject(RELOCATED_DATA_FINDER_PROJECT, afterTest);
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
            return HttpStatus.SC_NOT_FOUND == WebTestHelper.getHttpGetResponse(WebTestHelper.buildURL("project", getProjectName(), "begin"));
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
        goToProjectHome();
        ListHelper listHelper = new ListHelper(this);
        listHelper.importListArchive(listArchive);

        for (String studyAccession : loadedStudies)
        {
            createStudy(studyAccession);
        }
        createStudy(PUBLIC_STUDY_NAME);
        createStudy(OPERATIONAL_STUDY_NAME);
        StudyPropertiesQueryUpdatePage queryUpdatePage = new StudyPropertiesQueryUpdatePage(this);
        queryUpdatePage.setStudyContainers(loadedStudies, PUBLIC_STUDY_NAME, OPERATIONAL_STUDY_NAME);
        createUsers();

        List<ModulePropertyValue> propList = new ArrayList<>();
        propList.add(new ModulePropertyValue("TrialShare", "/" + getProjectName(), "DataFinderCubeContainer", getProjectName()));
        setModuleProperties(propList);

        goToProjectHome();
        new PortalHelper(this).addWebPart(WEB_PART_NAME);
    }

    private void createStudy(String name)
    {
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);

        File studyArchive = TestFileUtils.getSampleData(name + ".folder.zip");
        containerHelper.createSubfolder(getProjectName(), name, "Study");
        importStudyFromZip(studyArchive, true, true);
    }

    private void createUsers()
    {
        log("Creating users and setting permisisons");
        goToProjectHome();

        _userHelper.createUser(PUBLIC_READER);
        _userHelper.createUser(CASALE_READER);
        _userHelper.createUser(WISPR_READER);

        PermissionsEditor permissionsEditor = new PermissionsEditor(this);

        goToProjectHome();
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setSiteGroupPermissions("All Site Users", "Reader");

        goToProjectHome();
        openFolderMenu();
        clickFolder("DataFinderTestPublicCasale");
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setUserPermissions(PUBLIC_READER, "Reader");
        permissionsEditor.setUserPermissions(CASALE_READER, "Reader");
        permissionsEditor.setUserPermissions(WISPR_READER, "Reader");

        goToProjectHome();
        openFolderMenu();
        clickFolder(PUBLIC_STUDY_NAME);
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setUserPermissions(PUBLIC_READER, "Reader");
        permissionsEditor.setUserPermissions(WISPR_READER, "Reader");

        goToProjectHome();
        openFolderMenu();
        clickFolder("DataFinderTestOperationalWISP-R");
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setUserPermissions(WISPR_READER, "Reader");

    }

    @Before
    public void preTest()
    {
        goToProjectHome();
        DataFinderPage finder = new DataFinderPage(this);
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
        DataFinderPage finder = new DataFinderPage(this);
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
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName());

        List<DataFinderPage.StudyCard> studyCards = finder.getStudyCards();

        studyCards.get(0).viewSummary();
    }

    @Test
    public void testStudySubset()
    {
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName());

        for (String subset : studySubsets.keySet())
        {
            finder.selectStudySubset(subset);
            List<DataFinderPage.StudyCard> studyCards = finder.getStudyCards();
            Set<String> studies = new HashSet<>();
            for (DataFinderPage.StudyCard studyCard : studyCards)
            {
                studies.add(studyCard.getShortName());
            }
            assertEquals("Wrong study cards for subset " + subset, studySubsets.get(subset), studies);
        }
    }

    @Test
    public void testPublicAccess()
    {
        goToProjectHome();
        impersonate(PUBLIC_READER);
        DataFinderPage finder = new DataFinderPage(this);
        Assert.assertFalse("Public user should not see the subset menu", finder.hasStudySubsetCombo());
        List<DataFinderPage.StudyCard> cards = finder.getStudyCards();
        Assert.assertEquals("Number of studies not as expected", studySubsets.get("Public").size(), cards.size());
        stopImpersonating();
        goToProjectHome();
        Assert.assertTrue("Admin user should see subset menu again", finder.hasStudySubsetCombo());

        impersonate(CASALE_READER);
        Assert.assertFalse("User with access to only Casale study should not see the subset menu", finder.hasStudySubsetCombo());
        cards = finder.getStudyCards();
        Assert.assertEquals("User with access to only Casale study should see only that study", 1, cards.size());
        stopImpersonating();
    }

    @Test
    public void testOperationalAccess()
    {
        goToProjectHome();
        impersonate(WISPR_READER);
        DataFinderPage finder = new DataFinderPage(this);
        Assert.assertTrue("Operational user should see the subset menu", finder.hasStudySubsetCombo());
        finder.selectStudySubset("Operational");
        List<DataFinderPage.StudyCard> cards = finder.getStudyCards();
        Assert.assertEquals("User with access to only WISP-R study should see only that study", 1, cards.size());
    }


    @Test
    public void testDataFinderRelocation()
    {
        log("Test that we can put the data finder in a project other than the one with the cube definition and lists");
        AbstractContainerHelper containerHelper = new APIContainerHelper(this);

        containerHelper.createProject(RELOCATED_DATA_FINDER_PROJECT, "Custom");
        containerHelper.addCreatedProject(RELOCATED_DATA_FINDER_PROJECT);
        containerHelper.enableModule(MODULE_NAME);
        List<ModulePropertyValue> propList = new ArrayList<>();
        propList.add(new ModulePropertyValue("TrialShare", "/" + RELOCATED_DATA_FINDER_PROJECT, "DataFinderCubeContainer", getProjectName()));
        setModuleProperties(propList);

        goToProjectHome(RELOCATED_DATA_FINDER_PROJECT);
        new PortalHelper(this).addWebPart(WEB_PART_NAME);
        DataFinderPage finder = new DataFinderPage(this);
        Assert.assertTrue("Should see the subset dropdown", finder.hasStudySubsetCombo());
        finder.selectStudySubset("Public");
        Assert.assertEquals("Should see all the study cards", 12, finder.getStudyCards().size());
        containerHelper.deleteProject(RELOCATED_DATA_FINDER_PROJECT, false);

    }

    @Test
    public void testSelection()
    {
        DataFinderPage finder = new DataFinderPage(this);
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

        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName());
        finder.selectStudySubset("Operational");

        DataFinderPage.FacetGrid facets = finder.getFacetsGrid();
        facets.toggleFacet(DataFinderPage.Dimension.ASSAY, "ELISA");


        List<DataFinderPage.StudyCard> filteredStudyCards = finder.getStudyCards();
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
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName());
        finder.selectStudySubset("Operational");

        List<DataFinderPage.StudyCard> studyCards = finder.getStudyCards();
        String searchString = studyCards.get(0).getAccession();

        finder.studySearch(searchString);

        shortWait().until(ExpectedConditions.stalenessOf(studyCards.get(1).getCardElement()));
        studyCards = finder.getStudyCards();

        assertEquals("Wrong number of studies after search", 1, studyCards.size());

        assertCountsSynced(finder);
    }

    @Test
    public void testStudySummaryWindow()
    {
        DataFinderPage finder = DataFinderPage.goDirectlyToPage(this, getProjectName());

        DataFinderPage.StudyCard studyCard = finder.getStudyCards().get(0);

        StudySummaryWindow summaryWindow = studyCard.viewSummary();

        assertEquals("Study card does not match summary (Accession)", studyCard.getAccession().toLowerCase(), summaryWindow.getAccession().toLowerCase());
        assertEquals("Study card does not match summary (Short Name)", studyCard.getShortName().toLowerCase(), summaryWindow.getShortName().toLowerCase());
        assertEquals("Study card does not match summary (Title)", studyCard.getTitle().toUpperCase(), summaryWindow.getTitle().toUpperCase());
//        String cardPI = studyCard.getPI();
//        String summaryPI = summaryWindow.getPI();
//        assertTrue("Study card does not match summary (PI)", summaryPI.contains(cardPI));

        summaryWindow.closeWindow();
    }

    @Test
    @Ignore("Participant counts are not expected to match for this data")
    public void testStudyParticipantCounts()
    {
        Map<String, Integer> finderParticipantCounts = new HashMap<>();
        Map<String, Integer> studyParticipantCounts = new HashMap<>();

        DataFinderPage finder = new DataFinderPage(this);
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
            DataFinderPage finder = new DataFinderPage(this);
            if (name.contains("Operational"))
                finder.selectStudySubset("Operational");
            else
                finder.selectStudySubset("Public");
            for (DataFinderPage.StudyCard studyCard : finder.getStudyCards())
            {
                if (name.contains(studyCard.getShortName()))
                {
                    String shortName = studyCard.getShortName();
                    foundNames.add(name);
                    studyCard.clickGoToStudy();
                    WebElement title = Locator.css(".labkey-folder-title > a").waitForElement(shortWait());
                    Assert.assertTrue("Study card " + name + " linked to wrong study", title.getText().contains(shortName));
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
        DataFinderPage finder = new DataFinderPage(this);
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
        DataFinderPage finder = new DataFinderPage(this);
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
        DataFinderPage finder = new DataFinderPage(this);
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
        DataFinderPage finder = new DataFinderPage(this);
        DataFinderPage.FacetGrid facetGrid = finder.getFacetsGrid();
        facetGrid.toggleFacet(DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy");

        Map<DataFinderPage.Dimension, List<String>> selections = finder.getFacetsGrid().getSelectedMembers();
        DataFinderPage.goDirectlyToPage(this, getProjectName());
        assertEquals("WebPart study finder filter didn't get applied", selections, finder.getFacetsGrid().getSelectedMembers());
    }



    @Test
    @Ignore("Not yet implemented")
    public void testGroupSaveAndLoad()
    {
        DataFinderPage finder = new DataFinderPage(this);
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


    @LogMethod(quiet = true)
    private void assertCountsSynced(DataFinderPage finder)
    {
        List<DataFinderPage.StudyCard> studyCards = finder.getStudyCards();
        Map<DataFinderPage.Dimension, Integer> studyCounts = finder.getSummaryCounts();

        assertEquals("Study count mismatch", studyCards.size(), studyCounts.get(DataFinderPage.Dimension.STUDIES).intValue());
    }

}