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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.DbScope;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.Table;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.BatchValidationException;
import org.labkey.api.query.FieldKey;
import org.labkey.api.security.User;
import org.labkey.trialshare.data.PublicationEditBean;
import org.labkey.trialshare.data.StudyAccess;
import org.labkey.trialshare.data.StudyPublicationBean;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TrialShareManager
{

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

    public Set<Object> getVisibleStudies(User user, Container container)
    {
        Set<Object> studyIdSet = new HashSet<>();
        TableInfo containerList = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ACCESS_TABLE);
        if (containerList != null)
        {
            List<StudyAccess> studyAccess = (new TableSelector(containerList, null, null)).getArrayList(StudyAccess.class);
            for (StudyAccess study : studyAccess)
            {
                if (study.hasPermission(user))
                {
                    studyIdSet.add(study.getCubeIdentifier());
                }
            }
        }
        return studyIdSet;
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

    public Set<Object> getVisibleAssays(User user, Container container)
    {
        Set<Object> idSet = new HashSet<>();

        Set<String> operationalStudyIds = getVisibleStudies(user, container,  TrialShareQuerySchema.OPERATIONAL_VISIBILITY);
        TableInfo assayAccess = TrialShareQuerySchema.getSchema(user, container).getTable(TrialShareQuerySchema.STUDY_ASSAY_TABLE);
        if (assayAccess != null)
        {
            Map<String, Object> valueMapArray[] = new TableSelector(assayAccess, null, null).getMapArray();
            for (Map<String, Object> valueMap : valueMapArray)
            {
                String visibility = (String) valueMap.get("Visibility");
                String cubeId = "[Study.AssayVisibility].[" + valueMap.get("StudyId") + "]";
                if (visibility == null || (visibility.equalsIgnoreCase(TrialShareQuerySchema.PUBLIC_VISIBILITY)))
                    idSet.add(cubeId);
                else if (visibility.equalsIgnoreCase(TrialShareQuerySchema.OPERATIONAL_VISIBILITY) && operationalStudyIds.contains(valueMap.get("StudyId")))
                    idSet.add(cubeId);
            }
        }
        return idSet;
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
                if (publication.getShow() != null && publication.getShow() && publication.hasPermission(user))
                {
                    publicationIds.add(publication.getCubeId());
                }
            }
        }
        return publicationIds;
    }

    public void insertPublication(User user, Container container, PublicationEditBean publication, BindException errors)
    {
        if (publication == null)
            return;

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);
            // insert the primary fields

            BatchValidationException batchValidationErrors = new BatchValidationException();
            List<Map<String, Object>> pubData = schema.getPublicationsTableInfo().getUpdateService().insertRows(user, container, Collections.singletonList(publication.getPrimaryFields()), batchValidationErrors, null, null);
            if (batchValidationErrors.hasErrors())
                throw batchValidationErrors;
            List<Map<String, Object>> dataList = new ArrayList<>();

            Integer publicationKey = (Integer) pubData.get(0).get(TrialShareQuerySchema.PUBLICATION_KEY_FIELD);

            // insert the one-to-many data
            // conditions

            for (String condition : publication.getConditions())
            {
                Map<String, Object> dataMap = new CaseInsensitiveHashMap<>();
                dataMap.put(TrialShareQuerySchema.PUBLICATION_ID_FIELD, publicationKey);
                dataMap.put(TrialShareQuerySchema.CONDITION_FIELD, condition);
                dataList.add(dataMap);
            }
            if (!dataList.isEmpty())
            {
                schema.getPublicationConditionTableInfo().getUpdateService().insertRows(user, container, dataList, batchValidationErrors, null, null);
                if (batchValidationErrors.hasErrors())
                    throw batchValidationErrors;
                dataList.clear();
            }

            // studies
            for (String studyId : publication.getStudyIds())
            {
                Map<String, Object> dataMap = new CaseInsensitiveHashMap<>();
                dataMap.put(TrialShareQuerySchema.PUBLICATION_ID_FIELD, publicationKey);
                dataMap.put(TrialShareQuerySchema.STUDY_ID_FIELD, studyId);
                dataList.add(dataMap);
            }
            if (!dataList.isEmpty())
            {
                schema.getPublicationStudyTableInfo().getUpdateService().insertRows(user, container, dataList, batchValidationErrors, null, null);
                if (batchValidationErrors.hasErrors())
                    throw batchValidationErrors;
                dataList.clear();
            }

            // Therapeutic Areas
            for (String area : publication.getTherapeuticAreas())
            {
                Map<String, Object> dataMap = new CaseInsensitiveHashMap<>();
                dataMap.put(TrialShareQuerySchema.PUBLICATION_ID_FIELD, publicationKey);
                dataMap.put(TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, area);
                dataList.add(dataMap);
            }
            if (!dataList.isEmpty())
            {
                schema.getPublicationTherapeuticAreaTableInfo().getUpdateService().insertRows(user, container, dataList, batchValidationErrors, null, null);
                if (batchValidationErrors.hasErrors())
                    throw batchValidationErrors;
            }

            transaction.commit();
        }
        catch (Exception e)
        {
            errors.reject("Publication insert failed", e.getMessage());
        }
    }

    public void updatePublication(User user, Container container, PublicationEditBean publication)
    {
        if (publication == null)
            return;

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);
            // insert the primary fields
            Table.update(user, schema.getPublicationsTableInfo(), publication.getPrimaryFields(), publication.getId());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(TrialShareQuerySchema.PUBLICATION_ID_FIELD, publication.getId());

            // update the many-to-one data
            SimpleFilter filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.PUBLICATION_ID_FIELD), publication.getId());

            // conditions
            // first get rid of the current values for this publication
            Table.delete(schema.getPublicationConditionTableInfo(), filter);
            // now add the new values
            for (String condition : publication.getConditions())
            {
                dataMap.put(TrialShareQuerySchema.CONDITION_FIELD, condition);
                Table.insert(user, schema.getPublicationConditionTableInfo(), dataMap);
            }
            dataMap.remove(TrialShareQuerySchema.CONDITION_FIELD);

            // studies
            // get rid of the current values for this publication
            Table.delete(schema.getPublicationStudyTableInfo(), filter);
            for (String studyId : publication.getStudyIds())
            {
                dataMap.put(TrialShareQuerySchema.STUDY_ID_FIELD, studyId);
                Table.insert(user, schema.getPublicationStudyTableInfo(), dataMap);
            }
            dataMap.remove(TrialShareQuerySchema.STUDY_ID_FIELD);
            dataMap.remove(TrialShareQuerySchema.STUDY_SHORT_NAME_FIELD);

            // Therapeutic Areas
            Table.delete(schema.getPublicationTherapeuticAreaTableInfo(), filter);
            for (String area : publication.getTherapeuticAreas())
            {
                dataMap.put(TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, area);
                Table.insert(user, schema.getPublicationTherapeuticAreaTableInfo(), dataMap);
            }

            transaction.commit();
        }
    }

    public void deletePublication(@NotNull User user, @NotNull Container container, @NotNull Integer publicationId)
    {
        if (publicationId == null)
            return;

        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
        {
            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);

            SimpleFilter filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.PUBLICATION_ID_FIELD), publicationId);
            Table.delete(schema.getPublicationConditionTableInfo(), filter);
            Table.delete(schema.getPublicationStudyTableInfo(), filter);
            Table.delete(schema.getPublicationTherapeuticAreaTableInfo(), filter);

            filter = new SimpleFilter(FieldKey.fromParts(TrialShareQuerySchema.PUBLICATION_KEY_FIELD), publicationId);
            Table.delete(schema.getPublicationsTableInfo(), filter);
            transaction.commit();
        }

    }

