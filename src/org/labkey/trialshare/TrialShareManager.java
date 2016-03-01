/*
 * Copyright (c) 2015 LabKey Corporation
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

package org.labkey.trialshare;

import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.FieldKey;
import org.labkey.api.security.User;
import org.labkey.trialshare.data.StudyAccess;
import org.labkey.trialshare.data.StudyPublicationBean;
import org.labkey.trialshare.query.TrialShareQuerySchema;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrialShareManager
{

    private static final TrialShareManager _instance = new TrialShareManager();

    private TrialShareManager()
    {
        // prevent external construction with a private default constructor
    }

    public static TrialShareManager get()
    {
        return _instance;
    }

    public boolean canSeeOperationalStudies(User user, Container container)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Visibility"), TrialShareQuerySchema.OPERATIONAL_VISIBILITY);
        TableInfo visibilityList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (visibilityList != null)
        {
            List<StudyAccess> containers = (new TableSelector(visibilityList, filter, null)).getArrayList(StudyAccess.class);
            for (StudyAccess study : containers)
            {
                if (study.hasPermission(user))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canSeeIncompleteManuscripts(User user, Container container)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Status"), TrialShareQuerySchema.COMPLETED_STATUS, CompareType.NEQ_OR_NULL);
        TableInfo publicationsList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, filter, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications)
            {
                if (publication.hasPermission(user))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Set<Object> getVisibleStudies(User user, Container container)
    {
        Set<Object> studyIdSet = new HashSet<>();
        TableInfo containerList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (containerList != null)
        {
            List<StudyAccess> studyAccess = (new TableSelector(containerList, null, null)).getArrayList(StudyAccess.class);
            for (StudyAccess study : studyAccess)
            {
                if (study.hasPermission(user))
                {
                    studyIdSet.add(study.getCubeIdentifier());
                }
            }
        }
        return studyIdSet;
    }

    public Set<String> getVisibleStudies(User user, Container container, String visibility)
    {
        Set<String> studyIdSet = new HashSet<>();

        TableInfo studyAccess = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (studyAccess != null)
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Visibility"), visibility);
            List<StudyAccess> studyAccessList = (new TableSelector(studyAccess, filter, null)).getArrayList(StudyAccess.class);
            for (StudyAccess access : studyAccessList)
            {
                if (access.hasPermission(user))
                {
                    studyIdSet.add(access.getStudyId());
                }
            }
        }
        return studyIdSet;
    }

    public Set<Object> getVisibleAssays(User user, Container container)
    {
        Set<Object> idSet = new HashSet<>();

        Set<String> operationalStudyIds = getVisibleStudies(user, container,  TrialShareQuerySchema.OPERATIONAL_VISIBILITY);
        TableInfo assayAccess = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ASSAY_TABLE);
        if (assayAccess != null)
        {
            Map<String, Object> valueMapArray[] = new TableSelector(assayAccess, null, null).getMapArray();
            for (Map<String, Object> valueMap : valueMapArray)
            {
                String visibility = (String) valueMap.get("Visibility");
                String cubeId = "[Study.AssayVisibility].[" + valueMap.get("StudyId") + "]";
                if (visibility == null || (visibility.equalsIgnoreCase(TrialShareQuerySchema.PUBLIC_VISIBILITY)))
                    idSet.add(cubeId);
                else if (visibility.equalsIgnoreCase(TrialShareQuerySchema.OPERATIONAL_VISIBILITY) && operationalStudyIds.contains(valueMap.get("StudyId")))
                    idSet.add(cubeId);
            }
        }
        return idSet;
    }

    public Set<Object> getVisiblePublications(User user, Container container)
    {
        Set<Object> publicationIds  = new HashSet<>();
        TableInfo publicationsList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, null, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications) {
                if (publication.getShow() && publication.hasPermission(user))
                {
                    publicationIds.add(publication.getCubeId());
                }
            }
        }
        return publicationIds;
    }
}