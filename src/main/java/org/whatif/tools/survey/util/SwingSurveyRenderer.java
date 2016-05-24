package org.whatif.tools.survey.util;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.whatif.tools.util.WhatifUtils;

public class SwingSurveyRenderer implements SurveyRenderer {
	
	@Override
	public Object renderQuestion(ProtegeSurveyQuestion q) {
		JPanel p = new JPanel(new GridLayout(1,0));
		Set<String> options = q.getOptions();
		WhatifUtils.p("TEST"+q.getOptionsType());
		if(q.getOptionsType().equals("radio")) {
			CheckboxGroup group = new CheckboxGroup();
			for(String op:options) {
				p.add(new Checkbox(op, group, false));
			}
		} else if(q.getOptionsType().equals("check")) {
			for(String op:options) {
				p.add(new Checkbox(op, false));
			}
		} if(q.getOptionsType().equals("text")) {
			JTextArea textArea = new JTextArea("Write your answer", 5, 10);
			p.add(textArea);
		}
		
		return p;
	}

	@Override
	public Set<String> parseResults(Object object) {
		JPanel panel = (JPanel)object;
		Set<String> result = new HashSet<String>();
		for(Component c:panel.getComponents()) {
			if(c instanceof JTextArea) {
				JTextArea ta = (JTextArea)c;
				result.add(ta.getText());
			} else if(c instanceof Checkbox) {
				Checkbox cb = (Checkbox)c;
				if(cb.getState()) {
					result.add(cb.getLabel());
				}
			}
		}
		return result;
	}

}
