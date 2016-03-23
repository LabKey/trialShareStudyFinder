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
import org.labkey.api.data.Container;
import org.labkey.api.data.SqlExecutor;
import org.labkey.api.data.TableInfo;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.query.QuerySchema;
import org.labkey.api.query.QueryService;
import org.labkey.api.search.SearchService;
import org.labkey.api.security.User;
import org.labkey.api.services.ServiceRegistry;
import org.labkey.api.util.Path;
import org.labkey.api.util.StringUtilsLabKey;
import org.labkey.api.view.ActionURL;
import org.labkey.api.webdav.SimpleDocumentResource;
import org.labkey.trialshare.query.TrialShareQuerySchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudyDocumentProvider implements SearchService.DocumentProvider
{
    private static final Logger _logger = LoggerFactory.getLogger(StudyDocumentProvider.class);
    public static Container getDocumentContainer()
    {
        Module trialShareModule = ModuleLoader.getInstance().getModule(TrialShareModule.NAME);
        return ((TrialShareModule) trialShareModule).getCubeContainer(null);
    }

    public static void reindex()
    {
        StudyDocumentProvider dp = new StudyDocumentProvider();
        SearchService ss = ServiceRegistry.get(SearchService.class);
        dp.enumerateDocuments(ss.defaultTask(), getDocumentContainer(), null);
    }


    @Override
    public void enumerateDocuments(SearchService.IndexTask task, @NotNull Container c, Date since)
    {

        QuerySchema listSchema = TrialShareQuerySchema.getSchema(User.getSearchUser(), c);

        String sql =
            "SELECT sa.StudyId," +
                "sa.StudyContainer, " +
                "sag.AgeGroup, " +
                "sas.Assay, " +
                "sc.Condition, " +
                "sph.Phase, " +
                "sta.TherapeuticArea, " +
                "sp.shortName, " +
                "sp.Title, " +
                "sp.StudyType, " +
                "sp.Description, " +
                "sp.Investigator " +
            "FROM " +
                "StudyAccess sa LEFT JOIN StudyProperties sp ON sa.StudyId = sp.StudyId " +
                "LEFT JOIN StudyAgeGroup sag on sa.StudyId = sag.StudyId " +
                "LEFT JOIN StudyAssay sas on sa.StudyId = sas.StudyId " +
                "LEFT JOIN StudyCondition sc on sa.StudyId = sc.StudyId " +
                "LEFT JOIN StudyPhase sph on sa.StudyId = sph.StudyId " +
                "LEFT JOIN StudyTherapeuticArea sta on sa.StudyId = sta.StudyId";

        try (ResultSet results = QueryService.get().select(listSchema,sql))
        {
            while (results.next())
            {

                StringBuilder body = new StringBuilder();

                for (String field : new String[]{"Description", "AgeGroup", "Assay", "Condition", "Phase", "TherapeuticArea", "StudyType"})
                {
                    if (results.getString(field) != null)
                        body.append(results.getString(field)).append("\n");
                }

                Map<String, Object> properties = new HashMap<>();

                StringBuilder identifiers = new StringBuilder();
                for (String field : new String[]{"shortName", "StudyId", "Investigator"})
                {
                    if (results.getString(field) != null)
                        identifiers.append(results.getString(field)).append(" ");
                }
//                body.append(" " + identifiers);

                properties.put(SearchService.PROPERTY.identifiersHi.toString(), identifiers.toString());
                properties.put(SearchService.PROPERTY.keywordsHi.toString(), results.getString("Title"));
                properties.put(SearchService.PROPERTY.title.toString(), results.getString("Title"));
                properties.put(SearchService.PROPERTY.categories.toString(), TrialShareModule.searchCategoryStudy.getName());

                String containerId = results.getString("StudyContainer") == null ? c.getId() : results.getString("StudyContainer");

                ActionURL url = new ActionURL(TrialShareController.StudyDetailAction.class, c).addParameter("studyId", (String) results.getString("StudyId")).addParameter("detailType", "study");
                url.setExtraPath(containerId);

                SimpleDocumentResource resource = new SimpleDocumentResource (
                                Path.parse("/" + results.getString("StudyId")),
                                "trialShare:study:" + containerId + ":" + results.getString("StudyId"),
                                containerId,
                                "text/html",
                                body.toString().getBytes(StringUtilsLabKey.DEFAULT_CHARSET),
                                url,
                                properties
                        );
                task.addResource(resource, SearchService.PRIORITY.item);
            }
        }
        catch (SQLException e)
        {
           _logger.error("Problem executing query for study indexing", e);
        }
    }

    @Override
    public void indexDeleted() throws SQLException
    {
        TrialShareQuerySchema querySchema = new TrialShareQuerySchema(User.getSearchUser(), null);
        SqlExecutor executor = new SqlExecutor(querySchema.getSchema().getDbSchema());

        for (TableInfo ti : querySchema.getStudyTables())
        {
            executor.execute("UPDATE " + ti.getFromSQL(ti.getName()) + " SET LastIndexed = NULL");
        }
    }
}
