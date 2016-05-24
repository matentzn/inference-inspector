package org.whatif.tools.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {

	public static Icon getIcon(String string) {
		System.out.println("######");
		URL cl = Icons.class.getClassLoader().getResource("/" + string);
		System.out.println("######" + cl);
		try {
			Icon loadedIcon = new ImageIcon(cl);
			return loadedIcon;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return org.protege.editor.core.ui.util.Icons.getIcon("warning.png");
	}
}
