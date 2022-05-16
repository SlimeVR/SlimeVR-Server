package dev.slimevr.gui.swing;

import javax.swing.*;
import java.awt.*;


public class EJBoxNoStretch extends EJPanel {

	public EJBoxNoStretch(int layout, boolean stretchVertical, boolean stretchHorizontal) {
		super();
		setLayout(new BoxLayoutNoStretch(this, layout, stretchVertical, stretchHorizontal));
	}

	private static class BoxLayoutNoStretch extends BoxLayout {

		private final boolean stretchVertical;
		private final boolean stretchHorizontal;

		public BoxLayoutNoStretch(
			Container target,
			int axis,
			boolean stretchVertical,
			boolean stretchHorizontal
		) {
			super(target, axis);
			this.stretchVertical = stretchVertical;
			this.stretchHorizontal = stretchHorizontal;
		}

		@Override
		public Dimension maximumLayoutSize(Container target) {
			Dimension pref = preferredLayoutSize(target);
			if (stretchVertical)
				pref.height = Integer.MAX_VALUE;
			if (stretchHorizontal)
				pref.width = Integer.MAX_VALUE;
			return pref;
		}
	}
}
