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

import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerFilter;
import org.labkey.api.data.ContainerFilterable;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.services.ServiceRegistry;
import org.labkey.api.wiki.WikiRendererType;
import org.labkey.api.wiki.WikiService;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by susanh on 12/7/15.
 */
public class StudyBean
{
    private static final String STUDY_TYPE_KEY = "studyType";
    private static final String SHORT_NAME_FIELD = "shortName";
    private static final String STUDY_ID_FIELD = "studyId";
    private static final String TITLE_FIELD = "title";
    private static final String INVESTIGATOR_FIELD = "investigator";
    private static final String EXTERNAL_URL_FIELD = "externalUrl";
    private static final String EXTERNAL_URL_DESCRIPTION_FIELD = "externalUrlDescription";
    private static final String ICON_URL_FIELD = "iconUrl";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String PARTICIPANT_COUNT_FIELD = "participantCount";

    private Map<String, Object> _primaryFields = new CaseInsensitiveHashMap<>();


    private String url;
    private Boolean isLoaded;
    private String briefDescription;
    private String studyIdPrefix = null; // common prefix used in labeling studies
    private String availability;
    private Boolean isHighlighted = false;
    private String visibility;
    private Boolean isPublic = false;
    private List<StudyAccess> _studyAccessList = new ArrayList<>();

    private List<StudyPersonnelBean> personnel;
    private List<StudyPublicationBean> publications = new ArrayList<>();
    private Integer manuscriptCount;
    private Integer abstractCount;


    public String getStudyId()
    {
        return (String) _primaryFields.get(STUDY_ID_FIELD);
    }

    public void setStudyId(String studyId)
    {
        _primaryFields.put(STUDY_ID_FIELD, studyId);
    }

    public String getStudyType() { return (String) _primaryFields.get(STUDY_TYPE_KEY); }

    public void setStudyType(String studyType) { _primaryFields.put(STUDY_TYPE_KEY, studyType); }

    public String getInvestigator()
    {
        return (String) _primaryFields.get(INVESTIGATOR_FIELD);
    }

    public void setInvestigator(String investigator)
    {
        _primaryFields.put(INVESTIGATOR_FIELD, investigator);
    }

    public String getTitle()
    {
        return (String) _primaryFields.get(TITLE_FIELD);
    }

    public void setTitle(String title)
    {
        _primaryFields.put(TITLE_FIELD, title);
    }

    public String getExternalUrl()
    {
        return (String) _primaryFields.get(EXTERNAL_URL_FIELD);
    }

    public void setExternalUrl(String externalUrl)
    {
        _primaryFields.put(EXTERNAL_URL_FIELD, externalUrl);
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

    public void setPublications(User user, Container container, @Nullable String publicationType)
    {
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        QuerySchema listSchema = coreSchema.getSchema("lists");

        if (listSchema != null)
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("studyId"), getStudyId());
            if (publicationType != null)
            {
                filter.addCondition(FieldKey.fromParts("PublicationType"), publicationType);
            }
            List<StudyPublicationBean> allPublications = (new TableSelector(listSchema.getTable(TrialShareQuerySchema.PUBLICATION_TABLE), filter, null)).getArrayList(StudyPublicationBean.class);
            this.publications.clear();
            for (StudyPublicationBean publication : allPublications)
            {
                if (publication.getShow() && publication.hasPermission(user))
                    this.publications.add(publication);
            }
        }
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
        return (String) _primaryFields.get(SHORT_NAME_FIELD);
    }

    public void setShortName(String shortName)
    {
        _primaryFields.put(SHORT_NAME_FIELD, shortName);
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
        return (String) _primaryFields.get(ICON_URL_FIELD);
    }

    public void setIconUrl(String iconUrl)
    {
        _primaryFields.put(ICON_URL_FIELD, iconUrl);
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
        return (Integer) _primaryFields.get(PARTICIPANT_COUNT_FIELD);
    }

    public void setParticipantCount(Integer participantCount)
    {
        _primaryFields.put(PARTICIPANT_COUNT_FIELD, participantCount);
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public void setUrl(User user, Boolean linkPublicStudies)
    {
        this.url = null;
        if (getStudyAccessList() == null)
            return;

        for (StudyAccess studyAccess: getStudyAccessList())
        {
            Container studyContainer = ContainerManager.getForId(studyAccess.getStudyContainer());
            if (studyContainer != null && studyContainer.hasPermission(user, ReadPermission.class))
            {
                if (studyAccess.getVisibility().equalsIgnoreCase(TrialShareQuerySchema.OPERATIONAL_VISIBILITY))
                    this.url = studyContainer.getStartURL(user).toString();
                else if (url == null && linkPublicStudies)
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
           setUrl(user, true);
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

    public String getDescription()
    {
        return (String) _primaryFields.get(DESCRIPTION_FIELD);
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
        _primaryFields.put(DESCRIPTION_FIELD, description);
    }


    public String getExternalUrlDescription()
    {
        return (String) _primaryFields.get(EXTERNAL_URL_DESCRIPTION_FIELD);
    }

    public void setExternalUrlDescription(String externalUrlDescription)
    {
        _primaryFields.put(EXTERNAL_URL_DESCRIPTION_FIELD, externalUrlDescription);
    }

    public List<StudyAccess> getStudyAccessList()
    {
        return _studyAccessList;
    }

    public void setStudyAccessList(List<StudyAccess> studyAccessList)
    {
        this._studyAccessList = studyAccessList;
    }

    public void setStudyAccessList(User user, Container currentContainer)
    {
        QuerySchema listSchema = TrialShareQuerySchema.getSchema(user, currentContainer);

        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("studyId"), getStudyId());

        TableInfo studyAccessTable = listSchema.getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        List<StudyAccess> studyAccessList = (new TableSelector(studyAccessTable, filter, null)).getArrayList(StudyAccess.class);
        this._studyAccessList.clear();
        for (StudyAccess studyAccess : studyAccessList)
        {
            Container container = ContainerManager.getForId(studyAccess.getStudyContainer());
            if (container != null && container.hasPermission(user, ReadPermission.class))
            {
                this._studyAccessList.add(studyAccess);
            }
        }
    }

    public Map<String, Object> getPrimaryFields()
    {
        return _primaryFields;
    }

    public void validate(Errors errors)
    {
// TODO
    }
}

