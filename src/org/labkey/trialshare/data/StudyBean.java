package org.labkey.trialshare.data;

import org.apache.commons.lang3.StringUtils;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerFilter;
import org.labkey.api.data.ContainerFilterable;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;
import org.labkey.api.view.ActionURL;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by susanh on 12/7/15.
 */
public class StudyBean
{
    private String shortName;
    private String studyId;
    private String title;
    private String investigator;
    private String externalUrl;
    private String externalUrlDescription;
    private String iconUrl;
    private Boolean isLoaded;
    private String description;
    private String briefDescription;
    private String studyIdPrefix = null; // common prefix used in labeling studies
    private String availability;
    private Boolean isPublic;
    private Integer participantCount;
    private Boolean isSelected = true;

    private List<StudyPersonnelBean> personnel;
    private List<StudyPublicationBean> publications;
    private Integer manuscriptCount;
    private Integer abstractCount;


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

    public String getExternalUrl()
    {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl)
    {
        this.externalUrl = externalUrl;
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

    public Integer getAbstractCount()
    {
        return abstractCount;
    }

    public void setAbstractCount(Integer abstractCount)
    {
        this.abstractCount = abstractCount;
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

    public ActionURL getUrl(Container c, User user)
    {
        if (!c.isRoot())
        {
            String comma = "\n";
            Container p = c.getProject();
            QuerySchema s = DefaultSchema.get(user, p).getSchema("study");
            TableInfo sp = s.getTable("StudyProperties");
            if (sp.supportsContainerFilter())
            {
                ContainerFilter cf = new ContainerFilter.AllInProject(user);
                ((ContainerFilterable) sp).setContainerFilter(cf);
            }
            Collection<Map<String, Object>> maps = new TableSelector(sp).getMapCollection();
            for (Map<String, Object> map : maps)
            {
                Container studyContainer = ContainerManager.getForId((String) map.get("container"));
                String studyAccession = (String)map.get("study_accession");
                // TODO study properties does not have the studyId in it...
                String name = (String)map.get("Label");
                if (null == studyAccession && getStudyIdPrefix() != null && name.startsWith(getStudyIdPrefix()))
                    studyAccession = name;
                if (null != studyContainer && StringUtils.equalsIgnoreCase(getStudyId(), studyAccession))
                {
                    return studyContainer.getStartURL(user);
                }
            }
        }
        return null;
    }

    public String getExternalUrlDescription()
    {
        return externalUrlDescription;
    }

    public void setExternalUrlDescription(String externalUrlDescription)
    {
        this.externalUrlDescription = externalUrlDescription;
    }
}

