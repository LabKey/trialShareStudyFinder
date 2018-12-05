/*
 * Copyright (c) 2016-2018 LabKey Corporation
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

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.Locator;
import org.labkey.test.WebTestHelper;
import org.labkey.test.categories.Git;
import org.labkey.test.components.trialshare.PublicationPanel;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.pages.trialshare.ManageDataPage;
import org.labkey.test.pages.trialshare.PublicationEditPage;
import org.labkey.test.pages.trialshare.PublicationsListHelper;
import org.labkey.test.pages.trialshare.StudiesListHelper;
import org.labkey.test.util.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.labkey.test.pages.trialshare.PublicationEditPage.EMPTY_VALUE;
import static org.labkey.test.pages.trialshare.PublicationEditPage.NOT_EMPTY_VALUE;
import static org.labkey.test.pages.trialshare.PublicationEditPage.TITLE;

@Category({Git.class})
public class ManagePublicationsTest extends DataFinderTestBase
{
    private CubeObjectType _objectType = CubeObjectType.publication;

    private static final String PUBLIC_STUDY_ID = "Casale";
    private static final String OPERATIONAL_STUDY_ID = "WISP-R";
    private static final String PROJECT_NAME = "ManagePublicationTest Project";
    private static final String DATA_PROJECT_NAME = "ManagePublicationsTestData Project";
    private static final String OPERATIONAL_STUDY_SUBFOLDER_NAME = "/" + DATA_PROJECT_NAME + "/" + OPERATIONAL_STUDY_NAME;
    private static final String PUBLIC_STUDY_SUBFOLDER_NAME = "/" + DATA_PROJECT_NAME + "/" + PUBLIC_STUDY_NAME;

    private static final Map<String, Object> EXISTING_PUB_FIELDS = new HashMap<>();
    static
    {
        EXISTING_PUB_FIELDS.put(TITLE,"Efficacy of Remission-Induction Regimens for ANCA-Associated Vasculitis" );
        EXISTING_PUB_FIELDS.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.STATUS, "Complete");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.SUBMISSION_STATUS, "Submitted");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.AUTHOR, NOT_EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.CITATION, "New Eng J Med 2013; 369:417-427");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.YEAR, "2013");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.JOURNAL, "New England Journal of Medicine");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.ABSTRACT, NOT_EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.DOI, EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.PMID, "23902481");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.PMCID, EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.MANUSCRIPT_CONTAINER, PUBLIC_STUDY_SUBFOLDER_NAME);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.PERMISSIONS_CONTAINER, OPERATIONAL_STUDY_SUBFOLDER_NAME);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.KEYWORDS, EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.STUDIES, EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.THERAPEUTIC_AREAS, "Autoimmune");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.LINK1, "https://www.immunetolerance.org/sites/files/Specks_NEJM_2013.pdf");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.DESCRIPTION1, "Paper on immunetolerance.org");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.LINK2, "http://www.ncbi.nlm.nih.gov/pubmed/23902481");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.DESCRIPTION2, "PubMed.gov Citation");
        EXISTING_PUB_FIELDS.put(PublicationEditPage.LINK3, EMPTY_VALUE);
        EXISTING_PUB_FIELDS.put(PublicationEditPage.DESCRIPTION3, EMPTY_VALUE);
    }

    @Nullable
    @Override
    protected String getProjectName()
    {
        return PROJECT_NAME;
    }

    @Override
    public String getDataProjectName() { return DATA_PROJECT_NAME; }

    @Override
    protected void createStudies(String parentProjectName)
    {
        createStudy(parentProjectName, PUBLIC_STUDY_NAME);
        createStudy(parentProjectName, OPERATIONAL_STUDY_NAME);
    }

    @Override
    protected BrowserType bestBrowser()
    {
        return BrowserType.CHROME;
    }

    @Override
    protected void createUsers()
    {
        _userHelper.createUser(PUBLIC_READER);

        makeProjectReadable(getDataProjectName());
        clickFolder(PUBLIC_STUDY_NAME);
        _apiPermissionsHelper.setUserPermissions(PUBLIC_READER, "Reader");
    }

    @Test
    public void testInsertNewDataLinkPermissions()
    {
        log("Checking for insert new data link");
        DataFinderPage dataFinder = goDirectlyToDataFinderPage(getProjectName(), false);
        Assert.assertTrue("Insert New link is not available", dataFinder.canInsertNewData());
        dataFinder.goToInsertNewData();
        switchToWindow(1);
        waitForText("Insert Publication");
        getDriver().close();
        switchToMainWindow();
        log("Impersonating user without insert permission");
        goToProjectHome();
        impersonate(PUBLIC_READER);
        goDirectlyToDataFinderPage(getProjectName(), false);
        Assert.assertFalse("Insert New link should not be available", dataFinder.canManageData());
    }

    @Test
    public void testManageDataLinkPermissions()
    {
        log("Checking for manage data link");
        DataFinderPage dataFinder = goDirectlyToDataFinderPage(getProjectName(), false);
        Assert.assertTrue("Manage Data link is not available", dataFinder.canManageData());
        dataFinder.goToManageData();
        switchToWindow(1);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        Assert.assertTrue("No data shown for publication", manageData.getCount() > 0);
        getDriver().close();
        switchToMainWindow();
        log("Impersonating user without insert permission");
        goToProjectHome();
        impersonate(PUBLIC_READER);
        goDirectlyToDataFinderPage(getProjectName(), false);
        Assert.assertFalse("Manage Data link should not be available", dataFinder.canManageData());
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        assertTextPresent("User does not have permission");
    }

    @Test
    public void testSwitchToStudies()
    {
        goDirectlyToManageDataPage(getProjectName(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        Assert.assertTrue("Should see a link to manage studies", manageData.hasManageStudiesLink());
        manageData.goToManageStudies();
        ManageDataPage manageStudiesData = new ManageDataPage(this, CubeObjectType.study);
        Assert.assertTrue("Should be manage studies view", manageStudiesData.isManageDataView());
    }

    @Test
    public void testGoToInsertNewAndCancel()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
        Assert.assertFalse("Submit button should not be enabled", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(editPage::cancel);
        Assert.assertTrue("Should be manage publications view", manageData.isManageDataView());
    }

    @Test @Ignore("Finding the fields that are display/disabled fields is not yet implemented")
    public void testViewDetails()
    {
        goToProjectHome();
        PublicationsListHelper pubUpdatePage = new PublicationsListHelper(this);
        pubUpdatePage.setPermissionsContainer((String) EXISTING_PUB_FIELDS.get(TITLE), "/" + getProjectName() + "/" + OPERATIONAL_STUDY_NAME, true);
        pubUpdatePage.setManuscriptContainer((String) EXISTING_PUB_FIELDS.get(TITLE), "/" + getProjectName() + "/" + PUBLIC_STUDY_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        // FIXME on the details page, many fields are not form fields, so finding them is more difficult
        manageData.goToEditRecord((String) EXISTING_PUB_FIELDS.get(TITLE));
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
        Map<String, String> unexpectedValues = editPage.compareFormValues(EXISTING_PUB_FIELDS);
        Assert.assertTrue("Found unexpected values: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testInvalidPMCID()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        manageData.goToEditRecord("Quality assessments of un-gated flow cytometry FCS files in a clinical trial setting");
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
        editPage.setTextFormValue("PMCID", "invalid", false);
        Assert.assertFalse("Submit should be disabled when invalid PMCID is input", editPage.isSubmitEnabled());
    }

    @Test
    public void testRequiredFields()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
        Assert.assertFalse("Submit button should not be enabled", editPage.isSubmitEnabled());
        editPage.setTextFormValue("title", "testRequiredFields");
        Assert.assertFalse("Submit button should not be enabled with only title", editPage.isSubmitEnabled());
        editPage.cancel();

        manageData.goToInsertNew();
        editPage.setFormField(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        Assert.assertFalse("Submit button should not be enabled with only publication type", editPage.isSubmitEnabled());
        editPage.cancel();

        manageData.goToInsertNew();
        editPage.setFormField(PublicationEditPage.STATUS, "Complete");
        Assert.assertFalse("Submit button should not be enabled with only status", editPage.isSubmitEnabled());
        editPage.cancel();

        manageData.goToInsertNew();
        editPage.setTextFormValue("title", "testRequiredFields");
        editPage.setFormField(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        Assert.assertFalse("Submit button should not be enabled with title, publication type but no status", editPage.isSubmitEnabled());
        editPage.setFormField(PublicationEditPage.STATUS, "Complete");
        Assert.assertTrue("Submit button should be enabled with all required fields", editPage.isSubmitEnabled());
    }

    @Test
    public void testSaveAndCloseReturnUrl()
    {
        goToProjectHome();
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        finder.clearAllFilters();
        DataFinderPage.DataCard card = finder.getDataCards().get(0);
        PublicationPanel publicationPanel = card.viewDetail();
        publicationPanel.clickEditLink();
        switchToWindow(1);
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
        editPage.saveAndClose("Data Finder");
        sleep(1000); // yes, it's a hack.  But everyone needs sleep at some point.
        // we should go back to the finder page and have the publication tab active
        Assert.assertEquals("Wrong tab on finder active after save and close", "Publications",  finder.getSelectedFinderObject());
        getDriver().close();
        switchToMainWindow();
    }

    @Test
    public void testCancelReturnUrl()
    {
        goToProjectHome();
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        finder.clearAllFilters();
        DataFinderPage.DataCard card = finder.getDataCards().get(0);
        PublicationPanel publicationPanel = card.viewDetail();
        publicationPanel.clickEditLink();
        switchToWindow(1);
        {
            PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
            editPage.cancel();
            // we should go back to the finder page and have the publication tab active
            Assert.assertEquals("Wrong tab on finder active after cancel", "Publications", new DataFinderPage(this, false).getSelectedFinderObject());
            getDriver().close();
        }
        switchToMainWindow();
    }

    @Test
    public void testInsertWithAllFields()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> newFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        int count = manageData.getCount();
        newFields.put(PublicationEditPage.TITLE, "testInsertWithAllFields_" + count);
        newFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        newFields.put(PublicationEditPage.STATUS, "In Progress");
        newFields.put(PublicationEditPage.SUBMISSION_STATUS, "Submitted");
        newFields.put(PublicationEditPage.AUTHOR, "test1, test2");
        newFields.put(PublicationEditPage.CITATION, "test publications: v24, no 15");
        newFields.put(PublicationEditPage.YEAR, "2016");
        newFields.put(PublicationEditPage.JOURNAL, "Test Publications");
        // TODO this field is not editable like a text field.
//        newFields.put(PublicationEditPage.ABSTRACT, "<b>We are concrete.</b>");
        newFields.put(PublicationEditPage.DOI, "doi:123/445");
        newFields.put(PublicationEditPage.PMID, "1212");
        newFields.put(PublicationEditPage.PMCID, "PMC1411");
        newFields.put(PublicationEditPage.MANUSCRIPT_CONTAINER, PUBLIC_STUDY_SUBFOLDER_NAME);
        newFields.put(PublicationEditPage.PERMISSIONS_CONTAINER, OPERATIONAL_STUDY_SUBFOLDER_NAME);
        newFields.put(PublicationEditPage.KEYWORDS, "key words keywords");
        newFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID});
        newFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune"});
        newFields.put(PublicationEditPage.LINK1, "http://link/to.this");
        newFields.put(PublicationEditPage.DESCRIPTION1, "Link 1 Description");
        newFields.put(PublicationEditPage.LINK2, "http://also/link/to.this");
        newFields.put(PublicationEditPage.DESCRIPTION2, "Link 2 Description");
        newFields.put(PublicationEditPage.LINK3, "http://finally/link/to.this");
        newFields.put(PublicationEditPage.DESCRIPTION3, "Link 3 Description");

        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());

        editPage.setFormFields(newFields, false);
        editPage.save();
        Map<String, String> unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted publication: " + unexpectedValues, unexpectedValues.isEmpty());

        newFields.put(PublicationEditPage.TITLE, "testInsertWithAllFields_" + count + "saveAndClose");
        newFields.remove(PublicationEditPage.STUDIES);
        newFields.remove(PublicationEditPage.THERAPEUTIC_AREAS);
        editPage.setFormFields(newFields, false);
        editPage.saveAndClose("Manage");
        manageData.goToEditRecord((String) newFields.get(TITLE));
        unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted publication: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testCountsAfterInsertAndEdit()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);
        goToProjectHome();
        DataFinderPage finder = goDirectlyToDataFinderPage(getProjectName(), false);
        DataFinderPage.FacetGrid fg = finder.getFacetsGrid();
        finder.clearAllFilters();
        Map<String, Map<String, DataFinderPage.FacetGrid.MemberCount>> beforeCounts = fg.getAllMemberCounts();

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> newFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        int count = manageData.getCount();
        newFields.put(PublicationEditPage.TITLE, "testCountsAfterInsertAndEdit_" + count);
        newFields.put(PublicationEditPage.STATUS, "In Progress");
        newFields.put(PublicationEditPage.SUBMISSION_STATUS, "Submitted");
        newFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        newFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune"});
        newFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID});
        newFields.put(PublicationEditPage.YEAR, "2016");

        newFields.put(PublicationEditPage.MANUSCRIPT_CONTAINER, PUBLIC_STUDY_SUBFOLDER_NAME);
        newFields.put(PublicationEditPage.PERMISSIONS_CONTAINER, OPERATIONAL_STUDY_SUBFOLDER_NAME);

        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());

        editPage.setFormFields(newFields, true);
        editPage.saveAndClose("Manage");

        goToProjectHome();
        goDirectlyToDataFinderPage(getProjectName(), false);
        finder.clearAllFilters();
        Map<String, Map<String, DataFinderPage.FacetGrid.MemberCount>> afterInsertCounts = fg.getAllMemberCounts();
        assertEquals("Count for 'In Progress' not updated", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.STATUS, "In Progress")+1, fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.STATUS, "In Progress"));
        assertEquals("Count for 'Submitted' not updated", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.SUBMISISON_STATUS, "Submitted")+1, fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.SUBMISISON_STATUS, "Submitted"));
        assertEquals("Count for 'Manuscript' not updated", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.PUBLICATION_TYPE, "Manuscript")+1, fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.PUBLICATION_TYPE, "Manuscript"));
        assertEquals("Count for 'Autoimmune' not updated", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.THERAPEUTIC_AREA, "Autoimmune")+1, fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.THERAPEUTIC_AREA, "Autoimmune"));
        assertEquals("Count for '" + PUBLIC_STUDY_NAME + "' not updated", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.PUB_STUDY, PUBLIC_STUDY_ID)+1, fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.PUB_STUDY, PUBLIC_STUDY_ID));
        assertEquals("Count for '2016' not updated", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.YEAR, "2016")+1, fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.YEAR, "2016"));


        // Now edit the above fields to have different values and check counts again.

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        manageData.goToEditRecord((String) newFields.get(TITLE));
        newFields.remove(PublicationEditPage.TITLE);
        // not updating status since the default data has no other in-progress publications and making them all "complete" removes status and submission status
        newFields.put(PublicationEditPage.SUBMISSION_STATUS, "Draft");
        newFields.put(PublicationEditPage.PUBLICATION_TYPE, "Abstract");
        // for these two multi-select fields, the items that are selected will be deselected and ones not selected will be selected
        newFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune", "Allergy"});
        newFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID, OPERATIONAL_STUDY_ID});
        newFields.put(PublicationEditPage.YEAR, "2014");

        editPage.setFormFields(newFields, false);
        editPage.saveAndClose("Manage");
        goToProjectHome();
        finder.selectDataFinderObject("Publications");
        finder.clearAllFilters();
        Map<String, Map<String, DataFinderPage.FacetGrid.MemberCount>> afterEditCounts = fg.getAllMemberCounts();
        assertEquals("Count for 'In Progress' updated but should not have been", fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.STATUS, "In Progress"), fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.STATUS, "In Progress"));
        assertEquals("Count for 'Submitted' not updated to original value", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.SUBMISISON_STATUS, "Submitted"), fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.SUBMISISON_STATUS, "Submitted"));
        assertEquals("Count for 'Draft' not updated", fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.SUBMISISON_STATUS, "Draft")+1, fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.SUBMISISON_STATUS, "Draft"));
        assertEquals("Count for 'Manuscript' not updated to original value", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.PUBLICATION_TYPE, "Manuscript"), fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.PUBLICATION_TYPE, "Manuscript"));
        assertEquals("Count for 'Abstract' not updated", fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.PUBLICATION_TYPE, "Abstract")+1, fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.PUBLICATION_TYPE, "Abstract"));
        assertEquals("Count for 'Autoimmune' not updated to original value", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.THERAPEUTIC_AREA, "Autoimmune"), fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.THERAPEUTIC_AREA, "Autoimmune"));
        assertEquals("Count for 'Allergy' not updated", fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy")+1, fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.THERAPEUTIC_AREA, "Allergy"));
        assertEquals("Count for '" + PUBLIC_STUDY_ID + "' not updated to original value", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.PUB_STUDY, PUBLIC_STUDY_ID), fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.PUB_STUDY, PUBLIC_STUDY_ID));
        assertEquals("Count for '" + OPERATIONAL_STUDY_ID + "' not updated", fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.PUB_STUDY, OPERATIONAL_STUDY_ID)+1, fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.PUB_STUDY, OPERATIONAL_STUDY_ID));
        assertEquals("Count for '2016' not updated to original value", fg.getSelectedCount(beforeCounts, DataFinderPage.Dimension.YEAR, "2016"), fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.YEAR, "2016"));
        assertEquals("Count for '2014' not updated", fg.getSelectedCount(afterInsertCounts, DataFinderPage.Dimension.YEAR, "2014")+1, fg.getSelectedCount(afterEditCounts, DataFinderPage.Dimension.YEAR, "2014"));
    }

    @Test
    public void testInsertMultiValuedFields()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> newFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        newFields.put(PublicationEditPage.TITLE, "testInsertMultiValuedFields_" + manageData.getCount());
        newFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        newFields.put(PublicationEditPage.STATUS, "In Progress");

        newFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID, OPERATIONAL_STUDY_ID});
        newFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune", "Allergy"});

        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());

        editPage.setFormFields(newFields, false);
        editPage.saveAndClose("Manage");

        manageData.goToEditRecord((String) newFields.get(TITLE));
        Map<String, String> unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted publication: " + unexpectedValues,  unexpectedValues.isEmpty());
    }

    @Test
    public void testEditMultiValuedFields()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(PublicationEditPage.TITLE, "testEditMultiValuedFields_" + manageData.getCount());
        initialFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        initialFields.put(PublicationEditPage.STATUS, "In Progress");

        initialFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID});
        initialFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune", "Allergy"});

        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());

        editPage.setFormFields(initialFields, false);
        editPage.saveAndClose("Manage");

        manageData.goToEditRecord((String) initialFields.get(TITLE));

        Map<String, Object> newFields = new HashMap<>();
        newFields.put(PublicationEditPage.STUDIES, new String[]{OPERATIONAL_STUDY_ID});
        newFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM"});
        editPage.setFormFields(newFields, false);
        editPage.saveAndClose("Manage");

        manageData.goToEditRecord((String) initialFields.get(TITLE));
        initialFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID, OPERATIONAL_STUDY_ID});
        initialFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune", "Allergy", "T1DM"});

        Map<String, String> unexpectedValues = editPage.compareFormValues(initialFields);
        Assert.assertTrue("Found unexpected values in edit page of updated publication: " + unexpectedValues,  unexpectedValues.isEmpty());
    }

    @Test
    public void testUpdatePublication()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(PublicationEditPage.TITLE, "testUpdatePublication_" + manageData.getCount());
        initialFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        initialFields.put(PublicationEditPage.STATUS, "In Progress");
        initialFields.put(PublicationEditPage.SUBMISSION_STATUS, "Submitted");
        initialFields.put(PublicationEditPage.AUTHOR, "test1, test2");
        initialFields.put(PublicationEditPage.CITATION, "test publications: v24, no 15");
        initialFields.put(PublicationEditPage.YEAR, "2016");
        initialFields.put(PublicationEditPage.JOURNAL, "Test Publications");
//        newFields.put(PublicationEditPage.ABSTRACT, "<b>We are concrete.</b>");
        initialFields.put(PublicationEditPage.DOI, "doi:123/445");
        initialFields.put(PublicationEditPage.PMID, "1212");
        initialFields.put(PublicationEditPage.PMCID, "PMC1411");
        initialFields.put(PublicationEditPage.MANUSCRIPT_CONTAINER, PUBLIC_STUDY_SUBFOLDER_NAME);
        initialFields.put(PublicationEditPage.PERMISSIONS_CONTAINER, OPERATIONAL_STUDY_SUBFOLDER_NAME);
        initialFields.put(PublicationEditPage.KEYWORDS, "key words keywords");
        initialFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID});
        initialFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune"});
        initialFields.put(PublicationEditPage.LINK1, "http://link/to.this");
        initialFields.put(PublicationEditPage.DESCRIPTION1, "Link 1 Description");
        initialFields.put(PublicationEditPage.LINK2, "http://also/link/to.this");
        initialFields.put(PublicationEditPage.DESCRIPTION2, "Link 2 Description");
        initialFields.put(PublicationEditPage.LINK3, "http://finally/link/to.this");
        initialFields.put(PublicationEditPage.DESCRIPTION3, "Link 3 Description");

        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());

        editPage.setFormFields(initialFields, false);
        editPage.saveAndClose("Manage");

        Map<String, Object> updatedFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        int count = manageData.getCount();
        updatedFields.put(PublicationEditPage.TITLE, "testUpdatePublication_" + count + "_updated");
        updatedFields.put(PublicationEditPage.PUBLICATION_TYPE, "Abstract");
        updatedFields.put(PublicationEditPage.STATUS, "Complete");
        updatedFields.put(PublicationEditPage.SUBMISSION_STATUS, "Submitted");
        updatedFields.put(PublicationEditPage.AUTHOR, "test1, test2 updated");
        updatedFields.put(PublicationEditPage.CITATION, "test publications: v24, no 15 updated");
        updatedFields.put(PublicationEditPage.YEAR, "2015");
        updatedFields.put(PublicationEditPage.JOURNAL, "Test Publications updated");
        updatedFields.put(PublicationEditPage.DOI, "doi:123/445-u");
        updatedFields.put(PublicationEditPage.PMID, "1213");
        updatedFields.put(PublicationEditPage.PMCID, "PMC1412");
        updatedFields.put(PublicationEditPage.MANUSCRIPT_CONTAINER,  OPERATIONAL_STUDY_SUBFOLDER_NAME);
        updatedFields.put(PublicationEditPage.PERMISSIONS_CONTAINER, PUBLIC_STUDY_SUBFOLDER_NAME);
        updatedFields.put(PublicationEditPage.KEYWORDS, "key words keywords updated");

        // multi-value fields are tested separately
        updatedFields.put(PublicationEditPage.LINK1, "http://link/to.this updated");
        updatedFields.put(PublicationEditPage.DESCRIPTION1, "Link 1 Description updated");
        updatedFields.put(PublicationEditPage.LINK2, "http://also/link/to.this updated");
        updatedFields.put(PublicationEditPage.DESCRIPTION2, "Link 2 Description updated");
        updatedFields.put(PublicationEditPage.LINK3, "http://finally/link/to.this updated");
        updatedFields.put(PublicationEditPage.DESCRIPTION3, "Link 3 Description updated");

        manageData.goToEditRecord((String) initialFields.get(TITLE));
        Assert.assertTrue("Workbench button should be enabled if data has not changed", editPage.isWorkbenchEnabled());
        // Use JavaScript. WebElement.getAttribute("value") returns the display value rather than the value attribute
        String studyId = (String) executeScript("return arguments[0].getAttribute('value');", Locator.name("studyIds").findElement(getDriver()));
        String publicationId = getUrlParam("id");
        String workbenchContainer = "Studies/" + studyId + "OPR/Study Data";

        clickButton("Workbench", 0);

        switchToWindow(1);
        {
            String url = getDriver().getCurrentUrl();
            // From workbench button handler in CubeObjectDetailsFormPanel.js
            String expectedWorkbenchUrl = WebTestHelper.buildURL("project", workbenchContainer, "begin", Maps.of("pageId", "Manuscripts", "publicationId", publicationId));

            Assert.assertEquals("Wrong url pointed to by the 'Workbench' button.", expectedWorkbenchUrl, url);
            getDriver().close();
        }
        switchToMainWindow();

        editPage.setFormFields(updatedFields, false);
        Assert.assertFalse("Workbench button should be disabled if data has changed", editPage.isWorkbenchEnabled());
        editPage.save();
        sleep(500); // HACK!  Tests on Windows need a break here.
        Map<String, String> unexpectedValues = editPage.compareFormValues(updatedFields);
        Assert.assertTrue("Found unexpected values in edit page of updated publication: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testInsertAndDelete()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);
        goDirectlyToManageDataPage(getDataProjectName(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(PublicationEditPage.TITLE, "testInsertAndDelete_" + manageData.getCount());
        initialFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        initialFields.put(PublicationEditPage.STATUS, "In Progress");
        initialFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID});
        initialFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune"});
        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());
        editPage.setFormFields(initialFields, false);
        editPage.saveAndClose("Manage");
        manageData.deleteRecord((String) initialFields.get(PublicationEditPage.TITLE));
        PublicationsListHelper listHelper = new PublicationsListHelper(this);

        goToProjectHome(getDataProjectName());
        Assert.assertEquals("Found deleted publication", 0, listHelper.getPublicationCount((String) initialFields.get(PublicationEditPage.TITLE), true));
        goToProjectHome(getDataProjectName());
        Assert.assertEquals("Found studies for deleted publication", 0, listHelper.getPublicationStudyCount((String) initialFields.get(PublicationEditPage.TITLE), true));
        goToProjectHome(getDataProjectName());
        Assert.assertEquals("Found therapeutic areas for deleted publication", 0, listHelper.getPublicationTherapeuticAreaCount((String) initialFields.get(PublicationEditPage.TITLE), true));
    }

    @Test
    public void testInsertWithoutRefresh()
    {
        goToProjectHome(getDataProjectName());
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(PublicationEditPage.TITLE, "testInsertAndDelete_" + manageData.getCount());
        initialFields.put(PublicationEditPage.PUBLICATION_TYPE, "Manuscript");
        initialFields.put(PublicationEditPage.STATUS, "Complete");
        initialFields.put(PublicationEditPage.PERMISSIONS_CONTAINER, PUBLIC_STUDY_SUBFOLDER_NAME);
        manageData.goToInsertNew();
        PublicationEditPage editPage = new PublicationEditPage(this.getDriver());

        editPage.setFormFields(initialFields, true);
        editPage.saveAndClose("Manage");

        goToProjectHome(); // there should be no error alert after inserting but before refreshing
        DataFinderPage finder = goDirectlyToDataFinderPage(getCurrentContainerPath(), false);
        finder.clearAllFilters();
        finder.search((String) initialFields.get(PublicationEditPage.TITLE));
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();

        assertEquals("Should find newly inserted publication", 1, dataCards.size());
    }
}