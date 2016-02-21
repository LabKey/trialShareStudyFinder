/*
 * Copyright (c) 2015-2016 LabKey Corporation
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

import org.jetbrains.annotations.Nullable;
import org.labkey.api.action.ApiAction;
import org.labkey.api.action.Marshal;
import org.labkey.api.action.Marshaller;
import org.labkey.api.action.ReturnUrlForm;
import org.labkey.api.action.SimpleResponse;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.data.Container;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.gwt.client.util.StringUtils;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.DefaultSchema;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.security.RequiresPermission;
import org.labkey.api.security.permissions.InsertPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.util.Pair;
import org.labkey.api.view.HtmlView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.labkey.api.view.VBox;
import org.labkey.trialshare.data.FacetFilter;
import org.labkey.trialshare.data.StudyBean;
import org.labkey.trialshare.data.StudyFacetBean;
import org.labkey.trialshare.data.StudyPublicationBean;
import org.labkey.trialshare.data.StudySubset;
import org.labkey.trialshare.view.DataFinderWebPart;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Marshal(Marshaller.Jackson)
public class TrialShareController extends SpringActionController
{
    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(TrialShareController.class);
    public static final String NAME = "trialshare";

    public enum DetailType { publications, study };

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
            return new JspView("/org/labkey/trialshare/view/dataFinder.jsp", getFinderBean(getContainer()));
        }
    }

    public static FinderBean getFinderBean(Container container)
    {
        FinderBean bean = new FinderBean();
        bean.setDataModuleName(TrialShareModule.NAME);
        bean.addCubeConfig(getCubeConfigBean("studies", container));
        bean.addCubeConfig(getCubeConfigBean("publications", container));
        return bean;
    }

    public static CubeConfigBean getCubeConfigBean(String objectName, Container container)
    {
        if (objectName == null)
            objectName = "studies";

        CubeConfigBean bean = new CubeConfigBean();
        bean.setSchemaName("lists");
        bean.setDataModuleName(TrialShareModule.NAME);
        bean.setShowSearch(false);
        bean.setShowParticipantFilters(false);
        Module trialShareModule = ModuleLoader.getInstance().getModule(TrialShareModule.NAME);
        bean.setCubeContainer(((TrialShareModule) trialShareModule).getCubeContainer(container));

        if (objectName.equalsIgnoreCase("studies"))
        {
            bean.setObjectName("Study");
            bean.setObjectNamePlural("Studies");
            bean.setCubeName("StudyCube");
            bean.setConfigId("TrialShare:/StudyCube");
            bean.setFilterByLevel("[Study].[Study]");
            bean.setCountDistinctLevel("[Study].[Study]");
            bean.setFilterByFacetUniqueName("[Study]");
            bean.setIsDefault(true);
            bean.setSubsetLevelName("[Study.Public].[Public]");
        }
        else if (objectName.equalsIgnoreCase("publications"))
        {
            bean.setObjectName("Publication");
            bean.setObjectNamePlural("Publications");
            bean.setCubeName("PublicationCube");
            bean.setConfigId("TrialShare:/PublicationCube");
            bean.setFilterByLevel("[Publication].[Publication]");
            bean.setCountDistinctLevel("[Publication].[Publication]");
            bean.setFilterByFacetUniqueName("[Publication]");
            bean.setIsDefault(false);
            bean.setSubsetLevelName("[Publication.Status].[Status]");
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
            this._cubeConfigs = cubeConfigs;
        }

        public void addCubeConfig(CubeConfigBean cubeConfig)
        {
            _cubeConfigs.add(cubeConfig);
        }
    }

    public static class CubeConfigBean
    {
        private String _objectName;
        private String _objectNamePlural;
        private String _cubeName;
        private String _dataModuleName;
        private String _configId;
        private String _schemaName;
        private Boolean _showSearch;
        private String _filterByLevel;
        private String _countDistinctLevel;
        private String _filterByFacetUniqueName;
        private Boolean _showParticipantFilters;
        private Boolean _isDefault;
        private String _subsetLevelName;
        private String _cubeContainerPath;
        private String _cubeContainerId;

        public String getObjectName()
        {
            return _objectName;
        }

        public void setObjectName(String objectName)
        {
            _objectName = objectName;
        }

        public String getObjectNamePlural()
        {
            return _objectNamePlural;
        }

        public void setObjectNamePlural(String objectNamePlural)
        {
            _objectNamePlural = objectNamePlural;
        }

        public String getConfigId()
        {
            return _configId;
        }

        public void setConfigId(String configId)
        {
            _configId = configId;
        }

        public String getCubeName()
        {
            return _cubeName;
        }

        public void setCubeName(String cubeName)
        {
            _cubeName = cubeName;
        }

        public String getDataModuleName()
        {
            return _dataModuleName;
        }

        public void setDataModuleName(String dataModuleName)
        {
            _dataModuleName = dataModuleName;
        }

        public String getSchemaName()
        {
            return _schemaName;
        }

        public void setSchemaName(String schemaName)
        {
            _schemaName = schemaName;
        }

        public Boolean getShowSearch()
        {
            return _showSearch;
        }

        public void setShowSearch(Boolean showSearch)
        {
            _showSearch = showSearch;
        }

        public String getCountDistinctLevel()
        {
            return _countDistinctLevel;
        }

        public void setCountDistinctLevel(String countDistinctLevel)
        {
            _countDistinctLevel = countDistinctLevel;
        }

        public String getFilterByFacetUniqueName()
        {
            return _filterByFacetUniqueName;
        }

        public void setFilterByFacetUniqueName(String filterByFacetUniqueName)
        {
            _filterByFacetUniqueName = filterByFacetUniqueName;
        }

        public String getFilterByLevel()
        {
            return _filterByLevel;
        }

        public void setFilterByLevel(String filterByLevel)
        {
            _filterByLevel = filterByLevel;
        }

        public Boolean getShowParticipantFilters()
        {
            return _showParticipantFilters;
        }

        public void setShowParticipantFilters(Boolean showParticipantFilters)
        {
            _showParticipantFilters = showParticipantFilters;
        }

        public Boolean getIsDefault()
        {
            return _isDefault;
        }

        public void setIsDefault(Boolean aDefault)
        {
            _isDefault = aDefault;
        }

        public String getSubsetLevelName()
        {
            return _subsetLevelName;
        }

        public void setSubsetLevelName(String subsetLevelName)
        {
            _subsetLevelName = subsetLevelName;
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

    @RequiresPermission(ReadPermission.class)
    public class DataFinderAction extends SimpleViewAction
    {
        public ModelAndView getView(Object o, BindException errors) throws Exception
        {
            setTitle("Data Finder");
            DataFinderWebPart wp = new DataFinderWebPart(getContainer());
            wp.setIsAutoResize(true);
            return wp;
        }

        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudiesAction extends ApiAction
    {
        @Override
        public Object execute(Object form, BindException errors) throws Exception
        {
            QuerySchema coreSchema = DefaultSchema.get(getUser(), getContainer()).getSchema("core");
            TableInfo studyProperties = coreSchema.getSchema("lists").getTable("studyProperties");

            if (studyProperties != null)
            {
                List<StudyBean> studies = (new TableSelector(studyProperties)).getArrayList(StudyBean.class);
                TableInfo publicationsList = coreSchema.getSchema("lists").getTable("manuscriptsAndAbstracts");
                Map<String, Pair<Integer, Integer>> pubCounts = new HashMap<>();
                if (publicationsList != null)
                {
                    List<StudyPublicationBean> publications = (new TableSelector(publicationsList).getArrayList(StudyPublicationBean.class));

                    for (StudyPublicationBean pub : publications)
                    {
                        if (pubCounts.get(pub.getStudyId()) == null)
                            pubCounts.put(pub.getStudyId(), new Pair<>(0, 0));
                        Pair<Integer, Integer> countPair = pubCounts.get(pub.getStudyId());
                        if (pub.getPublicationType() != null)
                            if (pub.getPublicationType().equalsIgnoreCase("Manuscript"))
                                countPair.first += 1;
                            else
                                countPair.second += 1;

                    }
                }
                for (StudyBean study : studies)
                {
                    study.setUrl(getUser());
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
    public class PublicationsAction extends ApiAction
    {

        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            QuerySchema coreSchema = DefaultSchema.get(getUser(), getContainer()).getSchema("core");
            TableInfo publicationsList = coreSchema.getSchema("lists").getTable("manuscriptsAndAbstracts");
            if (publicationsList != null)
            {
                List<StudyPublicationBean> publications = (new TableSelector(publicationsList).getArrayList(StudyPublicationBean.class));
                for (StudyPublicationBean publication : publications)
                {
                    publication.setIsHighlighted(publication.getStatus().equalsIgnoreCase("in progress"));
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
    public class FacetsAction extends ApiAction<CubeObjectTypeForm>
    {
        @Override
        public void validateForm(CubeObjectTypeForm form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid form.  Please check your syntax.");
            else
                form.validate(errors);
        }

        @Override
        public Object execute(CubeObjectTypeForm form, BindException errors) throws Exception
        {
            if (form.getObjectName().equalsIgnoreCase("publication"))
                return success(getPublicationFacets());
            else
                return success(getStudyFacets());
        }

        private List<StudyFacetBean> getStudyFacets()
        {
            List<StudyFacetBean> facets = new ArrayList<>();
            StudyFacetBean facet;

            facet = new StudyFacetBean("Therapeutic Area", "Therapeutic Areas", "Study.Therapeutic Area", "Therapeutic Area", "[Study.Therapeutic Area][(All)]", FacetFilter.Type.OR, 1);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study Type", "Study Types", "Study.Study Type", "StudyType", "[Study.Study Type][(All)]", FacetFilter.Type.OR, 2);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Assay", "Assays", "Study.Assay", "Assay", "[Study.Assay][(All)]", FacetFilter.Type.OR, 3);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Condition", "Conditions", "Study.Condition", "Condition", "[Study.Condition][(All)]", FacetFilter.Type.OR, 6);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Age Group", "Age Groups", "Study.AgeGroup", "AgeGroup", "[Study.AgeGroup][(All)]", FacetFilter.Type.OR, 4);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Phase", "Phases", "Study.Phase", "Phase", "[Study.Phase][(All)]", FacetFilter.Type.OR, 5);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study", "Studies", "Study", "Study", "[Study].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Visibility", "Visibility", "Study.AssayVisibility", "Visibility", "[Study.AssayVisibility].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDisplayFacet(false);
            facets.add(facet);

            return facets;
        }

        private List<StudyFacetBean> getPublicationFacets()
        {
            List<StudyFacetBean> facets = new ArrayList<>();
            StudyFacetBean facet;

            facet = new StudyFacetBean("Status", "Statuses", "Publication.Status", "Status", "[Publication.Status][(All)]", FacetFilter.Type.OR, 1);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDisplayFacet(TrialShareManager.get().canSeeIncompleteManuscripts(getUser(), getContainer()));
            facets.add(facet);
            facet = new StudyFacetBean("Therapeutic Area", "Therapeutic Areas", "Publication.Therapeutic Area", "Therapeutic Area", "[Publication.Therapeutic Area][(All)]", FacetFilter.Type.OR, 3);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Publication Type", "Publication Types", "Publication.Publication Type", "PublicationType", "[Publication.Publication Type][(All)]", FacetFilter.Type.OR, 2);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Assay", "Assays", "Publication.Assay", "Assay", "[Publication.Assay][(All)]", FacetFilter.Type.OR, 7);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Condition", "Conditions", "Publication.Condition", "Condition", "[Publication.Condition][(All)]", FacetFilter.Type.OR, 8);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Year", "Years", "Publication.Year", "Year", "[Publication.Year][(All)]", FacetFilter.Type.OR, 4);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Journal", "Journals", "Publication.Journal", "Journal", "[Publication.Journal][(All)]", FacetFilter.Type.OR, 5);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study", "Studies", "Publication.Study", "Study", "[Publication.Study].[(All)]", FacetFilter.Type.OR, 6);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Publication", "Publications", "Publication", "Publication", "[Publication].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Visibility", "Visibility", "Publication.AssayVisibility", "Visibility", "[Publication.AssayVisibility].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facet.setDisplayFacet(false);
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
            _studyId = (null==form) ? null : form.getStudyId();
            if (StringUtils.isEmpty(_studyId))
                errors.reject(ERROR_MSG, "Study not specified");
        }

        @Override
        public ModelAndView getView(StudyIdForm form, BindException errors) throws Exception
        {

            QuerySchema coreSchema = DefaultSchema.get(getUser(), getContainer()).getSchema("core");
            QuerySchema listSchema = coreSchema.getSchema("lists");
            TableInfo studyPropertiesList = listSchema.getTable("studyProperties");
            if (studyPropertiesList != null)
            {
                StudyBean study = (new TableSelector(listSchema.getTable("studyProperties"))).getObject(_studyId, StudyBean.class);


                SimpleFilter filter = new SimpleFilter();
                filter.addCondition(FieldKey.fromParts("studyId"), _studyId);
                study.setPublications((new TableSelector(listSchema.getTable("manuscriptsAndAbstracts"), filter, null)).getArrayList(StudyPublicationBean.class));

                VBox v = new VBox();
                if (null != form.getReturnActionURL())
                {
                    v.addView(new HtmlView(PageFlowUtil.textLink("back", form.getReturnActionURL()) + "<br>"));
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
    public class PublicationDetailAction extends SimpleViewAction<PublicationIdForm>
    {
        Integer _id;

        @Override
        public void validate(PublicationIdForm form, BindException errors)
        {
            _id = (null==form) ? null : form.getId();
            if (_id == null)
                errors.reject(ERROR_MSG, "Publication not specified");
        }

        @Override
        public ModelAndView getView(PublicationIdForm form, BindException errors) throws Exception
        {

            QuerySchema coreSchema = DefaultSchema.get(getUser(), getContainer()).getSchema("core");
            QuerySchema listSchema = coreSchema.getSchema("lists");
            TableInfo publicationsList = listSchema.getTable("manuscriptsAndAbstracts");
            if (publicationsList != null)
            {
                StudyPublicationBean publication = (new TableSelector(listSchema.getTable("manuscriptsAndAbstracts"))).getObject(_id, StudyPublicationBean.class);

                SimpleFilter filter = new SimpleFilter();
                filter.addCondition(FieldKey.fromParts("key"), _id);
                publication.setStudies((new TableSelector(listSchema.getTable("publicationStudy"), filter, null)).getArrayList(StudyBean.class));


                VBox v = new VBox();
                if (null != form.getReturnActionURL())
                {
                    v.addView(new HtmlView(PageFlowUtil.textLink("back", form.getReturnActionURL()) + "<br>"));
                }
                v.addView(new JspView<StudyPublicationBean>("/org/labkey/trialshare/view/publicationDetail.jsp", publication));

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
    public class SubsetsAction extends ApiAction<CubeObjectTypeForm>
    {

        @Override
        public Object execute(CubeObjectTypeForm form, BindException errors) throws Exception
        {
            List<StudySubset> subsets = new ArrayList<>();
            StudySubset subset = new StudySubset();


            if (form.getObjectName() == null || form.getObjectName().equalsIgnoreCase("study"))
            {
                // query study properties list for StudyContainer and "isPublic"
                if (TrialShareManager.get().canSeeOperationalStudies(getUser(), getContainer()))
                {
                    subset.setId("[Study.Public].[false]");
                    subset.setName("Operational");
                    subset.setIsDefault(false);
                    subsets.add(subset);
                }

                subset = new StudySubset();
                subset.setId("[Study.Public].[true]");
                subset.setName("Public");
                subset.setIsDefault(true);
                subsets.add(subset);
            }
//            else if (form.getObjectName().equalsIgnoreCase("publication"))
//            {
//                if (getContainer().hasPermission(getUser(), InsertPermission.class))
//                {
//                    subset.setId("[Publication.Status].[All]");
//                    subset.setName("All");
//                    subset.setIsDefault(true);
//                    subsets.add(subset);
//                }
//
//                subset = new StudySubset();
//                subset.setId("[Publication.Status].[Complete]");
//                subset.setName("Complete");
//                subset.setIsDefault(!getContainer().hasPermission(getUser(), InsertPermission.class));
//                subsets.add(subset);
//
//                if (getContainer().hasPermission(getUser(), InsertPermission.class))
//                {
//                    subset = new StudySubset();
//                    subset.setId("[Publication.Status].[In Progress]");
//                    subset.setName("In Progress");
//                    subset.setIsDefault(false);
//                    subsets.add(subset);
//                }
//            }
            return success(subsets);
        }
    }

    public static class CubeObjectTypeForm
    {
        private String objectName;


        public String getObjectName()
        {
            return objectName;
        }

        public void setObjectName(String objectName)
        {
            this.objectName = objectName;
        }

        public void validate(Errors errors)
        {
            if (getObjectName() == null)
                errors.rejectValue("objectName", ERROR_REQUIRED, "Object name is required");
        }
    }

}