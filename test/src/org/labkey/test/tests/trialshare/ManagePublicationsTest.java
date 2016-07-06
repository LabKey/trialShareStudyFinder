package org.labkey.test.tests.trialshare;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.categories.Git;
import org.labkey.test.pages.PermissionsEditor;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.pages.trialshare.ManageDataPage;
import org.labkey.test.pages.trialshare.PublicationEditPage;
import org.labkey.test.pages.trialshare.PublicationsListHelper;
import org.labkey.test.pages.trialshare.StudiesListHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.labkey.test.pages.trialshare.PublicationEditPage.EMPTY_VALUE;
import static org.labkey.test.pages.trialshare.PublicationEditPage.NOT_EMPTY_VALUE;
import static org.labkey.test.pages.trialshare.PublicationEditPage.TITLE;

/**
 * Created by susanh on 6/29/16.
 */
@Category({Git.class})
public class ManagePublicationsTest extends DataFinderTestBase
{
    private CubeObjectType _objectType = CubeObjectType.publication;

    private static final String PUBLIC_STUDY_ID = "Casale";
    private static final String OPERATIONAL_STUDY_ID = "WISP-R";
    private static final String PROJECT_NAME = "ManagePublicationTest Project";
    private static final String OPERATIONAL_STUDY_SUBFOLDER_NAME = "/" + PROJECT_NAME + "/" + OPERATIONAL_STUDY_NAME;
    private static final String PUBLIC_STUDY_SUBFOLDER_NAME = "/" + PROJECT_NAME + "/" + PUBLIC_STUDY_NAME;

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
        return "ManagePublicationTest Project";
    }


    @Override
    protected void createStudies()
    {
        createStudy(PUBLIC_STUDY_NAME);
        createStudy(OPERATIONAL_STUDY_NAME);
    }

    @Override
    protected void createUsers()
    {
        _userHelper.createUser(PUBLIC_READER);

        PermissionsEditor permissionsEditor = new PermissionsEditor(this);

        goToProjectHome();
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setSiteGroupPermissions("All Site Users", "Reader");

        permissionsEditor.selectFolder(PUBLIC_STUDY_NAME);
        permissionsEditor.setUserPermissions(PUBLIC_READER, "Reader");
    }

    @Test
    public void testManageDataLinkPermissions()
    {
        log("Checking for manage data link");
        DataFinderPage dataFinder = goDirectlyToDataFinderPage(getCurrentContainerPath(), false);
        Assert.assertTrue("Manage Data link is not available", dataFinder.canManageData());
        dataFinder.goToManageData();
        switchToWindow(1);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        Assert.assertTrue("No data shown for publication", manageData.getCount() > 0);

        log("Impersonating user without insert permission");
        goToProjectHome();
        impersonate(PUBLIC_READER);
        goDirectlyToDataFinderPage(getCurrentContainerPath(), false);
        Assert.assertFalse("Manage Data link should not be available", dataFinder.canManageData());
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        assertTextPresent("User does not have permission");
    }

    @Test
    public void testSwitchToStudies()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
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
        doAndWaitForPageToLoad(() -> editPage.cancel());
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
        editPage.setTextFormValue("PMCID", "invalid");
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
        doAndWaitForPageToLoad(editPage::cancel);

        manageData.goToInsertNew();
        editPage.selectMenuItem("Publication Type *:", "Manuscript");
        Assert.assertFalse("Submit button should not be enabled with only publication type", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(editPage::cancel);

        manageData.goToInsertNew();
        editPage.selectMenuItem("Status *:", "Complete");
        Assert.assertFalse("Submit button should not be enabled with only status", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(editPage::cancel);

        manageData.goToInsertNew();
        editPage.setTextFormValue("title", "testRequiredFields");
        editPage.selectMenuItem("Publication Type *:", "Manuscript");
        Assert.assertFalse("Submit button should not be enabled with title, publication type but no status", editPage.isSubmitEnabled());
        editPage.selectMenuItem("Status *:", "Complete");
        Assert.assertTrue("Submit button should be enabled with all required fields", editPage.isSubmitEnabled());
    }

    @Test
    public void testInsertWithAllFields()
    {
        goToProjectHome();
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        Map<String, Object> newFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        newFields.put(PublicationEditPage.TITLE, "testInsertWithAllFields_" + manageData.getCount());
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
        editPage.submit();

        manageData.goToEditRecord((String) newFields.get(TITLE));
        Map<String, String> unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted publication: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testInsertMultiValuedFields()
    {
        goToProjectHome();
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
        editPage.submit();

        manageData.goToEditRecord((String) newFields.get(TITLE));
        Map<String, String> unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted publication: " + unexpectedValues,  unexpectedValues.isEmpty());
    }

    @Test
    public void testEditMultiValuedFields()
    {
        goToProjectHome();
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
        editPage.submit();

        manageData.goToEditRecord((String) initialFields.get(TITLE));

        Map<String, Object> newFields = new HashMap<>();
        newFields.put(PublicationEditPage.STUDIES, new String[]{OPERATIONAL_STUDY_ID});
        newFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM"});
        editPage.setFormFields(newFields, false);
        editPage.submit();

        manageData.goToEditRecord((String) initialFields.get(TITLE));
        initialFields.put(PublicationEditPage.STUDIES, new String[]{PUBLIC_STUDY_ID, OPERATIONAL_STUDY_ID});
        initialFields.put(PublicationEditPage.THERAPEUTIC_AREAS, new String[]{"Autoimmune", "Allergy", "T1DM"});

        Map<String, String> unexpectedValues = editPage.compareFormValues(initialFields);
        Assert.assertTrue("Found unexpected values in edit page of updated publication: " + unexpectedValues,  unexpectedValues.isEmpty());
    }

    @Test
    public void testUpdatePublication()
    {
        goToProjectHome();
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
        editPage.submit();

        Map<String, Object> updatedFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        updatedFields.put(PublicationEditPage.TITLE, "testUpdatePublication_" + manageData.getCount() + "_updated");
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
        editPage.setFormFields(updatedFields, false);
        editPage.submit();

        manageData.goToEditRecord((String) updatedFields.get(TITLE));
        Map<String, String> unexpectedValues = editPage.compareFormValues(updatedFields);
        Assert.assertTrue("Found unexpected values in edit page of updated publication: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testInsertAndDelete()
    {
        goToProjectHome();
        StudiesListHelper studiesListHelper = new StudiesListHelper(this);
        studiesListHelper.setStudyContainer(PUBLIC_STUDY_ID, PUBLIC_STUDY_SUBFOLDER_NAME, true);
        studiesListHelper.setStudyContainer(OPERATIONAL_STUDY_ID, OPERATIONAL_STUDY_SUBFOLDER_NAME, false);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
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
        editPage.submit();

        manageData.deleteRecord((String) initialFields.get(PublicationEditPage.TITLE));

        PublicationsListHelper listHelper = new PublicationsListHelper(this);

        goToProjectHome();
        Assert.assertEquals("Found deleted publication", 0, listHelper.getPublicationCount((String) initialFields.get(PublicationEditPage.TITLE), true));
        goToProjectHome();
        Assert.assertEquals("Found studies for deleted publication", 0, listHelper.getPublicationStudyCount((String) initialFields.get(PublicationEditPage.TITLE), true));
        goToProjectHome();
        Assert.assertEquals("Found therapeutic areas for deleted publication", 0, listHelper.getPublicationTherapeuticAreaCount((String) initialFields.get(PublicationEditPage.TITLE), true));
    }

    @Test
    public void testInsertAndRefresh()
    {
        goToProjectHome();
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
        editPage.submit();

        goToProjectHome(); // there should be no error alert after inserting but before refreshing
        DataFinderPage finder = goDirectlyToDataFinderPage(getCurrentContainerPath(), false);
        finder.search((String) initialFields.get(PublicationEditPage.TITLE));
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();

        assertEquals("Should not find newly inserted publication without reindex", 0, dataCards.size());

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        manageData.refreshCube();

        goToProjectHome();
        finder = goDirectlyToDataFinderPage(getCurrentContainerPath(), false);
        finder.search((String) initialFields.get(PublicationEditPage.TITLE));
        dataCards = finder.getDataCards();

        assertEquals("Should find newly inserted publication after reindex", 1, dataCards.size());

    }
}