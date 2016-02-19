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

import org.jetbrains.annotations.NotNull;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.module.DefaultModule;
import org.labkey.api.module.FolderTypeManager;
import org.labkey.api.module.ModuleContext;
import org.labkey.api.view.SimpleWebPartFactory;
import org.labkey.api.view.WebPartFactory;
import org.labkey.trialshare.view.DataFinderWebPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TrialShareModule extends DefaultModule
{
    public static final String NAME = "TrialShare";

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public double getVersion()
    {
        return 15.31;
    }

    @Override
    public boolean hasScripts()
    {
        return true;
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
        // add a container listener so we'll know when our container is deleted:
        ContainerManager.addContainerListener(new TrialShareContainerListener());
        FolderTypeManager.get().registerFolderType(this, new StudyITNFolderType(this));
    }

    @Override
    @NotNull
    public Collection<String> getSummary(Container c)
    {
        return Collections.emptyList();
    }

    @Override
    @NotNull
    public Set<String> getSchemaNames()
    {
        return Collections.singleton(TrialShareSchema.NAME);
    }
}