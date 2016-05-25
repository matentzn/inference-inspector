package org.whatif.tools.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.inference.ReasonerStatus;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.selector.OWLEntitySelectorPanel;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationGeneratorFactory;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owl.explanation.impl.blackbox.checker.InconsistentOntologyExplanationGeneratorFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLNaryClassAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.whatif.tools.axiompattern.AddedAxiomRedundantPattern;
import org.whatif.tools.axiompattern.AssertedAxiomPattern;
import org.whatif.tools.axiompattern.AxiomPattern;
import org.whatif.tools.axiompattern.AxiomPatternFactory;
import org.whatif.tools.axiompattern.ProfileAxiomPattern;
import org.whatif.tools.axiompattern.TautologyAxiomPattern;
import org.whatif.tools.axiompattern.ViolatesOWLDLAxiomPattern;
import org.whatif.tools.axiompattern.ViolatesOWLELAxiomPattern;
import org.whatif.tools.axiompattern.ViolatesOWLQLAxiomPattern;
import org.whatif.tools.axiompattern.ViolatesOWLRLAxiomPattern;
import org.whatif.tools.consequence.WhatifAddedAssertionConsequence;
import org.whatif.tools.consequence.WhatifConsequence;
import org.whatif.tools.consequence.WhatifInferenceConsequence;
import org.whatif.tools.survey.util.AxiomRendererSelect;
import org.whatif.tools.survey.util.DLSyntaxAxiomRendererSelect;
import org.whatif.tools.survey.util.ManchesterSyntaxAxiomRendererSelect;
import org.whatif.tools.survey.util.ProtegeSyntaxRendererSelect;
import org.whatif.tools.util.AxiomListGroup;
import org.whatif.tools.util.AxiomPatternPriorityTable;
import org.whatif.tools.util.AxiomPriority;
import org.whatif.tools.util.EnitySelectDialog;
import org.whatif.tools.util.EntailmentGenerator;
import org.whatif.tools.util.EventLogging;
import org.whatif.tools.util.Icons;
import org.whatif.tools.util.OWLOntologyAxiomSelector;
import org.whatif.tools.util.ProfileVilationProvider;
import org.whatif.tools.util.ReasonerRunDiff;
import org.whatif.tools.util.TautologyManager;
import org.whatif.tools.util.TerseAxiomList;
import org.whatif.tools.util.UpdateInferenceInspector;
import org.whatif.tools.util.WhatifAxiomTable;
import org.whatif.tools.util.WhatifHistoryTable;
import org.whatif.tools.util.WhatifUtils;


