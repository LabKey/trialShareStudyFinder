package org.labkey.test.pages.trialshare;

import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.DataRegionTable;

/**
 * Created by susanh on 2/5/16.
 */
public class StudiesListHelper extends LabKeyPage
{
    public StudiesListHelper(BaseWebDriverTest test)
    {
        super(test);
    }

    public void setStudyContainers()
    {
        log("Setting up study container links");
        String projectName = getCurrentProject();
        clickAndWait(Locator.linkWithText("StudyAccess"));
        DataRegionTable table = new DataRegionTable("query", _test);
        for (int i = 0; i < table.getDataRowCount(); i++)
        {
            String name = table.getDataAsText(i, "StudyId");
            Boolean isPublic = "Public".equalsIgnoreCase(table.getDataAsText(i, "Visibility"));
            clickAndWait(table.updateLink(i));
            selectOptionByText(Locators.studyContainerSelect, "/" + projectName + "/DataFinderTest" + (isPublic ? "Public" : "Operational") + name);
            clickButton("Submit");
        }
    }

    public void addStudyAccessEntry(String shortName, String containerName, String visibility, Boolean navigateToList)
    {
        log("Adding study access entry for study name " + shortName + " and container " + containerName + " to " + visibility);
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("StudyAccess"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        table.clickHeaderMenu("Insert", "Insert New");
        selectOptionByText(Locators.studyContainerSelect, containerName);
        selectOptionByText(Locators.studyVisibility, visibility);
        selectOptionByText(Locators.studyIdSelect, shortName);
        clickButton("Submit");
    }

    public void setStudyContainer(String studyId, String containerName, Boolean navigateToList)
    {
        log("Setting up study container link for studyId " + studyId);
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText("StudyAccess"));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        int rowIndex = table.getRow("StudyId", studyId);
        clickAndWait(table.updateLink(rowIndex));
        selectOptionByText(Locators.studyContainerSelect, containerName);
        clickButton("Submit");
    }

    public int getStudyCount(String studyId, Boolean navigateToList)
    {
        return getStudyListCount("StudyProperties", studyId, navigateToList);
    }

    public int getStudyAgeGroupCount(String studyId, Boolean navigateToList)
    {
        return getStudyListCount("StudyAgeGroup", studyId, navigateToList);
    }

    public int getStudyConditionCount(String studyId, Boolean navigateToList)
    {
        return getStudyListCount("StudyCondition", studyId, navigateToList);
    }

    public int getStudyTherapeuticAreaCount(String studyId, Boolean navigateToList)
    {
        return getStudyListCount("StudyTherapeuticArea", studyId, navigateToList);
    }

    public int getStudyPhaseCount(String studyId, Boolean navigateToList)
    {
        return getStudyListCount("StudyPhase", studyId, navigateToList);
    }

    public int getStudyAccessCount(String studyId, Boolean navigateToList)
    {
        return getStudyListCount("StudyAccess", studyId, navigateToList);
    }

    public int getStudyListCount(String listName, String studyId, Boolean navigateToList)
    {
        if (navigateToList)
        {
            clickAndWait(Locator.linkWithText(listName));
        }
        DataRegionTable table = new DataRegionTable("query", _test);
        table.setFilter("StudyId", "Equals", studyId);
        return table.getDataRowCount();
    }

    private static class Locators
    {
        static final Locator.XPathLocator studyContainerSelect = Locator.tagWithName("select", "quf_StudyContainer");
        static final Locator.XPathLocator studyIdSelect = Locator.tagWithName("select", "quf_StudyId");
        static final Locator.XPathLocator studyVisibility = Locator.tagWithName("select", "quf_Visibility");
    }

}
