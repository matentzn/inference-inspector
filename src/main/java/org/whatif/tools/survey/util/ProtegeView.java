package org.whatif.tools.survey.util;

import org.whatif.tools.util.WhatifUtils;

public class ProtegeView {

	final String view;
	int gridx = -1;
	int gridy = -1;
	double weightx = -1;
	double weighty = -1;
	
	public ProtegeView(String view) {
		this.view = view;
	}
	
	public int getGridx() {
		return gridx;
	}

	public void setGridx(int gridx) {
		this.gridx = gridx;
	}

	public int getGridy() {
		return gridy;
	}

	public void setGridy(int gridy) {
		this.gridy = gridy;
	}

	public double getWeightx() {
		return weightx;
	}

	public void setWeightx(double weightx) {
		this.weightx = weightx;
	}

	public double getWeighty() {
		return weighty;
	}

	public void setWeighty(double weighty) {
		this.weighty = weighty;
	}

	public String getId() {
		return view;
	}

	

}
