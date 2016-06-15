package org.labkey.trialshare.query;

import org.labkey.api.data.ActionButton;
import org.labkey.api.data.ButtonBar;
import org.labkey.api.data.DataRegion;
import org.labkey.api.data.TableInfo;
import org.labkey.api.query.QueryView;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.DataView;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;
import org.springframework.validation.BindException;

import java.util.Set;

/**
 * Created by susanh on 6/14/16.
 */
abstract class ManageCubeObjectQueryView extends QueryView
{
    ManageCubeObjectQueryView(ViewContext context, BindException errors)
    {
        super(TrialShareQuerySchema.getUserSchema(context.getUser(), context.getContainer()));
        setShowInsertNewButton(true);
        setShowImportDataButton(false);
    }


    @Override
    protected void populateButtonBar(DataView view, ButtonBar bar, boolean exportAsWebPage)
    {
        super.populateButtonBar(view, bar, exportAsWebPage);
        addRefreshCubeButton(bar);
    }

    private void addRefreshCubeButton(ButtonBar bar)
    {
        ActionButton refreshButton = new ActionButton(TrialShareController.CubeAdminAction.class, "Refresh Cube", DataRegion.MODE_GRID, ActionButton.Action.POST);
        refreshButton.setDisplayPermission(InsertPermission.class);
        ActionURL refreshURL = new ActionURL(TrialShareController.CubeAdminAction.class, getViewContext().getContainer());
        refreshURL.addParameter("method", "reindex,clearCache");
        refreshURL.addParameter(ActionURL.Param.returnUrl, getViewContext().getActionURL().toString());
        refreshButton.setURL(refreshURL);
        refreshButton.setActionType(ActionButton.Action.POST);
        refreshButton.setRequiresSelection(false);
        bar.add(refreshButton);
    }

    @Override
    protected void setupDataView(DataView ret)
    {
        super.setupDataView(ret);
        hideColumns(ret.getTable());
    }

    protected abstract Set<String> getDefaultColumns();

    @Override
    protected void configureDataRegion(DataRegion rgn)
    {
        super.configureDataRegion(rgn);
        Set<String> columnsToShow = getDefaultColumns();
        rgn.getDisplayColumnNames().stream().filter(name -> !columnsToShow.contains(name)).forEach(rgn::removeColumns);
    }

    void hideColumns(TableInfo tableInfo)
    {
        Set<String> columnsToShow = getDefaultColumns();
        tableInfo.getColumns().stream().filter(column -> !columnsToShow.contains(column.getName())).forEach(columnInfo -> {
            columnInfo.setHidden(true);
        });
    }
}
