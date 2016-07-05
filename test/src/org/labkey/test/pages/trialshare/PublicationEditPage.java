package org.labkey.test.pages.trialshare;

import org.apache.commons.lang3.StringUtils;
import org.labkey.test.Locator;
import org.labkey.test.pages.LabKeyPage;
import org.labkey.test.util.Ext4Helper;
import org.labkey.test.util.LoggedParam;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by susanh on 6/30/16.
 */
public class PublicationEditPage extends LabKeyPage
{
    public static final String NOT_EMPTY_VALUE = "NOT EMPTY VALUE";
    public static final String EMPTY_VALUE = "EMPTY VALUE";

    public static final String TITLE = "title";
    public static final String PUBLICATION_TYPE ="publicationType";
    public static final String STATUS ="status";
    public static final String SUBMISSION_STATUS ="submissionStatus";
    public static final String AUTHOR ="author";
    public static final String CITATION ="citation";
    public static final String YEAR ="year";
    public static final String JOURNAL ="journal";
    public static final String ABSTRACT ="abstractText";
    public static final String DOI ="DOI";
    public static final String PMID ="PMID";
    public static final String PMCID ="PMCID";
    public static final String MANUSCRIPT_CONTAINER ="manuscriptContainer";
    public static final String PERMISSIONS_CONTAINER  ="permissionsContainer";
    public static final String KEYWORDS ="keywords";
    public static final String STUDIES ="studyIds";
    public static final String CONDITIONS ="conditions";
    public static final String THERAPEUTIC_AREAS ="therapeuticAreas";
    public static final String LINK1 ="link1";
    public static final String DESCRIPTION1 ="description1";
    public static final String LINK2 ="link2";
    public static final String DESCRIPTION2 ="description2";
    public static final String LINK3 ="link3";
    public static final String DESCRIPTION3="description3";

    public static final Map<String, String> DROPDOWN_FIELD_NAMES = new HashMap<>();
    static
    {
        DROPDOWN_FIELD_NAMES.put(PUBLICATION_TYPE, "Publication Type *:");
        DROPDOWN_FIELD_NAMES.put(STATUS, "Status *:");
        DROPDOWN_FIELD_NAMES.put(SUBMISSION_STATUS, "Submission Status:");
        DROPDOWN_FIELD_NAMES.put(MANUSCRIPT_CONTAINER, "Manuscript Container:");
        DROPDOWN_FIELD_NAMES.put(PERMISSIONS_CONTAINER, "Permissions Container:");
    }

    public static final Map<String, String> MULTI_SELECT_FIELD_NAMES = new HashMap<>();
    static
    {
        MULTI_SELECT_FIELD_NAMES.put(STUDIES, "Studies:");
        MULTI_SELECT_FIELD_NAMES.put(CONDITIONS, "Conditions:");
        MULTI_SELECT_FIELD_NAMES.put(THERAPEUTIC_AREAS, "Therapeutic Areas:");
    }

    public static final Set<String> FIELD_NAMES = new HashSet<>();
    static
    {
        FIELD_NAMES.add(TITLE);
        FIELD_NAMES.add(PUBLICATION_TYPE);
        FIELD_NAMES.add(STATUS);
        FIELD_NAMES.add(SUBMISSION_STATUS);
        FIELD_NAMES.add(AUTHOR);
        FIELD_NAMES.add(CITATION);
        FIELD_NAMES.add(YEAR);
        FIELD_NAMES.add(JOURNAL);
        FIELD_NAMES.add(ABSTRACT);
        FIELD_NAMES.add(DOI);
        FIELD_NAMES.add(PMID);
        FIELD_NAMES.add(PMCID);
        FIELD_NAMES.add(MANUSCRIPT_CONTAINER);
        FIELD_NAMES.add(PERMISSIONS_CONTAINER);
        FIELD_NAMES.add(KEYWORDS);
        FIELD_NAMES.add(STUDIES);
        FIELD_NAMES.add(CONDITIONS);
        FIELD_NAMES.add(THERAPEUTIC_AREAS);
        FIELD_NAMES.add(LINK1);
        FIELD_NAMES.add(DESCRIPTION1);
        FIELD_NAMES.add(LINK2);
        FIELD_NAMES.add(DESCRIPTION2);
        FIELD_NAMES.add(LINK3);
        FIELD_NAMES.add(DESCRIPTION3);
    }

    public PublicationEditPage(WebDriver driver)
    {
        super(driver);
    }

    public void setTextFormValue(String key, String value)
    {
        Locator fieldLocator = Locator.name(key);
        setFormElement(fieldLocator, value);
    }

    public void selectMenuItem(String label, String value)
    {
        _ext4Helper.selectComboBoxItem(label, value);
    }

    public void setFormField(String key, Object value)
    {
        log("Setting field " + key + " to " + value);
        if (DROPDOWN_FIELD_NAMES.keySet().contains(key))
            _ext4Helper.selectComboBoxItem(DROPDOWN_FIELD_NAMES.get(key), (String) value);
        else if (MULTI_SELECT_FIELD_NAMES.keySet().contains(key))
            multiSelectComboBoxItem(MULTI_SELECT_FIELD_NAMES.get(key), (String[]) value);
        else
            setTextFormValue(key, (String) value);
    }

    // the similar method in ext4Helper is looking for a property that does not exist to
    // decide if this is a multi-select box or not.
    public void multiSelectComboBoxItem(String label, @LoggedParam String... selections)
    {
        Locator.XPathLocator comboBox = Ext4Helper.Locators.formItemWithLabel(label);
        _ext4Helper.openComboList(comboBox);

        try
        {
            for (String selection : selections)
            {
                _ext4Helper.selectItemFromOpenComboList(selection, Ext4Helper.TextMatchTechnique.EXACT);
            }
        }
        catch (StaleElementReferenceException retry) // Combo-box might still be loading previous selection (no good way to detect)
        {
            for (String selection : selections)
            {
                _ext4Helper.selectItemFromOpenComboList(selection, Ext4Helper.TextMatchTechnique.EXACT);
            }
        }

        Locator arrowTrigger = comboBox.append("//div[contains(@class,'arrow')]");
        arrowTrigger.findElement(this.getDriver()).click();
    }

    public void setFormFields(Map<String, Object> fieldMap, Boolean showOnDashboard)
    {
        log("Setting form fields");
        for (String key : fieldMap.keySet())
        {
            setFormField(key, fieldMap.get(key));
        }
        if (showOnDashboard)
        {
            checkShowOnDashboard();
        }
    }

    public void checkShowOnDashboard()
    {
        _ext4Helper.checkCheckbox("Show on Dashboard:");
    }

    public Map<String, String> getFormValues()
    {
        Map<String, String> formValues = new HashMap<>();
        for (String field : FIELD_NAMES)
        {
            Locator fieldLocator = Locator.name(field);
            formValues.put(field, getFormElement(fieldLocator));
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
        log("Cancelling publication edit");
        Locators.cancelButton.findElement(getDriver()).click();
    }

    public void submit()
    {
        log("Submitting publication edit form");
        click(Locators.submitButton);
        sleep(1000);
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
