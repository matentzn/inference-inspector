package org.whatif.tools.survey.util;

import java.util.HashMap;
import java.util.Map;

public class QuestionMetadata {
	
	int clickcount = -1;
	int keystrokecount = -1;
	long start = -1;
	long end = -1;
	double scrollamount = -1;
	boolean skipped = false;
	
	public void setClickcount(int clickcount) {
		this.clickcount = clickcount;
	}
	public void setKeystrokecount(int keystrokecount) {
		this.keystrokecount = keystrokecount;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public void setScrollAmount(double scrolltime) {
		this.scrollamount = scrolltime;
	}

	public Map<String,String> getRecord() {
		Map<String,String> rec = new HashMap<String,String>();
		rec.put("start", start+"");
		rec.put("end", end+"");
		rec.put("keystrokecount", keystrokecount+"");
		rec.put("clickcount", clickcount+"");
		rec.put("skipped", skipped+"");
		rec.put("scrollamount", scrollamount+"");
		return rec;
	}
	public void setSkipped(boolean skip) {
		this.skipped=skip;
	}
}
