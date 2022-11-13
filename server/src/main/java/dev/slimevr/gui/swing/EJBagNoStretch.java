package dev.slimevr.gui.swing;

import java.awt.*;


public class EJBagNoStretch extends EJPanel {

	public EJBagNoStretch(boolean stretchVertical, boolean stretchHorizontal) {
		super(new EGridBagLayoutNoStretch(stretchVertical, stretchHorizontal));
	}

	private static class EGridBagLayoutNoStretch extends GridBagLayout {

		private final boolean stretchVertical;
		private final boolean stretchHorizontal;

		public EGridBagLayoutNoStretch(boolean stretchVertical, boolean stretchHorizontal) {
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
