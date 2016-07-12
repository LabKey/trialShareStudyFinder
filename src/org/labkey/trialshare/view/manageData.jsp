<%
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
%>
<%@ page import="org.labkey.api.util.PageFlowUtil" %>
<%@ page import="org.labkey.api.view.ActionURL" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.trialshare.TrialShareController" %>
<%@ page import="org.springframework.web.servlet.ModelAndView" %>
<%@ taglib prefix="labkey" uri="http://www.labkey.org/taglib" %>
<%@ page extends="org.labkey.api.jsp.JspBase"%>
<%
    JspView<TrialShareController.CubeObjectNameForm> me = (JspView<TrialShareController.CubeObjectNameForm>) HttpView.currentView();
    TrialShareController.CubeObjectNameForm bean = me.getModelBean();
    TrialShareController.ObjectName thisObjectName = bean.getObjectName();
    ModelAndView manageObjectsView = me.getView("manageObjectsView");

%>
<labkey:errors/>
<p>
    You can insert, edit, or delete <%=h(thisObjectName.getPluralName().toLowerCase())%> from this page.
    Remember to refresh the cube when you are ready for your changes to show on the data finder.
</p>
<p>
<%
    for (TrialShareController.ObjectName objectName : TrialShareController.ObjectName.values())
    {
        if (objectName != thisObjectName)
        {
            out.println(PageFlowUtil.textLink(" Manage " + objectName.getPluralName(), new ActionURL(TrialShareController.ManageDataAction.class, this.getContainer())
                    .addParameter("objectName", objectName.toString())
                    .addParameter("query.viewName", "manageData")));
        }
    }
%>
</p>
<%
    if (manageObjectsView != null)
    {
        me.include(manageObjectsView, out);
    }
%>