/*
 * Copyright (c) 2016 LabKey Corporation
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
package org.labkey.test.pages.trialshare;

import org.apache.commons.lang3.StringUtils;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
            elementCache().saveAndCloseButton.isDisplayed(); // Make sure it exists
            ExpectedCondition<Boolean> saveIsDisabled = elementCache().disabledButtonCondition(elementCache().saveAndCloseButton);
            if (submitIsDisabled)
                shortWait().until(saveIsDisabled);
            else
                shortWait().until(ExpectedConditions.not(saveIsDisabled));
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
            sleep(1000);
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
        log("Hack-Nap while combo box gets filled. Maybe?");
        sleep(2000);
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
        Boolean result = elementCache().disabledButtonCondition(elementCache().saveAndCloseButton).apply(null);
        return result != null && result;
    }

    public boolean isWorkbenchEnabled()
    {
        Boolean result = elementCache().disabledButtonCondition(elementCache().workbenchButton).apply(null);
        return result != null && result;
    }

    public void cancel()
    {
        log("Cancelling edit");
        clickAndWait(elementCache().cancelButton);
    }

    public void save()
    {
        log("Saving edit form");
        elementCache().saveButton.click();
    }

    public void saveAndClose(String returnToHeader)
    {
        log("Saving and closing edit form");
        clickAndWait(elementCache().saveAndCloseButton);
        waitForElement(Locator.css(".labkey-wp-title-text").containing(returnToHeader));
    }

    @Override
    protected ElementCache elementCache()
    {
        return (ElementCache) super.elementCache();
    }

    @Override
    protected ElementCache newElementCache()
    {
        return new ElementCache();
    }

    private class ElementCache extends LabKeyPage.ElementCache
    {
        final WebElement showOnDashField = Locator.css(".labkey-field-editor input.x4-form-checkbox").findWhenNeeded(this);
        final WebElement saveButton = Locator.linkWithText("Save").findWhenNeeded(this);
        final WebElement saveAndCloseButton = Locator.linkWithText("Save And Close").findWhenNeeded(this);
        final WebElement cancelButton = Locator.linkWithText("Cancel").findWhenNeeded(this);
        final WebElement workbenchButton = Locator.linkWithText("Workbench").findWhenNeeded(this);

        ExpectedCondition<Boolean> disabledButtonCondition(WebElement buttonEl)
        {
            return ExpectedConditions.attributeContains(elementCache().saveAndCloseButton, "class", "disabled");
        }
    }
}
