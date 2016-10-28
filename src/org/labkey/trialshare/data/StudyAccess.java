/*
 * Copyright (c) 2016 LabKey Corporation
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
import org.labkey.api.data.ContainerManager;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.ReadPermission;

/**
 * Created by susanh on 2/23/16.
 */
public class StudyAccess
{
    private Integer _key;
    private String _studyId;
    private String _visibility;
    private String _studyContainer;
    private String _displayName;

    public Integer getKey()
    {
        return _key;
    }

    public void setKey(Integer key)
    {
        _key = key;
    }

    public void set_Key(Integer key) // this is necessary for SQLServer
    {
        _key = key;
    }


    public String getStudyContainer()
    {
        return _studyContainer;
    }

    public void setStudyContainer(String studyContainer)
    {
        _studyContainer = studyContainer;
    }

    public String getStudyId()
    {
        return _studyId;
    }

    public void setStudyId(String studyId)
    {
        _studyId = studyId;
    }

    public String getVisibility()
    {
        return _visibility;
    }

    public void setVisibility(String visibility)
    {
        _visibility = visibility;
    }

    public String getDisplayName()
    {
        return _displayName;
    }

    public void setDisplayName(String displayName)
    {
        _displayName = displayName;
    }

    public String getStudyContainerPath()
    {
        Container container = ContainerManager.getForId(getStudyContainer());
        if (container != null)
            return container.getPath();
        return null;
    }

    public boolean hasPermission(User user)
    {
        if (getStudyContainer() != null)
        {
            Container studyContainer = ContainerManager.getForId(getStudyContainer());
            if (studyContainer != null && studyContainer.hasPermission(user, ReadPermission.class))
                return true;
        }
        return false;
    }

    public String getCubeContainerIdentifier()
    {
        return "[Study].[" + getStudyContainer() + "]";
    }

    public String getCubeIdentifier()
    {
        return "[Study].[" + getStudyId() + "]";
    }
}
