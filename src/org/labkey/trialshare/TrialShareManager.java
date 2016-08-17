/*
 * Copyright (c) 2015 LabKey Corporation
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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.DbScope;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.BatchValidationException;
import org.labkey.api.query.DuplicateKeyException;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.InvalidKeyException;
import org.labkey.api.query.QueryService;
import org.labkey.api.query.QueryUpdateServiceException;
import org.labkey.api.security.User;
import org.labkey.trialshare.data.PublicationEditBean;
import org.labkey.trialshare.data.StudyAccess;
import org.labkey.trialshare.data.StudyBean;
import org.labkey.trialshare.data.StudyEditBean;
import org.labkey.trialshare.data.StudyPublicationBean;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.springframework.validation.BindException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.labkey.api.action.SpringActionController.ERROR_MSG;

public class TrialShareManager
{

    private static final Logger _logger = Logger.getLogger(TrialShareManager.class);
    private static final TrialShareManager _instance = new TrialShareManager();

    private TrialShareManager()
    {
        // prevent external construction with a private default constructor
    }

    public static TrialShareManager get()
    {
        return _instance;
    }

    @Nullable
    public Container getCubeContainer(@Nullable Container container)
    {
        return ((TrialShareModule) ModuleLoader.getInstance().getModule(TrialShareModule.NAME)).getCubeContainer(container);
    }

    boolean canSeeOperationalStudies(User user, Container container)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Visibility"), TrialShareQuerySchema.OPERATIONAL_VISIBILITY);
        TableInfo visibilityList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (visibilityList != null)
        {
            List<StudyAccess> containers = (new TableSelector(visibilityList, filter, null)).getArrayList(StudyAccess.class);
            for (StudyAccess study : containers)
            {
                if (study.hasPermission(user))
                {
                    return true;
                }
            }
        }

        return false;
    }

    boolean canSeeIncompleteManuscripts(User user, Container container)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Status"), TrialShareQuerySchema.COMPLETED_STATUS, CompareType.NEQ_OR_NULL);
        TableInfo publicationsList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, filter, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications)
            {
                if (publication.hasPermission(user))
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Set<Object> getVisibleStudyContainers(User user, Container container)
    {
        Set<Object> idSet = new HashSet<>();
        TableInfo containerList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (containerList != null)
        {
            List<StudyAccess> studyAccess = (new TableSelector(containerList, null, null)).getArrayList(StudyAccess.class);
            for (StudyAccess study : studyAccess)
            {
                if (study.hasPermission(user))
                {
                    idSet.add(study.getCubeContainerIdentifier());
                }
            }
        }
        return idSet;
    }


    public Set<String> getVisibleStudies(User user, Container container, String visibility)
    {
        Set<String> studyIdSet = new HashSet<>();

        TableInfo studyAccess = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (studyAccess != null)
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Visibility"), visibility);
            List<StudyAccess> studyAccessList = (new TableSelector(studyAccess, filter, null)).getArrayList(StudyAccess.class);
            for (StudyAccess access : studyAccessList)
            {
                if (access.hasPermission(user))
                {
                    studyIdSet.add(access.getStudyId());
                }
            }
        }
        return studyIdSet;
    }

    public Set<Object> getVisiblePublications(User user, Container container)
    {
        Set<Object> publicationIds  = new HashSet<>();
        TableInfo publicationsList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.PUBLICATION_TABLE);
        if (publicationsList != null)
        {
            List<StudyPublicationBean> publications = (new TableSelector(publicationsList, null, null)).getArrayList(StudyPublicationBean.class);
            for (StudyPublicationBean publication : publications)
            {
                if (BooleanUtils.isTrue(publication.getShow()) && publication.hasPermission(user))
                {
                    publicationIds.add(publication.getCubeId());
                }
            }
        }
        return publicationIds;
    }

    public Integer insertPublication(User user, Container container, PublicationEditBean publication, BindException errors)
    {
        if (publication == null)
            return null;

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);
            // insert the primary fields

            BatchValidationException batchValidationErrors = new BatchValidationException();
            Map<String, Object> fields = publication.getPrimaryFields();
            fields.putAll(publication.getUrlFields());
            List<Map<String, Object>> pubData = schema.getPublicationsTableInfo().getUpdateService().insertRows(user, container, Collections.singletonList(fields), batchValidationErrors, null, null);
            if (batchValidationErrors.hasErrors())
                throw batchValidationErrors;

            Integer publicationKey = (Integer) pubData.get(0).get(TrialShareQuerySchema.PUBLICATION_KEY_FIELD);

            // insert the one-to-many data
            addJoinTableData(schema.getPublicationStudyTableInfo(), TrialShareQuerySchema.PUBLICATION_ID_FIELD, publicationKey, TrialShareQuerySchema.STUDY_ID_FIELD, publication.getStudyIds(), user, container);
            addJoinTableData(schema.getPublicationTherapeuticAreaTableInfo(), TrialShareQuerySchema.PUBLICATION_ID_FIELD, publicationKey, TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, publication.getTherapeuticAreas(), user, container);

            transaction.commit();

            return publicationKey;
        }
        catch (Exception e)
        {
            _logger.error(e);
            errors.reject(ERROR_MSG, "Publication insert failed: " + e.getMessage());
        }

        return null;
    }

    public Integer updatePublication(User user, Container container, PublicationEditBean publication, BindException errors)
    {
        if (publication == null)
        {
            errors.reject(ERROR_MSG, "No publication data provided to update");
            return null;
        }
        if (publication.getId() == null)
        {
            errors.reject(ERROR_MSG, "Publication id is null");
            return null;
        }

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);

            // update the primary fields
            Map<String, Object> fields = publication.getPrimaryFields();
            fields.putAll(publication.getUrlFields());
            schema.getPublicationsTableInfo().getUpdateService().updateRows(user, container, Collections.singletonList(fields), null, null, null);

            // update the many-to-one data
            SimpleFilter filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.PUBLICATION_ID_FIELD), publication.getId());

            // update the many-to-one data
            // first get rid of the current values for this publication.  Then add the new data
            schema.getPublicationStudyTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getPublicationStudyTableInfo(), filter), null, null);
            addJoinTableData(schema.getPublicationStudyTableInfo(), TrialShareQuerySchema.PUBLICATION_ID_FIELD, publication.getId(), TrialShareQuerySchema.STUDY_ID_FIELD, publication.getStudyIds(), user, container);

            schema.getPublicationTherapeuticAreaTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getPublicationTherapeuticAreaTableInfo(), filter), null, null);
            addJoinTableData(schema.getPublicationTherapeuticAreaTableInfo(), TrialShareQuerySchema.PUBLICATION_ID_FIELD, publication.getId(), TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, publication.getTherapeuticAreas(), user, container);
            transaction.commit();

            Integer publicationKey = publication.getId();
            return publicationKey;
        }
        catch (Exception e)
        {
            _logger.error(e);
            errors.reject(ERROR_MSG, "Publication update failed: " + e.getMessage());
        }

        return null;
    }


    public void deletePublications(@NotNull User user, @NotNull Container container, Set<String> publicationIds, BindException errors)
    {
        Set<Integer> integerIds = new HashSet<>();
        for (String id : publicationIds)
        {
            try
            {
               integerIds.add(Integer.valueOf(id));
            }
            catch (NumberFormatException e)
            {
                errors.reject(ERROR_MSG, "Invalid id (expecting integer): " + id);
            }
        }

        if (errors.hasErrors())
            return;
        SimpleFilter idFilter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.PUBLICATION_ID_FIELD), integerIds, CompareType.IN);

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);

            deleteJoinTableData(schema.getPublicationStudyTableInfo(), user, container, idFilter);
            deleteJoinTableData(schema.getPublicationTherapeuticAreaTableInfo(), user, container, idFilter);

            List<Map<String, Object>> pkMaps = new ArrayList<>();
            for (Integer id : integerIds)
            {
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put(TrialShareQuerySchema.PUBLICATION_KEY_FIELD, id);
                pkMaps.add(keyMap);
            }

            schema.getPublicationsTableInfo().getUpdateService().deleteRows(user, container, pkMaps, null, null);

            transaction.commit();
        }
        catch (Exception e)
        {
            _logger.error(e);
            errors.reject(ERROR_MSG, e.getMessage());
        }

    }

    public void updateStudy(@NotNull User user, @NotNull Container container, StudyEditBean study, BindException errors)
    {
        if (study == null)
        {
            errors.reject(ERROR_MSG, "No study data provided to update");
            return;
        }
        if (study.getStudyId() == null)
        {
            errors.reject(ERROR_MSG, "No study id provided");
            return;
        }

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);

            // update the primary fields
            schema.getStudyPropertiesTableInfo().getUpdateService().updateRows(user, container, Collections.singletonList(study.getPrimaryFields()), null, null, null);

            String studyId = study.getStudyId();
            SimpleFilter filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.STUDY_ID_FIELD), studyId);

            // update the many-to-one data.  First get rid of the current values for the study
            schema.getStudyConditionTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getStudyConditionTableInfo(), filter), null, null);
            addJoinTableData(schema.getStudyConditionTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.CONDITION_FIELD, study.getConditions(), user, container);

            schema.getStudyAgeGroupTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getStudyAgeGroupTableInfo(), filter), null, null);
            addJoinTableData(schema.getStudyAgeGroupTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.AGE_GROUP_FIELD, study.getAgeGroups(), user, container);

            schema.getStudyPhaseTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getStudyPhaseTableInfo(), filter), null, null);
            addJoinTableData(schema.getStudyPhaseTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.PHASE_FIELD, study.getPhases(), user, container);

            schema.getStudyTherapeuticAreaTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getStudyTherapeuticAreaTableInfo(), filter), null, null);
            addJoinTableData(schema.getStudyTherapeuticAreaTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, study.getTherapeuticAreas(), user, container);

            schema.getStudyAccessTableInfo().getUpdateService().deleteRows(user, container, getJoinTableIds(schema.getStudyAccessTableInfo(), filter), null, null);
            addStudyAccessData(schema.getStudyAccessTableInfo(), studyId, study.getStudyAccessList(), user, container);

            transaction.commit();
        }
        catch (Exception e)
        {
            _logger.error(e);
            errors.reject(ERROR_MSG, "Problem inserting study " + e.getMessage());
        }

    }

    public void insertStudy(@NotNull User user, @NotNull Container container, StudyEditBean study, BindException errors)
    {
        if (study == null)
        {
            errors.reject(ERROR_MSG, "No study data provided to update");
            return;
        }
        if (study.getStudyId() == null)
        {
            errors.reject(ERROR_MSG, "No study id provided");
            return;
        }

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);
            // insert the primary fields

            BatchValidationException batchValidationErrors = new BatchValidationException();
            List<Map<String, Object>> studyData = schema.getStudyPropertiesTableInfo().getUpdateService().insertRows(user, container, Collections.singletonList(study.getPrimaryFields()), batchValidationErrors, null, null);
            if (batchValidationErrors.hasErrors())
                throw batchValidationErrors;

            String studyId = study.getStudyId();
            // insert the one-to-many data
            // conditions
            addJoinTableData(schema.getStudyConditionTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.CONDITION_FIELD, study.getConditions(), user, container);
            addJoinTableData(schema.getStudyAgeGroupTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.AGE_GROUP_FIELD, study.getAgeGroups(), user, container);
            addJoinTableData(schema.getStudyPhaseTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.PHASE_FIELD, study.getPhases(), user, container);
            addJoinTableData(schema.getStudyTherapeuticAreaTableInfo(), TrialShareQuerySchema.STUDY_ID_FIELD, studyId, TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, study.getTherapeuticAreas(), user, container);
            addStudyAccessData(schema.getStudyAccessTableInfo(), studyId, study.getStudyAccessList(), user, container);

            transaction.commit();
        }
        catch (Exception e)
        {
            _logger.error(e);
            errors.reject(ERROR_MSG, "Problem inserting study " + e.getMessage());
        }
    }


    public void deleteStudies(@NotNull User user, @NotNull Container container, Set<String> ids, BindException errors)
    {

        if (errors.hasErrors())
            return;
        SimpleFilter idFilter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.STUDY_ID_FIELD), ids, CompareType.IN);

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);

            deleteJoinTableData(schema.getStudyPhaseTableInfo(), user, container, idFilter);
            deleteJoinTableData(schema.getStudyAgeGroupTableInfo(), user, container, idFilter);
            deleteJoinTableData(schema.getStudyConditionTableInfo(), user, container, idFilter);
            deleteJoinTableData(schema.getStudyTherapeuticAreaTableInfo(), user, container, idFilter);
            deleteJoinTableData(schema.getStudyAccessTableInfo(), user, container, idFilter);

            List<Map<String, Object>> pkMaps = new ArrayList<>();
            for (String id : ids)
            {
                Map<String, Object> keyMap = new HashMap<>();
                keyMap.put(TrialShareQuerySchema.STUDY_ID_FIELD, id);
                pkMaps.add(keyMap);
            }

            schema.getStudyPropertiesTableInfo().getUpdateService().deleteRows(user, container, pkMaps, null, null);

            transaction.commit();
        }
        catch (Exception e)
        {
            _logger.error(e);
            errors.reject(ERROR_MSG, e.getMessage());
        }
    }


    private List<Map<String, Object>> getJoinTableIds(@NotNull TableInfo tableInfo, SimpleFilter objectIdFilter)
    {
        String keyName = tableInfo.getPkColumnNames().get(0);
        // select the keys of the rows that have the object ids selected by the object filter
        List<Integer> keys = new TableSelector(tableInfo, Collections.singleton(keyName), objectIdFilter, null).getArrayList(Integer.class);

        List<Map<String, Object>> pkMaps = new ArrayList<>();
        for (Integer key : keys)
        {
            Map<String, Object> keyMap = new CaseInsensitiveHashMap<>();
            keyMap.put(keyName, key);
            pkMaps.add(keyMap);
        }
        return pkMaps;
    }


    private void deleteJoinTableData(@NotNull TableInfo tableInfo, @NotNull User user, @NotNull Container container, SimpleFilter objectIdFilter) throws SQLException, QueryUpdateServiceException, BatchValidationException, InvalidKeyException
    {
        tableInfo.getUpdateService().deleteRows(user, container, getJoinTableIds(tableInfo, objectIdFilter), null, null);
    }

    private void batchInsertRows(TableInfo tableInfo, User user, Container container, List<Map<String, Object>> dataList) throws SQLException, QueryUpdateServiceException, BatchValidationException, DuplicateKeyException
    {
        BatchValidationException batchValidationErrors = new BatchValidationException();
        if (!dataList.isEmpty())
        {
            tableInfo.getUpdateService().insertRows(user, container, dataList, batchValidationErrors, null, null);
            if (batchValidationErrors.hasErrors())
                throw batchValidationErrors;
            dataList.clear();
        }
    }

    private void addJoinTableData(TableInfo tableInfo, String idField, Object id, String dataField, List<String> dataValues, User user, Container container) throws SQLException, QueryUpdateServiceException, BatchValidationException, DuplicateKeyException
    {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (String value : dataValues)
        {
            Map<String, Object> dataMap = new CaseInsensitiveHashMap<>();
            dataMap.put(idField, id);
            dataMap.put(dataField, value);
            dataList.add(dataMap);
        }
        if (!dataList.isEmpty())
        {
            batchInsertRows(tableInfo, user, container, dataList);
        }
    }

    private void addStudyAccessData(TableInfo tableInfo, String studyId, List<StudyAccess> studyAccesses, User user, Container container) throws SQLException, QueryUpdateServiceException, BatchValidationException, DuplicateKeyException
    {
        List<Map<String, Object>> studyAccessList = new ArrayList<>();
        for (StudyAccess studyAccess : studyAccesses)
        {
            Map<String, Object> dataMap = new CaseInsensitiveHashMap<>();
            dataMap.put("studyId", studyId);
            dataMap.put("visibility", studyAccess.getVisibility());
            dataMap.put("studyContainer", studyAccess.getStudyContainer());
            dataMap.put("displayName", studyAccess.getDisplayName());
            studyAccessList.add(dataMap);
        }
        if (!studyAccessList.isEmpty())
        {
            batchInsertRows(tableInfo, user, container, studyAccessList);
        }
    }

    public PublicationEditBean getPublication(Integer id, User user, Container container)
    {
        TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);

        StudyPublicationBean studyPublication = new TableSelector(schema.getPublicationsTableInfo()).getObject(id, StudyPublicationBean.class);
        PublicationEditBean publication = new PublicationEditBean(studyPublication);
        SimpleFilter filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.PUBLICATION_ID_FIELD), id);
        publication.setStudyIds(new TableSelector(schema.getPublicationStudyTableInfo(), Collections.singleton(TrialShareQuerySchema.STUDY_ID_FIELD), filter, null).getArrayList(String.class));
        publication.setTherapeuticAreas(new TableSelector(schema.getPublicationTherapeuticAreaTableInfo(), Collections.singleton(TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD), filter, null).getArrayList(String.class));
        return publication;
    }

    public StudyEditBean getStudy(String id, User user, Container container)
    {
        TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);
        StudyBean study = new TableSelector(schema.getStudyPropertiesTableInfo()).getObject(id, StudyBean.class);
        StudyEditBean editStudy = new StudyEditBean(study);
        SimpleFilter filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.STUDY_ID_FIELD), id);
        editStudy.setAgeGroups(new TableSelector(schema.getStudyAgeGroupTableInfo(), Collections.singleton(TrialShareQuerySchema.AGE_GROUP_FIELD), filter, null).getArrayList(String.class));
        editStudy.setConditions(new TableSelector(schema.getStudyConditionTableInfo(), Collections.singleton(TrialShareQuerySchema.CONDITION_FIELD), filter, null).getArrayList(String.class));
        editStudy.setPhases(new TableSelector(schema.getStudyPhaseTableInfo(), Collections.singleton(TrialShareQuerySchema.PHASE_FIELD), filter, null).getArrayList(String.class));
        editStudy.setTherapeuticAreas(new TableSelector(schema.getStudyTherapeuticAreaTableInfo(), Collections.singleton(TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD), filter, null).getArrayList(String.class));
        editStudy.setStudyAccessList(new TableSelector(TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE), filter, null).getArrayList(StudyAccess.class));
        return editStudy;
    }

    public void clearCache(BindException errors)
    {
        Container _cubeContainer = getCubeContainer(null);
        if (_cubeContainer == null)
        {
            if (errors != null)
                errors.reject("Container path is required", "Container path not provided");
            return;
        }
        QueryService.get().cubeDataChanged(_cubeContainer);
    }


    public void refreshPublications(BindException errors)
    {
        PublicationDocumentProvider.reindex();
        clearCache(errors);
    }

    public void refreshStudies(BindException errors)
    {
        StudyDocumentProvider.reindex();
        clearCache(errors);
    }

}