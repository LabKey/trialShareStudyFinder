/*
 * Copyright (c) 2016-2019 LabKey Corporation
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

import org.labkey.api.data.ActionButton;
import org.labkey.api.data.ButtonBar;
import org.labkey.api.data.Container;
import org.labkey.api.data.TableInfo;
import org.labkey.api.query.DetailsURL;
import org.labkey.api.query.QueryAction;
import org.labkey.api.query.QueryView;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.DeletePermission;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.DataView;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;
import org.labkey.trialshare.TrialShareManager;
import org.springframework.validation.BindException;

import java.util.Collections;
import java.util.Set;

/**
 * Created by susanh on 6/14/16.
 */
abstract class ManageCubeObjectQueryView extends QueryView
{
    ManageCubeObjectQueryView(ViewContext context, BindException errors)
    {
        super(TrialShareQuerySchema.getUserSchema(context.getUser(), context.getContainer()));
        Container _cubeContainer = TrialShareManager.get().getCubeContainer(getContainer());

        setShowInsertNewButton(true);
        setShowImportDataButton(false);
        setShowExportButtons(false);
        setShowDetailsColumn(true);
        Boolean showUpdate = _cubeContainer != null && _cubeContainer.hasPermission(getUser(), InsertPermission.class);
        if (showUpdate)
        {
            setShowDetailsColumn(true);
            setDetailsURL(DetailsURL.fromString("/trialshare/viewData.view?id=${" + getKeyField() + "}&objectName=" + getCubeObjectName().toString()).toString());

            setShowUpdateColumn(true);
            setUpdateURL(DetailsURL.fromString("/trialshare/updateData.view?id=${" + getKeyField() + "}&objectName=" + getCubeObjectName().toString()).toString());
        }
    }

    protected abstract TrialShareController.ObjectName getCubeObjectName();

    protected abstract TableInfo getTable(User user, Container container);

    @Override
    protected boolean canInsert()
    {
        TableInfo table = getTable(getUser(), getContainer());
        return table != null && table.hasPermission(getUser(), InsertPermission.class);
    }

    @Override
    protected boolean canDelete()
    {
        TableInfo table = getTable(getUser(), getContainer());
        return table != null && table.hasPermission(getUser(), DeletePermission.class);
    }

    @Override
    protected void populateButtonBar(DataView view, ButtonBar bar)
    {
        super.populateButtonBar(view, bar);
        addRefreshCubeButton(bar);
    }

    private void addRefreshCubeButton(ButtonBar bar)
    {
        ActionButton refreshButton = new ActionButton(TrialShareController.CubeAdminAction.class, "Refresh Cube", ActionButton.Action.POST);
        refreshButton.setDisplayPermission(InsertPermission.class);
        ActionURL refreshURL = new ActionURL(TrialShareController.CubeAdminAction.class, getViewContext().getContainer());
        refreshURL.addParameter("method", "reindex,clearCache");
        refreshURL.addParameter(ActionURL.Param.returnUrl, getViewContext().getActionURL().toString());
        refreshButton.setURL(refreshURL);
        refreshButton.setActionType(ActionButton.Action.POST);
        refreshButton.setRequiresSelection(false);
        bar.add(refreshButton);
    }

    protected abstract String getKeyField();

    protected Set<String> getDefaultColumns()
    {
        return Collections.emptySet();
    }

    @Override
    protected ActionURL urlFor(QueryAction action)
    {
        if (action.equals(QueryAction.deleteQueryRows))
        {
            ActionURL url = super.urlFor(action);
            url.setAction(TrialShareController.DeleteCubeObjectsAction.class).addParameter("objectName", getCubeObjectName().toString());
            return url;
        }
        else if (action.equals(QueryAction.insertQueryRow))
        {
            ActionURL url = super.urlFor(action);
            url.setAction(TrialShareController.InsertDataFormAction.class).addParameter("objectName", getCubeObjectName().toString());
            return url;
        }
        return super.urlFor(action);
    }
}
