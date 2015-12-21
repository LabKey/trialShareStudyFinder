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

import org.labkey.api.action.ApiAction;
import org.labkey.api.action.Marshal;
import org.labkey.api.action.Marshaller;
import org.labkey.api.action.ReturnUrlForm;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.data.SqlExecutor;
import org.labkey.api.gwt.client.util.StringUtils;
import org.labkey.api.security.RequiresPermission;
import org.labkey.api.security.permissions.ReadPermission;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.view.HtmlView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.labkey.api.view.NotFoundException;
import org.labkey.api.view.VBox;
import org.labkey.trialshare.data.FacetFilter;
import org.labkey.trialshare.data.StudyBean;
import org.labkey.trialshare.data.StudyFacetBean;
import org.labkey.trialshare.data.StudyFacetMember;
import org.labkey.trialshare.data.StudySubset;
import org.labkey.trialshare.view.DataFinderWebPart;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Marshal(Marshaller.Jackson)
public class TrialShareController extends SpringActionController
{
    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(TrialShareController.class);
    public static final String NAME = "trialshare";

    public TrialShareController()
    {
        setActionResolver(_actionResolver);
    }

    @RequiresPermission(ReadPermission.class)
    public class BeginAction extends SimpleViewAction
    {
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }

        @Override
        public ModelAndView getView(Object o, BindException errors) throws Exception
        {
            return new JspView("/org/labkey/trialshare/view/dataFinder.jsp");
        }
    }


    @RequiresPermission(ReadPermission.class)
    public class DataFinderAction extends SimpleViewAction
    {
        public ModelAndView getView(Object o, BindException errors) throws Exception
        {
            setTitle("Data Finder");
            DataFinderWebPart wp = new DataFinderWebPart(getContainer());
            wp.setIsAutoResize(true);
            return wp;
        }

        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudiesAction extends ApiAction<StudiesForm>
    {

        @Override
        public Object execute(StudiesForm form, BindException errors) throws Exception
        {
            List<StudyBean> studies = new ArrayList<StudyBean>();
            studies.add(getStudy("ITN029ST"));
            studies.add(getStudy("ITN021AI"));
            studies.add(getStudy("ITN033AI"));

            return success(studies);
        }
    }

    public static class StudiesForm
    {}

    private StudyBean getStudy(String studyId)
    {
        SqlExecutor executor = new SqlExecutor(TrialShareManager.getSchema());

        StudyBean study = new StudyBean();
        if (studyId.equals("ITN029ST"))
        {
            study.setStudyId("ITN029ST");
            study.setInvestigator("Sandy Feng, MD, PhD");
            study.setTitle("Immunosuppression Withdrawal for Pediatric Living-donor Liver Transplant Recipients");
            study.setDescription("This is a prospective multicenter, open-label, single-arm trial in which 20 pediatric recipients of parental living-donor liver allografts will undergo gradual withdrawal of immunosuppression with the goal of complete withdrawal. Patients on stable immunosuppression regimens with good organ function and no evidence of acute or chronic rejection or other forms of allograft dysfunction will be enrolled. Participants will undergo gradual withdrawal of immunosuppression and will be followed for a minimum of 4 years after completion of immunosuppression withdrawal. Immunologic and genetic profiles will be collected at multiple time points and compared between tolerant and nontolerant participants.");
            study.setIsLoaded(true);
            study.setAvailability("operational");
            study.setHasManuscript(true);
            study.setUrl("https://www.itntrialshare.org/project/Studies/ITN029STOPR/Study%20Data/begin.view");
        }
        else if (studyId.equals("ITN021AI"))
        {
            study.setStudyId("ITN021AI");
            study.setInvestigator("John H. Stone, MD, MPH");
            study.setTitle("Rituximab for ANCA-Associated Vasculitis");
            study.setIsLoaded(false);
            study.setAvailability("public");
            study.setHasManuscript(false);
            study.setDescription("Current conventional therapies for ANCA-associated vasculitis (AAV) are associated with high incidences of treatment failure, disease relapse, substantial toxicity, and patient morbidity and mortality. Rituximab is a monoclonal antibody used to treat non-Hodgkin's lymphoma. This study will evaluate the efficacy of rituximab with glucocorticoids in inducing disease remission in adults with severe forms of AAV (WG and MPA).\n" +
                    "\n" +
                    "The study consists of two phases: a 6-month remission induction phase, followed by a 12-month remission maintenance phase. All participants will receive at least 1 g of pulse IV methylprednisolone or a dose-equivalent of another glucocorticoid preparation. Depending on the participant's condition, he or she may receive up to 3 days of IV methylprednisolone for a total of 3 g of methylprednisolone (or a dose-equivalent). During the remission induction phase, all participants will receive oral prednisone daily (1 mg/kg/day, not to exceed 80 mg/day). Prednisone tapering will be completed by the Month 6 study visit.\n" +
                    "\n" +
                    "Next, participants will be randomly assigned to one of two arms. Arm 1 participants will receive rituximab (375 mg/m2) infusions once weekly for 4 weeks and cyclophosphamide (CYC) placebo daily for 3 to 6 months. Arm 2 participants will receive rituximab placebo infusions once weekly for 4 weeks and CYC daily for 3 to 6 months. During the remission maintenance phase, participants in Arm 1 will discontinue CYC placebo and start oral azathioprine (AZA) placebo daily until Month 18. Participants in Arm 2 will discontinue CYC and start AZA daily until Month 18. Participants who fail treatment before Month 6 will be crossed over to the other treatment arm unless there are specific contraindications.\n" +
                    "\n" +
                    "All participants will be followed for at least 18 months. Initially, study visits are weekly, progressing to monthly and then quarterly visits as the study proceeds. Blood collection will occur at each study visit.");
        }
        else if (studyId.equals("ITN033AI"))
        {
            study.setStudyId("ITN033AI");
            study.setInvestigator("Richard A. Nash, MD");
            study.setTitle("High Dose Immunosuppression and Autologous Transplantation for Multiple Sclerosis");
            study.setIsLoaded(false);
            study.setAvailability("operational");
            study.setHasManuscript(false);
            study.setDescription("This study is a prospective, multicenter Phase II clinical trial evaluating high-dose immunosuppressive therapy (HDIT) using Carmustine, Etoposide, Cytarabine, and Melphalan (BEAM) plus Thymoglobulin (rATG) with autologous transplantation of CD34+ HCT for the treatment of poor-risk MS. The active treatment period will be approximately 3 months from the time of initiation of mobilization to the day of discharge after transplant. Subjects will be followed up to 60 months (5 years) after transplant. Total study duration will be 60 months after the last subject is transplanted.");
            study.setUrl("https://www.itntrialshare.org/project/Studies/ITN033AIOPR/Study%20Data/begin.view");
        }
        return study;
    }

    @RequiresPermission(ReadPermission.class)
    public class StudyFacetMembersAction extends ApiAction
    {
        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            List<StudyFacetMember> members = new ArrayList<>();

            StudyFacetMember member = new StudyFacetMember();
            member.setName("Transplant");
            member.setUniqueName("Transplant");
            member.setFacetName("Therapeutic Area");
            member.setFacetUniqueName("TherapeuticArea");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(4);
            member.setPercent(50);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Autoimmune");
            member.setUniqueName("Autoimmune");
            member.setFacetName("Therapeutic Area");
            member.setFacetUniqueName("TherapeuticArea");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(3);
            member.setPercent(42);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Allergy");
            member.setUniqueName("Allergy");
            member.setFacetName("Therapeutic Area");
            member.setFacetUniqueName("TherapeuticArea");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(1);
            member.setPercent(8);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("T1DM");
            member.setUniqueName("T1DM");
            member.setFacetName("Therapeutic Area");
            member.setFacetUniqueName("TherapeuticArea");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(0);
            member.setPercent(0);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Interventional");
            member.setUniqueName("Interventional");
            member.setFacetName("Study Type");
            member.setFacetUniqueName("StudyType");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(5);
            member.setPercent(50);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Observational");
            member.setUniqueName("Observational");
            member.setFacetName("Study Type");
            member.setFacetUniqueName("StudyType");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(2);
            member.setPercent(20);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Expanded Access");
            member.setUniqueName("Expanded Access");
            member.setFacetName("Study Type");
            member.setFacetUniqueName("StudyType");
            member.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            member.setCount(3);
            member.setPercent(30);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Adult");
            member.setUniqueName("Adult");
            member.setFacetName("Age Group");
            member.setFacetUniqueName("AgeGroup");
            member.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            member.setCount(4);
            member.setPercent(50);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Child");
            member.setUniqueName("Child");
            member.setFacetName("Age Group");
            member.setFacetUniqueName("AgeGroup");
            member.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            member.setCount(1);
            member.setPercent(13);
            members.add(member);

            member = new StudyFacetMember();
            member.setName("Senior");
            member.setUniqueName("Senior");
            member.setFacetName("Age Group");
            member.setFacetUniqueName("AgeGroup");
            member.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            member.setCount(3);
            member.setPercent(37);
            members.add(member);

            return success(members);
        }
    }


    private List<FacetFilter> getFacetFilters(Boolean includeAnd, Boolean includeOr, FacetFilter.Type defaultType)
    {
        List<FacetFilter> filterOptions = new ArrayList<>();
        FacetFilter filter;
        if (includeOr)
        {
            filter = new FacetFilter();
            filter.setType(FacetFilter.Type.OR);
            filter.setCaption("is any of");
            filter.setDefault(FacetFilter.Type.OR == defaultType);
            filterOptions.add(filter);
        }
        if (includeAnd)
        {
            filter = new FacetFilter();
            filter.setType(FacetFilter.Type.AND);
            filter.setCaption("is all of");
            filter.setDefault(FacetFilter.Type.AND == defaultType);
            filterOptions.add(filter);
        }
        return filterOptions;
    }


    @RequiresPermission(ReadPermission.class)
    public class StudyFacetsAction extends ApiAction
    {

        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            List<StudyFacetBean> facets = new ArrayList<>();
            StudyFacetBean facet;

            facet = new StudyFacetBean("Therapeutic Area", "Therapeutic Areas", "Study.Therapeutic Area", "Therapeutic Area", "[Study.Therapeutic Area][(All)]", FacetFilter.Type.OR, 1);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study Type", "Study Types", "Study.Study Type", "StudyType", "[Study.Study Type][(All)]", FacetFilter.Type.OR, 2);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Condition", "Conditions", "Study.Condition", "Condition", "[Study.Condition][(All)]", FacetFilter.Type.OR, 5);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Age Group", "Age Groups", "Study.AgeGroup", "AgeGroup", "[Study.AgeGroup][(All)]", FacetFilter.Type.OR, 3);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Phase", "Phases", "Study.Phase", "Phase", "[Study.Phase][(All)]", FacetFilter.Type.OR, 4);
            facet.setFilterOptions(getFacetFilters(true, true, FacetFilter.Type.OR));
            facets.add(facet);
            facet = new StudyFacetBean("Study", "Studies", "Study", "Study", "[Study].[(All)]", FacetFilter.Type.OR, null);
            facet.setFilterOptions(getFacetFilters(false, true, FacetFilter.Type.OR));
            facets.add(facet);

            return success(facets);
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudyDetailAction extends SimpleViewAction<StudyIdForm>
    {
        StudyIdForm _form;
        StudyBean _study = new StudyBean();

        @Override
        public ModelAndView getView(StudyIdForm form, BindException errors) throws Exception
        {
            _form = form;

            String studyId = (null==form) ? null : form.getStudyId();
            if (StringUtils.isEmpty(studyId))
                throw new NotFoundException("study not specified");

//            _study.study = (new TableSelector(DbSchema.get("immport").getTable("study"))).getObject(studyId, StudyBean.class);
//            if (null == _study.study)
//                throw new NotFoundException("study not found: " + form.getStudy());
//            SimpleFilter filter = new SimpleFilter();
//            filter.addCondition(new FieldKey(null,"study_accession"),studyId);
//            _study.personnel = (new TableSelector(DbSchema.get("immport").getTable("study_personnel"),filter,null)).getArrayList(StudyPersonnelBean.class);
//            _study.pubmed = (new TableSelector(DbSchema.get("immport").getTable("study_pubmed"),filter,null)).getArrayList(StudyPubmedBean.class);

            _study = getStudy(studyId);
            VBox v = new VBox();
            if (null != _form.getReturnActionURL())
            {
                v.addView(new HtmlView(PageFlowUtil.textLink("back",_form.getReturnActionURL()) + "<br>"));
            }
            v.addView(new JspView<StudyBean>("/org/labkey/trialshare/view/studyDetail.jsp", _study));
            return v;
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }
    }

    public static class StudyIdForm extends ReturnUrlForm
    {
        private String studyId;

        public StudyIdForm(){}

        public String getStudyId()
        {
            return studyId;
        }

        public void setStudyId(String studyId)
        {
            this.studyId = studyId;
        }
    }

    @RequiresPermission(ReadPermission.class)
    public class StudySubsetsAction extends ApiAction
    {

        @Override
        public Object execute(Object o, BindException errors) throws Exception
        {
            List<StudySubset> subsets = new ArrayList<>();
            StudySubset subset = new StudySubset();
//            subset.setId("all");
//            subset.setName("All");
//
//            subsets.add(subset);

//            subset = new StudySubset();
            subset.setId("operational");
            subset.setName("Operational");
            subset.setDefault(false);
            subsets.add(subset);

            subset = new StudySubset();
            subset.setId("public");
            subset.setName("Public");
            subset.setDefault(true);
            subsets.add(subset);
            return success(subsets);
        }
    }

}