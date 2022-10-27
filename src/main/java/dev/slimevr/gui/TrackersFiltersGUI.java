package dev.slimevr.gui;

import com.jme3.math.FastMath;
import dev.slimevr.VRServer;
import dev.slimevr.config.FiltersConfig;
import dev.slimevr.filtering.TrackerFilters;
import dev.slimevr.gui.swing.EJBagNoStretch;
import io.eiren.util.StringUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;


public class TrackersFiltersGUI extends EJBagNoStretch {

	private final VRServer server;
	private final JLabel amountLabel;
	TrackerFilters filterType;
	float filterAmount;
	FiltersConfig filtersConfig;

	public TrackersFiltersGUI(VRServer server, VRServerGUI gui) {

		super(false, true);
		this.server = server;
		filtersConfig = server
			.getConfigManager()
			.getVrConfig()
			.getFilters();

		int row = 0;

		setAlignmentY(TOP_ALIGNMENT);
		add(Box.createVerticalStrut(10));
		filterType = server.getConfigManager().getVrConfig().getFilters().enumGetType();

		JComboBox<String> filterSelect;
		add(filterSelect = new JComboBox<>(), s(c(0, row, 2), 4, 1));

		for (TrackerFilters f : TrackerFilters.values()) {
			filterSelect.addItem(f.name());
		}
		if (filterType != null) {
			filterSelect.setSelectedItem(filterType.toString());
		}

		filterSelect.addActionListener((e) ->{
				filterType = TrackerFilters
					.getByConfigkey(filterSelect.getSelectedItem().toString());
				filtersConfig
					.enumSetType(filterType);
				filtersConfig
					.updateTrackersFilters();
				server.getConfigManager().saveConfig();
		});
		add(Box.createVerticalStrut(40));
		row++;

		filterAmount = FastMath
			.clamp(
				server.getConfigManager().getVrConfig().getFilters().getAmount(),
				0.1f,
				1
			);

		add(new JLabel("Amount"), c(0, row, 2));
		add(new AdjButton("+", false), c(1, row, 2));
		add(
			amountLabel = new JLabel(StringUtils.prettyNumber(filterAmount * 100f) + "%"),
			c(2, row, 2)
		);
		add(new AdjButton("-", true), c(3, row, 2));
	}

	void adjustAmount(boolean neg) {
		if (neg) {
			filterAmount = FastMath.clamp(filterAmount - 0.1f, 0.1f, 1);
		} else {
			filterAmount = FastMath.clamp(filterAmount + 0.1f, 0.1f, 1);
		}
		amountLabel.setText((StringUtils.prettyNumber(filterAmount * 100f)) + "%");

		filtersConfig
			.setAmount(filterAmount);
		filtersConfig
			.updateTrackersFilters();

		server.getConfigManager().saveConfig();
	}

	private class AdjButton extends JButton {

		public AdjButton(String text, boolean neg) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					adjustAmount(neg);
				}
			});
		}
	}
}
