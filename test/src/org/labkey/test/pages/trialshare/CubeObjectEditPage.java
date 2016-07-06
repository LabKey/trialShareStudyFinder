package org.labkey.test.pages.trialshare;

import org.apache.commons.lang3.StringUtils;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by susanh on 6/30/16.
 */
public abstract class CubeObjectEditPage extends LabKeyPage
{
    public static final String NOT_EMPTY_VALUE = "NOT EMPTY VALUE";
    public static final String EMPTY_VALUE = "EMPTY VALUE";

    CubeObjectEditPage(WebDriver driver)
    {
        super(driver);
    }

    public void setTextFormValue(String key, String value)
    {
        setTextFormValue(key, value, false);
    }

    public void setTextFormValue(String key, String value, Boolean waitForSubmit)
    {
        setTextFormValue(key, value, waitForSubmit, false);
    }

    public void setTextFormValue(String key, String value, Boolean waitForSubmit, Boolean submitIsDisabled)
    {
        Locator fieldLocator = Locator.name(key);
        setFormElement(fieldLocator, value);
        if (waitForSubmit)
        {
            if (submitIsDisabled)
                waitForElement(Locators.disabledSubmitButton);
            else
                waitForElement(Locators.submitButton);
        }

    }

    public abstract Map<String, String> getDropdownFieldNames();
    public abstract Map<String, String> getMultiSelectFieldNames();
    public abstract Set<String> getFieldNames();

    public void selectMenuItem(String label, String value)
    {
        _ext4Helper.selectComboBoxItem(label, value);
    }

    public void setFormField(String key, Object value)
    {
        log("Setting field " + key + " to " + (value instanceof String[] ? StringUtils.join((String []) value, "; ") : value));
        if (getDropdownFieldNames().keySet().contains(key))
            _ext4Helper.selectComboBoxItem(getDropdownFieldNames().get(key), (String) value);
        else if (getMultiSelectFieldNames().keySet().contains(key))
            multiSelectComboBoxItem(getMultiSelectFieldNames().get(key), (String[]) value);
        else
            setTextFormValue(key, (String) value);
        log("Field " + key + " new value is " + getFormValue(key));
    }

    // the similar method in ext4Helper is looking for a property that does not exist to
    // decide if this is a multi-select box or not.
    public void multiSelectComboBoxItem(String label, @LoggedParam String... selections)
    {
        Locator.XPathLocator comboBox = Ext4Helper.Locators.formItemWithLabel(label);
        _ext4Helper.openComboList(comboBox);
        log("Hack-Nap while combo box gets filled. Maybe?");
        sleep(2000);

        try
        {
            for (String selection : selections)
            {
                log("Selecting " + selection);
                _ext4Helper.selectItemFromOpenComboList(selection, Ext4Helper.TextMatchTechnique.EXACT);
            }
        }
        catch (StaleElementReferenceException retry) // Combo-box might still be loading previous selection (no good way to detect)
        {
            for (String selection : selections)
            {
                log("Selecting " + selection);
                _ext4Helper.selectItemFromOpenComboList(selection, Ext4Helper.TextMatchTechnique.EXACT);
            }
        }

        log("Closing combo box " + label);
        Locator arrowTrigger = comboBox.append("//div[contains(@class,'arrow')]");
        arrowTrigger.findElement(this.getDriver()).click();
    }



    public void setFormFields(Map<String, Object> fieldMap)
    {
        log("Setting form fields for keys: " + StringUtils.join(fieldMap.keySet(), ", "));
        for (String key : fieldMap.keySet())
        {
            setFormField(key, fieldMap.get(key));
        }
    }

    public String getFormValue(String field)
    {
        Locator fieldLocator = Locator.name(field);
        return getFormElement(fieldLocator);
    }

    public Map<String, String> getFormValues()
    {
        Map<String, String> formValues = new HashMap<>();
        for (String field : getFieldNames())
        {
            formValues.put(field, getFormValue(field));
        }
        return formValues;
    }

    public Map<String, String> compareFormValues(Map<String, Object> expectedValues)
    {
        Map<String, String> formValues = getFormValues();
        Map<String, String> unexpectedValues = new HashMap<>();
        for (String key : expectedValues.keySet())
        {
            String expectedValue;
            if (expectedValues.get(key) instanceof String)
            {
                expectedValue = (String) expectedValues.get(key);
            }
            else // should be an array of Strings
            {
                expectedValue = StringUtils.join((String[]) expectedValues.get(key), "; ");
            }
            String formValue = formValues.get(key);
            log("Comparing field values for " + key + " expecting " + expectedValue + " actual " + formValue);
            if (expectedValue.equals(EMPTY_VALUE))
            {
                if (formValue != null && !formValue.trim().isEmpty())
                    unexpectedValues.put(key, "expected: " + expectedValue + " actual: " + formValue);
            }
            else if (expectedValue.equals(NOT_EMPTY_VALUE))
            {
                if (formValue == null || formValue.trim().isEmpty())
                    unexpectedValues.put(key, "expected: " + expectedValue + " actual: " + formValue);
            }
            else if (!expectedValue.equals(formValue))
            {
                unexpectedValues.put(key, "expected: " + expectedValue + " actual: " + formValue);
            }
        }
        return unexpectedValues;
    }

    public boolean isSubmitEnabled()
    {
        return !isElementPresent(Locators.disabledSubmitButton);
    }

    public void cancel()
    {
        log("Cancelling edit");
        Locators.cancelButton.findElement(getDriver()).click();
    }

    public void submit()
    {
        log("Submitting edit form");
        click(Locators.submitButton);
        waitForElement(Locators.ackSubmit);
        clickAndWait(Locators.ackSubmit, WAIT_FOR_PAGE);
    }

    private static class Locators
    {
        static final Locator showOnDashField = Locator.css(".labkey-field-editor input.x4-form-checkbox");
        static final Locator disabledSubmitButton = Locator.css("a.x4-disabled").withText("SUBMIT"); // why do we need to have the all caps text here?
        static final Locator submitButton = Locator.linkWithText("Submit");
        static final Locator cancelButton = Locator.linkWithText("Cancel");
        static final Locator ackSubmit = Locator.linkWithText("OK");
    }

}
