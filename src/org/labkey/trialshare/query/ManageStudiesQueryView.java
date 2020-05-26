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
package org.labkey.trialshare.query;

import org.labkey.api.data.Container;
import org.labkey.api.data.TableInfo;
import org.labkey.api.query.QueryView;
import org.labkey.api.security.User;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;
import org.springframework.validation.BindException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by susanh on 6/14/16.
 */
public class ManageStudiesQueryView extends ManageCubeObjectQueryView
{
    private static final Set<String> _defaultColumns = new HashSet<>();
    static
    {
        _defaultColumns.add("shortName");
        _defaultColumns.add("studyId");
        _defaultColumns.add("title");
    }

    public ManageStudiesQueryView(ViewContext context, BindException errors)
    {
        super(context, errors);
        setSettings(getSchema().getSettings(context, QueryView.DATAREGIONNAME_DEFAULT, TrialShareQuerySchema.STUDY_TABLE));
    }

    @Override
    protected TableInfo getTable(User user, Container container)
    {
        return new TrialShareQuerySchema(user, container).getStudyPropertiesTableInfo();
    }

    @Override
    protected TrialShareController.ObjectName getCubeObjectName()
    {
        return TrialShareController.ObjectName.study;
    }

    @Override
    protected String getKeyField() { return "StudyId"; }

    @Override
    protected Set<String> getDefaultColumns()
    {
        return _defaultColumns;
    }
}
