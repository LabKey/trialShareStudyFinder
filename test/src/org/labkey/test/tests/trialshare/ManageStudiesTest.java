package org.labkey.test.tests.trialshare;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.test.categories.Git;
import org.labkey.test.pages.PermissionsEditor;
import org.labkey.test.pages.trialshare.DataFinderPage;
import org.labkey.test.pages.trialshare.ManageDataPage;
import org.labkey.test.pages.trialshare.StudiesListHelper;
import org.labkey.test.pages.trialshare.StudyEditPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by susanh on 6/29/16.
 */
@Category({Git.class})
public class ManageStudiesTest extends DataFinderTestBase
{
    CubeObjectType _objectType = CubeObjectType.study;

    private static final String PROJECT_NAME = "ManageStudiesTest Project";

    @Nullable
    @Override
    protected String getProjectName()
    {
        return PROJECT_NAME;
    }

    @Override
    protected void createStudies()
    {
    }

    @Override
    protected void createUsers()
    {
        _userHelper.createUser(PUBLIC_READER);

        PermissionsEditor permissionsEditor = new PermissionsEditor(this);

        goToProjectHome();
        clickAdminMenuItem("Folder", "Permissions");
        permissionsEditor.setSiteGroupPermissions("All Site Users", "Reader");
    }

    @Test
    public void testManageDataLinkPermissions()
    {
        log("Checking for manage data link");
        DataFinderPage dataFinder = goDirectlyToDataFinderPage(getCurrentContainerPath(), true);
        Assert.assertTrue("Manage Data link is not available", dataFinder.canManageData());
        dataFinder.goToManageData();
        switchToWindow(1);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        Assert.assertTrue("No data shown for studies", manageData.getCount() > 0);

        log("Impersonating user without insert permission");
        goToProjectHome();
        impersonate(PUBLIC_READER);
        goDirectlyToDataFinderPage(getCurrentContainerPath(), true);
        Assert.assertFalse("Manage Data link should not be available", dataFinder.canManageData());
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        assertTextPresent("User does not have permission");
    }

