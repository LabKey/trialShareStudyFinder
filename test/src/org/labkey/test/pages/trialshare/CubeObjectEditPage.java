/*
 * Copyright (c) 2016-2017 LabKey Corporation
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
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.labkey.test.Locator;
import org.labkey.test.components.ext4.Checkbox;
import org.labkey.test.components.ext4.ComboBox;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.LogMethod;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.labkey.test.components.ext4.Checkbox.Ext4Checkbox;

public abstract class CubeObjectEditPage extends LabKeyPage
{
    public static final String NOT_EMPTY_VALUE = "NOT EMPTY VALUE";
    public static final String EMPTY_VALUE = "";

    CubeObjectEditPage(WebDriver driver)
    {
        super(driver);
        addPageLoadListener(new PageLoadListener()
        {
            @Override
            public void afterPageLoad()
            {
                clearCache();
            }
        });
    }

    public void setTextFormValue(String fieldName, String value)
    {
        setTextFormValue(fieldName, value, null);
    }

    public void setTextFormValue(String fieldName, String value, @Nullable Boolean expectSubmitEnabled)
    {
        WebElement field = Locator.name(fieldName).findElement(getDriver());
        setFormElement(field, value);
        fireEvent(field, SeleniumEvent.blur);
        waitFor(() -> !field.getAttribute("class").contains("x4-field-focus"), 1000);
        if (expectSubmitEnabled != null)
        {
            if (!waitFor(() -> expectSubmitEnabled == isSubmitEnabled(), WAIT_FOR_JAVASCRIPT))
                assertEquals("Submit button state not enabled as expected", expectSubmitEnabled, isSubmitEnabled());
        }
    }

    public abstract Map<String, String> getDropdownFieldNames();
    public abstract Map<String, String> getMultiSelectFieldNames();
    public abstract Set<String> getFieldNames();

    @LogMethod
    public void setFormField(@LoggedParam String fieldName, @LoggedParam Object value)
    {
        String expectedValue;
        if (getMultiSelectFieldNames().keySet().contains(fieldName))
        {
            String[] options = (String[]) value;
            String formValue = getFormValue(fieldName);
            List<String> selection = new ArrayList<>(Arrays.asList(formValue.split(";\\s*")));
            for (String option : options)
            {
                if (selection.contains(option))
                    selection.remove(option);
                else
                    selection.add(option);
            }
            selection.remove("");
            expectedValue = String.join("; ", selection);
            if (formValue.isEmpty())
                log("Setting field " + fieldName + " to " + expectedValue);
            else
                log(String.format("Updating field %s: currently \"%s\", toggling [%s]", fieldName, formValue, String.join(", ", options)));

            ComboBox comboBox = findComboBox(fieldName);
            comboBox.toggleComboBoxItems(options);
        }
        else
        {
            expectedValue = (String) value;
            log("Setting field " + fieldName + " to " + expectedValue);
            if (getDropdownFieldNames().keySet().contains(fieldName))
            {
                ComboBox comboBox = findComboBox(fieldName);
                comboBox.selectComboBoxItem(expectedValue);
            }
            else
            {
                setTextFormValue(fieldName, expectedValue);
            }
        }
        assertEquals(expectedValue, getFormValue(fieldName));
        log("Field " + fieldName + " new value is " + getFormValue(fieldName));
    }

    private ComboBox findComboBox(String fieldName)
    {
        String label;
        boolean isMultiSelect;
        if (getDropdownFieldNames().keySet().contains(fieldName))
        {
            label = getDropdownFieldNames().get(fieldName);
            isMultiSelect = false;
        }
        else if (getMultiSelectFieldNames().keySet().contains(fieldName))
        {
            label = getMultiSelectFieldNames().get(fieldName);
            isMultiSelect = true;
        }
        else
        {
            throw new IllegalArgumentException("No combo-box specified for '" + fieldName + "' in " + this.getClass().getSimpleName());
        }

        return new ComboBox.ComboBoxFinder(getDriver()).withLabel(label).find(getDriver()).setMultiSelect(isMultiSelect);
    }

    public void setFormFields(Map<String, Object> fieldMap)
    {
        for (String fieldName : fieldMap.keySet())
        {
            setFormField(fieldName, fieldMap.get(fieldName));
        }
    }

    public String getFormValue(String field)
    {
        elementCache().cancelButton.isDisplayed();
        Locator fieldLocator = Locator.name(field);
        return StringUtils.trimToEmpty(getFormElement(fieldLocator));
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
        elementCache().cancelButton.isDisplayed(); // Make sure
        Map<String, String> formValues = getFormValues();
        Map<String, String> unexpectedValues = new HashMap<>();
        for (String fieldName : expectedValues.keySet())
        {
            String expectedValue;
            if (expectedValues.get(fieldName) instanceof String)
            {
                expectedValue = (String) expectedValues.get(fieldName);
            }
            else // should be an array of Strings
            {
                expectedValue = StringUtils.join((String[]) expectedValues.get(fieldName), "; ");
            }
            String formValue = StringUtils.trimToEmpty(formValues.get(fieldName));
            log("Comparing field values for " + fieldName + " expecting " + expectedValue + " actual " + formValue);
            if (expectedValue.equals(NOT_EMPTY_VALUE))
            {
                if (formValue.isEmpty())
                    unexpectedValues.put(fieldName, "expected: " + expectedValue + " actual: " + formValue);
            }
            else if (!expectedValue.equals(formValue))
            {
                unexpectedValues.put(fieldName, " expected: \"" + expectedValue + "\" but was: \"" + formValue + "\"");
            }
        }
        return unexpectedValues;
    }

    public boolean isSubmitEnabled()
    {
        return !elementCache().saveAndCloseButton.getAttribute("class").contains("disabled");
    }

    public boolean isWorkbenchEnabled()
    {
        return !elementCache().workbenchButton.getAttribute("class").contains("disabled");
    }

    public void cancel()
    {
        log("Cancelling edit");
        clickAndWait(elementCache().cancelButton);
    }

    public void save()
    {
        log("Saving edit form");
        Assert.assertTrue("Save buttons are disabled", isSubmitEnabled());
        clickAndWait(elementCache().saveButton);
    }

    public void saveAndClose(String returnToHeader)
    {
        log("Saving and closing edit form");
        Assert.assertTrue("Save buttons are disabled", isSubmitEnabled());
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

    protected class ElementCache extends LabKeyPage.ElementCache
    {
        final Checkbox showOnDashField = Ext4Checkbox().withLabel("Show on Dashboard:").findWhenNeeded(this);

        // Buttons become stale when becoming enabled or disabled so refindWhenNeeded
        final WebElement saveButton = Locator.linkWithText("Save").refindWhenNeeded(this).withTimeout(10000);
        final WebElement saveAndCloseButton = Locator.linkWithText("Save And Close").refindWhenNeeded(this).withTimeout(10000);
        final WebElement cancelButton = Locator.linkWithText("Cancel").refindWhenNeeded(this).withTimeout(10000);
        final WebElement workbenchButton = Locator.linkWithText("Workbench").refindWhenNeeded(this).withTimeout(10000);
    }
}
