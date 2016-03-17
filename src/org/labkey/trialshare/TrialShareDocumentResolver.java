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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.labkey.api.search.SearchService;
import org.labkey.api.security.User;
import org.labkey.api.view.HttpView;
import org.labkey.api.webdav.WebdavResource;

/*
 * Resolve resources starting with "trialshare", eg. "trialshare:study:ITN007AI"
 */
public class TrialShareDocumentResolver implements SearchService.ResourceResolver
{
    @Override
    public WebdavResource resolve(@NotNull String resourceIdentifier)
    {
        String[] parts = StringUtils.split(resourceIdentifier,":");
        if (parts.length != 2)
            return null;
        String type = parts[0];
        String id = parts[1];
        switch (type)
        {
            case "study":
                return createStudyResource(resourceIdentifier, id);
            case "publication":
                return createPublicationResource(parts[2]);
        }
        return null;
    }

    public HttpView getCustomSearchResult(User user, @NotNull String resourceIdentifier)
    {
        return null;
    }

    public static WebdavResource createStudyResource(String id, String study_accession)
    {
        return null;
    }

    public WebdavResource createPublicationResource(String publicationId)
    {
        return null;
    }
}
