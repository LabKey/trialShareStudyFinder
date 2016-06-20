package org.labkey.trialshare.query;

import org.labkey.api.query.QueryView;
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
