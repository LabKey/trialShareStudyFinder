package org.labkey.trialshare.data;

import java.util.List;

/**
 * Created by susanh on 12/7/15.
 */
public class StudyBean
{
    private String shortName;
    private String studyId;
    private String title;
    private Boolean hasManuscript; // TODO generalize?
    private String investigator;
    private String url;
    private String iconUrl;
    private Boolean isLoaded;
    private String description;
    private String briefDescription;
    private String studyIdPrefix = null; // common prefix used in labeling studies
    private String availability;
    private Boolean isPublic;
    private Integer participantCount;
    private Boolean isSelected = true;

    private List<StudyPersonnelBean> personnel; // TODO remove?
    private List<StudyPublicationBean> publications;
    private Integer manuscriptCount;


    public String getStudyId()
    {
        return studyId;
    }

    public void setStudyId(String studyId)
    {
        this.studyId = studyId;
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

    public List<StudyPublicationBean> getPublications()
    {
        return publications;
    }

    public void setPublications(List<StudyPublicationBean> publications)
    {
        this.publications = publications;
    }

    public String getStudyIdPrefix()
    {
        return studyIdPrefix;
    }

    public void setStudyIdPrefix(String studyIdPrefix)
    {
        this.studyIdPrefix = studyIdPrefix;
    }

    public Boolean getHasManuscript()
    {
        return hasManuscript;
    }

    public void setHasManuscript(Boolean hasManuscript)
    {
        this.hasManuscript = hasManuscript;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public String getAvailability()
    {
        return availability;
    }

    public void setAvailability(String availability)
    {
        this.availability = availability;
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl)
    {
        this.iconUrl = iconUrl;
    }

    public Integer getManuscriptCount()
    {
        return manuscriptCount;
    }

    public void setManuscriptCount(Integer manuscriptCount)
    {
        this.manuscriptCount = manuscriptCount;
    }

    public Boolean getIsPublic()
    {
        return isPublic;
    }

    public void setIsPublic(Boolean aPublic)
    {
        isPublic = aPublic;
    }

    public Integer getParticipantCount()
    {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount)
    {
        this.participantCount = participantCount;
    }

    public Boolean getIsSelected()
    {
        return isSelected;
    }

    public void setIsSelected(Boolean selected)
    {
        isSelected = selected;
    }
}

