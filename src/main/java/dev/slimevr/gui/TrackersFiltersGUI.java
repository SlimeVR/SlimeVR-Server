package dev.slimevr.gui;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import com.jme3.math.FastMath;

import dev.slimevr.VRServer;
import dev.slimevr.gui.swing.EJBagNoStretch;
import dev.slimevr.vr.trackers.IMUTracker;
import dev.slimevr.vr.trackers.ReferenceAdjustedTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerFilters;
import io.eiren.util.StringUtils;
import io.eiren.util.collections.FastList;

public class TrackersFiltersGUI extends EJBagNoStretch {
	
	private final VRServer server;
	String filterType;
	float filterAmount;
	int filterTicks;
	private List<Tracker> allTrackers = new FastList<>();
	private JLabel amountLabel, ticksLabel;
	
	public TrackersFiltersGUI(VRServer server, VRServerGUI gui) {
		
		super(false, true);
		this.server = server;
		
		int row = 0;
		
		setAlignmentY(TOP_ALIGNMENT);
		add(Box.createVerticalStrut(10));
		
		filterType = server.config.getString("filters.type");
		if(filterType == null) {
			filterType = "NONE";
		}
		
		JComboBox<String> filterSelect;
		add(filterSelect = new JComboBox<>(), s(c(0, row, 2), 4, 1));
		
		for(TrackerFilters f : TrackerFilters.values()) {
			filterSelect.addItem(f.name());
		}
		if(filterType != null) {
			filterSelect.setSelectedItem(filterType);
		} else {
			filterSelect.setSelectedItem(TrackerFilters.NONE);
		}
		
		filterSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterType = filterSelect.getSelectedItem().toString();
				server.config.setProperty("filters.type", filterType);
				server.saveConfig();
				updateTrackersFilters();
			}
		});
		add(Box.createVerticalStrut(40));
		row++;
		
		filterAmount = (Float) FastMath.clamp(server.config.getFloat("filters.amount", 0.3f), 0, 1);
		
		add(new JLabel("Intensity"), c(0, row, 2));
		add(new AdjButton("+", 0, false), c(1, row, 2));
		add(amountLabel = new JLabel(StringUtils.prettyNumber(filterAmount * 100f) + "%"), c(2, row, 2));
		add(new AdjButton("-", 0, true), c(3, row, 2));
		row++;
		
		filterTicks = (int) FastMath.clamp(server.config.getInt("filters.tickCount", 1), 0, 80);
		
		add(new JLabel("Ticks"), c(0, row, 2));
		add(new AdjButton("+", 1, false), c(1, row, 2));
		add(ticksLabel = new JLabel(StringUtils.prettyNumber(filterTicks)), c(2, row, 2));
		add(new AdjButton("-", 1, true), c(3, row, 2));
		
	}
	
	void updateTrackersFilters() {
		IMUTracker imu;
		for(Tracker t : server.getAllTrackers()) {
			Tracker realTracker = t;
			if(t instanceof ReferenceAdjustedTracker)
				realTracker = ((ReferenceAdjustedTracker<? extends Tracker>) t).getTracker();
			if(realTracker instanceof IMUTracker) {
				imu = (IMUTracker) realTracker;
				imu.setFilter(filterType, filterAmount, filterTicks);
			}
		}
	}
	
	void adjustValues(int cat, boolean neg) {
		if(cat == 0) {
			if(neg) {
				filterAmount = (Float) FastMath.clamp(filterAmount - 0.1f, 0, 1);
			} else {
				filterAmount = (Float) FastMath.clamp(filterAmount + 0.1f, 0, 1);
			}
			amountLabel.setText((StringUtils.prettyNumber(filterAmount * 100f)) + "%");
			server.config.setProperty("filters.amount", filterAmount);
		} else if(cat == 1) {
			if(neg) {
				filterTicks = (int) FastMath.clamp(filterTicks - 1, 0, 50);
			} else {
				filterTicks = (int) FastMath.clamp(filterTicks + 1, 0, 50);
			}
			ticksLabel.setText((StringUtils.prettyNumber(filterTicks)));
			server.config.setProperty("filters.tickCount", filterTicks);
		}
		
		server.saveConfig();
		updateTrackersFilters();
	}
	
	private class AdjButton extends JButton {
		
		public AdjButton(String text, int category, boolean neg) {
			super(text);
			addMouseListener(new MouseInputAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					adjustValues(category, neg);
				}
			});
		}
		
	}
}
