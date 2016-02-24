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
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.trialshare.data.StudyBean;
import org.labkey.trialshare.data.StudyContainer;
import org.labkey.trialshare.data.StudyPublicationBean;
import org.labkey.trialshare.query.TrialShareQuerySchema;

import java.util.HashSet;
import java.util.List;
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
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Visibility"), TrialShareQuerySchema.OPERATIONAL_VISIBILITY);
        TableInfo propertiesList = coreSchema.getSchema("lists").getTable(TrialShareQuerySchema.STUDY_CONTAINER_TABLE);
        if (propertiesList != null)
        {
            List<StudyContainer> containers = (new TableSelector(propertiesList, filter, null)).getArrayList(StudyContainer.class);
            for (StudyContainer study : containers)
            {
                if (study.getStudyContainer() != null)
                {
                    Container studyContainer = ContainerManager.getForId(study.getStudyContainer());
                    if (studyContainer != null && studyContainer.hasPermission(user, ReadPermission.class))
                        return true;
                }
            }
        }

        return false;
    }

    public boolean canSeeIncompleteManuscripts(User user, Container container)
    {
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Status"), TrialShareQuerySchema.COMPLETED_STATUS, CompareType.NEQ_OR_NULL);
        TableInfo publicationsList = coreSchema.getSchema("lists").getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, filter, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications)
            {
                if (publication.getPermissionsContainer() != null)
                {
                    Container permissionsContainer = ContainerManager.getForId(publication.getPermissionsContainer());
                    if (permissionsContainer == null || permissionsContainer.hasPermission(user, InsertPermission.class))
                        return true;
                }
            }
        }

        return false;
    }

    public Set<Object> getVisibleStudies(User user, Container container)
    {
        Set<Object> studyIdSet = new HashSet<>();
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        TableInfo containerList = coreSchema.getSchema("lists").getTable(TrialShareQuerySchema.STUDY_CONTAINER_TABLE);
        if (containerList != null)
        {
            (new TableSelector(containerList, null, null)).forEachMap((map) -> {
                String identifier = "[Study].[" + map.get("StudyId") + "]";
                if (map.get("StudyContainer") == null)
                {
                    studyIdSet.add(identifier);
                }
                else
                {
                    Container permissionsContainer = ContainerManager.getForId((String) map.get("StudyContainer"));
                    if (permissionsContainer == null)
                        studyIdSet.add(identifier);
                    else if (permissionsContainer.hasPermission(user, ReadPermission.class))
                    {
                        studyIdSet.add(identifier);
                    }
                }
            }
            );

        }
        return studyIdSet;
    }

    public Set<Object> getVisiblePublications(User user, Container container)
    {
        Set<Object> publicationIds  = new HashSet<>();
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        TableInfo publicationsList = coreSchema.getSchema("lists").getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, null, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications) {
                String identifier = "[Publication].[" + publication.getId() + "]";
                if (publication.getShow())
                {
                    if (publication.getPermissionsContainer() == null)
                        publicationIds.add(identifier);
                    else
                    {
                        Container permissionsContainer = ContainerManager.getForId(publication.getPermissionsContainer());
                        if (permissionsContainer == null)
                            publicationIds.add(identifier);
                        else if (publication.getStatus().equalsIgnoreCase(TrialShareQuerySchema.IN_PROGRESS_STATUS))
                        {
                            if (permissionsContainer.hasPermission(user, InsertPermission.class))
                                publicationIds.add(identifier);
                        }
                        else if (permissionsContainer.hasPermission(user, ReadPermission.class))
                            publicationIds.add(identifier);
                    }
                }
            };
        }
        return publicationIds;
    }
}