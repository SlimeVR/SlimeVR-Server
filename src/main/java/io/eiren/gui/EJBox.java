package io.eiren.gui;

import javax.swing.BoxLayout;

public class EJBox extends EJPanel {
	
	public EJBox(int layout) {
		super();
		setLayout(new BoxLayout(this, layout));
	}
}
