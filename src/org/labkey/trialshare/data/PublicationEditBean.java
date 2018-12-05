/*
 * Copyright (c) 2016-2018 LabKey Corporation
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by susanh on 6/20/16.
 */
public class PublicationEditBean extends StudyPublicationBean
{
    private List<String> _studyIds = new ArrayList<>();
    private List<String> _conditions = new ArrayList<>();
    private List<String> _therapeuticAreas = new ArrayList<>();

    public PublicationEditBean() {}

    public PublicationEditBean(StudyPublicationBean base)
    {
        setPrimaryFields(base.getPrimaryFields());
        setUrls(base.getUrls());
    }

    public List<String> getConditions()
    {
        return _conditions;
    }

    public void setConditions(List<String> conditions)
    {
        _conditions = conditions;
    }

    public List<String> getStudyIds()
    {
        return _studyIds;
    }

    public void setStudyIds(List<String> studyIds)
    {
        _studyIds = studyIds;
    }

    public List<String> getTherapeuticAreas()
    {
        return _therapeuticAreas;
    }

    public void setTherapeuticAreas(List<String> therapeuticAreas)
    {
        _therapeuticAreas = therapeuticAreas;
    }
}
