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
import org.labkey.trialshare.data.StudyPublicationBean;

import java.util.ArrayList;
import java.util.List;

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
        filter.addCondition(FieldKey.fromParts("Visibility"), "Operational");
        TableInfo propertiesList = coreSchema.getSchema("lists").getTable("studyProperties");
        if (propertiesList != null)
        {
            List<StudyBean> studies = (new TableSelector(propertiesList, filter, null)).getArrayList(StudyBean.class);
            for (StudyBean study : studies)
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
        filter.addCondition(FieldKey.fromParts("Status"), "Complete", CompareType.NEQ_OR_NULL);
        TableInfo publicationsList = coreSchema.getSchema("lists").getTable("ManuscriptsAndAbstracts");
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

    public List<Object> getVisiblePublications(User user, Container container)
    {
        List<Object> publicationIds  = new ArrayList<>();
        QuerySchema coreSchema = DefaultSchema.get(user, container).getSchema("core");
        SimpleFilter filter = new SimpleFilter();
        TableInfo publicationsList = coreSchema.getSchema("lists").getTable("ManuscriptsAndAbstracts");
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, filter, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications) {
                if (publication.getShow())
                {
                    if (publication.getPermissionsContainer() == null)
                        publicationIds.add("[Publication].[" + publication.getId() + "]");
                    else
                    {
                        Container permissionsContainer = ContainerManager.getForId(publication.getPermissionsContainer());
                        if (permissionsContainer == null)
                            publicationIds.add("[Publication].[" + publication.getId() + "]");
                        else if (publication.getStatus().equalsIgnoreCase("In Progress"))
                        {
                            if (permissionsContainer.hasPermission(user, InsertPermission.class))
                                publicationIds.add("[Publication].[" + publication.getId() + "]");
                        }
                        else if (permissionsContainer.hasPermission(user, ReadPermission.class))
                            publicationIds.add("[Publication].[" + publication.getId() + "]");
                    }
                }
            };
        }
        return publicationIds;
    }
}