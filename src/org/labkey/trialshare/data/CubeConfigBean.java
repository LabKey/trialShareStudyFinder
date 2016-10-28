/*
 * Copyright (c) 2016 LabKey Corporation
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
package org.labkey.trialshare.data;

import org.jetbrains.annotations.Nullable;
import org.labkey.api.data.Container;

/**
 * Created by susanh on 6/14/16.
 */
public class CubeConfigBean
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
    private String _searchCategory;
    private String _searchScope;
    private Boolean _hasContainerFilter;
    private String _countField;
    private String _primaryTableName;

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

    public String getSearchCategory()
    {
        return _searchCategory;
    }

    public void setSearchCategory(String searchCategory)
    {
        _searchCategory = searchCategory;
    }

    public String getSearchScope()
    {
        return _searchScope;
    }

    public void setSearchScope(String searchScope)
    {
        _searchScope = searchScope;
    }

    public Boolean getHasContainerFilter()
    {
        return _hasContainerFilter;
    }

    public void setHasContainerFilter(Boolean hasContainerFilter)
    {
        _hasContainerFilter = hasContainerFilter;
    }

    public String getCountField()
    {
        return _countField;
    }

    public void setCountField(String countField)
    {
        _countField = countField;
    }

    public String getPrimaryTableName()
    {
        return _primaryTableName;
    }

    public void setPrimaryTableName(String primaryTableName)
    {
        _primaryTableName = primaryTableName;
    }
}
