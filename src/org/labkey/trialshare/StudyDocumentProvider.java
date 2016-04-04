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
                "SELECT  "+
                        "sa.StudyId, "+
                        "sa.StudyContainer, "+
                        "sc.condition, "+
                        "sas.Assay, "+
                        "sph.Phase,  "+
                        "sag.AgeGroup, "+
                        "sta.TherapeuticArea, "+
                        "sp.shortName, "+
                        "sp.Title, "+
                        "sp.StudyType, "+
                        "sp.Description, "+
                        "sp.Investigator "+
                "FROM StudyAccess sa  "+
                        "   LEFT JOIN StudyProperties sp ON sa.StudyId = sp.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(Assay) AS Assay FROM StudyAssay GROUP BY StudyId) sas on sa.StudyId = sas.StudyId  "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(Condition) As Condition FROM StudyCondition GROUP BY StudyId) sc on sa.StudyId = sc.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(AgeGroup) AS AgeGroup FROM StudyAgeGroup GROUP BY StudyId) sag on sa.StudyId = sag.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(Phase) AS Phase FROM StudyPhase GROUP BY StudyId) sph on sa.StudyId = sph.StudyId "+
                        "   LEFT JOIN (SELECT StudyId, group_concat(TherapeuticArea) AS TherapeuticArea FROM StudyTherapeuticArea GROUP BY StudyId) sta on sa.StudyId = sta.StudyId ";

        try (ResultSet results = QueryService.get().select(listSchema,sql))
        {
            while (results.next())
            {

                StringBuilder body = new StringBuilder();

                for (String field : new String[]{"Description", "Title"})
                {
                    if (results.getString(field) != null)
                        body.append(results.getString(field).replaceAll(",", " ")).append(" " );
                }

                Map<String, Object> properties = new HashMap<>();

                StringBuilder keywords = new StringBuilder();
                // See #26028: we want to avoid stemming of the following fields, so we use keywords instead
                for (String field : new String[]{"shortName", "StudyId", "Investigator", "AgeGroup", "Assay", "Condition", "Phase", "TherapeuticArea", "StudyType"})
                {
                    if (results.getString(field) != null)
                        keywords.append(results.getString(field)).append(" ");
                }

                properties.put(SearchService.PROPERTY.indentifiersMed.toString(), results.getString("StudyId"));
                properties.put(SearchService.PROPERTY.keywordsMed.toString(), keywords.toString());
                properties.put(SearchService.PROPERTY.title.toString(), results.getString("Title"));
                properties.put(SearchService.PROPERTY.categories.toString(), TrialShareModule.searchCategoryStudy.getName());

                String containerId = results.getString("StudyContainer") == null ? c.getId() : results.getString("StudyContainer");

                ActionURL url = new ActionURL(TrialShareController.StudyDetailAction.class, c).addParameter("studyId", results.getString("StudyId")).addParameter("detailType", "study");
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
        // we currently do not support the LastIndexed setting
    }
}