@SuppressWarnings("rawtypes")
public class EntailmentInspectorView extends AbstractOWLSelectionViewComponent
		implements UpdateInferenceInspector, ActionListener, OWLOntologyChangeListener,
		OWLSelectionModelListener, ProfileVilationProvider, PropertyChangeListener {
	private static final long serialVersionUID = -4515710047558710080L;

	private static final Logger log = Logger.getLogger(EntailmentInspectorView.class);
	private static OWLObjectRenderer ren = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	private JProgressBar progressBar = new JProgressBar(0, 100);
	private Task task;
	private JLabel taskOutput = new JLabel("Progress..");
	private WorkspaceTab fake = null;

	List<ReasonerRunDiff> history_diffs = new ArrayList<ReasonerRunDiff>();
	Map<Class<? extends ProfileAxiomPattern>, OWLProfileReport> current_profile_reports = new HashMap<Class<? extends ProfileAxiomPattern>, OWLProfileReport>();

	Set<WhatifConsequence> consequences_inferred = new HashSet<WhatifConsequence>();
	Set<WhatifConsequence> consequences_removed_inferred = new HashSet<WhatifConsequence>();
	Set<WhatifConsequence> consequences_asserted = new HashSet<WhatifConsequence>();
	ReasonerRunDiff selected_history_point = null;

	private OWLModelManagerListener owlModelManagerListener;
	
	JPanel panel_top = null;
	JPanel panel_center = null;
	JPanel panel_inferenceinspector = null;
	JSplitPane split_view_options = null;
	JSplitPane split_history_options = null;

	JPanel panel_difftool = null;
	JPanel panel_report = null;
	JPanel panel_wii = null;

	JPanel typeselectpanel = null;
	JPanel typecheckboxpanel = null;
	JPanel orderpanel = null;
	JPanel axiompanel_added = null;
	JPanel axiompanel_removed = null;
	JScrollPane axiomscrollpanel_added = null;
	JScrollPane axiomscrollpanel_removed = null;
	JScrollPane axiomscrollpanel_added_asserted = null;
	JPanel filterpanel = null;
	JPanel panel_history_axioms = null;
	// JPanel view_hierachies_added = new JPanel(new GridLayout(4, 1));
	// JPanel view_hierachies_removed = new JPanel(new GridLayout(4, 1));

	JPanel view_added = new JPanel(new BorderLayout());
	JPanel view_removed = new JPanel(new BorderLayout());

	JPanel inferenceinspectoroptions = null;
	JPanel difftooloptions = null;

	JToggleButton bt_ii_history = new JToggleButton("History", true);
	JToggleButton bt_ii_opts = new JToggleButton("Options", true);
	JToggleButton bt_diff_diff = new JToggleButton("Diff", true);
	JToggleButton bt_diff_opts = new JToggleButton("Options", true);

	private EnitySelectDialog dialog_selectentity = null;
	private OWLEntitySelectorPanel entityselect = null;

	final WhatifHistoryTable table_history_select = new WhatifHistoryTable();
	ButtonGroup buttong_group_table_history_select = new ButtonGroup();
	JRadioButton lastButton = null;

	WhatifAxiomTable axioms_added = null;
	WhatifAxiomTable axioms_removed = null;
	WhatifAxiomTable axioms_added_assertions = null;
	AxiomPatternPriorityTable table_axiom_pattern_priority = new AxiomPatternPriorityTable(this);
	JList<AxiomType> axiomtypelist = new JList<AxiomType>();

	ExplanationGeneratorFactory<OWLAxiom> genFac = null;
	ExplanationGeneratorFactory<OWLAxiom> incongenFac = null;
	static ExplanationGenerator<OWLAxiom> gen = null;
	static ExplanationGenerator<OWLAxiom> incongen = null;

	TerseAxiomList list_history_added_axioms = new TerseAxiomList();
	TerseAxiomList list_history_removed_axioms = new TerseAxiomList();

	/*
	 * OWLWhatifHierarchyViewComponent whatif_classhierarchy_added = null;
	 * OWLWhatifHierarchyViewComponent whatif_datapropertyhierarchy_added =
	 * null; OWLWhatifHierarchyViewComponent
	 * whatif_objectpropertyhierarchy_added = null;
	 * OWLWhatifHierarchyViewComponent whatif_individualtypehierarchy_added =
	 * null;
	 * 
	 * OWLWhatifHierarchyViewComponent whatif_classhierarchy_removed = null;
	 * OWLWhatifHierarchyViewComponent whatif_datapropertyhierarchy_removed =
	 * null; OWLWhatifHierarchyViewComponent
	 * whatif_objectpropertyhierarchy_removed = null;
	 * OWLWhatifHierarchyViewComponent whatif_individualtypehierarchy_removed =
	 * null;
	 */

	JCheckBox cb_tautology = new JCheckBox("Include Tautologies", false);
	// JCheckBox cb_non_tautology = new JCheckBox("Non-tautologies", true);
	// JCheckBox cb_direct = new JCheckBox("Direct", false);
	// JCheckBox cb_indirect = new JCheckBox("Indirect", false);
	// JCheckBox cb_inferred = new JCheckBox("Inferred", false);
	// JCheckBox cb_asserted = new JCheckBox("Asserted", false);
	JCheckBox cb_selected = new JCheckBox("Focus selected", false);
	JCheckBox cb_keepimportant = new JCheckBox("Keep critical", true);

	JCheckBox cb_axt_csub = new JCheckBox("Subsumption", false);
	JCheckBox cb_axt_assert = new JCheckBox("Types", false);
	JCheckBox cb_axt_property_assert = new JCheckBox("Relations", false);
	JCheckBox cb_axt_equiv = new JCheckBox("Equivalence", false);
	JCheckBox cb_axt_disj = new JCheckBox("Disjointness", false);
	JCheckBox cb_axt_character = new JCheckBox("Characteristics", false);

	TautologyManager tautologytracker = null;

	JButton bt_axtypes_advanced = new JButton("Advanced");

	JButton bt_entity_filter_reset = new JButton("Reset");
	JButton bt_entity_filter = new JButton("Entity Filter");
	JTextArea entity_select_label = new JTextArea("None selected.");

	JComboBox<AxiomListGroup> combo_group = new JComboBox<AxiomListGroup>();
	JComboBox<AxiomRendererSelect> combo_renderer = new JComboBox<AxiomRendererSelect>();
	JComboBox<String> combo_type = new JComboBox<String>();

	JToggleButton bt_diff = new JToggleButton();
	JLabel l = new JLabel();
	JToggleButton bt_history = new JToggleButton();
	// JToggleButton bt_toggle_axiom_hierarchy = new JToggleButton();
	JToggleButton bt_report = new JToggleButton();
	JLabel syncstatus = new JLabel();
	JLabel addedlabel = new JLabel("New Inferences");
	JLabel removedlabel = new JLabel("Lost Inferences");

	boolean all_atomic_subs = false;
	boolean initialised = false;
	boolean reallyclassified = false;

	long startreasonertime = 0;
	long endreasonertime = 0;

	Map<AxiomPattern, Long> pattern_timings = new HashMap<AxiomPattern, Long>();

	@Override
	public void initialiseView() throws Exception {
		setLayout(new BorderLayout());
		//EventLogging.prepare(new File("output_wii.txt"));
		getOWLModelManager().addOntologyChangeListener(this);
		getOWLWorkspace().getOWLSelectionModel().addListener(this);
		// GridBagConstraints c = createInitialGridBagConstraints();
		ActionListener al = createUpdateViewActionListener();
		ChangeListener cl = createUpdateViewChangeListener();

		panel_top = new JPanel();
		panel_top.setLayout(new GridLayout());
		panel_center = new JPanel();
		panel_center.setLayout(new GridLayout(1, 1));

		panel_inferenceinspector = new JPanel(new GridLayout(1, 1));
		panel_wii = new JPanel(new GridLayout(1, 1));
		createInferenceInspectorInfo();

		panel_difftool = new JPanel(new GridLayout(1, 1));
		panel_report = new JPanel(new GridLayout(1, 1));

		typeselectpanel = new JPanel();
		typeselectpanel.setLayout(new GridLayout(2, 1));

		orderpanel = new JPanel();
		orderpanel.setLayout(new BorderLayout());
		
		axiompanel_added = new JPanel(new GridLayout(1, 1));
		axiompanel_removed = new JPanel(new GridLayout(2, 1));

		combo_type.addItem("Syntactic");
		combo_type.addItem("Semantic");

		combo_group.addItem(AxiomListGroup.AXIOMTYPE);
		combo_group.addItem(AxiomListGroup.NONE);
		combo_group.addItem(AxiomListGroup.ENTITY);
		combo_group.addItem(AxiomListGroup.ENTITY);

		DefaultListModel dm = new DefaultListModel<AxiomType>();
		axiomtypelist.setModel(dm);
		for (AxiomType t : getAllAxiomTypes()) {
			dm.addElement(t);
		}
		axiomtypelist.setSelectionModel(new DefaultListSelectionModel() {
			@Override
			public void setSelectionInterval(int index0, int index1) {
				if (super.isSelectedIndex(index0)) {
					super.removeSelectionInterval(index0, index1);
				} else {
					super.addSelectionInterval(index0, index1);
				}
			}
		});

		combo_renderer.addItem(new ManchesterSyntaxAxiomRendererSelect());
		combo_renderer.addItem(new DLSyntaxAxiomRendererSelect());
		combo_renderer.addItem(new ProtegeSyntaxRendererSelect());
		
		// combo_renderer.addItem(new
		// LatexOWLAxiomRendererSelect(getO().getOWLOntologyManager().getOWLDataFactory()));
		// combo_renderer.addItem(new ProtegeSyntaxRendererSelect());
		combo_renderer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"change_renderer", "NA", "wii");
				ren = ((AxiomRendererSelect) combo_renderer.getSelectedItem()).getRenderer();
				updateView();
			}
		});

		entityselect = new OWLEntitySelectorPanel(getOWLEditorKit(), true);
		dialog_selectentity = new EnitySelectDialog(getFrame(), this, entityselect);

		combo_group.addActionListener(al);

		prepareMainButtons();

		bt_entity_filter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"entity_filter", "NA", "wii");
				dialog_selectentity.setVisible(true);
			}
		});
		
		bt_axtypes_advanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel panel_at_advanced_plus_label = getLabelledGridLayoutPanel("Advanced Axiom Type Selection",
						axiomtypelist);
				JOptionPane.showMessageDialog(EntailmentInspectorView.this, panel_at_advanced_plus_label);
			}
		});

		bt_entity_filter_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"entity_filter_reset", "NA", "wii");
				entityselect.setSelection(new HashSet<OWLEntity>());
				updateEntitySelectTA();
				updateView();
			}
		});

		// bt_entity_filter.setPreferredSize(new Dimension(100,50));
		// bt_entity_filter_reset.setPreferredSize(new Dimension(100,50));

		typeselectpanel.add(combo_type);

		createAxiomTypeCheckboxPanel(al);

		createOptionsViewButtons();

		createMainToolbar(al);


		// createStatusSyncSymbol();

		genFac = ExplanationManager.createExplanationGeneratorFactory(
				getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory());
		incongenFac = new InconsistentOntologyExplanationGeneratorFactory(getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory(),10000l);
		gen = genFac.createExplanationGenerator(getO());
		incongen = incongenFac.createExplanationGenerator(getO());
		
		axioms_added = new WhatifAxiomTable(getOWLModelManager(), getOWLWorkspace().getOWLSelectionModel(), getOWLEditorKit(),
				"inferred_consequences", this);
		axioms_removed = new WhatifAxiomTable(getOWLModelManager(), getOWLWorkspace().getOWLSelectionModel(), getOWLEditorKit(),
				"removed_inferred_consequences", this);
		axioms_removed.hideColumn("Info");
		axioms_added_assertions = new WhatifAxiomTable(getOWLModelManager(), getOWLWorkspace().getOWLSelectionModel(), getOWLEditorKit(),
				"added_assertions_consequences", this);
		axioms_added_assertions.showColumn(2);

		createHierarchyAndAxiomViewsForHistoryView();
		createTautologyManager();

		// Now create the actual explanation generator for our ontology

		panel_top.add(orderpanel);

		createReasonerSnapshot(System.currentTimeMillis(), getAllAxiomTypes(), new HashSet<OWLAxiom>(),
				new HashSet<WhatifInferenceConsequence>());

		createKnowledgeAddedHeader();
		createKnowledgeRemovedHeader();

		view_added.add(axiompanel_added, BorderLayout.CENTER);
		view_removed.add(axiompanel_removed, BorderLayout.CENTER);

		JSplitPane split_addition_removal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, view_added, view_removed);
		split_addition_removal.setOneTouchExpandable(false);
		// split_addition_removal.setDividerSize(2);
		// split_addition_removal.setDividerLocation(0.5);
		split_addition_removal.setResizeWeight(0.5);
		split_addition_removal.setContinuousLayout(true);

		JScrollPane panel_scroll_filterpanel = createInferenceHistoryOptionsPanel(cl);
		panel_scroll_filterpanel.getVerticalScrollBar().setUnitIncrement(20);

		JComponent tab_history_history_panel = new JPanel();
		tab_history_history_panel.setLayout(new BorderLayout());
		split_history_options = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel_scroll_filterpanel,
				tab_history_history_panel);
		split_history_options.setOneTouchExpandable(false);
		split_history_options.setResizeWeight(0.5);
		split_history_options.getLeftComponent().setMinimumSize(new Dimension());
		split_history_options.getRightComponent().setMinimumSize(new Dimension());

		split_view_options = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split_addition_removal, split_history_options);
		split_view_options.setOneTouchExpandable(false);
		split_view_options.setResizeWeight(0.5);

		createInferenceHistoryHistoryPanel(tab_history_history_panel);
		add(panel_top, BorderLayout.PAGE_START);

		panel_inferenceinspector.add(split_view_options);
		bt_history.setSelected(true);
		inferenceinspectoroptions.setVisible(true);
		panel_center.add(panel_inferenceinspector);
		add(panel_center, BorderLayout.CENTER);

		progressBar.setStringPainted(true);

		createOWLModelManagerLister();
		renderSyncStatus(false);

		// split_view_options.repaint();
		// split_history_options.repaint();

		if (getOWLModelManager().getOWLReasonerManager().getReasonerStatus().equals(ReasonerStatus.INITIALIZED)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
			//updateView();
		} else if (getOWLModelManager().getOWLReasonerManager().getReasonerStatus().equals(ReasonerStatus.INCONSISTENT)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
			//updateView();
		}
		else {
			updateView();
		}
		//ClickButtons cb = new ClickButtons();
		// cb.execute();
		
		// bt_ii_history.setSelected(false);
		// bt_ii_opts.setEnabled(false);
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				bt_ii_history.doClick();
				bt_ii_opts.doClick();
			}
		});
		
		
		initialised = true;
		log.info("Entailment inspector initialized");
	}

	private JPanel createFontSizePanel() {
		JPanel fontsizepanel = new JPanel(new BorderLayout());
		JPanel fontsizebuttonpanel = new JPanel();
		fontsizebuttonpanel.setLayout(new BoxLayout(fontsizebuttonpanel, BoxLayout.X_AXIS));
		JLabel label = new JLabel("Font-size");
		label.setFont(label.getFont().deriveFont(Font.PLAIN, 18.0f));
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		fontsizepanel.add(label, BorderLayout.NORTH);
		fontsizepanel.add(fontsizebuttonpanel, BorderLayout.CENTER);
		JButton bt_plus = new JButton("+");
		bt_plus.setFont(bt_plus.getFont().deriveFont(Font.PLAIN, 20.0f));

		bt_plus.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"font_size_plus", "NA", "wii");
				increaseFontSize();

			}
		});
		JButton bt_minus = new JButton("-");
		bt_minus.setFont(bt_minus.getFont().deriveFont(Font.PLAIN, 20.0f));

		bt_minus.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"font_size_minus", "NA", "wii");
				reduceFontSize();

			}
		});

		JButton bt_reset = new JButton("Reset");
		bt_reset.setFont(bt_reset.getFont().deriveFont(Font.PLAIN, 20.0f));
		bt_reset.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"font_size_reset", "NA", "wii");
				resetFontSize();

			}
		});

		fontsizebuttonpanel.add(bt_plus);
		fontsizebuttonpanel.add(bt_minus);
		fontsizebuttonpanel.add(bt_reset);
		fontsizebuttonpanel.add(new JLabel(""));
		return fontsizepanel;
	}

	protected void resetFontSize() {
		axioms_added.resetFontSize();
	}

	protected void reduceFontSize() {
		axioms_added.decreaseFontSize();
		axioms_removed.decreaseFontSize();
		axioms_added_assertions.decreaseFontSize();
	}

	protected void increaseFontSize() {
		axioms_added.increaseFontSize();
		axioms_removed.increaseFontSize();
		axioms_added_assertions.increaseFontSize();
	}

	private Set<AxiomType> getAllAxiomTypes() {
		Set<AxiomType> types = new HashSet<AxiomType>();
		types.add(AxiomType.SUBCLASS_OF);
		types.add(AxiomType.SUB_OBJECT_PROPERTY);
		types.add(AxiomType.SUB_DATA_PROPERTY);
		types.add(AxiomType.CLASS_ASSERTION);
		types.add(AxiomType.EQUIVALENT_CLASSES);
		types.add(AxiomType.EQUIVALENT_DATA_PROPERTIES);
		types.add(AxiomType.EQUIVALENT_OBJECT_PROPERTIES);
		types.add(AxiomType.DISJOINT_CLASSES);
		//types.add(AxiomType.DISJOINT_OBJECT_PROPERTIES);
		//types.add(AxiomType.DISJOINT_DATA_PROPERTIES);
		//types.add(AxiomType.DATA_PROPERTY_ASSERTION);
		types.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
		types.add(AxiomType.FUNCTIONAL_DATA_PROPERTY);
		types.add(AxiomType.FUNCTIONAL_OBJECT_PROPERTY);
		types.add(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
		types.add(AxiomType.REFLEXIVE_OBJECT_PROPERTY);
		types.add(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
		types.add(AxiomType.TRANSITIVE_OBJECT_PROPERTY);
		types.add(AxiomType.SYMMETRIC_OBJECT_PROPERTY);
		types.add(AxiomType.ASYMMETRIC_OBJECT_PROPERTY);

		return types;
	}

	private JLabel createDefaultRemovedJLabel() {
		JLabel removedlabel = new JLabel("Lost Knowledge");
		removedlabel.setIcon(Icons.getIcon("bin.png"));
		removedlabel.setPreferredSize(new Dimension(30, 30));
		removedlabel.setHorizontalTextPosition(JLabel.LEFT);
		removedlabel.setIconTextGap(30);
		removedlabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		removedlabel.setFont(removedlabel.getFont().deriveFont(Font.PLAIN, 11.0f));
		// removedlabel.setFont(new Font("Serif", Font.BOLD, 18));
		removedlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// removedlabel.setBackground(new Color(227, 111, 111));
		removedlabel.setOpaque(true);
		return removedlabel;
	}

	private void createKnowledgeAddedHeader() {
		addedlabel.setIcon(Icons.getIcon("added.png"));
		addedlabel.setPreferredSize(new Dimension(30, 30));
		addedlabel.setHorizontalTextPosition(JLabel.LEFT);
		addedlabel.setIconTextGap(30);
		addedlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		addedlabel.setFont(addedlabel.getFont().deriveFont(Font.PLAIN, 18.0f));
		addedlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// addedlabel.setBackground(new Color(111, 227, 111));
		addedlabel.setOpaque(true);
		view_added.add(addedlabel, BorderLayout.PAGE_START);
	}

	private void createKnowledgeRemovedHeader() {
		removedlabel.setIcon(Icons.getIcon("bin.png"));
		removedlabel.setPreferredSize(new Dimension(30, 30));
		removedlabel.setHorizontalTextPosition(JLabel.LEFT);
		removedlabel.setIconTextGap(30);
		removedlabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		removedlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		removedlabel.setFont(removedlabel.getFont().deriveFont(Font.PLAIN, 18.0f));
		// addedlabel.setBackground(new Color(111, 227, 111));
		removedlabel.setOpaque(true);
		view_removed.add(removedlabel, BorderLayout.PAGE_START);
	}

	private JLabel createDefaultAddedJLabel() {
		JLabel addedlabel = new JLabel("Added Knowledge");
		addedlabel.setIcon(Icons.getIcon("added.png"));
		addedlabel.setPreferredSize(new Dimension(30, 30));
		addedlabel.setHorizontalTextPosition(JLabel.LEFT);
		addedlabel.setIconTextGap(30);
		addedlabel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		addedlabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// addedlabel.setBackground(new Color(111, 227, 111));
		addedlabel.setOpaque(true);
		return addedlabel;
	}

	private GridBagConstraints createInitialGridBagConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		return c;
	}

	private ChangeListener createUpdateViewChangeListener() {
		ChangeListener cl = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateView();
			}
		};
		return cl;
	}

	private ActionListener createUpdateViewActionListener() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateView();
			}
		};
		return al;
	}

	protected void updateHistoryPanel() {
		list_history_added_axioms.clearAxioms();
		list_history_added_axioms.addAxioms(getAddedAxiomsComparedToHistoryState());
		list_history_removed_axioms.clearAxioms();
		list_history_removed_axioms.addAxioms(getRemovedAxiomsComparedToHistoryState());
		list_history_added_axioms.validate();
		list_history_removed_axioms.validate();
		list_history_added_axioms.repaint();
		list_history_removed_axioms.repaint();
	}

	private Set<OWLAxiom> getAddedAxiomsComparedToHistoryState() {
		Set<OWLAxiom> added = new HashSet<OWLAxiom>();
		int index = history_diffs.indexOf(selected_history_point);
		Set<OWLAxiom> prev = index > 0 ? history_diffs.get(index - 1).getAssertions() : new HashSet<OWLAxiom>();
		Set<OWLAxiom> currentcompare = selected_history_point.getAssertions();
		added.addAll(currentcompare);
		added.removeAll(prev);
		return added;
	}

	private ReasonerRunDiff getLatestSnapshot() {
		return history_diffs.size() > 0 ? history_diffs.get(history_diffs.size() - 1) : null;
	}

	private Set<OWLAxiom> getRemovedAxiomsComparedToHistoryState() {
		Set<OWLAxiom> removed = new HashSet<OWLAxiom>();
		int index = history_diffs.indexOf(selected_history_point);
		Set<OWLAxiom> prev = index > 0 ? history_diffs.get(index - 1).getAssertions() : new HashSet<OWLAxiom>();
		Set<OWLAxiom> currentcompare = selected_history_point.getAssertions();
		removed.addAll(prev);
		removed.removeAll(currentcompare);
		return removed;
	}

	private void createOWLModelManagerLister() {
		owlModelManagerListener = new OWLModelManagerListener() {
			public void handleChange(OWLModelManagerChangeEvent event) {
				try {
					handleModelManagerEvent(event.getType());
				} catch (Exception t) {
					//ProtegeApplication.getLogManager()event..getErrorLog().logError(t);
				}
			}
		};
		getOWLModelManager().addListener(owlModelManagerListener);
	}

	private JScrollPane createInferenceHistoryOptionsPanel(ChangeListener cl) {
		// tab_history_view_panel.setLayout(new GridLayout(1, 2));

		//JPanel fontsize_panel = createFontSizePanel();

		filterpanel = new JPanel();
		// filterpanel.setBackground(Color.BLUE);
		filterpanel.setLayout(new BoxLayout(filterpanel, BoxLayout.Y_AXIS));
		filterpanel.setBorder(new EmptyBorder(3, 10, 3, 10));

		JPanel panel_axiomtypecheckboxes_plus_label = getLabelledGridLayoutPanel("Filter Axiomtypes",
				typecheckboxpanel);
		// JPanel panel_group_plus_label =
		// getLabelledGridLayoutPanel("Grouping", combo_group);
		JPanel panel_renderer_plus_label = getLabelledGridLayoutPanel("Renderer", combo_renderer);
		

		axiomtypelist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"list_axiomtypes_selection", "NA", "wii");
			}
		});

		JPanel panel_axiomprio_plus_label = getLabelledGridLayoutPanel("Priority Table", table_axiom_pattern_priority);

		table_axiom_pattern_priority.addMouseListener(
				new LoggingMouseListener(table_axiom_pattern_priority, "table_axiom_pattern_priority_click"));

		JPanel gl = new JPanel(new GridLayout(1, 2));
		gl.add(bt_entity_filter);
		gl.add(bt_entity_filter_reset);
		gl.setPreferredSize(new Dimension(0, 25));
		JPanel panel_entityfilter_plus_label = getLabelledGridLayoutPanel("Entity Filter", gl);

		// JPanel panel_tautology_select_plus_label =
		// createTautologyCheckboxPanel(cl);

		// panel_axiomtypecheckboxes_plus_label.setMaximumSize(new
		// Dimension(0,10));
		//filterpanel.add(getMainLabel("Options"));
		//filterpanel.add(Box.createRigidArea(new Dimension(10, 20)));
		//filterpanel.add(fontsize_panel);
		//filterpanel.add(Box.createRigidArea(new Dimension(10, 10)));
		//filterpanel.add(new JSeparator(SwingConstants.HORIZONTAL));

		JPanel tautologypanel = createTautologyCheckboxPanel(cl);
		
		filterpanel.add(panel_axiomtypecheckboxes_plus_label);
		filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		filterpanel.add(bt_axtypes_advanced);
		filterpanel.add(Box.createRigidArea(new Dimension(10, 10)));
		filterpanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		filterpanel.add(tautologypanel);		
		filterpanel.add(Box.createRigidArea(new Dimension(10, 10)));			
		//filterpanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		//filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		//filterpanel.add(panel_at_advanced_plus_label);
		//filterpanel.add(Box.createRigidArea(new Dimension(10, 10)));
		//filterpanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		//filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		//filterpanel.add(panel_renderer_plus_label);
		//filterpanel.add(Box.createRigidArea(new Dimension(10, 10)));
		filterpanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		filterpanel.add(panel_entityfilter_plus_label);
		filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		filterpanel.add(entity_select_label);
		filterpanel.add(Box.createRigidArea(new Dimension(10, 10)));
		filterpanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		filterpanel.add(Box.createRigidArea(new Dimension(10, 5)));
		filterpanel.add(panel_axiomprio_plus_label);
		// filterpanel.add(panel_tautology_select_plus_label);

		JScrollPane panel_scroll_filterpanel = new JScrollPane(filterpanel);
		return panel_scroll_filterpanel;
	}

	private void createInferenceHistoryHistoryPanel(JComponent tab_history_history_panel) {
		JComponent p = createHistoryPane();
		JLabel addedlabel = createDefaultAddedJLabel();
		addedlabel.setText("Additions");
		JLabel removedlabel = createDefaultRemovedJLabel();
		removedlabel.setText("Removals");
		JPanel panel_history_add_removed = new JPanel(new GridLayout(2, 1));
		JPanel panel_history_add = new JPanel(new BorderLayout());
		JPanel panel_history_removed = new JPanel(new BorderLayout());
		// panel_history_add_removed.setLayout(new
		// BoxLayout(panel_history_add_removed, BoxLayout.Y_AXIS));
		JScrollPane js1 = new JScrollPane(list_history_added_axioms);
		list_history_added_axioms.setAutoscrolls(true);
		list_history_added_axioms.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"list_history_added_axioms_selection", "NA", "wii");
			}
		});

		JScrollPane js2 = new JScrollPane(list_history_removed_axioms);
		list_history_removed_axioms.setAutoscrolls(true);
		list_history_removed_axioms.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"list_history_removed_axioms_selection", "NA", "wii");
			}
		});

		panel_history_add.add(addedlabel, BorderLayout.NORTH);
		panel_history_add.add(js1, BorderLayout.CENTER);
		panel_history_removed.add(removedlabel, BorderLayout.NORTH);
		panel_history_removed.add(js2, BorderLayout.CENTER);
		// panel_history_add_removed.setPreferredSize(new Dimension(200, 0));
		// panel_history_add_removed
		panel_history_add_removed.add(panel_history_add);
		panel_history_add_removed.add(panel_history_removed);
		panel_history_add_removed.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		// panel_history_add.setPreferredSize(new Dimension(180, 0));
		// panel_history_removed.setPreferredSize(new Dimension(120, 0));
		p.setPreferredSize(new Dimension(200, 0));
		p.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
		tab_history_history_panel.add(getMainLabel("History"), BorderLayout.NORTH);
		tab_history_history_panel.add(p, BorderLayout.LINE_START);
		tab_history_history_panel.add(panel_history_add_removed, BorderLayout.CENTER);
	}

	private void createHierarchyAndAxiomViewsForHistoryView() {
		/*
		 * whatif_classhierarchy_added = new
		 * OWLWhatifHierarchyViewComponent(getOWLDataFactory().getOWLThing());
		 * whatif_datapropertyhierarchy_added = new
		 * OWLWhatifHierarchyViewComponent(
		 * getOWLDataFactory().getOWLTopDataProperty());
		 * whatif_objectpropertyhierarchy_added = new
		 * OWLWhatifHierarchyViewComponent(
		 * getOWLDataFactory().getOWLTopObjectProperty());
		 * whatif_individualtypehierarchy_added = new
		 * OWLWhatifHierarchyViewComponent(getOWLDataFactory().getOWLThing());
		 * 
		 * whatif_classhierarchy_removed = new
		 * OWLWhatifHierarchyViewComponent(getOWLDataFactory().getOWLThing());
		 * whatif_datapropertyhierarchy_removed = new
		 * OWLWhatifHierarchyViewComponent(
		 * getOWLDataFactory().getOWLTopDataProperty());
		 * whatif_objectpropertyhierarchy_removed = new
		 * OWLWhatifHierarchyViewComponent(
		 * getOWLDataFactory().getOWLTopObjectProperty());
		 * whatif_individualtypehierarchy_removed = new
		 * OWLWhatifHierarchyViewComponent(getOWLDataFactory().getOWLThing());
		 * 
		 * view_hierachies_added.add(whatif_classhierarchy_added);
		 * view_hierachies_added.add(whatif_datapropertyhierarchy_added);
		 * view_hierachies_added.add(whatif_objectpropertyhierarchy_added);
		 * view_hierachies_added.add(whatif_individualtypehierarchy_added);
		 * view_hierachies_removed.add(whatif_classhierarchy_removed);
		 * view_hierachies_removed.add(whatif_datapropertyhierarchy_removed);
		 * view_hierachies_removed.add(whatif_objectpropertyhierarchy_removed);
		 * view_hierachies_removed.add(whatif_individualtypehierarchy_removed);
		 * 
		 */

		axiomscrollpanel_added = new JScrollPane(axioms_added);
		axiomscrollpanel_removed = new JScrollPane(axioms_removed);
		axiomscrollpanel_added_asserted = new JScrollPane(axioms_added_assertions);
		axiompanel_added.add(axiomscrollpanel_added);
		axiompanel_removed.add(axiomscrollpanel_removed);
		JPanel p = getLabelledGridLayoutPanel("Consequences of new assertions", axiomscrollpanel_added_asserted);
		axiompanel_removed.add(p);
		Map<WhatifConsequence, AxiomPriority> infs = new HashMap<WhatifConsequence, AxiomPriority>();
		/*
		 * for (WhatifConsequence con : inferred) { AxiomPriority prio =
		 * getPriority(con); if (prio == AxiomPriority.REMOVE) { infs.put(con,
		 * prio); } }
		 */
		axioms_added.setAxioms(infs);
	}

	private JPanel createTautologyCheckboxPanel(ChangeListener cl) {
		cb_tautology.addChangeListener(cl);
		JPanel panel_tautology_select = new JPanel();
		panel_tautology_select.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel_tautology_select.add(cb_tautology);
		JPanel panel_tautology_select_plus_label = getLabelledGridLayoutPanel("Filter Tautologies",
				panel_tautology_select);
		return panel_tautology_select_plus_label;
	}

	private void createStatusSyncSymbol() {
		JPanel pstatus = new JPanel();
		pstatus.setLayout(new GridLayout(1, 1));
		// pstatus.add(bt_toggle_axiom_hierarchy, c);
		pstatus.add(syncstatus);
		// orderpanel.add(pstatus, BorderLayout.LINE_END);
	}

	private void createTautologyManager() {
		try {
			OWLReasoner r = getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory()
					.getReasonerFactory().createReasoner(OWLManager.createOWLOntologyManager().createOntology());

			tautologytracker = new TautologyManager(r);

		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createMainToolbar(ActionListener al) {
		JPanel passerted = new JPanel();
		passerted.setLayout(new GridLayout(2, 1));
		passerted.add(cb_keepimportant);
		passerted.add(cb_selected);

		/*
		Insets insets = passerted.getInsets();
		Dimension size = cb_keepimportant.getPreferredSize();
		cb_keepimportant.setBounds(5 + insets.left, 0 + insets.top, size.width, size.height);
		size = cb_selected.getPreferredSize();
		// cb_inferred.setBounds(5 + insets.left, 17 + insets.top, size.width,
		// size.height);
		// size = cb_selected.getPreferredSize();
		cb_selected.setBounds(5 + insets.left, 18 + insets.top, size.width, size.height);
		*/
		cb_keepimportant.addActionListener(al);
		cb_keepimportant.addMouseListener(new LoggingMouseListener(cb_keepimportant, "cb_keepimportant"));
		cb_selected.addActionListener(al);
		cb_selected.addMouseListener(new LoggingMouseListener(cb_selected, "cb_selected"));
		
		JPanel x = new JPanel(new BorderLayout());
		x.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		x.add(taskOutput, BorderLayout.NORTH);
		x.add(progressBar, BorderLayout.CENTER);
		bt_diff.setVisible(false);
		// pdiff.add(difftooloptions);
		// pdiff.add(bt_report);
		bt_report.setVisible(false);
		
		JPanel pdiff = new JPanel();
		pdiff.setLayout(new BorderLayout());
		pdiff.add(x,BorderLayout.CENTER);
		pdiff.add(passerted,BorderLayout.LINE_START);
		passerted.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		
		orderpanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 12, 10));
		orderpanel.add(inferenceinspectoroptions, BorderLayout.LINE_START);
		orderpanel.add(pdiff, BorderLayout.CENTER);
	}

	class LoggingMouseListener implements MouseListener {

		String name;
		Component cb;

		LoggingMouseListener(Component cb, String name) {
			this.name = name;
			this.cb = cb;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			String state = "NA";
			if (cb instanceof AbstractButton) {
				state = ((AbstractButton) cb).isSelected() + "";
			}
			EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(), name, state,
					"wii");
		}
	}

	private void createLoggingMouseListener(JCheckBox cb, String name) {
		MouseListener l = new LoggingMouseListener(cb, name);
		cb.addMouseListener(l);
	}

	private void createAxiomTypeCheckboxPanel(ActionListener al) {
		typecheckboxpanel = new JPanel();
		// typecheckboxpanel.setBackground(Color.YELLOW);
		typecheckboxpanel.setLayout(new GridLayout(3, 2));
		// typecheckboxpanel.setPreferredSize(new Dimension(100, 50));
		typecheckboxpanel.add(cb_axt_csub);
		typecheckboxpanel.add(cb_axt_equiv);
		typecheckboxpanel.add(cb_axt_disj);
		typecheckboxpanel.add(cb_axt_character);
		typecheckboxpanel.add(cb_axt_assert);
		typecheckboxpanel.add(cb_axt_property_assert);

		createLoggingMouseListener(cb_axt_csub, "cb_axt_csub");
		createLoggingMouseListener(cb_axt_equiv, "cb_axt_equiv");
		createLoggingMouseListener(cb_axt_disj, "cb_axt_disj");
		createLoggingMouseListener(cb_axt_character, "cb_axt_character");
		createLoggingMouseListener(cb_axt_assert, "cb_axt_assert");
		createLoggingMouseListener(cb_axt_property_assert, "cb_axt_property_assert");

		// typecheckboxpanel.setBackground(Color.BLUE);
		// typecheckboxpanel.setSize(new Dimension(300, 1));
		cb_axt_assert.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ListModel model = axiomtypelist.getModel();
				JCheckBox cb = (JCheckBox) e.getSource();
				for (int i = 0; i < model.getSize(); i++) {
					AxiomType at = (AxiomType) model.getElementAt(i);
					if (at == AxiomType.CLASS_ASSERTION) {
						if (cb.isSelected() && !axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						} else if (!cb.isSelected() && axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						}
					}
				}

			}
		});
		cb_axt_character.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				ListModel model = axiomtypelist.getModel();
				Set<AxiomType> types = new HashSet<AxiomType>();
				types.add(AxiomType.FUNCTIONAL_DATA_PROPERTY);
				types.add(AxiomType.FUNCTIONAL_OBJECT_PROPERTY);
				types.add(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
				types.add(AxiomType.REFLEXIVE_OBJECT_PROPERTY);
				types.add(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
				types.add(AxiomType.TRANSITIVE_OBJECT_PROPERTY);
				types.add(AxiomType.SYMMETRIC_OBJECT_PROPERTY);
				types.add(AxiomType.ASYMMETRIC_OBJECT_PROPERTY);
				for (int i = 0; i < model.getSize(); i++) {
					AxiomType at = (AxiomType) model.getElementAt(i);
					if (types.contains(at)) {
						if (cb.isSelected() && !axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						} else if (!cb.isSelected() && axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						}
					}
				}

			}
		});
		cb_axt_csub.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				ListModel model = axiomtypelist.getModel();
				Set<AxiomType> types = new HashSet<AxiomType>();
				types.add(AxiomType.SUBCLASS_OF);
				types.add(AxiomType.SUB_OBJECT_PROPERTY);
				types.add(AxiomType.SUB_DATA_PROPERTY);
				for (int i = 0; i < model.getSize(); i++) {
					AxiomType at = (AxiomType) model.getElementAt(i);
					if (types.contains(at)) {
						if (cb.isSelected() && !axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						} else if (!cb.isSelected() && axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						}
					}
				}

			}
		});
		cb_axt_disj.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				ListModel model = axiomtypelist.getModel();
				Set<AxiomType> types = new HashSet<AxiomType>();
				types.add(AxiomType.DISJOINT_CLASSES);
				//types.add(AxiomType.DISJOINT_OBJECT_PROPERTIES);
				//types.add(AxiomType.DISJOINT_DATA_PROPERTIES);
				for (int i = 0; i < model.getSize(); i++) {
					AxiomType at = (AxiomType) model.getElementAt(i);
					if (types.contains(at)) {
						if (cb.isSelected() && !axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						} else if (!cb.isSelected() && axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						}
					}
				}

			}
		});
		cb_axt_equiv.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				ListModel model = axiomtypelist.getModel();
				Set<AxiomType> types = new HashSet<AxiomType>();
				types.add(AxiomType.EQUIVALENT_CLASSES);
				types.add(AxiomType.EQUIVALENT_DATA_PROPERTIES);
				types.add(AxiomType.EQUIVALENT_OBJECT_PROPERTIES);
				for (int i = 0; i < model.getSize(); i++) {
					AxiomType at = (AxiomType) model.getElementAt(i);
					if (types.contains(at)) {
						if (cb.isSelected() && !axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						} else if (!cb.isSelected() && axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						}
					}
				}

			}
		});
		cb_axt_property_assert.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				ListModel model = axiomtypelist.getModel();
				Set<AxiomType> types = new HashSet<AxiomType>();
				//types.add(AxiomType.DATA_PROPERTY_ASSERTION);
				types.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
				for (int i = 0; i < model.getSize(); i++) {
					AxiomType at = (AxiomType) model.getElementAt(i);
					if (types.contains(at)) {
						if (cb.isSelected() && !axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						} else if (!cb.isSelected() && axiomtypelist.isSelectedIndex(i)) {
							axiomtypelist.setSelectedIndex(i);
						}
					}
				}

			}
		});
		cb_axt_csub.doClick();
		cb_axt_equiv.doClick();
		cb_axt_assert.doClick();
		cb_axt_property_assert.doClick();
	}

	private JLabel getMainLabel(String text) {
		JLabel l = new JLabel(text, SwingConstants.CENTER);
		l.setFont(l.getFont().deriveFont(Font.PLAIN, 20.0f));
		return l;
	}

	private JPanel getLabelledGridLayoutPanel(String label, JComponent c) {
		JPanel panel_entityfilter_plus_label = new JPanel(new BorderLayout());
		JLabel l = new JLabel(label);
		l.setFont(l.getFont().deriveFont(Font.PLAIN, 18.0f));
		l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel_entityfilter_plus_label.add(l, BorderLayout.NORTH);
		panel_entityfilter_plus_label.add(c, BorderLayout.CENTER);
		return panel_entityfilter_plus_label;
	}

	private void createOptionsViewButtons() {
		inferenceinspectoroptions = new JPanel(new GridLayout(1, 2));
		difftooloptions = new JPanel(new GridLayout(2, 1));
		
		bt_ii_history.setIcon(Icons.getIcon("history32.png"));
		bt_ii_history.setText("");
		bt_ii_history.setPreferredSize(new Dimension(40, 40));
		bt_ii_history.setMaximumSize(new Dimension(40, 40));
		
		bt_ii_history.setToolTipText("Select different historical snapshot");
		bt_ii_history.repaint();
		
		bt_ii_opts.setIcon(Icons.getIcon("options.png"));
		bt_ii_opts.setText("");
		bt_ii_history.setPreferredSize(new Dimension(40, 40));
		bt_ii_history.setMaximumSize(new Dimension(40, 40));
		bt_ii_opts.setToolTipText("Show Options");
		bt_ii_opts.repaint();

		bt_ii_history.addActionListener(new SplitPaneMouseListener());
		bt_ii_history.addMouseListener(new LoggingMouseListener(bt_ii_history, "bt_ii_history"));
		bt_ii_opts.addActionListener(new SplitPaneMouseListener());
		bt_ii_opts.addMouseListener(new LoggingMouseListener(bt_ii_opts, "bt_ii_opts"));
		// bt_diff_diff.addMouseListener(new SplitPaneMouseListener());
		// bt_diff_opts.addMouseListener(new SplitPaneMouseListener());

		inferenceinspectoroptions.add(bt_ii_history);
		inferenceinspectoroptions.add(bt_ii_opts);
		inferenceinspectoroptions.setVisible(false);

		//difftooloptions.add(bt_diff_diff);
		//difftooloptions.add(bt_diff_opts);
		difftooloptions.setVisible(false);
	}

	class SplitPaneMouseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			updateSplit();
		}

	}

	private void updateSplit() {
		ClickButtons cb = new ClickButtons();
		cb.execute();
	}

	private void prepareMainButtons() {

		ActionListener main_button_actionlistener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource().equals(bt_history)) {
					if (bt_history.isSelected()) {
						EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
								"bt_history", "NA", "wii");
						difftooloptions.setVisible(false);
						inferenceinspectoroptions.setVisible(true);
						bt_report.setSelected(false);
						bt_diff.setSelected(false);
						bt_history.setSelected(true);
						panel_center.removeAll();
						panel_center.add(panel_inferenceinspector);
						panel_inferenceinspector.validate();
					} else {
						difftooloptions.setVisible(false);
						inferenceinspectoroptions.setVisible(false);
						panel_center.removeAll();
						panel_center.add(panel_wii);
						panel_wii.validate();
					}
				} else if (e.getSource().equals(bt_diff)) {
					if (bt_diff.isSelected()) {
						difftooloptions.setVisible(true);
						inferenceinspectoroptions.setVisible(false);
						bt_report.setSelected(false);
						bt_diff.setSelected(true);
						bt_history.setSelected(false);
						panel_center.removeAll();
						panel_center.add(panel_difftool);
						panel_difftool.validate();
					} else {
						difftooloptions.setVisible(false);
						inferenceinspectoroptions.setVisible(false);
						panel_center.removeAll();
						panel_center.add(panel_wii);
						panel_wii.validate();
					}
				} else if (e.getSource().equals(bt_report)) {
					if (bt_report.isSelected()) {
						difftooloptions.setVisible(false);
						inferenceinspectoroptions.setVisible(false);
						bt_report.setSelected(true);
						bt_diff.setSelected(false);
						bt_history.setSelected(false);
						updateReport();
						panel_center.removeAll();
						panel_center.add(panel_report);
						panel_difftool.validate();
					} else {
						difftooloptions.setVisible(false);
						inferenceinspectoroptions.setVisible(false);
						panel_center.removeAll();
						panel_center.add(panel_wii);
						panel_wii.validate();
					}
				}
				panel_center.validate();
				panel_center.repaint();
				updateView();
			}
		};

		syncstatus.setPreferredSize(new Dimension(32, 32));
		syncstatus.setToolTipText("Reasoner out of sync");
		syncstatus.repaint();

		bt_history.setIcon(Icons.getIcon("history.png"));
		bt_history.setPreferredSize(new Dimension(32, 32));
		bt_history.setToolTipText("History - What changed?");
		bt_history.repaint();

		bt_report.setIcon(Icons.getIcon("report.png"));
		bt_report.setPreferredSize(new Dimension(32, 32));
		bt_report.setToolTipText("Create Inference Report");
		bt_report.repaint();

		bt_diff.setIcon(Icons.getIcon("diff.png"));
		bt_diff.setPreferredSize(new Dimension(32, 32));
		bt_diff.setToolTipText("Diff - Analyse impact of specific axioms");
		bt_diff.repaint();

		bt_history.addActionListener(main_button_actionlistener);
		bt_diff.addActionListener(main_button_actionlistener);
		bt_report.addActionListener(main_button_actionlistener);

		/*
		 * bt_toggle_axiom_hierarchy.setIcon(Icons.getIcon("inference.png"));
		 * bt_toggle_axiom_hierarchy.setPreferredSize(new Dimension(50, 50));
		 * bt_toggle_axiom_hierarchy.setToolTipText(
		 * "Change to hierarchy based view");
		 * bt_toggle_axiom_hierarchy.repaint();
		 * bt_toggle_axiom_hierarchy.addActionListener(new ActionListener() {
		 * public void actionPerformed(ActionEvent e) { switchViewStyle(); } });
		 */
	}

	/*
	 * protected void switchViewStyle() { if
	 * (bt_toggle_axiom_hierarchy.getToolTipText().contains("hierarchy")) {
	 * bt_toggle_axiom_hierarchy.setIcon(Icons.getIcon("subclass.png"));
	 * bt_toggle_axiom_hierarchy.setPreferredSize(new Dimension(50, 50));
	 * bt_toggle_axiom_hierarchy.setToolTipText("Change to axiom based view");
	 * bt_toggle_axiom_hierarchy.repaint(); view_added.remove(axiompanel_added);
	 * view_removed.remove(axiompanel_removed);
	 * view_added.add(view_hierachies_added, BorderLayout.CENTER);
	 * view_removed.add(view_hierachies_removed, BorderLayout.CENTER);
	 * 
	 * } else {
	 * bt_toggle_axiom_hierarchy.setIcon(Icons.getIcon("inference.png"));
	 * bt_toggle_axiom_hierarchy.setPreferredSize(new Dimension(50, 50));
	 * bt_toggle_axiom_hierarchy.setToolTipText("Change to hierarchy based view"
	 * ); bt_toggle_axiom_hierarchy.repaint();
	 * view_added.remove(view_hierachies_added);
	 * view_removed.remove(view_hierachies_removed);
	 * view_added.add(axiompanel_added, BorderLayout.CENTER);
	 * view_removed.add(axiompanel_removed, BorderLayout.CENTER); }
	 * view_added.repaint(); view_removed.repaint(); view_added.validate();
	 * view_removed.validate(); // invalidate(); validate(); }
	 */

	protected JComponent createHistoryPane() {
		panel_history_axioms = new JPanel(new BorderLayout());
		JPanel p = new JPanel(new GridLayout(1, 1));
		JScrollPane scroll = new JScrollPane(table_history_select);
		panel_history_axioms.add(new JLabel("Snapshot"), BorderLayout.NORTH);
		p.add(scroll);
		panel_history_axioms.add(p, BorderLayout.CENTER);
		// add(historyaxiompanel, BorderLayout.CENTER);
		p.setAutoscrolls(true);
		return panel_history_axioms;
	}

	public void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 50; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private WorkspaceFrame getFrame() {
		return ProtegeManager.getInstance().getEditorKitManager().getWorkspaceManager().getFrame(getWorkspace());
	}

	private void createInferenceInspectorInfo() {
		JEditorPane txtArea = new JEditorPane();
		txtArea.setContentType("text/html");
		txtArea.setAutoscrolls(true);
		txtArea.setPreferredSize(new Dimension(400, 300));
		txtArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JScrollPane txtAreaScroll = new JScrollPane();
		txtAreaScroll.setViewportView(txtArea);
		txtAreaScroll.setAutoscrolls(true);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h1>Whatif Inference Inspector</h1>" + "<br />");
		sb.append("<b>Info: </b>" + getO().getOntologyID().getOntologyIRI().toString() + "<br />");
		sb.append("<b>Date: </b>" + getDate() + "<br />");
		sb.append("<b>Features: </b>" + getO().getLogicalAxiomCount() + "<br />");
		sb.append("</html>");
		txtArea.setText(sb.toString());
		panel_wii.removeAll();
		panel_wii.add(txtArea);
		panel_wii.validate();
	}

	private void updateReport() {
		JEditorPane txtArea = new JEditorPane();
		txtArea.setContentType("text/html");
		txtArea.setAutoscrolls(true);
		txtArea.setPreferredSize(new Dimension(400, 300));
		txtArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		JScrollPane txtAreaScroll = new JScrollPane();
		txtAreaScroll.setViewportView(txtArea);
		txtAreaScroll.setAutoscrolls(true);
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<h1>Inference Report</h1>" + "<br />");
		sb.append("<b>Ontology: </b>" + getO().getOntologyID().getOntologyIRI().toString() + "<br />");
		sb.append("<b>Date: </b>" + getDate() + "<br />");
		sb.append("<b>Number of asserted logical axioms: </b>" + getO().getLogicalAxiomCount() + "<br />");
		sb.append("</html>");
		txtArea.setText(sb.toString());
		panel_report.removeAll();
		panel_report.add(txtArea);
		panel_report.validate();
	}

	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String strDate = sdf.format(now);
		return strDate;
	}

	private void updateTautologies(Set<WhatifInferenceConsequence> axioms) {
		tautologytracker.checkAxioms(axioms);
	}

	/*
	 * private void updateDirectinferences(Set<OWLAxiom> axioms) {
	 * WhatifUtils.p("updateTautologies(X) " + axioms.size()); for
	 * (OWLAxiom ax : axioms) { if (directinferences.contains(ax) ||
	 * indirectinferences.contains(ax)) {
	 * 
	 * } else { if (isDirect(ax)) { directinferences.add(ax); } else {
	 * indirectinferences.add(ax); } } } }
	 * 
	 * 
	 * private boolean isDirect(OWLAxiom ax) { // TODO Auto-generated method
	 * stub return false; }
	 */

	/*
	 * protected JComponent makeTextPanel(String text) { JPanel panel = new
	 * JPanel(false); panel.setLayout(new GridLayout(1, 1)); return panel; }
	 */
	private OWLOntology getO() {
		return getOWLModelManager().getActiveOntology();
	}

	private OWLReasoner getR() {
		return getOWLModelManager().getOWLReasonerManager().getCurrentReasoner();
	}

	public OWLObject updateView() {
		OWLEntity entity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
		Set<OWLEntity> selectedinselector = getSelectedObjects();

		Set<WhatifConsequence> inferred = filterSelectedTypes(this.consequences_inferred);
		filterTautologies(inferred);

		if (cb_selected.isSelected()) {
			filterAxiomsByEntity(inferred, Collections.singleton(entity));
		}
		else if (!selectedinselector.isEmpty()) {
			filterAxiomsByEntity(inferred, selectedinselector);
		}
		addCriticalInferences(inferred);

		Set<WhatifConsequence> removed_inferred = filterSelectedTypes(this.consequences_removed_inferred);
		filterTautologies(removed_inferred);

		if (cb_selected.isSelected()) {
			filterAxiomsByEntity(removed_inferred, Collections.singleton(entity));
		} else if (!selectedinselector.isEmpty()) {
			filterAxiomsByEntity(removed_inferred, selectedinselector);
		}

		Set<WhatifConsequence> asserted = new HashSet<WhatifConsequence>(this.consequences_asserted);

		if (cb_selected.isSelected()) {
			filterAxiomsByEntity(asserted, Collections.singleton(entity));
		} else if (!selectedinselector.isEmpty()) {
			filterAxiomsByEntity(asserted, selectedinselector);
		}

		Map<WhatifConsequence, AxiomPriority> infs = new HashMap<WhatifConsequence, AxiomPriority>();
		for (WhatifConsequence con : inferred) {
			AxiomPriority prio = getPriority(con);
			if (prio != AxiomPriority.REMOVE) {
				infs.put(con, prio);
			}
		}
		axioms_added.setAxioms(infs);

		WhatifUtils.p("Adding Consequences..");
		infs = new HashMap<WhatifConsequence, AxiomPriority>();
		for (WhatifConsequence con : asserted) {
			// SysteEntailm.out.println(con);
			AxiomPriority prio = getPriority(con);
			if (prio != AxiomPriority.REMOVE) {
				infs.put(con, prio);
			}
		}
		axioms_added_assertions.setAxioms(infs);
		axioms_added_assertions.sortColumn("Type");

		infs = new HashMap<WhatifConsequence, AxiomPriority>();
		for (WhatifConsequence con : removed_inferred) {
			AxiomPriority prio = getPriority(con);
			if (prio != AxiomPriority.REMOVE) {
				infs.put(con, prio);
			}
		}
		axioms_removed.setAxioms(infs);
		updateSplit();
		return (entity);
	}

	private Set<OWLEntity> getSelectedObjects() {
		Set<OWLEntity> selectedinselector = entityselect.getSelectedObjects();
		return selectedinselector;
	}

	private void makeSureLost(Set<WhatifConsequence> axioms) {
		WhatifUtils.p("Warning: makeSureLost(). Overly costly function");
		Set<WhatifConsequence> rem = new HashSet<WhatifConsequence>();
		for (WhatifConsequence con : axioms) {
			if (con instanceof WhatifInferenceConsequence) {
				WhatifInferenceConsequence ax = (WhatifInferenceConsequence) con;
				if (getR().isEntailed(ax.getOWLAxiom())) {
					rem.add(con);
				}
			}
		}
		axioms.removeAll(rem);
	}

	private void addCriticalInferences(Set<WhatifConsequence> consequences) {
		if (cb_keepimportant.isSelected()) {
			ReasonerRunDiff recent = getLatestSnapshot();
			if (recent != null) {
				for (WhatifConsequence con : recent.getInferences()) {
					if (getPriority(con) == AxiomPriority.P1) {
						consequences.add(con);
					}

				}
			}
		}
	}

	private void renderSyncStatus(boolean synced) {
		if (!synced) {
			syncstatus.setIcon(Icons.getIcon("warning.png"));
			addedlabel.setText("New Inferences (out of sync)");
			addedlabel.setIcon(Icons.getIcon("warning_small.png"));
			addedlabel.setToolTipText("Please run the reasoner to synconise the view.");

			removedlabel.setText("Lost Inferences (out of sync)");
			removedlabel.setIcon(Icons.getIcon("warning_small.png"));
			removedlabel.setToolTipText("Please run the reasoner to synconise the view.");

		} else {
			syncstatus.setIcon(null);
			addedlabel.setText("New Inferences");
			addedlabel.setIcon(Icons.getIcon("added.png"));
			addedlabel.setToolTipText(
					"New inferences compared to the selected previous state. Inferences are logical entailments that are not explicitly stated in the ontology.");

			removedlabel.setText("Lost inferences");
			removedlabel.setIcon(Icons.getIcon("bin.png"));
			removedlabel.setToolTipText(
					"Lost inferences compared to the selected previous state. Inferences are logical entailments that are not explicitly stated in the ontology.");
		}
	}

	/*
	 * private Set<OWLAxiom> filterTypes(Set<OWLAxiom> ax_in) { Set<OWLAxiom>
	 * axioms = new HashSet<OWLAxiom>(); Set<AxiomType> legaltypes = new
	 * HashSet<AxiomType>(); if (cb_axt_assert.isSelected()) {
	 * legaltypes.add(AxiomType.CLASS_ASSERTION); } if
	 * (cb_axt_csub.isSelected()) { legaltypes.add(AxiomType.SUBCLASS_OF);
	 * legaltypes.add(AxiomType.SUB_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.SUB_DATA_PROPERTY); } if
	 * (cb_axt_property_assert.isSelected()) {
	 * legaltypes.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
	 * legaltypes.add(AxiomType.DATA_PROPERTY_ASSERTION); } if
	 * (cb_axt_character.isSelected()) {
	 * legaltypes.add(AxiomType.FUNCTIONAL_DATA_PROPERTY);
	 * legaltypes.add(AxiomType.FUNCTIONAL_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.REFLEXIVE_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.TRANSITIVE_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.SYMMETRIC_OBJECT_PROPERTY);
	 * legaltypes.add(AxiomType.ASYMMETRIC_OBJECT_PROPERTY); } if
	 * (cb_axt_disj.isSelected()) { legaltypes.add(AxiomType.DISJOINT_CLASSES);
	 * legaltypes.add(AxiomType.DISJOINT_OBJECT_PROPERTIES);
	 * legaltypes.add(AxiomType.DISJOINT_DATA_PROPERTIES); } if
	 * (cb_axt_equiv.isSelected()) {
	 * legaltypes.add(AxiomType.EQUIVALENT_CLASSES);
	 * legaltypes.add(AxiomType.EQUIVALENT_DATA_PROPERTIES);
	 * legaltypes.add(AxiomType.EQUIVALENT_OBJECT_PROPERTIES); } for (OWLAxiom
	 * ax : ax_in) { if (legaltypes.contains(ax.getAxiomType())) {
	 * axioms.add(ax); } } return axioms; }
	 * 
	 * private Set<OWLAxiom> filterSelectedTypesAx(Set<OWLAxiom> filtered) {
	 * Set<AxiomType> types = filterAxiomTypesFromGUI(); Set<OWLAxiom> add = new
	 * HashSet<OWLAxiom>(); for (OWLAxiom ax : filtered) { if
	 * (types.contains(ax.getAxiomType())) { add.add(ax); } } return add; }
	 */

	private Set<WhatifConsequence> filterSelectedTypes(Set<WhatifConsequence> filtered) {
		Set<AxiomType> types = filterAxiomTypesFromGUI();
		Set<WhatifConsequence> add = new HashSet<WhatifConsequence>();
		for (WhatifConsequence con : filtered) {
			if (con instanceof WhatifInferenceConsequence) {
				WhatifInferenceConsequence ax = (WhatifInferenceConsequence) con;
				if (types.contains(ax.getOWLAxiomType())) {
					add.add(ax);
				}
			}
		}
		return add;
	}

	private void filterTautologies(Set<? extends WhatifConsequence> filtered) {
		Set<WhatifInferenceConsequence> rem = new HashSet<WhatifInferenceConsequence>();

		for (WhatifConsequence con : filtered) {
			if (con instanceof WhatifInferenceConsequence) {
				WhatifInferenceConsequence ax = (WhatifInferenceConsequence) con;
				if (!cb_tautology.isSelected()) {
					if (tautologytracker.isTautology(ax)) {
						rem.add(ax);
					}
				}
			}
		}
		filtered.removeAll(rem);
	}
	/*
	 * private void filterDirect(Set<OWLAxiom> filtered) { if
	 * (!cb_direct.isSelected()) { filtered.removeAll(directinferences); } if
	 * (!cb_indirect.isSelected()) { filtered.removeAll(indirectinferences); } }
	 */

	private void createReasonerSnapshot(long init_run_id, Set<AxiomType> types, Set<OWLAxiom> asserted,
			Set<WhatifInferenceConsequence> inferred) {

		updateTautologies(inferred);
		//filterTautologies(inferred);
		filterTrash(inferred);
		ReasonerRunDiff newstate = new ReasonerRunDiff(init_run_id, asserted, inferred, types);

		newstate.setReasoningTime(startreasonertime, endreasonertime);

		JRadioButton rb = new JRadioButton();
		rb.setEnabled(false);
		rb.setSelected(false);

		addUpdateHistoryPanelActionListenerToRadioButton(rb);

		if (lastButton != null) {
			lastButton.setSelected(true);
			lastButton.setEnabled(true);
		}

		buttong_group_table_history_select.add(rb);
		lastButton = rb;
		table_history_select.setHistoryRun(newstate, rb);
		if (history_diffs.size() > 0) {
			selected_history_point = history_diffs.get(history_diffs.size() - 1);

			int index = history_diffs.size() - 1;
			Set<OWLAxiom> prev = index > 0 ? history_diffs.get(index).getAssertions() : new HashSet<OWLAxiom>();
			Set<OWLAxiom> currentcompare = newstate.getAssertions();
			Set<OWLAxiom> added = new HashSet<OWLAxiom>();
			added.addAll(currentcompare);
			added.removeAll(prev);

			Set<WhatifConsequence> addition_consequences = getAdditionConsequences(added, currentcompare);
			WhatifUtils.p("addiditionCON: " + addition_consequences.size());
			newstate.addAddedAssertionConsequences(addition_consequences);
		}
		history_diffs.add(newstate);
	}

	private void filterTrash(Set<WhatifInferenceConsequence> filtered) {
		Set<WhatifInferenceConsequence> rem = new HashSet<WhatifInferenceConsequence>();

		for (WhatifConsequence con : filtered) {
			if (con instanceof WhatifInferenceConsequence) {
				WhatifInferenceConsequence ax = (WhatifInferenceConsequence) con;
				if (con.getOWLAxiom() instanceof OWLNaryClassAxiom) {
					if (((OWLNaryClassAxiom)con.getOWLAxiom()).getClassExpressions().size()<=1) {
						rem.add(ax);
					}
				}
			}
		}
		filtered.removeAll(rem);
		
	}

	private Set<WhatifConsequence> getAdditionConsequences(Set<OWLAxiom> axioms, Set<OWLAxiom> assertions) {
		WhatifUtils.p("getAdditionConsequences()");
		/*
		 * for (OWLAxiom ax : axioms) { WhatifUtils.p(ax); }
		 */
		Set<WhatifConsequence> cons = new HashSet<WhatifConsequence>();
		for (OWLAxiom ax : axioms) {
			Set<AxiomPattern> matches = getMatchingAxiomPatterns(ax, AxiomPatternFactory.getChangesetPatterns(),
					assertions);
			for (AxiomPattern p : matches) {
				cons.add(new WhatifAddedAssertionConsequence(ax, new HashSet<AxiomPattern>(Collections.singleton(p))));
			}
		}
		return cons;
	}

	private void addUpdateHistoryPanelActionListenerToRadioButton(JRadioButton rb) {
		rb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
						"rb_changehistory", "NA", "wii");
				DefaultTableModel dtm = (DefaultTableModel) table_history_select.getModel();
				int nRow = dtm.getRowCount();
				for (int i = 0; i < nRow; i++) {
					if (dtm.getValueAt(i, 2).equals(e.getSource())) {
						selected_history_point = (ReasonerRunDiff) dtm.getValueAt(i, 1);
					}
				}
				updateHistoryPanel();
				updateDiff();
				updateView();
			}
		});
	}

	private Set<WhatifInferenceConsequence> getWhatifAxioms(Set<OWLAxiom> axioms, Set<OWLAxiom> asserted) {
		Set<WhatifInferenceConsequence> wiaxioms = new HashSet<WhatifInferenceConsequence>();
		for (OWLAxiom ax : axioms) {
			if (ax instanceof OWLEquivalentClassesAxiom) {
				OWLEquivalentClassesAxiom axe = (OWLEquivalentClassesAxiom) ax;
				OWLDataFactory df = getO().getOWLOntologyManager().getOWLDataFactory();
				Set<OWLAxiom> neweqaxioms = new HashSet<OWLAxiom>();
				for (OWLClassExpression a : axe.getClassExpressions()) {
					for (OWLClassExpression b : axe.getClassExpressions()) {
						OWLEquivalentClassesAxiom axenew = df.getOWLEquivalentClassesAxiom(a, b);
						neweqaxioms.add(axenew);
					}
				}
				for (OWLAxiom axenew : neweqaxioms) {
					wiaxioms.add(new WhatifInferenceConsequence(axenew,
							getMatchingAxiomPatterns(axenew, AxiomPatternFactory.getAllPatterns(), asserted)));
				}
			} else {
				wiaxioms.add(new WhatifInferenceConsequence(ax,
						getMatchingAxiomPatterns(ax, AxiomPatternFactory.getAllPatterns(), asserted)));
			}
		}
		return wiaxioms;
	}

	long tautology = 0;
	long profile = 0;
	long total = 0;

	private Set<AxiomPattern> getMatchingAxiomPatterns(OWLAxiom axenew, Set<AxiomPattern> patterns,
			Set<OWLAxiom> asserted) {
		Set<AxiomPattern> matches = new HashSet<AxiomPattern>();

		for (AxiomPattern p : patterns) {
			long start = System.currentTimeMillis();
			AxiomPriority prio = table_axiom_pattern_priority.getPriority(p);
			
			if (p instanceof TautologyAxiomPattern) {
				long st = System.currentTimeMillis();
				if (prio == AxiomPriority.REMOVE) {
					 continue;
				}
				try {
					if (p.matchesPattern(axenew,
							getOWLModelManager().getOWLReasonerManager().getCurrentReasonerFactory()
									.getReasonerFactory()
									.createReasoner(OWLManager.createOWLOntologyManager().createOntology()))) {
						matches.add(p);
					}
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
				tautology += (System.currentTimeMillis() - st);
			} else if (p instanceof AssertedAxiomPattern) {
				// WhatifUtils.p("Checking asserted: " + axenew);
				
				if (p.matchesPattern(axenew, asserted)) {
					matches.add(p);
				}
			} else if (p instanceof AddedAxiomRedundantPattern) {
				// Saving some time to avoid generating Explanations. If
				// tautology, this pattern matches by default.
				if (prio == AxiomPriority.REMOVE) {
					 continue;
				}
				if (!tautologytracker.isTautology(axenew)) {
					if (p.matchesPattern(axenew, getR())) {
						matches.add(p);
					}
				} else {
					matches.add(p);
				}
			} else if (p instanceof ProfileAxiomPattern) {
				long st = System.currentTimeMillis();
				if (prio == AxiomPriority.REMOVE) {
					 continue;
				}
				// WhatifUtils.p(p.getClass());
				if (p.matchesPattern(axenew,
						current_profile_reports.get((Class<? extends ProfileAxiomPattern>) p.getClass()))) {
					// WhatifUtils.p("OKIDOKI");
					matches.add(p);
				} else {
					// WhatifUtils.p("NOMATCH");
				}
				profile += (System.currentTimeMillis() - st);
			} else {
				if (p.matchesPattern(axenew, getR())) {
					matches.add(p);
				}
			}
			long end = System.currentTimeMillis();
			if (!pattern_timings.containsKey(p)) {
				pattern_timings.put(p, 0l);
			}
			pattern_timings.put(p, pattern_timings.get(p) + (end - start));
			total += end - start;
		}

		return matches;
	}

	private AxiomPriority getPriority(WhatifConsequence con) {
		AxiomPattern p = con.getHighestPriorityAxiomPattern();
		return table_axiom_pattern_priority.getPriority(p);
	}

	private void updateDiff() {
		WhatifUtils.p("updateDiff()");
		/*
		 * ReasonerUtilities.warnUserIfReasonerIsNotConfigured( this,
		 * getOWLModelManager() .getOWLReasonerManager());
		 */
		ReasonerRunDiff latest = getLatestSnapshot();

		Set<AxiomType> l = latest.getAxiomTypes();
		Set<AxiomType> d = selected_history_point.getAxiomTypes();

		Set<AxiomType> intersect = new HashSet<AxiomType>();
		intersect.addAll(l);
		intersect.retainAll(d);

		Set<AxiomType> different_axiomtypes = new HashSet<AxiomType>();
		different_axiomtypes.addAll(l);
		different_axiomtypes.addAll(d);
		different_axiomtypes.removeAll(intersect);

		if (history_diffs.indexOf(selected_history_point) > 0) {
			if (!different_axiomtypes.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append(
						"The snapshot you have selected has a different set of precomputed inferences. The following kinds of inferences are removed from the analysis: "
								+ "\n" + "\n");
				for (AxiomType type : different_axiomtypes) {
					sb.append(type.getName() + " " + "\n");
				}
				JOptionPane.showConfirmDialog(this, sb.toString());
			}
		}

		consequences_inferred.clear();
		consequences_removed_inferred.clear();

		/*
		 * for (WhatifAxiom ax : latest.getInferences()) {
		 * WhatifUtils.p(ax.hashCode() + "|" + ax.getOWLAxiom() + "," +
		 * ax.isKnownTautology() + "," + ax.isTautology() + "," +
		 * ax.getOWLAxiomType()); boolean equal = false; for (WhatifAxiom axin :
		 * diff.getInferences()) { if (ax.equals(axin)) { equal = true; break; }
		 * 
		 * } if (!equal) { WhatifUtils.p("Done"); inferred.add(ax); } } for
		 * (WhatifAxiom ax : diff.getInferences()) {
		 * WhatifUtils.p(ax.hashCode() + "|" + ax.getOWLAxiom() + "," +
		 * ax.isKnownTautology() + "," + ax.isTautology() + "," +
		 * ax.getOWLAxiomType()); boolean equal = false; for (WhatifAxiom axin :
		 * latest.getInferences()) { WhatifUtils.p(axin.hashCode() + "|" +
		 * axin.getOWLAxiom() + "," + axin.isKnownTautology() + "," +
		 * axin.isTautology() + "," + axin.getOWLAxiomType()); if
		 * (ax.equals(axin)) { equal = true; break; } } if (!equal) {
		 * WhatifUtils.p("Done"); removed_inferred.add(ax); } }
		 */
		/*
		 * Set<WhatifAxiom> a = new HashSet<WhatifAxiom>();
		 * a.addAll(diff.getInferences()); a.removeAll(latest.getInferences());
		 * WhatifUtils.p("A: " + a);
		 * 
		 * Set<WhatifAxiom> b = new HashSet<WhatifAxiom>();
		 * b.addAll(latest.getInferences()); b.removeAll(diff.getInferences());
		 * WhatifUtils.p("B: " + b);
		 * 
		 * Set<WhatifAxiom> c = new HashSet<WhatifAxiom>();
		 * c.addAll(diff.getInferences()); c.retainAll(latest.getInferences());
		 * WhatifUtils.p("C: " + c);
		 * 
		 * Set<WhatifAxiom> d = new HashSet<WhatifAxiom>();
		 * d.addAll(latest.getInferences()); d.retainAll(diff.getInferences());
		 * WhatifUtils.p("D: " + d);
		 */

		this.consequences_inferred.addAll(latest.getInferences());
		this.consequences_inferred.removeAll(selected_history_point.getInferences());
		removeAxiomsOfType(this.consequences_inferred, different_axiomtypes);

		this.consequences_asserted.addAll(latest.getAddedAssertionConsequences());
		this.consequences_asserted.removeAll(selected_history_point.getAddedAssertionConsequences());
		// removeAxiomsOfType(this.asserted, different_axiomtypes);

		this.consequences_removed_inferred.addAll(selected_history_point.getInferences());
		this.consequences_removed_inferred.removeAll(latest.getInferences());
		removeAxiomsOfType(this.consequences_removed_inferred, different_axiomtypes);
		if (getR().isConsistent()) {
			makeSureLost(this.consequences_removed_inferred);
		}

		// WhatifUtils.p("REM: " + a);
		// WhatifUtils.p("LATEST INDEX: " + history_diffs.indexOf(latest));
		// WhatifUtils.p("DIFF INDEX: " +
		// history_diffs.indexOf(selected_history_point));
		// WhatifUtils.p("DIFF: " + selected_history_point.getInferences());
		// WhatifUtils.p("LATEST: " + latest.getInferences());
		/*
		 * for (WhatifAxiom ax : removed_inferred) {
		 * WhatifUtils.p(ax.hashCode() + "|" + ax.getOWLAxiom() + "," +
		 * ax.getOWLAxiomType()); for (WhatifAxiom axin : diff.getInferences())
		 * { WhatifUtils.p(axin.hashCode() + "|" + axin.getOWLAxiom() + ","
		 * + axin.getOWLAxiomType()); WhatifUtils.p("EQ: " +
		 * ax.equals(axin)); } }
		 */
		// updateTautologies(asserted);

		// WhatifUtils.p("Y3: " + inferred.size());
		// WhatifUtils.p("Y4: " + removed_inferred.size());
	}

	private void removeAxiomsOfType(Set<WhatifConsequence> consequences, Set<AxiomType> types) {
		Set<WhatifConsequence> rem = new HashSet<WhatifConsequence>();
		for (WhatifConsequence con : consequences) {
			if (con instanceof WhatifInferenceConsequence) {
				WhatifInferenceConsequence ax = (WhatifInferenceConsequence) con;
				if (types.contains(ax.getOWLAxiomType())) {
					rem.add(con);
				}
			}
		}
		consequences.removeAll(rem);
	}

	private Set<AxiomType> filterAxiomTypesFromGUI() {
		Set<AxiomType> types = new HashSet<AxiomType>();

		ListModel model = axiomtypelist.getModel();

		for (int i : axiomtypelist.getSelectedIndices()) {
			AxiomType at = (AxiomType) model.getElementAt(i);
			types.add(at);
		}
		/*
		 * if (cb_axt_csub.isSelected()) { types.add(AxiomType.SUBCLASS_OF);
		 * types.add(AxiomType.SUB_OBJECT_PROPERTY);
		 * types.add(AxiomType.SUB_DATA_PROPERTY);
		 * 
		 * } if (cb_axt_assert.isSelected()) {
		 * types.add(AxiomType.CLASS_ASSERTION); } if
		 * (cb_axt_equiv.isSelected()) {
		 * types.add(AxiomType.EQUIVALENT_CLASSES);
		 * types.add(AxiomType.EQUIVALENT_DATA_PROPERTIES);
		 * types.add(AxiomType.EQUIVALENT_OBJECT_PROPERTIES);
		 * 
		 * } if (cb_axt_disj.isSelected()) {
		 * types.add(AxiomType.DISJOINT_CLASSES);
		 * types.add(AxiomType.DISJOINT_OBJECT_PROPERTIES);
		 * types.add(AxiomType.DISJOINT_DATA_PROPERTIES);
		 * 
		 * } if (cb_axt_property_assert.isSelected()) {
		 * types.add(AxiomType.DATA_PROPERTY_ASSERTION);
		 * types.add(AxiomType.OBJECT_PROPERTY_ASSERTION);
		 * 
		 * } if (cb_axt_character.isSelected()) {
		 * types.add(AxiomType.FUNCTIONAL_DATA_PROPERTY);
		 * types.add(AxiomType.FUNCTIONAL_OBJECT_PROPERTY);
		 * types.add(AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY);
		 * types.add(AxiomType.REFLEXIVE_OBJECT_PROPERTY);
		 * types.add(AxiomType.IRREFLEXIVE_OBJECT_PROPERTY);
		 * types.add(AxiomType.TRANSITIVE_OBJECT_PROPERTY);
		 * types.add(AxiomType.SYMMETRIC_OBJECT_PROPERTY);
		 * types.add(AxiomType.ASYMMETRIC_OBJECT_PROPERTY);
		 * 
		 * }
		 */
		return types;
	}

	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			// Initialize progress property.
			setProgress(0);
			currentProcess = "Computing Profile Violations..";
			updateExpressivity();
			setProgress(20);
			currentProcess = "Updating inferences..";
			updateInferrences();
			WhatifUtils.p("TIMING: ");
			for (AxiomPattern p : pattern_timings.keySet()) {
				WhatifUtils.p(p.getClass() + ": " + pattern_timings.get(p));
			}

			currentProcess = "Rendering..";
			setProgress(90);
			renderSyncStatus(true);
			updateHistoryPanel();
			updateDiff();
			//setProgress(95);
			//updateView();
			setProgress(100);
			return null;
		}

		private void updateExpressivity() {
			OWLProfile p = new OWL2DLProfile();
			OWLProfileReport r1 = p.checkOntology(getO());
			current_profile_reports.put(ViolatesOWLDLAxiomPattern.class, r1);
			setProgress(5);

			p = new OWL2ELProfile();
			OWLProfileReport r2 = p.checkOntology(getO());
			current_profile_reports.put(ViolatesOWLELAxiomPattern.class, r2);

			setProgress(10);
			p = new OWL2RLProfile();
			OWLProfileReport r3 = p.checkOntology(getO());
			current_profile_reports.put(ViolatesOWLRLAxiomPattern.class, r3);

			setProgress(15);
			p = new OWL2QLProfile();
			OWLProfileReport r4 = p.checkOntology(getO());
			current_profile_reports.put(ViolatesOWLQLAxiomPattern.class, r4);
		}

		private void updateInferrences() {
			WhatifUtils.p("updateInferrences()");
			long init_run_id = System.currentTimeMillis();
			Set<AxiomType> types = filterAxiomTypesFromGUI();
			if (!getR().isConsistent()) {
				Set<OWLAxiom> asserted = new HashSet<OWLAxiom>();
				OWLAxiom ax = getOWLDataFactory().getOWLSubClassOfAxiom(getOWLDataFactory().getOWLThing(),
						getOWLDataFactory().getOWLNothing());
				Set<AxiomPattern> patterns = new HashSet<AxiomPattern>();
				patterns.add(AxiomPatternFactory.getInconsistentOntologyAxiomPattern());
				WhatifInferenceConsequence con = new WhatifInferenceConsequence(ax, patterns);
				Set<WhatifInferenceConsequence> inferred = new HashSet<WhatifInferenceConsequence>();
				inferred.add(con);
				WhatifUtils.p("ASS: " + asserted.size());
				WhatifUtils.p("INF: " + inferred.size());
				setProgress(25);
				createReasonerSnapshot(init_run_id, types, asserted, inferred);
			} else {
				Set<OWLAxiom> asserted = (OWLOntologyAxiomSelector.getLogicalAxiomsInClosure(getO()));

				Set<OWLAxiom> inferences = EntailmentGenerator.getInferredAxioms(getR(), getO(), types, false);
				setProgress(50);
				//inferences.addAll(asserted);
				Set<WhatifInferenceConsequence> inferred = getWhatifAxioms(inferences, asserted);
				setProgress(80);
				WhatifUtils.p("ASS: " + asserted.size());
				WhatifUtils.p("INF: " + inferred.size());
				createReasonerSnapshot(init_run_id, types, asserted, inferred);
			}

		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			setCursor(null); // turn off the wait cursor
			taskOutput.setText("Done!");
		}

		String currentProcess = "Progress";

		public String getCurrentProcess() {
			return currentProcess;
		}
	}

	class ClickButtons extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			boolean hist = bt_ii_history.isSelected();
			boolean opts = bt_ii_opts.isSelected();

			if (hist) {
				if (opts) {
					split_view_options.getTopComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.5d);

					split_view_options.getBottomComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.5d);

					split_history_options.getLeftComponent().setMinimumSize(new Dimension());
					split_history_options.setDividerLocation(0.5d);

					split_history_options.getRightComponent().setMinimumSize(new Dimension());
					split_history_options.setDividerLocation(0.5d);

				} else {
					split_view_options.getTopComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.5d);

					split_view_options.getBottomComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.5d);

					split_history_options.getLeftComponent().setMinimumSize(new Dimension());
					split_history_options.setDividerLocation(1.0d);

					split_history_options.getRightComponent().setMinimumSize(new Dimension());
					split_history_options.setDividerLocation(0.0d);
				}
			} else {
				if (opts) {
					split_view_options.getTopComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.5d);

					split_view_options.getBottomComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.5d);

					split_history_options.getLeftComponent().setMinimumSize(new Dimension());
					split_history_options.setDividerLocation(0.0d);

					split_history_options.getRightComponent().setMinimumSize(new Dimension());
					split_history_options.setDividerLocation(1.0d);
				} else {
					split_view_options.getTopComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(0.0d);

					split_view_options.getBottomComponent().setMinimumSize(new Dimension());
					split_view_options.setDividerLocation(1.0d);
				}
			}
			return null;
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {

		}

		String currentProcess = "Progress";

		public String getCurrentProcess() {
			return currentProcess;
		}
	}

	private void handleModelManagerEvent(EventType type) {
		WhatifUtils.p("handleModelManagerEvent(): "+type);
		switch (type) {
		case ONTOLOGY_CLASSIFIED:
			// dlg.setVisible(true);
			if(!reallyclassified) {
				break;
			}
			endreasonertime = System.currentTimeMillis();
			EventLogging.saveEvent(endreasonertime, getOWLWorkspace().getSelectedTab().getId(), "ontology_classify_end",
					"NA", "wii");
			EventLogging.saveEvent(endreasonertime, getOWLWorkspace().getSelectedTab().getId(),
					"ontology_classified_start", "NA", "wii");

			// progressBar = new ProgressMonitor(EntailmentInspectorView.this,
			// "Running a Long Task", "", 0, 100);
			// progressBar.setMaximum(100);
			// progressBar.setStringPainted(true);
			// progressBar.setVisible(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			task = new Task();
			task.addPropertyChangeListener(this);
			task.execute();
			//updateView();
			// progressBar.setVisible(false);
			reallyclassified = false;
			EventLogging.saveEvent(System.currentTimeMillis(), getOWLWorkspace().getSelectedTab().getId(),
					"ontology_classified_end", "NA", "wii");
			break;
		case ACTIVE_ONTOLOGY_CHANGED:
			renderSyncStatus(false);
			updateView();
			break;
		case ONTOLOGY_LOADED:
			resetInferenceInspector();
			renderSyncStatus(false);
			updateView();
			break;
		case ABOUT_TO_CLASSIFY:
			startreasonertime = System.currentTimeMillis();
			reallyclassified = true;
			EventLogging.saveEvent(startreasonertime, getOWLWorkspace().getSelectedTab().getId(),
					"ontology_classify_start", "NA", "wii");
			break;
		case ENTITY_RENDERER_CHANGED:
			break;
		case ENTITY_RENDERING_CHANGED:
			break;
		case ONTOLOGY_CREATED:
			resetInferenceInspector();
			renderSyncStatus(false);
			updateView();
			break;
		case ONTOLOGY_RELOADED:
			resetInferenceInspector();
			renderSyncStatus(false);
			updateView();
			break;
		case ONTOLOGY_SAVED:
			break;
		case ONTOLOGY_VISIBILITY_CHANGED:
			break;
		case REASONER_CHANGED:
			renderSyncStatus(false);
			// updateView();
			break;
		default:
			break;
		}
	}

	private void sleep() {
		try {
			Thread.sleep(25);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ActionListener getThis() {
		return this;
	}

	@Override
	public void disposeView() {
		EventLogging.clear();
		getOWLModelManager().removeOntologyChangeListener(this);
		getOWLModelManager().removeListener(owlModelManagerListener);
		getOWLWorkspace().getOWLSelectionModel().removeListener(this);
		entityselect.dispose();
		// entityselect.removeAncestorListener();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// if (e.getSource().equals(button_bottom)) {
		//
		// extractModule(ModuleType.BOT);
		//
		// } else if (e.getSource().equals(button_top)) {
		// extractModule(ModuleType.TOP);
		// } else if (e.getSource().equals(button_star)) {
		// extractModule(ModuleType.STAR);
		// } else if (e.getSource().equals(button_diff)) {
		// analyseDiff();
		// } else if (e.getSource().equals(button_export)) {
		// exportModule();
		// } else if (e.getActionCommand().equals("select_effected_super")) {
		// WhatifUtils.p("actioNperf()");
		// selectAffectedSuperClasses();
		// }

	}
	
	/*

	@Override
	public AxiomListGroup getGrouping() {
		return (AxiomListGroup) combo_group.getSelectedItem();
	}

	@Override
	public boolean renderTautologies() {
		return cb_tautology.isSelected();
	}

	@Override
	public boolean renderInferredOnlyAxioms() {
		return true;
	}

	@Override
	public boolean renderAxiomsForSelectedEntityOnly() {
		return cb_selected.isSelected();
	}

	@Override
	public boolean goodAxiom(OWLAxiom ax) {
		boolean good = dialog_selectentity.getEntities().isEmpty();

		if (!good) {
			for (OWLEntity e : ax.getSignature()) {
				if (dialog_selectentity.getEntities().contains(e)) {
					good = true;
				}
			}
		}
		return good;
	}
	
	*/

	@Override
	public void selectionChanged() throws Exception {
		if (cb_selected.isSelected()) {
			WhatifUtils.p("SCC");
			updateView();
		}
	}

	private void filterAxiomsByEntity(Set<? extends WhatifConsequence> filtered, Set<OWLEntity> es) {
		Set<WhatifConsequence> rem = new HashSet<WhatifConsequence>();

		for (WhatifConsequence con : filtered) {
			boolean out = true;
			for (OWLEntity e : es) {
				if (con.getOWLAxiom().getSignature().contains(e)) {
					out = false;
					break;
				}
			}
			if (out) {
				rem.add(con);

			}
		}
		filtered.removeAll(rem);
	}

	@Override
	public void ontologiesChanged(List<? extends OWLOntologyChange> arg0) throws OWLException {
		WhatifUtils.p("ontologiesChanged(): "
				+ !getOWLModelManager().getOWLReasonerManager().getReasonerStatus().equals(ReasonerStatus.INITIALIZED));
		WhatifUtils.p("STATUS(): " + getOWLModelManager().getOWLReasonerManager().getReasonerStatus());
		
		renderSyncStatus(false);
		gen = genFac.createExplanationGenerator(getO());
		incongen = incongenFac.createExplanationGenerator(getO());
	}

	private void resetInferenceInspector() {
		history_diffs.clear();
		consequences_inferred.clear();
		consequences_removed_inferred.clear();
		selected_history_point = null;

		bt_ii_history.setSelected(true);
		bt_ii_opts.setSelected(true);
		bt_diff_diff.setSelected(true);
		bt_diff_opts.setSelected(true);

		table_history_select.clearSelection();
		table_history_select.clear();

		buttong_group_table_history_select = new ButtonGroup();
		lastButton = null;

		axioms_added.clear();
		axioms_removed.clear();
		axioms_added_assertions.clear();

		list_history_added_axioms.reset();
		list_history_removed_axioms.reset();

		tautologytracker.clear();

		all_atomic_subs = false;

		createReasonerSnapshot(System.currentTimeMillis(), getAllAxiomTypes(), new HashSet<OWLAxiom>(),
				new HashSet<WhatifInferenceConsequence>());
	}

	public static OWLObjectRenderer getRenderer() {
		return ren;
	}

	public static ExplanationGenerator<OWLAxiom> getExplanationGenerator() {
		return gen;
	}
	
	public static ExplanationGenerator<OWLAxiom> getInconsistentExplanationGenerator() {
		return incongen;
	}

	public static OWLEntity getPrimaryEntityOfAxiom(OWLAxiom ax) {
		if (ax instanceof OWLEquivalentClassesAxiom) {
			OWLEquivalentClassesAxiom axe = (OWLEquivalentClassesAxiom) ax;
			for (OWLClassExpression a : axe.getClassExpressions()) {
				if (a.isClassExpressionLiteral()) {
					return (OWLClass) a;
				}
			}

		} else if (ax instanceof OWLSubClassOfAxiom) {
			OWLSubClassOfAxiom axe = (OWLSubClassOfAxiom) ax;
			for (OWLClassExpression a : axe.getSubClass().getNestedClassExpressions()) {
				if (a.isClassExpressionLiteral()) {
					return (OWLClass) a;
				}
			}
		} else {
			// TODO
			for (OWLEntity a : ax.getClassesInSignature()) {
				return a;
			}
			for (OWLEntity a : ax.getObjectPropertiesInSignature()) {
				return a;
			}
			for (OWLEntity a : ax.getDataPropertiesInSignature()) {
				return a;
			}
			for (OWLEntity a : ax.getIndividualsInSignature()) {
				return a;
			}
		}
		return null;
	}

	@Override
	public void updateInspector() {
		if (initialised) {
			updateView();
		}
	}

	@Override
	public Collection<OWLProfileViolation> getViolations(Class<? extends ProfileAxiomPattern> cl) {
		return current_profile_reports.get(cl).getViolations();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			taskOutput.setText(task.getCurrentProcess());
			if(progress==100) {
				updateView();
			}
		}
	}

	public void updateEntitySelectTA() {
		Set<OWLEntity> selectedinselector = entityselect.getSelectedObjects();
		if (selectedinselector.isEmpty()) {
			entity_select_label.setText("None selected...");
		} else {
			StringBuilder sb = new StringBuilder();
			for (OWLEntity e : selectedinselector) {
				sb.append(e.getIRI().getFragment() + ", ");
			}
			entity_select_label.setText(sb.toString().substring(0, sb.toString().length() - 2));
		}
	}


}
