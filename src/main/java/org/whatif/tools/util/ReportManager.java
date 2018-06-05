package org.whatif.tools.util;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.whatif.tools.axiompattern.ProfileAxiomPattern;
import org.whatif.tools.axiompattern.ViolatesOWLRLAxiomPattern;

import java.util.*;

public class ReportManager {
    private Map<Class<? extends ProfileAxiomPattern>, OWLProfileReport> current_profile_reports = new HashMap<>();
    private Map<Class<? extends ProfileAxiomPattern>, Map<OWLAxiom,Set<OWLProfileViolation>>> reps_by_axiom= new HashMap();

    public void addReport(Class<? extends ProfileAxiomPattern> pc, OWLProfileReport r) {
        current_profile_reports.put(pc,r);
        Map<OWLAxiom,Set<OWLProfileViolation>> vios = new HashMap<>();
        for(OWLProfileViolation vio:r.getViolations()) {
            try{
                OWLAxiom ax = vio.getAxiom();
                if(!vios.containsKey(ax)) {
                    vios.put(ax,new HashSet<>());
                }
                vios.get(ax).add(vio);
            } catch (Exception e) {

            }
        }
    }

    public Collection<OWLProfileViolation> getViolations(Class<? extends ProfileAxiomPattern> cl) {
        return current_profile_reports.get(cl).getViolations();
    }

    public boolean matches(OWLAxiom ax, ProfileAxiomPattern p) {
        if(reps_by_axiom.containsKey(p)) {
            if(reps_by_axiom.get(p).containsKey(ax)) {
                return true;
            }
        }
        return false;
    }
}
