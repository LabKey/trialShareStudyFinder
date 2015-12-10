package org.labkey.trialshare.data;

import java.util.List;

/**
 * Created by susanh on 12/7/15.
 */
public class StudyBean
{
    private String accession;
    private String title;
    private String investigator;
    private String url;
    private Boolean isLoaded;
    private String description;
    private String briefDescription;
    private List<StudyPersonnelBean> personnel;
    private List<StudyPubmedBean> pubmed;
    private String labelPrefix = null; // common prefix used in labeling studies

    public String getAccession()
    {
        return accession;
    }

    public void setAccession(String accession)
    {
        this.accession = accession;
    }

    public String getInvestigator()
    {
        return investigator;
    }

    public void setInvestigator(String investigator)
    {
        this.investigator = investigator;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public Boolean getIsLoaded()
    {
        return isLoaded;
    }

    public void setIsLoaded(Boolean loaded)
    {
        isLoaded = loaded;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getBriefDescription()
    {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription)
    {
        this.briefDescription = briefDescription;
    }

    public List<StudyPersonnelBean> getPersonnel()
    {
        return personnel;
    }

    public void setPersonnel(List<StudyPersonnelBean> personnel)
    {
        this.personnel = personnel;
    }

    public List<StudyPubmedBean> getPubmed()
    {
        return pubmed;
    }

    public void setPubmed(List<StudyPubmedBean> pubmed)
    {
        this.pubmed = pubmed;
    }

    public String getLabelPrefix()
    {
        return labelPrefix;
    }

    public void setLabelPrefix(String labelPrefix)
    {
        this.labelPrefix = labelPrefix;
    }
}

