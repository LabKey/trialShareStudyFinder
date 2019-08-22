/*
 * Copyright (c) 2015-2017 LabKey Corporation
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
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.module.CodeOnlyModule;
import org.labkey.api.module.FolderTypeManager;
import org.labkey.api.module.ModuleContext;
import org.labkey.api.module.ModuleProperty;
import org.labkey.api.search.SearchService;
import org.labkey.api.security.permissions.AdminPermission;
import org.labkey.api.services.ServiceRegistry;
import org.labkey.api.settings.AdminConsole;
import org.labkey.api.study.SpecimenService;
import org.labkey.api.util.ConfigurationException;
import org.labkey.api.view.SimpleWebPartFactory;
import org.labkey.api.view.WebPartFactory;
import org.labkey.trialshare.view.DataFinderWebPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class TrialShareModule extends CodeOnlyModule
{
    public static final String NAME = "TrialShare";
    private final ModuleProperty _cubeContainer;

    public static final SearchService.SearchCategory searchCategoryStudy = new SearchService.SearchCategory("trialshare_study", "TrialShare Study", false);
    public static final SearchService.SearchCategory searchCategoryPublication = new SearchService.SearchCategory("trialshare_publication", "TrialShare Publication", false);


    @Override
    public String getName()
    {
        return NAME;
    }

    public TrialShareModule()
    {
        _cubeContainer = new ModuleProperty(this, "DataFinderCubeContainer");
        _cubeContainer.setDescription("The container in which the lists containing the study and publication metadata are located.");
        _cubeContainer.setCanSetPerContainer(true);
        addModuleProperty(_cubeContainer);
    }

    @NotNull
    @Override
    protected Collection<WebPartFactory> createWebPartFactories()
    {
        ArrayList<WebPartFactory> list = new ArrayList<>();
        SimpleWebPartFactory factory = new SimpleWebPartFactory("TrialShare Data Finder", WebPartFactory.LOCATION_BODY, DataFinderWebPart.class, null);
        list.add(factory);
        return list;
    }

    @Override
    protected void init()
    {
        addController(TrialShareController.NAME, TrialShareController.class);
    }

    @Override
    public void doStartup(ModuleContext moduleContext)
    {
        FolderTypeManager.get().registerFolderType(this, new StudyITNFolderType(this));

        AdminConsole.addLink(AdminConsole.SettingsLinkType.Management, "Data Cube", TrialShareController.getCubeAdminURL(), AdminPermission.class);

        SearchService ss = SearchService.get();
        if (null != ss)
        {
            ss.addDocumentProvider(new StudyDocumentProvider());
            ss.addDocumentProvider(new PublicationDocumentProvider());
            ss.addSearchCategory(searchCategoryStudy);
            ss.addSearchCategory(searchCategoryPublication);
        }

        SpecimenService.get().registerRequestCustomizer(new ITNSpecimenRequestCustomizer());
    }

    @Override
    @NotNull
    public Collection<String> getSummary(Container c)
    {
        return Collections.emptyList();
    }


    public Container getCubeContainer(@Nullable Container c)
    {
        String containerPath = getPropertyValue(_cubeContainer, c);
        if (containerPath == null)
            return c;
        Container pathContainer = ContainerManager.getForPath(containerPath);
        if (pathContainer == null)
            throw new ConfigurationException(_cubeContainer.getName() + " not configured properly in container " +  c.getName() + ".  Check your module properties.");

        return pathContainer;
    }

    String getPropertyValue(ModuleProperty mp, @Nullable Container c)
    {
        if (!mp.isCanSetPerContainer() || null==c)
            c = ContainerManager.getRoot();
        return mp.getEffectiveValue(c);
    }
}