//    public void insertStudy(@NotNull User user, @NotNull Container container, StudyBean study)
//    {
//        try (DbScope.Transaction transaction = TrialShareQuerySchema.getSchema(user, container).getDbSchema().getScope().ensureTransaction())
//        {
//            TrialShareQuerySchema schema = new TrialShareQuerySchema(user, container);
//            // insert the primary fields
//            Map<String, Object> studyData = Table.insert(user, schema.getStudyPropertiesTableInfo(), study.getPrimaryFields());
//            Map<String, Object> dataMap = new HashMap<>();
//            dataMap.put(TrialShareQuerySchema.STUDY_ID_FIELD, studyData.get(TrialShareQuerySchema.STUDY_ID_FIELD));
//
//            // insert the one-to-many data
//            // conditions
//            for (String condition : study.getConditions())
//            {
//                dataMap.put(TrialShareQuerySchema.CONDITION_FIELD, condition);
//                Table.insert(user, schema.getPublicationConditionTableInfo(), dataMap);
//            }
//            dataMap.remove(TrialShareQuerySchema.CONDITION_FIELD);
//
//
//            // Therapeutic Areas
//            for (String area : study.getTherapeuticAreas())
//            {
//                dataMap.put(TrialShareQuerySchema.THERAPEUTIC_AREA_FIELD, area);
//                Table.insert(user, schema.getPublicationTherapeuticAreaTableInfo(), dataMap);
//            }
//
//            transaction.commit();
//        }
//
//    }
}