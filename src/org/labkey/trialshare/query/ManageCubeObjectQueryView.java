package org.labkey.trialshare.query;

import org.labkey.api.data.ActionButton;
import org.labkey.api.data.ButtonBar;
import org.labkey.api.data.Container;
import org.labkey.api.data.DataRegion;
import org.labkey.api.data.DisplayColumn;
import org.labkey.api.data.RenderContext;
import org.labkey.api.data.SimpleDisplayColumn;
import org.labkey.api.data.TableInfo;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QueryAction;
import org.labkey.api.query.QueryView;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.DataView;
import org.labkey.api.view.ViewContext;
import org.labkey.trialshare.TrialShareController;
import org.labkey.trialshare.TrialShareManager;
import org.springframework.validation.BindException;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

/**
 * Created by susanh on 6/14/16.
 */
abstract class ManageCubeObjectQueryView extends QueryView
{
    private Container _cubeContainer = null;

    ManageCubeObjectQueryView(ViewContext context, BindException errors)
    {
        super(TrialShareQuerySchema.getUserSchema(context.getUser(), context.getContainer()));
        _cubeContainer = TrialShareManager.get().getCubeContainer(getContainer());
        setShowInsertNewButton(true);
        setShowImportDataButton(false);
        setShowExportButtons(false);
        setShowDetailsColumn(true);
        setShowUpdateColumn(_cubeContainer.hasPermission(getUser(), InsertPermission.class));
    }

    protected abstract TrialShareController.ObjectName getCubeObjectName();

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
    protected void addDetailsAndUpdateColumns(List<DisplayColumn> ret, TableInfo table)
    {

        SimpleDisplayColumn detailsColumn = new SimpleDisplayColumn()
        {
            @Override
            public void renderGridCellContents(RenderContext ctx, Writer out) throws IOException
            {
                Container cubeContainer = TrialShareManager.get().getCubeContainer(getContainer());
                if (cubeContainer == null || !cubeContainer.hasPermission(getUser(), InsertPermission.class))
                    return;

                FieldKey keyFieldKey = FieldKey.fromParts(getKeyField());
                String id = String.valueOf(ctx.get(keyFieldKey));
                ActionURL actionUrl = new ActionURL(TrialShareController.ViewDataAction.class, cubeContainer);
                actionUrl.addParameter("id", id);
                out.write(PageFlowUtil.textLink("Details", actionUrl));
            }
        };
        ret.add(detailsColumn);

        SimpleDisplayColumn updateColumn = new SimpleDisplayColumn()
        {
            @Override
            public void renderGridCellContents(RenderContext ctx, Writer out) throws IOException
            {
                Container cubeContainer = TrialShareManager.get().getCubeContainer(getContainer());
                if (cubeContainer == null || !cubeContainer.hasPermission(getUser(), InsertPermission.class))
                    return;

                FieldKey keyFieldKey = FieldKey.fromParts(getKeyField());
                String id = String.valueOf(ctx.get(keyFieldKey));
                ActionURL actionUrl = new ActionURL(TrialShareController.EditDataAction.class, cubeContainer).addParameter("id", id);
                out.write(PageFlowUtil.textLink("Edit", actionUrl));
            }
        };
        ret.add(0, updateColumn);
    }

    protected abstract String getKeyField();

    protected abstract Set<String> getDefaultColumns();


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
