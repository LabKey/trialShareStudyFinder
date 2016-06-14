package org.labkey.trialshare.query;

import org.labkey.api.data.Container;
import org.labkey.api.data.TableInfo;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.User;
import org.labkey.trialshare.TrialShareModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by susanh on 2/23/16.
 */
public class TrialShareQuerySchema
{
    public static final String STUDY_TABLE = "StudyProperties";
    public static final String STUDY_ACCESS_TABLE = "StudyAccess";
    public static final String STUDY_ASSAY_TABLE = "StudyAssay";
    public static final String STUDY_CONDITION_TABLE = "StudyCondition";
    public static final String STUDY_AGE_GROUP_TABLE = "StudyAgeGroup";
    public static final String STUDY_PHASE_TABLE = "StudyPhase";
    public static final String STUDY_THERAPEUTIC_AREA_TABLE = "StudyTherapeuticArea";

    public static final String PUBLICATION_TABLE = "ManuscriptsAndAbstracts";
    public static final String PUBLICATION_ASSAY_TABLE = "PublicationAssay";
    public static final String PUBLICATION_CONDITION_TABLE = "PublicationCondition";
    public static final String PUBLICATION_STUDY_TABLE = "PublicationStudy";
    public static final String PUBLICATION_THERAPEUTIC_AREA_TABLE = "PublicationTherapeuticArea";

    // study visibility values
    public static final String OPERATIONAL_VISIBILITY = "Operational";
    public static final String PUBLIC_VISIBILITY = "Public";

    // publication status values
    public static final String IN_PROGRESS_STATUS = "In Progress";
    public static final String COMPLETED_STATUS = "Complete";
    private static final Set<String> _requiredPublicationLists = new HashSet<>();
    static
    {
        _requiredPublicationLists.add(PUBLICATION_TABLE);
        _requiredPublicationLists.add(PUBLICATION_CONDITION_TABLE);
        _requiredPublicationLists.add(PUBLICATION_STUDY_TABLE);
        _requiredPublicationLists.add(PUBLICATION_THERAPEUTIC_AREA_TABLE);
    }


    private static final Set<String> _requiredStudyLists = new HashSet<>();
    static {
        _requiredStudyLists.add(STUDY_ACCESS_TABLE);
        _requiredStudyLists.add(STUDY_TABLE);
        _requiredStudyLists.add(STUDY_ASSAY_TABLE);
        _requiredStudyLists.add(STUDY_CONDITION_TABLE);
        _requiredStudyLists.add(STUDY_AGE_GROUP_TABLE);
        _requiredStudyLists.add(STUDY_PHASE_TABLE);
        _requiredStudyLists.add(STUDY_THERAPEUTIC_AREA_TABLE);
    }

    private QuerySchema _listsSchema = null;

    public TrialShareQuerySchema(User user, Container container)
    {
        setSchema(user, container);
    }

    public static Set<String> getRequiredPublicationLists()
    {
        return _requiredPublicationLists;
    }

    public static Set<String> getRequiredStudyLists()
    {
        return _requiredStudyLists;
    }

    public QuerySchema getSchema()
    {
        return _listsSchema;
    }

    public void setSchema(User user, Container container)
    {
        _listsSchema = getSchema(user, container);
    }

    public static QuerySchema getSchema(User user, Container container)
    {
        Container cubeContainer = ((TrialShareModule) ModuleLoader.getInstance().getModule(TrialShareModule.NAME)).getCubeContainer(container);
        if (cubeContainer == null)
            cubeContainer = container;
        QuerySchema coreSchema = DefaultSchema.get(user, cubeContainer).getSchema("core");
        return coreSchema.getSchema("lists");
    }

    public static TableInfo getPublicationsTableInfo(User user, Container container)
    {
        QuerySchema listSchema = TrialShareQuerySchema.getSchema(user, container);
        return listSchema.getTable(PUBLICATION_TABLE);
    }

    public TableInfo getStudyPropertiesTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_TABLE);
    }

    public TableInfo getStudyAccessTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
    }

    public TableInfo getStudyAssayTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_ASSAY_TABLE);
    }

    public TableInfo getStudyConditionTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_CONDITION_TABLE);
    }

    public TableInfo getStudyAgeGroupTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_AGE_GROUP_TABLE);
    }

    public TableInfo getStudyPhaseTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_PHASE_TABLE);
    }

    public TableInfo getStudyTherapeuticAreaTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.STUDY_THERAPEUTIC_AREA_TABLE);
    }

    public TableInfo getPublicationsTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
    }

    public TableInfo getPublicationAssayTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.PUBLICATION_ASSAY_TABLE);
    }

    public TableInfo getPublicationConditionTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.PUBLICATION_CONDITION_TABLE);
    }

    public TableInfo getPublicationStudyTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.PUBLICATION_STUDY_TABLE);
    }

    public TableInfo getPublicationTherapeuticAreaTableInfo()
    {
        return _listsSchema.getTable(TrialShareQuerySchema.PUBLICATION_THERAPEUTIC_AREA_TABLE);
    }

    public List<TableInfo> getStudyTables() {
        List<TableInfo> list = new ArrayList<>();
        list.add(getStudyPropertiesTableInfo());
        list.add(getStudyAccessTableInfo());
        list.add(getStudyAgeGroupTableInfo());
        list.add(getStudyAssayTableInfo());
        list.add(getStudyConditionTableInfo());
        list.add(getStudyPhaseTableInfo());
        list.add(getStudyTherapeuticAreaTableInfo());
        return list;
    }

    public List<TableInfo> getPublicationTables() {
        List<TableInfo> list = new ArrayList<>();
        list.add(getPublicationsTableInfo());
        list.add(getPublicationAssayTableInfo());
        list.add(getPublicationConditionTableInfo());
        list.add(getPublicationStudyTableInfo());
        list.add(getPublicationTherapeuticAreaTableInfo());
        return list;
    }
}
