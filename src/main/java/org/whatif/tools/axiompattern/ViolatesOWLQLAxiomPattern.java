package org.whatif.tools.axiompattern;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.whatif.tools.util.WhatifUtils;

public class ViolatesOWLQLAxiomPattern implements AxiomPattern, ProfileAxiomPattern {

	ViolatesOWLQLAxiomPattern() {
	}

	@Override
	public boolean matchesPattern(OWLAxiom ax, Object o) {
		if (!(o instanceof OWLProfileReport)) {
			return false;
		}
		for (OWLProfileViolation vo : ((OWLProfileReport) o).getViolations()) {
			try {
				return vo.getAxiom().equals(ax);
			} catch (Exception e) {
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "OWL QL Profile Violation";
	}

	@Override
	public int getWeight() {
		return -1;
	}

}
