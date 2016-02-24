/*
 * Copyright (c) 2015-2016 LabKey Corporation
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
package org.labkey.trialshare.data;

import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerFilter;
import org.labkey.api.data.ContainerFilterable;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.services.ServiceRegistry;
import org.labkey.api.wiki.WikiRendererType;
import org.labkey.api.wiki.WikiService;
import org.labkey.trialshare.query.TrialShareQuerySchema;

import java.util.Collection;
import java.util.Collections;
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
    private String url;
    private String investigator;
    private String externalUrl;
    private String externalUrlDescription;
    private String iconUrl;
    private Boolean isLoaded;
    private String description;
    private String briefDescription;
    private String studyIdPrefix = null; // common prefix used in labeling studies
    private String availability;
    private Boolean isHighlighted = false;
    private String visibility;
    private Boolean isPublic = false;
    private Integer participantCount;
    private List<StudyContainer> studyContainers;

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

    public Boolean getIsHighlighted()
    {
        return isHighlighted;
    }

    public void setIsHighlighted(Boolean highlighted)
    {
        isHighlighted = highlighted;
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

    public String getVisibility()
    {
        return visibility;
    }

    public void setVisibility(String visibility)
    {
        this.visibility = visibility;
    }

    public Boolean getIsPublic()
    {
        return isPublic;
    }

    public Boolean getIsBorderHighlighted()
    {
        return !isPublic;
    }

    public void setIsPublic(Boolean aPublic)
    {
        isPublic = aPublic == null ? false : aPublic;
    }

    public Integer getParticipantCount()
    {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount)
    {
        this.participantCount = participantCount;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUrl(User user)
    {
        this.url = null;
        if (getStudyContainers() == null)
            return;

        for (StudyContainer container: getStudyContainers())
        {
            Container studyContainer = ContainerManager.getForId(container.getStudyContainer());
            if (studyContainer != null && studyContainer.hasPermission(user, ReadPermission.class))
            {
                if (container.getVisibility().equalsIgnoreCase(TrialShareQuerySchema.OPERATIONAL_VISIBILITY))
                    this.url = studyContainer.getStartURL(user).toString();
                else if (url == null)
                    this.url = studyContainer.getStartURL(user).toString();
            }
        }
    }

    public String getUrl()
    {
        return this.url;
    }

    public String getUrl(User user)
    {
        if (url == null)
        {
           setUrl(user);
        }
        return url;
    }


    public static Collection<Map<String, Object>> getStudyProperties(Container c, User user)
    {
        if (!c.isRoot())
        {
            Container p = c.getProject();
            QuerySchema s = DefaultSchema.get(user, p).getSchema("study");
            TableInfo sp = s.getTable("StudyProperties");
            if (sp.supportsContainerFilter())
            {
                ContainerFilter cf = new ContainerFilter.AllInProject(user);
                ((ContainerFilterable) sp).setContainerFilter(cf);
            }
            return new TableSelector(sp).getMapCollection();
        }
        return Collections.emptyList();
    }

    public String getDescription(Container c, User user)
    {
        return description;
    }

    private String getFormattedHtml(WikiRendererType rendererType, String markup)
    {
        WikiService wikiService = ServiceRegistry.get().getService(WikiService.class);

        if (null == wikiService)
            return null;

        if (rendererType == null)
            rendererType = wikiService.getDefaultMessageRendererType();

        return wikiService.getFormattedHtml(rendererType, markup);
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public String getExternalUrlDescription()
    {
        return externalUrlDescription;
    }

    public void setExternalUrlDescription(String externalUrlDescription)
    {
        this.externalUrlDescription = externalUrlDescription;
    }

    public List<StudyContainer> getStudyContainers()
    {
        return studyContainers;
    }

    public void setStudyContainers(List<StudyContainer> studyContainers)
    {
        this.studyContainers = studyContainers;
    }
}

