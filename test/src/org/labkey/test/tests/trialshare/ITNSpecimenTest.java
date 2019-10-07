package org.labkey.test.tests.trialshare;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.labkey.remoteapi.Connection;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.TestFileUtils;
import org.labkey.test.TestTimeoutException;
import org.labkey.test.categories.Git;
import org.labkey.test.components.study.SpecimenDetailGrid;
import org.labkey.test.pages.study.samples.ManageRequestPage;
import org.labkey.test.pages.study.samples.ShowCreateSampleRequestPage;
import org.labkey.test.util.ExperimentalFeaturesHelper;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.TestLogger;
import org.labkey.test.util.study.specimen.SpecimenHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Category({Git.class})
public class ITNSpecimenTest extends BaseWebDriverTest
{
    private static final File STUDY_ARCHIVE = TestFileUtils.getSampleData("studies/LabkeyDemoStudy.zip");
    private static final String ITN_SPECIMEN_HANDLING = "ITNSpecimenHandling";

    @Override
    protected void doCleanup(boolean afterTest) throws TestTimeoutException
    {
        super.doCleanup(afterTest);
        enableITNSpecimenHandling(false);
    }

    @BeforeClass
    public static void setupProject()
    {
        ITNSpecimenTest init = (ITNSpecimenTest) getCurrentTest();

        init.doSetup();
    }

    private void doSetup()
    {
        _containerHelper.createProject(getProjectName(), "Study");
        importStudyFromZip(STUDY_ARCHIVE);
    }

    @Before
    public void preTest()
    {
        enableITNSpecimenHandling(true);
        goToProjectHome();
    }

    @Test
    public void testSpecimenReportCustomization()
    {
        // TODO: Verify standard report behavior

        // TODO: type breakdown select is missing

        // TODO: Report looks like 'Primary Type' report
    }

    @Test
    public void testSpecimenRequestCustomization()
    {
        SpecimenHelper specimenHelper = new SpecimenHelper(this);
        specimenHelper.setupRequestStatuses();
        clickPortalTab("Specimen Data");

        clickAndWait(Locator.linkWithText("Urine"));
        SpecimenDetailGrid specimenGrid = specimenHelper.findSpecimenDetailGrid();
        specimenGrid.checkCheckbox(0);
        ShowCreateSampleRequestPage requestPage = specimenGrid.createNewRequest();

        TestLogger.log("\"Reqeusting Location\" should not be available for ITN specimen requests");
        assertElementNotVisible(Locator.id("destinationLocation"));

        ManageRequestPage manageRequestPage = requestPage
                .setDetails("plan", "info") // required fields
                .clickCreateAndViewDetails();

        specimenGrid = specimenHelper.findSpecimenDetailGrid();
        specimenGrid.checkCheckbox(0);
        specimenGrid.clickHeaderButtonAndWait("Remove Selected");

        TestLogger.log("Should be able to submit a request without any samples");
        manageRequestPage = manageRequestPage
                .submitRequest();

        TestLogger.log("Validate message from 'ITNSpecimenRequestCustomizer'");
        assertElementPresent(Locator.linkWithText("trialsharesupport@immunetolerance.org"));
        assertTextPresent("Thank you for your request. A representative from the ITN will be in touch with you.");
    }

    @Test
    public void testRequestListCustomization()
    {

    }

    @LogMethod(quiet = true)
    public void enableITNSpecimenHandling(boolean enable)
    {
        Connection cn = createDefaultConnection(false);
        ExperimentalFeaturesHelper.setExperimentalFeature(cn, ITN_SPECIMEN_HANDLING, enable);
    }

    @Override
    protected BrowserType bestBrowser()
    {
        return BrowserType.CHROME;
    }

    @Override
    protected String getProjectName()
    {
        return "TrialShareSpecimenTest Project";
    }

    @Override
    public List<String> getAssociatedModules()
    {
        return Arrays.asList("trialShare");
    }
}
