package org.whatif.tools.survey.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProtegeSurveyScenario {

	private final String id;
	private final String description;
	private final Queue<ProtegeSurveyTask> tasks = new LinkedList<ProtegeSurveyTask>();
	private final String uri;
	private ProtegeSurveyTask currentTask;
	
	public ProtegeSurveyScenario(List<ProtegeSurveyTask> tasks, String description, String uri,String id) {
		this.tasks.addAll(tasks);
		this.description = description;
		this.uri = uri;
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public ProtegeSurveyTask getCurrentTask() {
		return currentTask;
	}

	public boolean hasNextTask() {
		return !tasks.isEmpty();
	}

	public void nextTask() {
		currentTask= tasks.remove();
		currentTask.nextQuestion();
	}

	public boolean hasNextQuestion() {
		return currentTask.hasNextQuestion();
	}

	public String getURI() {
		return uri;
	}

	public String getId() {
		return id;
	}

}
