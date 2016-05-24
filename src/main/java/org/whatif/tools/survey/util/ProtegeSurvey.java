package org.whatif.tools.survey.util;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.core.FileUtils;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.whatif.tools.util.WhatifUtils;
import org.xml.sax.SAXException;

public class ProtegeSurvey {

	private final File configuration;
	private File output;
	private final Queue<ProtegeSurveyScenario> scenarios = new LinkedList<ProtegeSurveyScenario>();
	private final SurveyRenderer renderer;
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final Object[] FILE_HEADER = { "participant", "scenario", "task", "question", "question_phrase",
			"skipped", "answer", "correct_answer", "total_options", "true_pos", "true_neg", "false_pos", "false_neg",
			"clickcount", "keystrokecount", "start", "end", "scrollamount" };
	private static final List<Object> fileheaderlist = Arrays.asList(FILE_HEADER);
	private ProtegeSurveyScenario currentscenario;
	private SurveyState state;
	private String participant;
	private FileWriter fileWriter;
	private CSVPrinter csvFilePrinter;
	private CSVFormat csvFileFormat;

	public ProtegeSurvey(File config, SurveyRenderer renderer) {
		this.configuration = config;
		this.renderer = renderer;

		try {
			parse();
			this.fileWriter = new FileWriter(new File(output, "question_data" + System.currentTimeMillis() + ".csv"));
			this.csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
			this.csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
			this.csvFilePrinter.printRecord(FILE_HEADER);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		state = SurveyState.READY;
	}

	public void start(String participant) {
		this.participant = participant;
		currentscenario = scenarios.remove();
		currentscenario.nextTask();
		nextState();
	}

	private void parse() throws SAXException, IOException, ParserConfigurationException, OWLOntologyCreationException,
			ParserException, TransformerException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(configuration);

		doc.getDocumentElement().normalize();

		WhatifUtils.p("Root element :" + doc.getDocumentElement().getNodeName());
		String out = doc.getDocumentElement().getAttribute("outputdir");
		this.output = new File(out,"data_survey");
		this.output.mkdir();
		NodeList sList = doc.getElementsByTagName("scenario");

		for (int temp = 0; temp < sList.getLength(); temp++) {

			Node scenarioNode = sList.item(temp);

			if (scenarioNode.getNodeType() == Node.ELEMENT_NODE) {

				Element scenarioElement = (Element) scenarioNode;
				String ontology_url = scenarioElement.getAttribute("ontologyurl");
				String ontology_iri = scenarioElement.getAttribute("ontologyiri");
				// OWLDataFactory df = man.getOWLDataFactory();

				String scenario_id = scenarioElement.getAttribute("id");
				WhatifUtils.p("##############");
				WhatifUtils.p("Processing new scenario " + scenario_id);
				WhatifUtils.p(ontology_url);
				List<ProtegeSurveyTask> tasks = new ArrayList<ProtegeSurveyTask>();
				String scenariodescription = "";

				NodeList nl = scenarioElement.getChildNodes();
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
							Element taskElement = (Element) nl.item(i);
							if (taskElement.getNodeName().equals("description")) {
								scenariodescription = extractDescription(taskElement);
							} else if (taskElement.getNodeName().equals("task")) {
								String taskid = taskElement.getAttribute("id");
								boolean runreasoner = taskElement.hasAttribute("runreasoner")
										? taskElement.getAttribute("runreasoner").equals("true") : false;
								boolean manual = taskElement.hasAttribute("manual")
										? taskElement.getAttribute("manual").equals("true") : true;
								NodeList nlq = taskElement.getChildNodes();
								String taskdescription = "";
								List<ProtegeSurveyQuestion> questions = new ArrayList<ProtegeSurveyQuestion>();
								Map<String, Map<String, Set<String>>> additions = new HashMap<String, Map<String, Set<String>>>();
								Map<String, Map<String, Set<String>>> removals = new HashMap<String, Map<String, Set<String>>>();

								if (nlq != null && nlq.getLength() > 0) {
									for (int j = 0; j < nlq.getLength(); j++) {
										if (nlq.item(j).getNodeType() == Node.ELEMENT_NODE) {
											Element questionElement = (Element) nlq.item(j);
											if (questionElement.getNodeName().equals("description")) {
												taskdescription = extractDescription(questionElement);

											} else if (questionElement.getNodeName().equals("question")) {
												String questionid = taskElement.getAttribute("id");
												boolean questionratedifficulty = taskElement
														.getAttribute("ratedifficulty").equals("true");
												boolean questionexpectation = taskElement.getAttribute("expectation")
														.equals("true");

												String phrase = extractDescription((Element) questionElement
														.getElementsByTagName("phrase").item(0));

												Element optionsElement = (Element) questionElement
														.getElementsByTagName("options").item(0);
												String optionstype = optionsElement.getAttribute("type");
												NodeList nlo = optionsElement.getChildNodes();
												Set<String> options = new HashSet<String>();
												Set<String> correctoptions = new HashSet<String>();

												if (nlo != null && nlo.getLength() > 0) {
													for (int k = 0; k < nlo.getLength(); k++) {
														if (nlo.item(k).getNodeType() == Node.ELEMENT_NODE) {
															Element optionElement = (Element) nlo.item(k);
															if (optionElement.getNodeName().equals("option")) {
																String option = optionElement.getTextContent();
																options.add(option);
																if (optionElement.hasAttribute("correct")
																		& optionElement.getAttribute("correct")
																				.equals("true")) {
																	correctoptions.add(option);
																}
															}
														}
													}
												}

												Element viewsElement = (Element) questionElement
														.getElementsByTagName("views").item(0);
												NodeList nlv = viewsElement.getChildNodes();
												List<ProtegeView> views = new ArrayList<ProtegeView>();

												if (nlv != null && nlv.getLength() > 0) {
													for (int k = 0; k < nlv.getLength(); k++) {
														if (nlv.item(k).getNodeType() == Node.ELEMENT_NODE) {
															Element viewElement = (Element) nlv.item(k);
															if (viewElement.getNodeName().equals("view")) {
																String view = viewElement.getAttribute("id");
																// System.out.println(view);
																// System.exit(0);
																ProtegeView v = new ProtegeView(view);

																if (viewElement.hasAttribute("gridx")) {
																	int gridx = Integer.parseInt(
																			viewElement.getAttribute("gridx"));
																	v.setGridx(gridx);
																}
																if (viewElement.hasAttribute("gridy")) {
																	int gridy = Integer.parseInt(
																			viewElement.getAttribute("gridy"));
																	v.setGridy(gridy);
																}
																if (viewElement.hasAttribute("weightx")) {
																	double weightx = Double.parseDouble(
																			viewElement.getAttribute("weightx"));
																	v.setWeightx(weightx);
																}
																if (viewElement.hasAttribute("weighty")) {
																	double weighty = Double.parseDouble(
																			viewElement.getAttribute("weighty"));
																	v.setWeighty(weighty);
																}
																views.add(v);
															}
														}
													}
												}

												ProtegeSurveyQuestion q = new ProtegeSurveyQuestion(questionid, phrase,
														optionstype, options, correctoptions, views,
														questionratedifficulty, questionexpectation);
												questions.add(q);
											} else if (questionElement.getNodeName().equals("changes")) {
												NodeList nlv = questionElement.getChildNodes();

												if (nlv != null && nlv.getLength() > 0) {
													for (int k = 0; k < nlv.getLength(); k++) {
														if (nlv.item(k).getNodeType() == Node.ELEMENT_NODE) {
															Element changeElement = (Element) nlv.item(k);
															if (changeElement.getNodeName().equals("change")) {
																Map<String, Set<String>> entities = new HashMap<String, Set<String>>();
																NodeList nlchange = changeElement
																		.getElementsByTagName("entity");
																if (nlchange != null && nlchange.getLength() > 0) {
																	for (int l = 0; l < nlchange.getLength(); l++) {
																		if (nlchange.item(l)
																				.getNodeType() == Node.ELEMENT_NODE) {
																			Element entityElement = (Element) nlchange
																					.item(l);
																			if (entityElement.getNodeName()
																					.equals("entity")) {
																				String entitytype = entityElement
																						.getAttribute("type");
																				String name = entityElement
																						.getTextContent();
																				if (!entities.containsKey(entitytype)) {
																					entities.put(entitytype,
																							new HashSet<String>());
																				}
																				entities.get(entitytype).add(name);

																			}
																		}
																	}
																}
																String changetype = changeElement.getAttribute("type");
																WhatifUtils.p(changetype);
																String axiom = changeElement
																		.getElementsByTagName("axiom").item(0)
																		.getTextContent();

																if (changetype.equals("removal")) {
																	removals.put(axiom, entities);
																} else if (changetype.equals("addition")) {
																	additions.put(axiom, entities);
																} else {
																	System.err.println(
																			"UNKOWND change type, assuming addition");
																	additions.put(axiom, entities);
																}

															}
														}
													}
												}
											}
										}
									}
								}

								ProtegeSurveyTask task = new ProtegeSurveyTask(taskid, questions, taskdescription,
										additions, removals, manual, runreasoner);
								tasks.add(task);
							}
						}
					}
				}
				ProtegeSurveyScenario scenario = new ProtegeSurveyScenario(tasks, scenariodescription, ontology_url,
						scenario_id);
				scenarios.add(scenario);
			}
		}
	}

	private String extractDescription(Element e)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource(e), new StreamResult(buffer));

		String d = buffer.toString().replaceAll("<description>", "").replaceAll("</description>", "")
				.replaceAll("<phrase>", "").replaceAll("</phrase>", "");
		return d;
	}

	private void addDeclaration(String name, String type, OWLOntology o) {
		OWLOntologyManager man = o.getOWLOntologyManager();
		OWLDataFactory df = man.getOWLDataFactory();
		String o_iri = o.getOntologyID().getOntologyIRI().toString();
		WhatifUtils.p("Adding declaration");
		WhatifUtils.p(name);
		WhatifUtils.p(IRI.create(o_iri + "#" + name));

		OWLEntity e = null;
		if (type.equals("class")) {
			e = df.getOWLClass(IRI.create(o_iri + "#" + name));
		} else if (type.equals("object")) {
			e = df.getOWLObjectProperty(IRI.create(o_iri + "#" + name));
		} else if (type.equals("data")) {
			e = df.getOWLDataProperty(IRI.create(o_iri + "#" + name));
		} else if (type.equals("individual")) {
			e = df.getOWLNamedIndividual(IRI.create(o_iri + "#" + name));
		}
		WhatifUtils.p(e);
		if (e != null) {
			o.getOWLOntologyManager().addAxiom(o, df.getOWLDeclarationAxiom(e));
		}
	}

	private OWLAxiom parseAxiom(String ax, OWLOntology o) throws ParserException {
		OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
		ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(df, ax);
		Set<OWLOntology> importsClosure = o.getImportsClosure();
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		// Create a bidirectional short form provider to do the actual mapping.
		// It will generate names using the input
		// short form provider.
		BidirectionalShortFormProvider bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(
				o.getOWLOntologyManager(), importsClosure, shortFormProvider);
		parser.setDefaultOntology(o);
		OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
		parser.setOWLEntityChecker(entityChecker);
		return parser.parseAxiom();
	}

	public String getScenarioDescription() {
		return currentscenario.getDescription();
	}

	public SurveyState getState() {
		return state;
	}

	public void validateScenario(OWLOntology owlOntology) {
		state = SurveyState.SCENARIOVALIDATE;
	}

	public boolean validateTask(OWLOntology o, boolean reasonerinit) {
		Map<String, Map<String, Set<String>>> add = currentscenario.getCurrentTask().getAdditions();
		Map<String, Map<String, Set<String>>> rem = currentscenario.getCurrentTask().getRemovals();

		for (String axiom : add.keySet()) {
			try {
				OWLAxiom ax = parseAxiom(axiom, o);
				if (!o.containsAxiom(ax)) {
					System.out.println("Not contains: "+ax.toString());
					return false;
				}
			} catch (ParserException e) {
				e.printStackTrace();
				return false;
			}

		}
		for (String axiom : rem.keySet()) {
			try {
				OWLAxiom ax = parseAxiom(axiom, o);
				if (o.containsAxiom(ax)) {
					System.out.println("Contains: "+ax.toString());
					return false;
				}
			} catch (ParserException e) {
				e.printStackTrace();
				return false;
			}
		}
		if (currentscenario.getCurrentTask().isRunReasoner()) {
			if (!reasonerinit) {
				return false;
			}
		}
		state = SurveyState.TASKVALIDATE;
		return true;
	}

	public void applyTaskChanges(OWLOntology o, boolean skip) {
		if(!skip && currentscenario.getCurrentTask().isManual()) {
			return;
		}
		Map<String, Map<String, Set<String>>> add = currentscenario.getCurrentTask().getAdditions();
		Map<String, Map<String, Set<String>>> rem = currentscenario.getCurrentTask().getRemovals();
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

		for (String axiom : add.keySet()) {
			Map<String, Set<String>> signature = add.get(axiom);
			for (String type : signature.keySet()) {
				Set<String> entities = signature.get(type);
				for (String entity : entities) {
					addDeclaration(entity, type, o);
				}
			}
			try {
				OWLAxiom ax = parseAxiom(axiom, o);
				changes.add(new AddAxiom(o, ax));
			} catch (ParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		for (String axiom : rem.keySet()) {
			Map<String, Set<String>> signature = rem.get(axiom);
			for (String type : signature.keySet()) {
				Set<String> entities = signature.get(type);
				for (String entity : entities) {
					addDeclaration(entity, type, o);
				}
			}

			try {
				OWLAxiom ax = parseAxiom(axiom, o);
				changes.add(new RemoveAxiom(o, ax));
			} catch (ParserException e) {
				e.printStackTrace();
			}
		}
		o.getOWLOntologyManager().applyChanges(changes);
	}

	public void validateQuestion(Object answer, QuestionMetadata qm) {
		state = SurveyState.QUESTIONVALIDATE;
		Map<String, String> rec = prepareRecord(answer);
		rec.putAll(qm.getRecord());
		List<String> rec_str = getStringOfRec(rec);
		try {
			csvFilePrinter.printRecord(rec_str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getStringOfRec(Map<String, String> rec) {
		List<String> l = new ArrayList<String>();
		for (Object key : fileheaderlist) {
			l.add(rec.get(key));
		}
		return l;
	}

	private Map<String, String> prepareRecord(Object answer) {
		// "participant","scenario","task","question","answer","true_pos","true_neg","false_pos","false_neg"

		/*
		 * Rating Expection question id
		 * 
		 * If the question was an expectation question, indicate the id of the
		 * question this question was an expectation of
		 */
		Map<String, String> rec = new HashMap<String, String>();
		rec.put("participant", getParticipantId());
		rec.put("scenario", getCurrentScenarioId());
		rec.put("task", getCurrentTaskId());
		rec.put("question", getCurrentQuestionId());
		rec.put("question_phrase", currentscenario.getCurrentTask().getCurrentQuestion().getQuestionPhrase());
		rec.put("widget", tubeSeperatedView(currentscenario.getCurrentTask().getCurrentQuestion().getViews()));
		Set<String> results = renderer.parseResults(answer);
		Set<String> correct = currentscenario.getCurrentTask().getCurrentQuestion().getCorrectOptions();
		analyseQuestionAnswer(rec, results, correct);
		return rec;
	}

	private String tubeSeperatedView(Collection<ProtegeView> views) {
		Set<String> widgets = new HashSet<String>();
		for (ProtegeView view : views) {
			widgets.add(view.getId());
		}
		return tubeSeperated(widgets);
	}

	private String tubeSeperated(Set<String> widgets) {
		StringBuilder sb = new StringBuilder();
		for (String s : widgets) {
			sb.append(s + "|");
		}
		return sb.toString().replaceAll("[|]$", "");
	}

	private void analyseQuestionAnswer(Map<String, String> rec, Set<String> results, Set<String> correct) {
		Set<String> all = new HashSet<String>(correct);
		all.addAll(results);
		int truepos = 0;
		int trueneg = 0;
		int falsepos = 0;
		int falseneg = 0;
		for (String item : all) {
			if (correct.contains(item)) {
				if (results.contains(item)) {
					truepos++;
				} else {
					falseneg++;
				}
			} else {
				if (results.contains(item)) {
					falsepos++;
				} else {
					trueneg++;
				}
			}
		}

		rec.put("true_pos", truepos + "");
		rec.put("true_neg", trueneg + "");
		rec.put("false_pos", falsepos + "");
		rec.put("false_neg", falseneg + "");
		rec.put("total_options", currentscenario.getCurrentTask().getCurrentQuestion().getOptions().size() + "");

		rec.put("answer", tubeSeperated(results));
		rec.put("correct_answer", tubeSeperated(correct));

	}

	private String getCurrentQuestionId() {

		return currentscenario.getCurrentTask().getCurrentQuestion().getId();
	}

	private String getCurrentTaskId() {
		return currentscenario.getCurrentTask().getId();
	}

	private String getCurrentScenarioId() {
		return currentscenario.getId();
	}

	private String getParticipantId() {
		return participant;
	}

	public void validateExpectation(Object answer) {
		// PErhaps send state of all of protege to check whether reasoner has
		// run and so on
		state = SurveyState.EXPECTVALIDATE;
	}

	public void validateRating(Object answer) {
		// PErhaps send state of all of protege to check whether reasoner has
		// run and so on
		state = SurveyState.RATEQUESTION;
	}

	public String getTaskDescription() {
		return currentscenario.getCurrentTask().getDescription();
	}

	public void nextState() {
		switch (state) {
		case READY:
			state = SurveyState.SCENARIO;
			break;
		case SCENARIO:
			state = SurveyState.SCENARIOVALIDATE;
			break;
		case SCENARIOVALIDATE:
			state = SurveyState.TASK;
			break;
		case TASK:
			state = SurveyState.TASKVALIDATE;
			break;
		case TASKVALIDATE:
			state = SurveyState.EXPECT;
			break;
		case EXPECT:
			state = SurveyState.EXPECTVALIDATE;
			break;
		case EXPECTVALIDATE:
			state = SurveyState.QUESTION;
			break;
		case QUESTION:
			state = SurveyState.QUESTIONVALIDATE;
			break;
		case QUESTIONVALIDATE:
			state = SurveyState.RATEQUESTION;
			break;
		case RATEQUESTION:
			if (currentscenario.hasNextQuestion()) {
				state = SurveyState.QUESTION;
				currentscenario.getCurrentTask().nextQuestion();
			} else if (currentscenario.hasNextTask()) {
				state = SurveyState.TASK;
				currentscenario.nextTask();
			} else if (!scenarios.isEmpty()) {
				state = SurveyState.SCENARIO;
				currentscenario = scenarios.remove();
				currentscenario.nextTask();
			} else {
				state = SurveyState.FINISHED;
				try {
					fileWriter.flush();
					fileWriter.close();
					csvFilePrinter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case FINISHED:

			break;
		default:
			break;
		}
	}

	public String getQuestionDescription() {
		return currentscenario.getCurrentTask().getCurrentQuestion().getQuestionPhrase();
	}

	public Object getQuestionOptions() {
		return renderer.renderQuestion(currentscenario.getCurrentTask().getCurrentQuestion());
	}

	public List<ProtegeView> getViews() {
		return currentscenario.getCurrentTask().getCurrentQuestion().getViews();
	}

	public String getURI() {
		return currentscenario.getURI();
	}

}
