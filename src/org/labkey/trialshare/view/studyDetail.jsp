<%
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
%>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.labkey.api.data.Container" %>
<%@ page import="org.labkey.api.data.ContainerFilter" %>
<%@ page import="org.labkey.api.data.ContainerFilterable" %>
<%@ page import="org.labkey.api.data.ContainerManager" %>
<%@ page import="org.labkey.api.data.TableInfo" %>
<%@ page import="org.labkey.api.data.TableSelector" %>
<%@ page import="org.labkey.api.query.DefaultSchema" %>
<%@ page import="org.labkey.api.query.QuerySchema" %>
<%@ page import="org.labkey.api.view.ActionURL" %>
<%@ page import="org.labkey.api.view.HttpView" %>
<%@ page import="org.labkey.api.view.JspView" %>
<%@ page import="org.labkey.api.view.ViewContext" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.labkey.api.view.template.ClientDependency" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="org.labkey.trialshare.data.StudyBean" %>
<%@ page import="org.labkey.trialshare.data.StudyPersonnelBean" %>
<%@ page import="org.labkey.trialshare.data.StudyPubmedBean" %>
<%@ page extends="org.labkey.api.jsp.JspBase" %>
<%!
    public LinkedHashSet<ClientDependency> getClientDependencies()
    {
        LinkedHashSet<ClientDependency> resources = new LinkedHashSet<>();

        resources.add(ClientDependency.fromPath("dataFinder.css"));
        resources.add(ClientDependency.fromPath("immport/hipc.css"));

        return resources;
    }
%>
<%
    JspView<StudyBean> me = (JspView) HttpView.currentView();

    ViewContext context = HttpView.currentContext();
    Container c = context.getContainer();
    StudyBean study = me.getModelBean();
    String descriptionHTML;
    if (!StringUtils.isEmpty(study.getDescription()))
        descriptionHTML= study.getDescription();
    else
        descriptionHTML = h(study.getBriefDescription());

    ActionURL studyUrl = null;
    if (!c.isRoot())
    {
        String comma = "\n";
        Container p = c.getProject();
        QuerySchema s = DefaultSchema.get(context.getUser(), p).getSchema("study");
        TableInfo sp = s.getTable("StudyProperties");
        if (sp.supportsContainerFilter())
        {
            ContainerFilter cf = new ContainerFilter.AllInProject(context.getUser());
            ((ContainerFilterable) sp).setContainerFilter(cf);
        }
        Collection<Map<String, Object>> maps = new TableSelector(sp).getMapCollection();
        for (Map<String, Object> map : maps)
        {
            Container studyContainer = ContainerManager.getForId((String) map.get("container"));
            String studyAccession = (String)map.get("study_accession");
            String name = (String)map.get("Label");
            if (null == studyAccession && study.getLabelPrefix() != null && name.startsWith(study.getLabelPrefix()))
                studyAccession = name;
            if (null != studyContainer && StringUtils.equalsIgnoreCase(study.getAccession(), studyAccession))
            {
                studyUrl = studyContainer.getStartURL(context.getUser());
                break;
            }
        }
    }

    Map<String, String> linkProps = new HashMap<>();
    linkProps.put("target", "_blank");
%>

<div id="demographics" class="study-demographics">
<h2 class="study-accession"><% if (null!=studyUrl) {%><a style="color:#fff" href="<%=h(studyUrl)%>"><%}%><%=h(study.getAccession())%><% if (null!=studyUrl) {%></a><%}%></h2>
<div id="demographics-content">
<h3 class="study-title"><%=h(study.getTitle())%></h3>
    <div><%
        if (null != study.getPersonnel())
        {
            for (StudyPersonnelBean p : study.getPersonnel())
            {
                if ("Principal Investigator".equals(p.getRole_in_study()))
                {
                    %><div>
                        <span class="immport-highlight study-pi"><%=h(p.getHonorific())%> <%=h(p.getFirst_name())%> <%=h(p.getLast_name())%></span>
                        <span class="immport-highlight study-organization" style="float: right"><%=h(p.getOrganization())%></span>
                    </div><%
                }
            }
        }
        %><div class="study-description"><%=text(descriptionHTML)%></div>
        <div class="study-papers"><%
        if (null != study.getPubmed() && study.getPubmed().size() > 0)
        {
            %><span class="immport-highlight">Papers</span><%
            for (StudyPubmedBean pub : study.getPubmed())
            {
                %><p><span style="font-size:80%;"><span class="pub-journal" style="text-decoration:underline;"><%=h(pub.getJournal())%></span> <span class="pub-year"><%=h(pub.getYear())%></span></span><br/><%
                %><span class="pub-title"><%=h(pub.getTitle())%></span><%
                    if (!StringUtils.isEmpty(pub.getPubmed_id()))
                    {
                        %><br/><%=textLink("PubMed","http://www.ncbi.nlm.nih.gov/pubmed/?term=" + pub.getPubmed_id(), null, null, linkProps)%><%
                    }
                %></p><%
            }
        }
        %></div>
    </div>

    <% if (null != studyUrl) { %>
        <%= textLink("View study " + study.getAccession(), studyUrl.toString(), null, null, linkProps)%><br>
    <% } %>
</div>
</div>


