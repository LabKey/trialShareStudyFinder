/*
 * Copyright (c) 2015-2019 LabKey Corporation
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

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.labkey.api.action.ApiResponse;
import org.labkey.api.action.ApiSimpleResponse;
import org.labkey.api.action.FormHandlerAction;
import org.labkey.api.action.FormViewAction;
import org.labkey.api.action.HasValidator;
import org.labkey.api.action.Marshal;
import org.labkey.api.action.Marshaller;
import org.labkey.api.action.MutatingApiAction;
import org.labkey.api.action.ReadOnlyApiAction;
import org.labkey.api.action.ReturnUrlForm;
import org.labkey.api.action.SimpleErrorView;
import org.labkey.api.action.SimpleResponse;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.admin.AbstractFolderContext;
import org.labkey.api.admin.FolderExportContext;
import org.labkey.api.admin.FolderSerializationRegistry;
import org.labkey.api.admin.FolderWriter;
import org.labkey.api.admin.FolderWriterImpl;
import org.labkey.api.admin.StaticLoggerGetter;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.DataRegionSelection;
import org.labkey.api.data.PHI;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.pipeline.PipeRoot;
import org.labkey.api.pipeline.PipelineService;
import org.labkey.api.pipeline.PipelineUrls;
import org.labkey.api.query.QueryForm;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.QueryService;
import org.labkey.api.security.IgnoresTermsOfUse;
import org.labkey.api.security.RequiresPermission;
import org.labkey.api.security.permissions.AdminPermission;
import org.labkey.api.security.permissions.DeletePermission;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.security.permissions.UpdatePermission;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.util.Pair;
import org.labkey.api.util.URLHelper;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.HtmlView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.labkey.api.view.NotFoundException;
import org.labkey.api.view.UnauthorizedException;
import org.labkey.api.view.VBox;
import org.labkey.api.view.WebPartView;
import org.labkey.api.writer.FileSystemFile;
import org.labkey.api.writer.Writer;
import org.labkey.trialshare.data.CubeConfigBean;
import org.labkey.trialshare.data.FacetFilter;
import org.labkey.trialshare.data.PublicationEditBean;
import org.labkey.trialshare.data.StudyAccess;
import org.labkey.trialshare.data.StudyBean;
import org.labkey.trialshare.data.StudyEditBean;
import org.labkey.trialshare.data.StudyFacetBean;
import org.labkey.trialshare.data.StudyPublicationBean;
import org.labkey.trialshare.query.ManagePublicationsQueryView;
import org.labkey.trialshare.query.ManageStudiesQueryView;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.labkey.trialshare.view.DataFinderWebPart;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Marshal(Marshaller.Jackson)
public class TrialShareController extends SpringActionController
{
    public static final String OBJECT_NAME_PARAM = "object";

    public enum ObjectName
    {
        study("Study", "Studies"),
        publication("Publication", "Publications");

        private String _displayName;
        private String _pluralName;

        ObjectName(String displayName, String pluralName)
        {
            _displayName = displayName;
            _pluralName = pluralName;
        }

        public String getPluralName()
        {
            return _pluralName;
        }

        public String getDisplayName()
        {
            return _displayName;
        }

        public static ObjectName getFromTableName(String tableName)
        {
            if (TrialShareQuerySchema.PUBLICATION_TABLE.equalsIgnoreCase(tableName))
                return publication;
            else if (TrialShareQuerySchema.STUDY_TABLE.equalsIgnoreCase(tableName))
                return study;
            return null;
        }
    }

    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(TrialShareController.class);
    static final String NAME = "trialshare";

    public enum DetailType
    {
        publications(null, "Manuscripts and Abstracts"),
        study(null, "Manuscripts and Abstracts"),
        abstracts("Abstract", "Abstracts"),
        manuscripts("Manuscript", "Manuscripts");

        private String _dbFieldValue;
        private String _sectionHeader;

        DetailType(String dbField, String sectionHeader)
        {
            _dbFieldValue = dbField;
            _sectionHeader = sectionHeader;
        }

        public String getDbFieldValue()
        {
            return _dbFieldValue;
        }

        public void setDbFieldValue(String dbFieldValue)
        {
            _dbFieldValue = dbFieldValue;
        }

        public String getSectionHeader()
        {
            return _sectionHeader;
        }

        public void setSectionHeader(String sectionHeader)
        {
            _sectionHeader = sectionHeader;
        }
    }

    ;

    public TrialShareController()
    {
        setActionResolver(_actionResolver);
    }

    @RequiresPermission(ReadPermission.class)
    public class BeginAction extends SimpleViewAction
    {
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }

        @Override
        public ModelAndView getView(Object o, BindException errors) throws Exception
        {
            setTitle("TrialShare Data Finder");
            return new JspView<>("/org/labkey/trialshare/view/dataFinder.jsp", getFinderBean(getContainer(), getViewContext().getActionURL().getParameter(OBJECT_NAME_PARAM)));
        }
    }

    /*
        TrialShare: simple study export action:

        example usage (via LabKey Remote Java API):

        PostCommand pc = new PostCommand("pipeline","TrialShareExport");
        JSONObject jo = new JSONObject();
        pc.setJsonObject(jo);
        CommandResponse cr = pc.execute(cn, "/Studies/ITN027AIOPR/Study Data/");
        System.out.println(cr.getText());
     */

    public static class TrialShareExportForm
    {
        private boolean missingValueIndicators;
        private boolean study;
        private boolean assayDatasets;
        private boolean assaySchedule;
        private boolean categories;
        private boolean cohortSettings;
        private boolean crfDatasets;
        private boolean customParticipantView;
        private boolean datasetData;
        private boolean etlDefinitions;
        private boolean participantCommentSettings;
        private boolean participantGroups;
        private boolean protocolDocuments;
        private boolean qcStateSettings;
        private boolean specimenSettings;
        private boolean specimens;
        private boolean treatmentData;
        private boolean visitMap;
        private boolean folderTypeAndActiveModules;
        private boolean fullTextSearchSettings;
        private boolean webpartPropertiesAndLayout;
        private boolean containerSpecificModuleProperties;
        private boolean roleAssignmentsForUsersAndGroups;
        private boolean lists;
        private boolean queries;
        private boolean gridViews;
        private boolean reportsAndCharts;
        private boolean externalSchemaDefinitions;
        private boolean wikisAndTheirAttachments;
        private boolean notificationSettings;

        public boolean getMissingValueIndicators()
        {
            return missingValueIndicators;
        }

        public void setMissingValueIndicators(boolean missingValueIndicators)
        {
            this.missingValueIndicators = missingValueIndicators;
        }

        public boolean getStudy()
        {
            return study;
        }

        public void setStudy(boolean study)
        {
            this.study = study;
        }

        public boolean getAssayDatasets()
        {
            return assayDatasets;
        }

        public void setAssayDatasets(boolean assayDatasets)
        {
            this.assayDatasets = assayDatasets;
        }

        public boolean getAssaySchedule()
        {
            return assaySchedule;
        }

        public void setAssaySchedule(boolean assaySchedule)
        {
            this.assaySchedule = assaySchedule;
        }

        public boolean getCategories()
        {
            return categories;
        }

        public void setCategories(boolean categories)
        {
            this.categories = categories;
        }

        public boolean getCohortSettings()
        {
            return cohortSettings;
        }

        public void setCohortSettings(boolean cohortSettings)
        {
            this.cohortSettings = cohortSettings;
        }

        public boolean getCrfDatasets()
        {
            return crfDatasets;
        }

        public void setCrfDatasets(boolean crfDatasets)
        {
            this.crfDatasets = crfDatasets;
        }

        public boolean getEtlDefinitions()
        {
            return etlDefinitions;
        }

        public void setEtlDefinitions(boolean etlDefinitions)
        {
            this.etlDefinitions = etlDefinitions;
        }

        public boolean getCustomParticipantView()
        {
            return customParticipantView;
        }

        public void setCustomParticipantView(boolean customParticipantView)
        {
            this.customParticipantView = customParticipantView;
        }

        public boolean getDatasetData()
        {
            return datasetData;
        }

        public void setDatasetData(boolean datasetData)
        {
            this.datasetData = datasetData;
        }

        public boolean getParticipantCommentSettings()
        {
            return participantCommentSettings;
        }

        public void setParticipantCommentSettings(boolean participantCommentSettings)
        {
            this.participantCommentSettings = participantCommentSettings;
        }

        public boolean getParticipantGroups()
        {
            return participantGroups;
        }

        public void setParticipantGroups(boolean participantGroups)
        {
            this.participantGroups = participantGroups;
        }

        public boolean getProtocolDocuments()
        {
            return protocolDocuments;
        }

        public void setProtocolDocuments(boolean protocolDocuments)
        {
            this.protocolDocuments = protocolDocuments;
        }

        public boolean getQcStateSettings()
        {
            return qcStateSettings;
        }

        public void setQcStateSettings(boolean qcStateSettings)
        {
            this.qcStateSettings = qcStateSettings;
        }

        public boolean getSpecimenSettings()
        {
            return specimenSettings;
        }

        public void setSpecimenSettings(boolean specimenSettings)
        {
            this.specimenSettings = specimenSettings;
        }

        public boolean getSpecimens()
        {
            return specimens;
        }

        public void setSpecimens(boolean specimens)
        {
            this.specimens = specimens;
        }

        public boolean getTreatmentData()
        {
            return treatmentData;
        }

        public void setTreatmentData(boolean treatmentData)
        {
            this.treatmentData = treatmentData;
        }

        public boolean getVisitMap()
        {
            return visitMap;
        }

        public void setVisitMap(boolean visitMap)
        {
            this.visitMap = visitMap;
        }

        public boolean getFolderTypeAndActiveModules()
        {
            return folderTypeAndActiveModules;
        }

        public void setFolderTypeAndActiveModules(boolean folderTypeAndActiveModules)
        {
            this.folderTypeAndActiveModules = folderTypeAndActiveModules;
        }

        public boolean getFullTextSearchSettings()
        {
            return fullTextSearchSettings;
        }

        public void setFullTextSearchSettings(boolean fullTextSearchSettings)
        {
            this.fullTextSearchSettings = fullTextSearchSettings;
        }

        public boolean getWebpartPropertiesAndLayout()
        {
            return webpartPropertiesAndLayout;
        }

        public void setWebpartPropertiesAndLayout(boolean webpartPropertiesAndLayout)
        {
            this.webpartPropertiesAndLayout = webpartPropertiesAndLayout;
        }

        public boolean getContainerSpecificModuleProperties()
        {
            return containerSpecificModuleProperties;
        }

        public void setContainerSpecificModuleProperties(boolean containerSpecificModuleProperties)
        {
            this.containerSpecificModuleProperties = containerSpecificModuleProperties;
        }

        public boolean getRoleAssignmentsForUsersAndGroups()
        {
            return roleAssignmentsForUsersAndGroups;
        }

        public void setRoleAssignmentsForUsersAndGroups(boolean roleAssignmentsForUsersAndGroups)
        {
            this.roleAssignmentsForUsersAndGroups = roleAssignmentsForUsersAndGroups;
        }

        public boolean getLists()
        {
            return lists;
        }

        public void setLists(boolean lists)
        {
            this.lists = lists;
        }

        public boolean getQueries()
        {
            return queries;
        }

        public void setQueries(boolean queries)
        {
            this.queries = queries;
        }

        public boolean getGridViews()
        {
            return gridViews;
        }

        public void setGridViews(boolean gridViews)
        {
            this.gridViews = gridViews;
        }

        public boolean getReportsAndCharts()
        {
            return reportsAndCharts;
        }

        public void setReportsAndCharts(boolean reportsAndCharts)
        {
            this.reportsAndCharts = reportsAndCharts;
        }

        public boolean getExternalSchemaDefinitions()
        {
            return externalSchemaDefinitions;
        }

        public void setExternalSchemaDefinitions(boolean externalSchemaDefinitions)
        {
            this.externalSchemaDefinitions = externalSchemaDefinitions;
        }

        public boolean getWikisAndTheirAttachments()
        {
            return wikisAndTheirAttachments;
        }

        public void setWikisAndTheirAttachments(boolean wikisAndTheirAttachments)
        {
            this.wikisAndTheirAttachments = wikisAndTheirAttachments;
        }

        public boolean getNotificationSettings()
        {
            return notificationSettings;
        }

        public void setNotificationSettings(boolean notificationSettings)
        {
            this.notificationSettings = notificationSettings;
        }
    }

    /*
        todo: explain who uses this and why
     */
    @RequiresPermission(AdminPermission.class)
    @IgnoresTermsOfUse
    public class TrialShareExportAction extends MutatingApiAction<TrialShareExportForm>
    {
        private ActionURL _successURL;

        public ApiResponse execute(TrialShareExportForm form, BindException errors) throws Exception
        {
            // JSONObject json = form.getJsonObject();
            ApiSimpleResponse response = new ApiSimpleResponse();

            Container container = getContainer();
            if (container.isRoot())
            {
                throw new NotFoundException();
            }

            FolderWriterImpl writer = new FolderWriterImpl();

            Set<FolderDataTypes> types = new HashSet<>();

            for (FolderDataTypes type : FolderDataTypes.values())
            {
                if (type.shouldExport(form))
                    types.add(type);
            }

            if(types.isEmpty())
            {
                types = getDefaultExportDataTypes();
            }

            // todo: super important boolean false in 17.2 replaced by PHI enum.  what is new correct setting? PHI.Limited
            FolderExportContext ctx = new FolderExportContext(getUser(), container, types.stream().map(FolderDataTypes::getDescription).collect(Collectors.toSet()),
                    "new", false, PHI.NotPHI, false,
                    false, false, new StaticLoggerGetter(Logger.getLogger(FolderWriterImpl.class)));

            PipeRoot root = PipelineService.get().findPipelineRoot(container);
            if (root == null || !root.isValid())
            {
                throw new NotFoundException("No valid pipeline root found");
            }
            File exportDir = root.resolvePath("export");
            try
            {
                writer.write(container, ctx, new FileSystemFile(exportDir));
            }
            catch (Container.ContainerException e)
            {
                errors.reject(SpringActionController.ERROR_MSG, e.getMessage());
                response.put("success", false);
                response.put("reason", e.getMessage());
                return response;
            }
            _successURL = PageFlowUtil.urlProvider(PipelineUrls.class).urlBrowse(container);

            response.put("success", true);
            return response;
        }
    }

    @NotNull
    private static Set<FolderDataTypes> getDefaultExportDataTypes()
    {
        return Set.of(
                FolderDataTypes.folderTypeAndActiveModules,
                FolderDataTypes.fullTextSearchSettings,
                FolderDataTypes.webpartProperties,
                FolderDataTypes.moduleProperties,
                FolderDataTypes.qcStateSettings,

                FolderDataTypes.mvIndicators,
                FolderDataTypes.study,
                    FolderDataTypes.assayDatasets,
                    FolderDataTypes.assaySchedule,
                    FolderDataTypes.categories,
                    FolderDataTypes.cohortSettings,
                    FolderDataTypes.crfDatasets,
                    FolderDataTypes.customParticipantView,
                    FolderDataTypes.datasetData,
                    FolderDataTypes.participantCommentSettings,
                    FolderDataTypes.participantGroups,
                    FolderDataTypes.protocolDocuments,
                    FolderDataTypes.specimenSettings,
                    FolderDataTypes.specimens,
                    FolderDataTypes.treatmentData,
                    FolderDataTypes.visitMap,
                FolderDataTypes.queries,
                FolderDataTypes.gridViews,
                FolderDataTypes.reportsAndCharts,
                FolderDataTypes.externalSchemaDefinitions,
                FolderDataTypes.etlDefinitions,
                FolderDataTypes.lists,
                FolderDataTypes.wikisAndAttachments,
                FolderDataTypes.notificationSettings);
    }

    enum FolderDataTypes
    {
        mvIndicators("Missing value indicators", TrialShareExportForm::getMissingValueIndicators),
        study("Study", TrialShareExportForm::getStudy),
        assayDatasets("Assay Datasets", TrialShareExportForm::getAssayDatasets),
        assaySchedule("Assay Schedule", TrialShareExportForm::getAssaySchedule),
        categories("Categories", TrialShareExportForm::getCategories),
        cohortSettings("Cohort Settings", TrialShareExportForm::getCohortSettings),
        crfDatasets("CRF Datasets", TrialShareExportForm::getCrfDatasets),
        customParticipantView("Custom Participant View", TrialShareExportForm::getCustomParticipantView),
        datasetData("Dataset Data", TrialShareExportForm::getDatasetData),
        etlDefinitions("ETL Definitions", TrialShareExportForm::getEtlDefinitions),
        participantCommentSettings("Participant Comment Settings", TrialShareExportForm::getParticipantCommentSettings),
        participantGroups("Participant Groups", TrialShareExportForm::getParticipantGroups),
        protocolDocuments("Protocol Documents", TrialShareExportForm::getProtocolDocuments),
        qcStateSettings("QC State Settings", TrialShareExportForm::getQcStateSettings),
        specimenSettings("Specimen Settings", TrialShareExportForm::getSpecimenSettings),
        specimens("Specimens", TrialShareExportForm::getSpecimens),
        treatmentData("Treatment Data", TrialShareExportForm::getTreatmentData),
        visitMap("Visit Map", TrialShareExportForm::getVisitMap),
        folderTypeAndActiveModules("Folder type and active modules", TrialShareExportForm::getFolderTypeAndActiveModules),
        fullTextSearchSettings("Full-text search settings", TrialShareExportForm::getFullTextSearchSettings),
        webpartProperties("Webpart properties and layout", TrialShareExportForm::getWebpartPropertiesAndLayout),
        moduleProperties("Container specific module properties", TrialShareExportForm::getContainerSpecificModuleProperties),
        roleAssignments("Role assignments for users and groups", TrialShareExportForm::getRoleAssignmentsForUsersAndGroups),
        lists("Lists", TrialShareExportForm::getLists),
        queries("Queries", TrialShareExportForm::getQueries),
        gridViews("Grid Views", TrialShareExportForm::getGridViews),
        reportsAndCharts("Reports and Charts", TrialShareExportForm::getReportsAndCharts),
        externalSchemaDefinitions("External schema definitions", TrialShareExportForm::getExternalSchemaDefinitions),
        wikisAndAttachments("Wikis and their attachments", TrialShareExportForm::getWikisAndTheirAttachments),
        notificationSettings("Notification settings", TrialShareExportForm::getNotificationSettings);

        private final String _description;
        private final Function<TrialShareExportForm, Boolean> _formChecker;

        FolderDataTypes(String description, Function<TrialShareExportForm, Boolean> formChecker)
        {
            _description = description;
            _formChecker = formChecker;
        }

        public String getDescription()
        {
            return _description;
        }

        public boolean shouldExport(TrialShareExportForm form)
        {
            return _formChecker.apply(form);
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class CubeAdminAction extends FormViewAction<CubeAdminForm>
    {
        Container _cubeContainer = null;

        @Override
        public void validateCommand(CubeAdminForm target, Errors errors)
        {
            if (target.getPath() != null)
                _cubeContainer = ContainerManager.getForPath(target.getPath());
            if (_cubeContainer == null)
                _cubeContainer = TrialShareManager.get().getCubeContainer(null);
            if (_cubeContainer == null)
                errors.reject("Container path is required", "Container path not provided");
        }

        @Override
        public ModelAndView getView(CubeAdminForm form, boolean reshow, BindException errors) throws Exception
        {
            CubeDefinitionBean bean = new CubeDefinitionBean();

            Container cubeContainer = TrialShareManager.get().getCubeContainer(getContainer());
            if (cubeContainer != null)
            {
                bean.addCubeDefinition(cubeContainer.getPath(), "TrialShare:/StudyCube");
                bean.addCubeDefinition(cubeContainer.getPath(), "TrialShare:/PublicationCube");
            }
            JspView view = new JspView<>("/org/labkey/trialshare/view/cubeAdmin.jsp", bean, errors);

            view.setFrame(WebPartView.FrameType.PORTAL);
            view.setTitle("Data Cube Definitions");
            return view;
        }

        @Override
        public boolean handlePost(CubeAdminForm form, BindException errors) throws Exception
        {
            form.validate(errors);
            if (errors.hasErrors())
                return false;

            if (form.doReindex())
            {
                StudyDocumentProvider.reindex();
                PublicationDocumentProvider.reindex();
            }
            if (form.doClearCache())
            {
                QueryService.get().cubeDataChanged(_cubeContainer);
            }
            return true;
        }

        @Override
        public URLHelper getSuccessURL(CubeAdminForm form)
        {
            URLHelper url = getViewContext().getActionURL().getReturnURL();
            if (url != null)
                return url;
            else
                return getViewContext().getActionURL();
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return null;
        }
    }

    public static class CubeDefinitionBean
    {
        private Map<String, List<String>> _cubeDefinitionMap;

        public Map<String, List<String>> getCubeDefinitionMap()
        {
            return _cubeDefinitionMap;
        }

        public void setCubeDefinitionMap(Map<String, List<String>> cubeDefinitionMap)
        {
            _cubeDefinitionMap = cubeDefinitionMap;
        }

        public void addCubeDefinition(String containerPath, String cubeConfigId)
        {
            if (_cubeDefinitionMap == null)
                _cubeDefinitionMap = new HashMap<>();

            List<String> cubeDefs = _cubeDefinitionMap.get(containerPath);
            if (cubeDefs == null)
            {
                cubeDefs = new ArrayList<>();
                _cubeDefinitionMap.put(containerPath, cubeDefs);
            }
            cubeDefs.add(cubeConfigId);
        }
    }

    public static class CubeAdminForm
    {
        private String _path;
        private String _method;

        public String getPath()
        {
            return _path;
        }

        public void setPath(String path)
        {
            _path = path;
        }

        public String getMethod()
        {
            return _method;
        }

        public void setMethod(String method)
        {
            _method = method;
        }

        public boolean doReindex()
        {
            return getMethod().toLowerCase().contains("reindex");
        }

        public boolean doClearCache()
        {
            return getMethod().toLowerCase().contains("clearcache");
        }

        public void validate(Errors errors)
        {
            if (getMethod() == null)
                errors.reject("Method is required", "Method is required");
        }

    }

    public static ActionURL getCubeAdminURL()
    {
        return new ActionURL(CubeAdminAction.class, ContainerManager.getRoot());
    }

    public static FinderBean getFinderBean(Container container, String name)
    {
        FinderBean bean = new FinderBean();
        ObjectName objectName = null;
        if (name != null)
        {
            try
            {
                objectName = ObjectName.valueOf(name.toLowerCase());
            }
            catch (IllegalArgumentException e)
            {
            }
        }
        bean.setDataModuleName(TrialShareModule.NAME);
        bean.addCubeConfig(getCubeConfigBean(ObjectName.study, container, ObjectName.study == objectName));
        bean.addCubeConfig(getCubeConfigBean(ObjectName.publication, container, ObjectName.publication == objectName));
        return bean;
    }

    static CubeConfigBean getCubeConfigBean(ObjectName objectName, Container container, Boolean isDefault)
    {
        CubeConfigBean bean = new CubeConfigBean();
        bean.setSchemaName("lists");
        bean.setDataModuleName(TrialShareModule.NAME);
        bean.setShowSearch(true);
        bean.setShowParticipantFilters(false);
        bean.setCubeContainer(TrialShareManager.get().getCubeContainer(container));

        if (objectName == ObjectName.study)
        {
            bean.setObjectName(objectName.getDisplayName());
            bean.setObjectNamePlural(objectName.getPluralName());
            bean.setCubeName("StudyCube");
            bean.setConfigId("TrialShare:/StudyCube");
            bean.setFilterByLevel("[Study].[Study]");
            bean.setCountDistinctLevel("[Study.Study Name].[Study]");
            bean.setFilterByFacetUniqueName("[Study]");
            bean.setIsDefault(isDefault);
            bean.setSubsetLevelName("[Study.Public].[Public]");
            bean.setSearchCategory(TrialShareModule.searchCategoryStudy.getName());
            bean.setSearchScope("All");
            bean.setHasContainerFilter(true);
            bean.setCountField(objectName.getDisplayName());
        }
        else if (objectName == ObjectName.publication)
        {
            bean.setObjectName(objectName.getDisplayName());
            bean.setObjectNamePlural(objectName.getPluralName());
            bean.setCubeName("PublicationCube");
            bean.setConfigId("TrialShare:/PublicationCube");
            bean.setFilterByLevel("[Publication].[Publication]");
            bean.setCountDistinctLevel("[Publication].[Publication]");
            bean.setFilterByFacetUniqueName("[Publication]");
            bean.setIsDefault(isDefault);
            bean.setSubsetLevelName("[Publication.Status].[Status]");
            bean.setSearchCategory(TrialShareModule.searchCategoryPublication.getName());
            bean.setSearchScope("All");
            bean.setHasContainerFilter(false);
            bean.setCountField(objectName.getDisplayName());
        }

        return bean;
    }

    public static class FinderBean
    {
        private String _dataModuleName;
        private List<CubeConfigBean> _cubeConfigs = new ArrayList<>();

        public String getDataModuleName()
        {
            return _dataModuleName;
        }

        public void setDataModuleName(String dataModuleName)
        {
            _dataModuleName = dataModuleName;
        }

        public List<CubeConfigBean> getCubeConfigs()
        {
            return _cubeConfigs;
        }

        public void setCubeConfigs(List<CubeConfigBean> cubeConfigs)
        {
            _cubeConfigs = cubeConfigs;
        }

        public void addCubeConfig(CubeConfigBean cubeConfig)
        {
            _cubeConfigs.add(cubeConfig);
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class ReindexAction extends MutatingApiAction
    {
        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            StudyDocumentProvider.reindex();
            PublicationDocumentProvider.reindex();
            return success();
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class DataFinderAction extends SimpleViewAction
    {
        public ModelAndView getView(Object o, BindException errors) throws Exception
        {
            setTitle("Data Finder");
            return new DataFinderWebPart(getContainer());
        }

        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudiesAction extends ReadOnlyApiAction
    {
        @Override
        public Object execute(Object form, BindException errors) throws Exception
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(getUser(), getContainer());
            QuerySchema listsSchema =schema.getSchema();
            TableInfo studyProperties = listsSchema.getTable(TrialShareQuerySchema.STUDY_TABLE);

            if (studyProperties != null)
            {
                List<StudyBean> studies = (new TableSelector(studyProperties)).getArrayList(StudyBean.class);
                TableInfo publicationsList = listsSchema.getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
                Map<String, Pair<Integer, Integer>> pubCounts = new HashMap<>();
                if (publicationsList != null)
                {
                    List<StudyPublicationBean> publications = schema.getStudyPublications();

                    for (StudyPublicationBean pub : publications)
                    {
                        if (BooleanUtils.isTrue(pub.getShow()) && pub.hasPermission(getUser()))
                        {
                            pubCounts.putIfAbsent(pub.getStudyId(), new Pair<>(0, 0));
                            Pair<Integer, Integer> countPair = pubCounts.get(pub.getStudyId());
                            if (pub.getPublicationType() != null)
                                if (pub.getPublicationType().equalsIgnoreCase("Manuscript"))
                                    countPair.first += 1;
                                else
                                    countPair.second += 1;
                        }
                    }
                }
                TableInfo studyAccessTable = listsSchema.getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
                Map<String, List<StudyAccess>> studyAccessMap = new HashMap<>();
                if (studyAccessTable != null)
                {
                    List<StudyAccess> studyAccessList = (new TableSelector(studyAccessTable)).getArrayList(StudyAccess.class);

                    for (StudyAccess studyAccess : studyAccessList)
                    {
                        Container container = ContainerManager.getForId(studyAccess.getStudyContainer());
                        if (container != null && container.hasPermission(getUser(), ReadPermission.class))
                        {
                            studyAccessMap.putIfAbsent(studyAccess.getStudyId(), new ArrayList<>());
                            studyAccessMap.get(studyAccess.getStudyId()).add(studyAccess);
                        }
                    }
                }
                studies.removeIf(studyBean ->
                {
                    List<StudyAccess> accessList = studyAccessMap.get(studyBean.getStudyId());
                    return accessList == null || accessList.isEmpty();
                });
                for (StudyBean study : studies)
                {
                    study.setStudyAccessList(studyAccessMap.get(study.getStudyId()));
                    study.setUrl(getUser(), true);
                    if (pubCounts.get(study.getStudyId()) == null)
                    {
                        study.setManuscriptCount(0);
                        study.setAbstractCount(0);
                    }
                    else
                    {
                        study.setManuscriptCount(pubCounts.get(study.getStudyId()).first);
                        study.setAbstractCount(pubCounts.get(study.getStudyId()).second);
                    }
                    study.setIsHighlighted((study.getManuscriptCount() + study.getAbstractCount()) > 0);
                }

                return success(studies);
            }
            else
            {
                return new SimpleResponse(false);
            }
        }
    }

    private List<FacetFilter> getFacetFilters(Boolean includeAnd, Boolean includeOr, FacetFilter.Type defaultType)
    {
        List<FacetFilter> filterOptions = new ArrayList<>();
        FacetFilter filter;
        if (includeOr)
        {
            filter = new FacetFilter();
            filter.setType(FacetFilter.Type.OR);
            filter.setCaption("is any of");
            filter.setDefault(FacetFilter.Type.OR == defaultType);
            filterOptions.add(filter);
        }
        if (includeAnd)
        {
            filter = new FacetFilter();
            filter.setType(FacetFilter.Type.AND);
            filter.setCaption("is all of");
            filter.setDefault(FacetFilter.Type.AND == defaultType);
            filterOptions.add(filter);
        }
        return filterOptions;
    }

    @RequiresPermission(ReadPermission.class)
    public class PublicationsAction extends ReadOnlyApiAction
    {

        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            TableInfo publicationsList = TrialShareQuerySchema.getPublicationsTableInfo(getUser(), getContainer());
            if (publicationsList != null)
            {
                List<StudyPublicationBean> publications = (new TableSelector(publicationsList).getArrayList(StudyPublicationBean.class));

                for (StudyPublicationBean publication : publications)
                {
                    String containerId = publication.getManuscriptContainer();
                    if (containerId != null)
                    {
                        Container container = ContainerManager.getForId(containerId);
                        if (container != null && container.hasPermission(getUser(), ReadPermission.class))
                            publication.setDataUrl(new ActionURL("project" + PageFlowUtil.encodeURI(container.getPath() + "/begin.view?pageId=study.DATA_ANALYSIS")).toString());
                    }
                    publication.setThumbnails(getUser(), getViewContext().getActionURL());
                }
                return success(publications);
            }
            else
            {
                return new SimpleResponse(false);
            }
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class FacetsAction extends ReadOnlyApiAction<CubeObjectNameForm>
    {
        @Override
        public void validateForm(CubeObjectNameForm form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(CubeObjectNameForm form, BindException errors) throws Exception
        {
            if (form.getObjectName() == ObjectName.publication)
                return success(getPublicationFacets());
            else
                return success(getStudyFacets());
        }

        private List<StudyFacetBean> getStudyFacets()
        {
            List<StudyFacetBean> facets = new ArrayList<>();
            StudyFacetBean facet;

            facet = new StudyFacetBean("Visibility", "Visibility", "Study.Visibility", "Visibility", "[Study.Visibility][(All)]", FacetFilter.Type.OR, 1);
            // N.B.  AND doesn't really work on this dimension unless the containers associated with public and operational are the same.
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDisplayFacet(TrialShareManager.get().canSeeOperationalStudies(getUser(), getContainer()));
            facets.add(facet);
            facet = new StudyFacetBean("Therapeutic Area", "Therapeutic Areas", "Study.Therapeutic Area", "Therapeutic Area", "[Study.Therapeutic Area][(All)]", FacetFilter.Type.OR, 2);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study Type", "Study Types", "Study.Study Type", "StudyType", "[Study.Study Type][(All)]", FacetFilter.Type.OR, 3);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Age Group", "Age Groups", "Study.AgeGroup", "AgeGroup", "[Study.AgeGroup][(All)]", FacetFilter.Type.OR, 4);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Phase", "Phases", "Study.Phase", "Phase", "[Study.Phase][(All)]", FacetFilter.Type.OR, 5);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
//            facet = new StudyFacetBean("Assay", "Assays", "Study.Assay", "Assay", "[Study.Assay][(All)]", FacetFilter.Type.OR, 6);
//            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
//            facets.add(facet);
            facet = new StudyFacetBean("Condition", "Conditions", "Study.Condition", "Condition", "[Study.Condition][(All)]", FacetFilter.Type.OR, 7);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study", "Studies", "Study", "Study", "[Study].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);

            return facets;
        }

        private List<StudyFacetBean> getPublicationFacets()
        {
            List<StudyFacetBean> facets = new ArrayList<>();
            StudyFacetBean facet;
            boolean isInternalUser = TrialShareManager.get().canSeeIncompleteManuscripts(getUser(), getContainer());

            facet = new StudyFacetBean("Status", "Statuses", "Publication.Status", "Status", "[Publication.Status][(All)]", FacetFilter.Type.OR, 1);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDisplayFacet(isInternalUser);
            if (isInternalUser)
            {
                facet.setDefaultSelectedUniqueNames(Arrays.asList("[Publication.Status].[In Progress]"));
            }
            facets.add(facet);
            facet = new StudyFacetBean("Therapeutic Area", "Therapeutic Areas", "Publication.Therapeutic Area", "Therapeutic Area", "[Publication.Therapeutic Area][(All)]", FacetFilter.Type.OR, 4);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Publication Type", "Publication Types", "Publication.Publication Type", "PublicationType", "[Publication.Publication Type][(All)]", FacetFilter.Type.OR, 3);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDefaultSelectedUniqueNames(Arrays.asList("[Publication.Publication Type].[Manuscript]"));
            facets.add(facet);
            facet = new StudyFacetBean("Submission Status", "Submission Statuses", "Publication.Submission Status", "SubmissionStatus", "[Publication.Submission Status][(All)]", FacetFilter.Type.OR, 2);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDisplayFacet(isInternalUser);
            facets.add(facet);
            facet = new StudyFacetBean("Study", "Studies", "Publication.Study", "Study", "[Publication.Study].[(All)]", FacetFilter.Type.OR, 5);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Year", "Years", "Publication.Year", "Year", "[Publication.Year][(All)]", FacetFilter.Type.OR, 6);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);

//            facet = new StudyFacetBean("Featured", "Featured", "Publication.Featured", "Featured", "[Publication.Featured][(All)]", FacetFilter.Type.OR, 3);
//            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
//            facets.add(facet);
//            facet = new StudyFacetBean("Journal", "Journals", "Publication.Journal", "Journal", "[Publication.Journal][(All)]", FacetFilter.Type.OR, 6);
//            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
//            facets.add(facet);
//            facet = new StudyFacetBean("Assay", "Assays", "Publication.Assay", "Assay", "[Publication.Assay][(All)]", FacetFilter.Type.OR, 8);
//            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
//            facets.add(facet);
//            facet = new StudyFacetBean("Condition", "Conditions", "Publication.Condition", "Condition", "[Publication.Condition][(All)]", FacetFilter.Type.OR, 9);
//            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
//            facets.add(facet);

            facet = new StudyFacetBean("Publication", "Publications", "Publication", "Publication", "[Publication].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);

            return facets;
        }
    }

    public static class StudyDetailBean
    {
        private StudyBean _study;
        private DetailType _detailType;

        public DetailType getDetailType()
        {
            return _detailType;
        }

        public void setDetailType(DetailType detailType)
        {
            _detailType = detailType;
        }

        public StudyBean getStudy()
        {
            return _study;
        }

        public void setStudy(StudyBean study)
        {
            _study = study;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudyDetailAction extends SimpleViewAction<StudyIdForm>
    {
        String _studyId;

        @Override
        public void validate(StudyIdForm form, BindException errors)
        {
            _studyId = (null == form) ? null : form.getStudyId();
            if (StringUtils.isEmpty(_studyId))
                errors.reject(ERROR_MSG, "Study not specified");
        }

        @Override
        public ModelAndView getView(StudyIdForm form, BindException errors) throws Exception
        {
            QuerySchema listSchema = TrialShareQuerySchema.getSchema(getUser(), getContainer());
            TableInfo studyPropertiesList = listSchema.getTable(TrialShareQuerySchema.STUDY_TABLE);
            if (studyPropertiesList != null)
            {
                StudyBean study = (new TableSelector(listSchema.getTable(TrialShareQuerySchema.STUDY_TABLE))).getObject(_studyId, StudyBean.class);

                study.setStudyAccessList(getUser(), getContainer());
                study.setUrl(getUser(), true);
                study.setPublications(getUser(), getContainer(), form.getDetailType().getDbFieldValue());

                VBox v = new VBox();
                if (null != form.getReturnActionURL())
                {
                    v.addView(new HtmlView(PageFlowUtil.link("back").href(form.getReturnActionURL()) + "<br>"));
                }
                StudyDetailBean bean = new StudyDetailBean();
                bean.setStudy(study);
                bean.setDetailType(form.getDetailType());
                v.addView(new JspView<StudyDetailBean>("/org/labkey/trialshare/view/studyDetail.jsp", bean));

                return v;
            }
            else
            {
                return null;
            }
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    public static class StudyIdForm extends ReturnUrlForm
    {
        private String studyId;
        private DetailType detailType;

        public StudyIdForm(){}

        public String getStudyId()
        {
            return studyId;
        }

        public void setStudyId(String studyId)
        {
            this.studyId = studyId;
        }

        public DetailType getDetailType()
        {
            return detailType;
        }

        public void setDetailType(DetailType detailType)
        {
            this.detailType = detailType;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudyDetailsAction extends ReadOnlyApiAction<StudyIdForm>
    {
        String _id;

        @Override
        public void validateForm(StudyIdForm form, Errors errors)
        {
            _id = (null == form) ? null : form.getStudyId();
            if (_id == null)
                errors.reject(ERROR_MSG, "Study not specified");
        }

        @Override
        public Object execute(StudyIdForm sform, BindException errors) throws Exception
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(getUser(), getContainer());
            QuerySchema listSchema = schema.getSchema();
            StudyEditBean study = (new TableSelector(listSchema.getTable(TrialShareQuerySchema.STUDY_TABLE))).getObject(_id, StudyEditBean.class);
            study.setStudyAccessList(getUser(), getContainer());
            study.setUrl(getUser(), true);
            study.setPublications(getUser(), getContainer(), null);
            return success(study);
        }

    }

    @RequiresPermission(ReadPermission.class)
    public class PublicationDetailsAction extends ReadOnlyApiAction<PublicationIdForm>
    {
        Integer _id;

        @Override
        public void validateForm(PublicationIdForm form, Errors errors)
        {
            _id = (null == form) ? null : form.getId();
            if (_id == null)
                errors.reject(ERROR_MSG, "Publication not specified");
        }

        @Override
        public Object execute(PublicationIdForm form, BindException errors) throws Exception
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(getUser(), getContainer());
            QuerySchema listSchema = schema.getSchema(getUser(), getContainer());
            StudyPublicationBean publication = (new TableSelector(listSchema.getTable(TrialShareQuerySchema.PUBLICATION_TABLE))).getObject(_id, StudyPublicationBean.class);
            String containerId = publication.getManuscriptContainer();
            if (containerId != null)
            {
                Container container = ContainerManager.getForId(containerId);
                if (container != null && container.hasPermission(getUser(), ReadPermission.class))
                    publication.setDataUrl(new ActionURL("project" + PageFlowUtil.encodeURI(container.getPath() + "/begin.view?pageId=study.DATA_ANALYSIS")).toString());
            }
            publication.setThumbnails(getUser(), getViewContext().getActionURL());


            publication.setStudies(schema.getPublicationStudies(_id));
            for (StudyBean study : publication.getStudies())
            {
                study.setStudyAccessList(getUser(), getContainer());
                study.setUrl(getUser(), false);
            }

            return success(publication);
        }
    }


    public static class PublicationIdForm extends ReturnUrlForm
    {
        private Integer id;

        public PublicationIdForm(){}

        public Integer getId()
        {
            return id;
        }

        public void setId(Integer id)
        {
            this.id = id;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class SubsetsAction extends ReadOnlyApiAction<CubeObjectNameForm>
    {
        @Override
        public Object execute(CubeObjectNameForm form, BindException errors) throws Exception
        {
            return success();
        }
    }

    public static class CubeObjectNameForm
    {
        private ObjectName _objectName = null;

        public ObjectName getObjectName()
        {
            return _objectName;
        }

        public void setObjectName(String name)
        {
            if (name == null)
                _objectName = null;
            else
            {
                try
                {
                    _objectName = ObjectName.valueOf(name.toLowerCase());
                }
                catch (IllegalArgumentException e)
                {

                }
            }
        }

        public void validate(Errors errors)
        {
            if (getObjectName() == null)
                errors.rejectValue("objectName", ERROR_REQUIRED, "Object name not recognized or not supplied");
        }
    }

    public static class CubeObjectForm extends CubeObjectNameForm
    {
        private Object _cubeObject = null;

        public Object getCubeObject()
        {
            return _cubeObject;
        }

        public void setCubeObject(Object cubeObject)
        {
            _cubeObject = cubeObject;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class AccessibleMembersAction extends ReadOnlyApiAction<CubeObjectNameForm>
    {
        @Override
        public void validateForm(CubeObjectNameForm form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(CubeObjectNameForm form, BindException errors) throws Exception
        {
            Map<String, Object> levelMembers = new HashMap<>();

            if (form.getObjectName() == ObjectName.study)
            {
                Map<String, Object> members = new HashMap<>();
                members.put("[Study].[Container]", TrialShareManager.get().getVisibleStudyContainers(getUser(), getContainer()));
                levelMembers.put("[Study].[Study]", members);
            }
            else if (form.getObjectName() == ObjectName.publication)
            {
                levelMembers.put("[Publication].[Publication]", TrialShareManager.get().getVisiblePublications(getUser(), getContainer()));
            }
            return success(levelMembers);
        }
    }


    @RequiresPermission(ReadPermission.class)
    public class ManageDataAction extends SimpleViewAction<CubeObjectNameForm>
    {
        @Override
        public void validate(CubeObjectNameForm form, BindException errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public ModelAndView getView(CubeObjectNameForm form, BindException errors) throws Exception
        {
            if (errors.hasErrors())
                return new SimpleErrorView(errors);

            Container cubeContainer = TrialShareManager.get().getCubeContainer(getContainer());
            if (cubeContainer == null)
                throw new Exception("Invalid configuration.  No cube container defined");
            if (!cubeContainer.hasPermission(getUser(), InsertPermission.class))
                throw new UnauthorizedException();

            JspView<CubeObjectNameForm> view = new JspView("/org/labkey/trialshare/view/manageData.jsp", form);
            view.setTitle("Manage " + form.getObjectName().getPluralName());

            if (form.getObjectName() == ObjectName.publication)
                view.setView("manageObjectsView", new ManagePublicationsQueryView(getViewContext(), errors));
            else
                view.setView("manageObjectsView", new ManageStudiesQueryView(getViewContext(), errors));
            return view;
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return null;
        }
    }

    private ActionURL getManageDataUrl(ObjectName name)
    {
        return new ActionURL(ManageDataAction.class, getContainer()).addParameter("objectName", name.toString()).addParameter("query.viewName", "manageData");

    }

    @RequiresPermission(InsertPermission.class)
    public class InsertPublicationAction extends CaseInsensitiveApiAction<PublicationEditBean>
    {

        @Override
        public void validateForm(PublicationEditBean form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(PublicationEditBean form, BindException errors) throws Exception
        {
            Integer publicationId = TrialShareManager.get().insertPublication(getUser(), getContainer(), form, errors);
            if (publicationId == null)
            {
                errors.reject(ERROR_MSG, "Publication insert failed.");
            }
            if (errors.hasErrors())
            {
                return errors;
            }
            else
            {
                TrialShareManager.get().refreshPublications(errors);
                return success(publicationId);
            }
        }
    }

    @RequiresPermission(UpdatePermission.class)
    public class UpdatePublicationAction extends CaseInsensitiveApiAction<PublicationEditBean>
    {
        @Override
        public void validateForm(PublicationEditBean form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(PublicationEditBean form, BindException errors) throws Exception
        {
            TrialShareManager.get().updatePublication(getUser(), getContainer(), form, errors);
            if (!errors.hasErrors())
                TrialShareManager.get().refreshPublications(errors);
            return success();
        }
    }

    @RequiresPermission(InsertPermission.class)
    public class InsertStudyAction extends CaseInsensitiveApiAction<StudyEditBean>
    {

        @Override
        public void validateForm(StudyEditBean form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(StudyEditBean form, BindException errors) throws Exception
        {
            String studyId = TrialShareManager.get().insertStudy(getUser(), getContainer(), form, errors);
            if (studyId == null)
            {
                errors.reject(ERROR_MSG, "Study insert failed.");
            }
            if (errors.hasErrors())
            {
                return errors;
            }
            if (!errors.hasErrors())
                TrialShareManager.get().refreshStudies(errors);
            return success(studyId);
        }
    }

    @RequiresPermission(UpdatePermission.class)
    public class UpdateStudyAction extends CaseInsensitiveApiAction<StudyEditBean>
    {
        @Override
        public void validateForm(StudyEditBean form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(StudyEditBean form, BindException errors) throws Exception
        {
            TrialShareManager.get().updateStudy(getUser(), getContainer(), form, errors);
            if (!errors.hasErrors())
                TrialShareManager.get().refreshStudies(errors);
            return success();
        }
    }


    @RequiresPermission(DeletePermission.class)
    public class DeleteCubeObjectsAction extends FormHandlerAction<CubeObjectQueryForm>
    {

        @Override
        public void validateCommand(CubeObjectQueryForm form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public boolean handlePost(CubeObjectQueryForm form, BindException errors) throws Exception
        {
            Set<String> ids = DataRegionSelection.getSelected(form.getViewContext(), null, true);
            if (form.getObjectName() == ObjectName.publication)
            {
                TrialShareManager.get().deletePublications(getUser(), getContainer(), ids, errors);
                TrialShareManager.get().refreshPublications(errors);
            }
            else if (form.getObjectName() == ObjectName.study)
            {
                TrialShareManager.get().deleteStudies(getUser(), getContainer(), ids, errors);
                TrialShareManager.get().refreshStudies(errors);
            }
            else
                errors.reject(ERROR_MSG, "Invalid object name: " + form.getObjectName());
            return !errors.hasErrors();
        }

        @Override
        public URLHelper getSuccessURL(CubeObjectQueryForm queryForm)
        {
            return getManageDataUrl(queryForm.getObjectName());
        }
    }

    public static class CubeObjectQueryForm extends QueryForm implements HasValidator
    {
        private ObjectName _objectName = null;

        public ObjectName getObjectName()
        {
            return _objectName;
        }

        public void setObjectName(String name)
        {
            if (name == null)
                _objectName = null;
            else
            {
                try
                {
                    _objectName = ObjectName.valueOf(name.toLowerCase());
                }
                catch (IllegalArgumentException ignore)
                {

                }
            }
        }

        public void validate(Errors errors)
        {
            if (getObjectName() == null)
                errors.rejectValue("objectName", ERROR_REQUIRED, "Object name not recognized or not supplied");
        }
    }

    @RequiresPermission(InsertPermission.class)
    public class InsertDataFormAction extends CubeObjectDetailFormAction
    {
        protected String getMode()
        {
            return "insert";
        }
    }


    @RequiresPermission(UpdatePermission.class)
    public class UpdateDataAction extends CubeObjectDetailFormAction
    {
        protected String getMode()
        {
            return "update";
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class ViewDataAction extends CubeObjectDetailFormAction
    {
        protected String getMode()
        {
            return "view";
        }
    }

    @RequiresPermission(ReadPermission.class)
    private abstract class CubeObjectDetailFormAction extends SimpleViewAction<CubeObjectDetailForm>
    {
        @Override
        public void validate(CubeObjectDetailForm form, BindException errors)
        {
            form.validate(errors);
            if (form.getObjectName() == ObjectName.publication && form.getId() != null)
            {
                try
                {
                    Integer.valueOf((String) form.getId());
                }
                catch (NumberFormatException ignore)
                {
                    errors.reject(ERROR_MSG, "Invalid publication id: " + form.getId());
                }
            }
        }

        protected abstract String getMode();

        @Override
        public ModelAndView getView(CubeObjectDetailForm bean, BindException errors) throws Exception
        {
            if (errors.hasErrors())
                return new SimpleErrorView(errors);

            setTitle(StringUtils.capitalize(getMode()) + bean.getObjectName().getDisplayName());
            bean.setMode(getMode());
            bean.setCubeContainer(TrialShareManager.get().getCubeContainer(getContainer()));
            if (bean.getObjectName() == ObjectName.publication)
            {
                if (bean.getId() != null)
                    bean.setCubeObject(TrialShareManager.get().getPublication(Integer.valueOf((String) bean.getId()), getUser(), getContainer()));
                return new JspView<>("/org/labkey/trialshare/view/publicationDetails.jsp", bean);
            }
            else
            {
                JspView<CubeObjectNameForm> view = new JspView<>("/org/labkey/trialshare/view/studyDetails.jsp", bean);

                if (bean.getId() != null)
                    bean.setCubeObject(TrialShareManager.get().getStudy((String) bean.getId(), getUser(), getContainer()));
                return view;
            }
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            String name = getViewContext().getActionURL().getParameter("objectName");
            if (name != null)
            {
                try
                {
                    ObjectName objectName = ObjectName.valueOf(name.toLowerCase());
                    root.addChild("Manage " + objectName.getPluralName(), getManageDataUrl(objectName));
                    root.addChild(StringUtils.capitalize(getMode()) + " " + objectName.getDisplayName());
                }
                catch (IllegalArgumentException ignore) {} // Don't throw because of bad user input
            }
            return root;
        }
    }

    public static class CubeObjectDetailForm extends CubeObjectForm
    {
        private Integer _accessListId; // identifier of the list that containes access parameters (used only for studies currently)
        private String _mode;
        private Object _id;
        private String _cubeContainerPath;
        private String _cubeContainerId;

        public CubeObjectDetailForm()
        {
        }

        public CubeObjectDetailForm(@NotNull String mode)
        {
            _mode = mode;
        }

        public Integer getAccessListId()
        {
            return _accessListId;
        }

        public void setAccessListId(Integer accessListId)
        {
            _accessListId = accessListId;
        }

        public Object getId()
        {
            return _id;
        }

        public void setId(Object id)
        {
            _id = id;
        }

        public String getMode()
        {
            return _mode;
        }

        public void setMode(String mode)
        {
            _mode = mode;
        }

        public String getIdField()
        {
            if (getObjectName() == ObjectName.publication)
                return "Key";
            else
                return "StudyId";
        }

        public void setCubeContainer(@Nullable Container cubeContainer)
        {
            if (cubeContainer != null)
            {
                _cubeContainerId = cubeContainer.getId();
                _cubeContainerPath = cubeContainer.getPath();
            }
        }

        public String getCubeContainerId()
        {
            return _cubeContainerId;
        }

        public void setCubeContainerId(String cubeContainerId)
        {
            _cubeContainerId = cubeContainerId;
        }

        public String getCubeContainerPath()
        {
            return _cubeContainerPath;
        }

        public void setCubeContainerPath(String cubeContainerPath)
        {
            _cubeContainerPath = cubeContainerPath;
        }

    }


    private abstract class CaseInsensitiveApiAction<FORM> extends MutatingApiAction<FORM>
    {
        @Override
        protected ObjectReader getObjectReader(Class c)
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            return objectMapper.reader(c);
        }
    }

    public static class TrialShareExportTest
    {
        private final Collection<FolderWriter> folderWriters = FolderSerializationRegistry.get().getRegisteredFolderWriters();

        @Test
        public void testDataTypes()
        {
            final Set<String> expectedDataTypes = Arrays.stream(FolderDataTypes.values()).map(FolderDataTypes::getDescription).collect(Collectors.toSet());
            final Set<String> actualDataTypes = getRegisteredDataTypes(false);

            Iterator<String> iterator = expectedDataTypes.iterator();
            while (iterator.hasNext())
            {
                String expectedDataType = iterator.next();
                if (actualDataTypes.contains(expectedDataType))
                {
                    iterator.remove();
                    actualDataTypes.remove(expectedDataType);
                }
            }

            if (!expectedDataTypes.isEmpty())
            {
                Assert.fail("Did not find expected data type(s): " + expectedDataTypes + ". Unaccounted for data types: " + actualDataTypes);
            }
        }

        @Test
        public void testDefaultDataTypes()
        {
            final List<String> expectedDataTypes = getDefaultExportDataTypes().stream().map(FolderDataTypes::getDescription).collect(Collectors.toList());
            final List<String> actualDefaultDataTypes = new ArrayList<>(getRegisteredDataTypes(true));

            // Sort to make error message more readable
            Collections.sort(expectedDataTypes);
            Collections.sort(actualDefaultDataTypes);

            Assert.assertEquals("TrialShareExport default data types do not match core defaults", expectedDataTypes, actualDefaultDataTypes);
        }

        public Set<String> getRegisteredDataTypes(boolean onlyDefault)
        {
            Set<String> dataTypes = new HashSet<>();
            Set<FolderWriter> filteredFolderWriters;
            if (onlyDefault)
                filteredFolderWriters = folderWriters.stream().filter(fw -> fw.selectedByDefault(AbstractFolderContext.ExportType.ALL)).collect(Collectors.toSet());
            else
                filteredFolderWriters = new HashSet<>(folderWriters);

            filteredFolderWriters.forEach(fw -> {
                dataTypes.add(fw.getDataType());
                Collection<Writer> children = fw.getChildren(false, false);
                if (children != null)
                    children.forEach(w -> dataTypes.add(w.getDataType()));
            });

            dataTypes.remove(null);
            return dataTypes;
        }
    }
}