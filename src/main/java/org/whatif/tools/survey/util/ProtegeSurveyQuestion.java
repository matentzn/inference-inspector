package org.whatif.tools.survey.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProtegeSurveyQuestion {

	final String id;
	final String phrase;
	final String optionstype;
	final Set<String> options;
	final Set<String> correctoptions;
	final List<ProtegeView> views;
	final boolean questionratedifficulty;
	final boolean questionexpectation;
	
	public ProtegeSurveyQuestion(String id, String phrase, String optionstype, Set<String> options, Set<String> correctoptions, List<ProtegeView> views, boolean questionratedifficulty, boolean questionexpectation) {
		this.id = id;
		this.phrase = phrase;
		this.optionstype = optionstype;
		this.options = options;
		this.correctoptions = correctoptions;
		this.views = views;
		this.questionratedifficulty = questionratedifficulty;
		this.questionexpectation = questionexpectation;
	}
	
	public String getQuestionPhrase() {
		return phrase;
	}

	public String getOptionsType() {
		return optionstype;
	}

	public Set<String> getOptions() {
		return options;
	}

	public List<ProtegeView> getViews() {
		return views;
	}

	public Set<String> getCorrectOptions() {
		return correctoptions;
	}

	public String getId() {
		return id;
	}
}