    @Test
    public void testSwitchToPublications()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        Assert.assertTrue("Should see a link to manage publications", manageData.hasManagePublicationsLink());
        manageData.goToManagePublications();
        ManageDataPage managePublicationsData = new ManageDataPage(this, CubeObjectType.publication);
        Assert.assertTrue("Should be manage publications view", managePublicationsData.isManageDataView());
    }

    @Test
    public void testGoToInsertNewAndCancel()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());
        Assert.assertFalse("Submit button should not be enabled", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(() -> editPage.cancel());
        Assert.assertTrue("Should be manage studies view", manageData.isManageDataView());
    }

    @Test
    public void testRequiredFields()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);
        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());
        Assert.assertFalse("Submit button should not be enabled", editPage.isSubmitEnabled());
        editPage.setTextFormValue("title", "testRequiredFields");
        Assert.assertFalse("Submit button should not be enabled with only title", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(editPage::cancel);

        manageData.goToInsertNew();
        editPage.setTextFormValue("shortName", "ShortName");
        Assert.assertFalse("Submit button should not be enabled with only study type", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(editPage::cancel);

        manageData.goToInsertNew();
        editPage.setTextFormValue("studyId", "StudyId");
        Assert.assertFalse("Submit button should not be enabled with only study id", editPage.isSubmitEnabled());
        doAndWaitForPageToLoad(editPage::cancel);

        manageData.goToInsertNew();
        editPage.setTextFormValue("title", "testRequiredFields");
        editPage.setTextFormValue("shortName", "ShortName");
        Assert.assertFalse("Submit button should not be enabled with title, short name but no studyId", editPage.isSubmitEnabled());
        editPage.setTextFormValue("studyId", "StudyId", true);
        Assert.assertTrue("Submit button should be enabled with all required fields", editPage.isSubmitEnabled());
    }

    @Test
    public void testInsertWithAllFields()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        int count = manageData.getCount();
        Map<String, Object> newFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        newFields.put(StudyEditPage.SHORT_NAME, "TIWAF" + count);
        newFields.put(StudyEditPage.STUDY_ID, "TIWAF_ID" + count);
        newFields.put(StudyEditPage.TITLE, "testInsertWithAllFields_" + count);
        newFields.put(StudyEditPage.PARTICIPANT_COUNT, String.valueOf(count));
        newFields.put(StudyEditPage.STUDY_TYPE, "Interventional");
        newFields.put(StudyEditPage.ICON_URL, "not your regular url");
        newFields.put(StudyEditPage.EXTERNAL_URL, "external url");
        // N.B. leaving out external URL description and Description fields because
        // not sure how to attach to the iframe
        newFields.put(StudyEditPage.INVESTIGATOR, "investigate");
        newFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Child"});
        newFields.put(StudyEditPage.PHASES, new String[]{"Phase 0"});
        newFields.put(StudyEditPage.CONDITIONS, new String[]{"Eczema"});
        newFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM"});

        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());

        editPage.setFormFields(newFields);
        editPage.submit();
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);

        manageData.goToEditRecord((String) newFields.get(StudyEditPage.STUDY_ID));
        Map<String, String> unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted study: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testInsertMultiValuedFields()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        int count = manageData.getCount();
        Map<String, Object> newFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        newFields.put(StudyEditPage.SHORT_NAME, "TIMVF" + count);
        newFields.put(StudyEditPage.STUDY_ID, "TIMVF_ID" + count);
        newFields.put(StudyEditPage.TITLE, "testInsertMultiValuedFields_" + count);

        // N.B. leaving out external URL description and Description fields because
        // not sure how to attach to the iframe
        newFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Child", "Adult"});
        newFields.put(StudyEditPage.PHASES, new String[]{"Phase 0", "Phase 4"});
        newFields.put(StudyEditPage.CONDITIONS, new String[]{"Eczema", "Bone Marrow Transplantation", "Hay Fever"});
        newFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM", "Allergy"});

        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());

        editPage.setFormFields(newFields);
        editPage.submit();

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);

        manageData.goToEditRecord((String) newFields.get(StudyEditPage.STUDY_ID));
        Map<String, String> unexpectedValues = editPage.compareFormValues(newFields);
        Assert.assertTrue("Found unexpected values in edit page of newly inserted publication: " + unexpectedValues,  unexpectedValues.isEmpty());
    }

    @Test
    public void testEditMultiValuedFields()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        int count = manageData.getCount();
        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(StudyEditPage.SHORT_NAME, "TEMVF" + count);
        initialFields.put(StudyEditPage.STUDY_ID, "TEMVF_ID" + count);
        initialFields.put(StudyEditPage.TITLE, "testEditMultiValuedFields_" + count);

        // N.B. leaving out external URL description and Description fields because
        // not sure how to attach to the iframe
        initialFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Child", "Adult"});
        initialFields.put(StudyEditPage.PHASES, new String[]{"Phase 0", "Phase 4"});
        initialFields.put(StudyEditPage.CONDITIONS, new String[]{"Eczema", "Bone Marrow Transplantation", "Hay Fever"});
        initialFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM", "Allergy"});

        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());

        editPage.setFormFields(initialFields);
        editPage.submit();

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);

        manageData.goToEditRecord((String) initialFields.get(StudyEditPage.STUDY_ID));

        Map<String, Object> newFields = new HashMap<>();
        // this removes "Child" and adds "Senior"
        newFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Child", "Senior"});
        // this removes both Phase 0 and Phase 4
        newFields.put(StudyEditPage.PHASES, new String[]{"Phase 0", "Phase 4"});
        // this adds "Allergy" and "Cat Allergy"
        newFields.put(StudyEditPage.CONDITIONS, new String[]{"Allergy", "Cat Allergy"});
        // this removes "T1DM"
        newFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM"});
        editPage.setFormFields(newFields);
        editPage.submit();


        manageData.goToEditRecord((String) initialFields.get(StudyEditPage.STUDY_ID));
        initialFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Adult", "Senior"});
        initialFields.put(StudyEditPage.PHASES, new String[]{});
        initialFields.put(StudyEditPage.CONDITIONS, new String[]{"Eczema", "Bone Marrow Transplantation", "Hay Fever", "Allergy", "Cat Allergy"});
        initialFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"Allergy"});

        Map<String, String> unexpectedValues = editPage.compareFormValues(initialFields);
        Assert.assertTrue("Found unexpected values in edit page of updated publication: " + unexpectedValues,  unexpectedValues.isEmpty());
    }

    @Test
    public void testUpdateStudy()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        int count = manageData.getCount();
        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(StudyEditPage.SHORT_NAME, "TUS" + count);
        initialFields.put(StudyEditPage.STUDY_ID, "TUS_ID" + count);
        initialFields.put(StudyEditPage.TITLE, "testUpdateStudy_" + count);
        initialFields.put(StudyEditPage.PARTICIPANT_COUNT, String.valueOf(count));
        initialFields.put(StudyEditPage.STUDY_TYPE, "Interventional");
        initialFields.put(StudyEditPage.ICON_URL, "not your regular url");
        initialFields.put(StudyEditPage.EXTERNAL_URL, "external url");
        // N.B. leaving out external URL description and Description fields because
        // not sure how to attach to the iframe
        initialFields.put(StudyEditPage.INVESTIGATOR, "investigate");
        initialFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Child"});
        initialFields.put(StudyEditPage.PHASES, new String[]{"Phase 0"});
        initialFields.put(StudyEditPage.CONDITIONS, new String[]{"Eczema"});
        initialFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"T1DM"});

        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());

        editPage.setFormFields(initialFields);
        editPage.submit();
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);


        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put(StudyEditPage.SHORT_NAME, "TUS" + count + "_U");
        updatedFields.put(StudyEditPage.TITLE, "testUpdateStudy_" + count + "_updated");
        updatedFields.put(StudyEditPage.PARTICIPANT_COUNT, String.valueOf(count+1));
        updatedFields.put(StudyEditPage.STUDY_TYPE, "Observational");
        updatedFields.put(StudyEditPage.ICON_URL, "not your regular url updated");
        updatedFields.put(StudyEditPage.EXTERNAL_URL, "external url updated");
        // N.B. leaving out external URL description and Description fields because
        // not sure how to attach to the iframe
        updatedFields.put(StudyEditPage.INVESTIGATOR, "investigate updated");
        updatedFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Adult"});
        updatedFields.put(StudyEditPage.PHASES, new String[]{"Phase 2"});
        updatedFields.put(StudyEditPage.CONDITIONS, new String[]{"Asthma"});
        updatedFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"Allergy"});


        manageData.goToEditRecord((String) initialFields.get(StudyEditPage.STUDY_ID));
        editPage.setFormFields(updatedFields);
        editPage.submit();

        manageData.goToEditRecord((String) initialFields.get(StudyEditPage.STUDY_ID));
        Map<String, String> unexpectedValues = editPage.compareFormValues(updatedFields);
        Assert.assertTrue("Found unexpected values in edit page of updated study: " + unexpectedValues, unexpectedValues.isEmpty());
    }

    @Test
    public void testInsertAndDelete()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        int count = manageData.getCount();
        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(StudyEditPage.SHORT_NAME, "TUS" + count);
        initialFields.put(StudyEditPage.STUDY_ID, "TUS_ID" + count);
        initialFields.put(StudyEditPage.TITLE, "testUpdateStudy_" + count);
        initialFields.put(StudyEditPage.AGE_GROUPS, new String[]{"Adult"});
        initialFields.put(StudyEditPage.PHASES, new String[]{"Phase 2"});
        initialFields.put(StudyEditPage.CONDITIONS, new String[]{"Asthma"});
        initialFields.put(StudyEditPage.THERAPEUTIC_AREAS, new String[]{"Allergy"});

        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());

        editPage.setFormFields(initialFields);
        editPage.submit();
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);

        manageData.deleteRecord((String) initialFields.get(StudyEditPage.STUDY_ID));

        StudiesListHelper listHelper = new StudiesListHelper(this);

        goToProjectHome();
        Assert.assertEquals("Found deleted study", 0, listHelper.getStudyCount((String) initialFields.get(StudyEditPage.STUDY_ID), true));
        goToProjectHome();
        Assert.assertEquals("Found age group(s) for deleted study", 0, listHelper.getStudyAgeGroupCount((String) initialFields.get(StudyEditPage.STUDY_ID), true));
        goToProjectHome();
        Assert.assertEquals("Found condition(s) for deleted study", 0, listHelper.getStudyConditionCount((String) initialFields.get(StudyEditPage.STUDY_ID), true));
        goToProjectHome();
        Assert.assertEquals("Found therapeutic area(s) for deleted study", 0, listHelper.getStudyTherapeuticAreaCount((String) initialFields.get(StudyEditPage.STUDY_ID), true));
        goToProjectHome();
        Assert.assertEquals("Found phase(s) for deleted study", 0, listHelper.getStudyPhaseCount((String) initialFields.get(StudyEditPage.STUDY_ID), true));
        goToProjectHome();
        Assert.assertEquals("Found study access data for deleted study", 0, listHelper.getStudyAccessCount((String) initialFields.get(StudyEditPage.STUDY_ID), true));
    }

    @Test
    public void testInsertAndRefresh()
    {
        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        ManageDataPage manageData = new ManageDataPage(this, _objectType);

        int count = manageData.getCount();
        String shortName = "TUS" + count;
        String studyId = "TUS_ID" + count;
        createStudy(shortName, false);

        Map<String, Object> initialFields = new HashMap<>();
        // add the count so multiple runs of this test have distinct titles
        initialFields.put(StudyEditPage.SHORT_NAME, shortName);
        initialFields.put(StudyEditPage.STUDY_ID, studyId);
        initialFields.put(StudyEditPage.TITLE, "testUpdateStudy_" + count);

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        manageData.goToInsertNew();
        StudyEditPage editPage = new StudyEditPage(this.getDriver());

        editPage.setFormFields(initialFields);
        editPage.submit();

        goToProjectHome();

        StudiesListHelper studyListHelper = new StudiesListHelper(this);
        studyListHelper.addStudyAccessEntry(shortName,  "/" + PROJECT_NAME + "/" + shortName , "Public", true);

        goToProjectHome(); // there should be no error alert after inserting but before refreshing
        DataFinderPage finder = new DataFinderPage(this, true);

        finder.search(studyId);
        List<DataFinderPage.DataCard> dataCards = finder.getDataCards();

        assertEquals("Should not find newly inserted study without reindex", 0, dataCards.size());

        goDirectlyToManageDataPage(getCurrentContainerPath(), _objectType);
        manageData.refreshCube();

        goToProjectHome();

        finder.search(studyId);
        dataCards = finder.getDataCards();

        assertEquals("Should find newly inserted study after reindex", 1, dataCards.size());
    }
}