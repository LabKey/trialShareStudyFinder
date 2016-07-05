package org.labkey.test.pages.trialshare;

import org.openqa.selenium.WebDriver;

import java.util.Map;

/**
 * Created by susanh on 6/30/16.
 */
public class PublicationEditPage extends CubeObjectEditPage
{
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
    public static final String THERAPEUTIC_AREAS ="therapeuticAreas";
    public static final String LINK1 ="link1";
    public static final String DESCRIPTION1 ="description1";
    public static final String LINK2 ="link2";
    public static final String DESCRIPTION2 ="description2";
    public static final String LINK3 ="link3";
    public static final String DESCRIPTION3="description3";

    static
    {
        DROPDOWN_FIELD_NAMES.put(PUBLICATION_TYPE, "Publication Type *:");
        DROPDOWN_FIELD_NAMES.put(STATUS, "Status *:");
        DROPDOWN_FIELD_NAMES.put(SUBMISSION_STATUS, "Submission Status:");
        DROPDOWN_FIELD_NAMES.put(MANUSCRIPT_CONTAINER, "Manuscript Container:");
        DROPDOWN_FIELD_NAMES.put(PERMISSIONS_CONTAINER, "Permissions Container:");
    }

    static
    {
        MULTI_SELECT_FIELD_NAMES.put(STUDIES, "Studies:");
        MULTI_SELECT_FIELD_NAMES.put(THERAPEUTIC_AREAS, "Therapeutic Areas:");
    }

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

    public void setFormFields(Map<String, Object> fieldMap, Boolean showOnDashboard)
    {
        setFormFields(fieldMap);
        if (showOnDashboard)
        {
            checkShowOnDashboard();
        }
    }

    private void checkShowOnDashboard()
    {
        _ext4Helper.checkCheckbox("Show on Dashboard:");
    }
}
