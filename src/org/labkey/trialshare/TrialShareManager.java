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

import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableSelector;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.trialshare.data.StudyBean;

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
        filter.addCondition(FieldKey.fromParts("isPublic"), false);
        List<StudyBean> studies  = (new TableSelector(coreSchema.getSchema("lists").getTable("studyProperties"), filter, null)).getArrayList(StudyBean.class);
        for (StudyBean study : studies)
        {
            if (study.getStudyContainer() != null)
            {
                Container studyContainer = ContainerManager.getForId(study.getStudyContainer());
                if (studyContainer.hasPermission(user, ReadPermission.class))
                    return true;
            }
        }

        return false;
    }
}