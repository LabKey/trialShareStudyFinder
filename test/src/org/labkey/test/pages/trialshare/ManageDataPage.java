package org.labkey.test.pages.trialshare;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.labkey.test.BaseWebDriverTest;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.tests.trialshare.DataFinderTestBase;
import org.labkey.test.util.DataRegionTable;

/**
 * Created by susanh on 6/30/16.
 */
public class ManageDataPage extends LabKeyPage
{
    private DataRegionTable _table;
    private DataFinderTestBase.CubeObjectType _objectType;


    public ManageDataPage(BaseWebDriverTest test, DataFinderTestBase.CubeObjectType objectType)
    {
        super(test.getDriver());

        _table = new DataRegionTable("query", test);
        _objectType = objectType;
    }

    public int getCount()
    {
        return _table.getDataRowCount();
    }

    public boolean isManageDataView()
    {
        return isTextPresent(_objectType.getManageDataTitle()) && hasExpectedColumns();
    }

    public boolean hasExpectedColumns()
    {
        for (String header : _table.getColumnHeaders())
        {
            if (!StringUtils.isEmpty(header) && !header.trim().isEmpty() && !_objectType.getManageDataHeaders().contains(header))
                return false;
        }
        return true;
    }

    public boolean hasManageStudiesLink()
    {
        return isElementPresent(Locators.manageStudiesLink);
    }

    public void goToManageStudies()
    {
        doAndWaitForPageToLoad(() -> Locators.manageStudiesLink.findElement(getDriver()).click());
    }

    public boolean hasManagePublicationsLink()
    {
        return isElementPresent(Locators.managePublicationsLink);
    }

    public void goToManagePublications()
    {
        doAndWaitForPageToLoad(() -> Locators.managePublicationsLink.findElement(getDriver()).click());
    }

    public void goToInsertNew()
    {
        log("Going to insert new " + _objectType);
        doAndWaitForPageToLoad(() -> _table.clickHeaderButtonByText("Insert New"));
    }

    public int getRowIndex(String value)
    {
        Integer rowIndex = _table.getRow(_objectType.getKeyField(), value);
        Assert.assertTrue("Did not find row with " + _objectType.getKeyField() + " '" + value + "'", rowIndex >= 0);
        return rowIndex;
    }

    public void goToEditRecord(String keyValue)
    {
        Integer rowIndex = getRowIndex(keyValue);
        doAndWaitForPageToLoad(() -> editLink(rowIndex).findElement(getDriver()).click());
    }

    public void deleteRecord(String keyValue)
    {
        log("Deleting record with key value '" + keyValue + "'");
        Integer rowIndex = getRowIndex(keyValue);
        Assert.assertTrue("Record with key '" + keyValue + "' not found", rowIndex >= 0);
        _table.checkCheckbox(rowIndex);
        //_table.clickHeaderButtonByText("Delete");

        log("Waiting for delete confirmation to show up");
        waitForAlert("Are you sure you want to delete the selected row?", 1000);
    }

    public Locator.XPathLocator detailsLink(int row)
    {
        return Locator.tagWithClass("table", "labkey-data-region").append(Locator.xpath("/tbody/tr[" + (row + 5) + "]/td[3]/a "));
    }

    public Locator.XPathLocator editLink(int row)
    {
        return Locator.tagWithClass("table", "labkey-data-region").append(Locator.xpath("/tbody/tr[" + (row + 5) + "]/td[2]/a "));
    }

    public void refreshCube()
    {
        log("Refreshing cube");
        _table.clickHeaderButtonByText("Refresh Cube");
        sleep(2000); // give time for the refresh to happen
    }

    private static class Locators
    {
        public static Locator manageStudiesLink = Locator.linkWithText("Manage Studies");
        public static Locator managePublicationsLink = Locator.linkWithText("Manage Publications");
    }

}
