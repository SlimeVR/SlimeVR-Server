package io.eiren.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.vr.VRServer;
import io.eiren.vr.bridge.NamedPipeVRBridge;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseEvent;

import static javax.swing.BoxLayout.PAGE_AXIS;
import static javax.swing.BoxLayout.LINE_AXIS;

public class VRServerGUI extends JFrame {
	
	public final VRServer server;
	private final TrackersList trackersList;
	private final SkeletonList skeletonList;
	private JButton resetButton;
	private JScrollPane scroll;
	private EJBox pane;
	
	private float zoom = 1.5f;
	private float initZoom = zoom;
	
	@AWTThread
	public VRServerGUI(VRServer server) {
		super("SlimeVR Server");
		//increaseFontSize();
		
		this.server = server;
		
		this.zoom = server.config.getFloat("zoom", zoom);
		this.initZoom = zoom;
		setDefaultFontSize(zoom);
		// All components should be constructed to the current zoom level by default
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), PAGE_AXIS));
		
		this.trackersList = new TrackersList(server, this);
		this.skeletonList = new SkeletonList(server, this);
		
		add(scroll = new JScrollPane(pane = new EJBox(PAGE_AXIS), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		
		build();
	}
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void refresh() {
		// Pack and display
		pack();
		setVisible(true);
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				toFront();
				repaint();
			}
		});
	}
	
	@AWTThread
	private void build() {
		pane.removeAll();
		
		NamedPipeVRBridge npvb = server.getVRBridge(NamedPipeVRBridge.class);
		
		pane.add(new EJBox(LINE_AXIS) {{
			setBorder(new EmptyBorder(i(5)));
			
			add(Box.createHorizontalGlue());
			add(resetButton = new JButton("RESET") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						reset();
					}
				});
			}});
			add(Box.createHorizontalGlue());
			if(npvb != null) {
				add(new JButton(npvb.isOneTrackerMode() ? "Trackers: 1" : "Trackers: 3") {{
					addMouseListener(new MouseInputAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							npvb.setSpawnOneTracker(!npvb.isOneTrackerMode());
							setText(npvb.isOneTrackerMode() ? "Trackers: 1" : "Trackers: 3");
						}
					});
				}});
				add(Box.createHorizontalStrut(10));
			}
			add(new JButton("GUI Zoom (x" + StringUtils.prettyNumber(zoom, 2) + ")") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						guiZoom();
						setText("GUI Zoom (x" + StringUtils.prettyNumber(zoom, 2) + ")");
					}
				});
			}});
			add(Box.createHorizontalStrut(10));
		}});
		
		pane.add(new EJBox(LINE_AXIS) {{
			setBorder(new EmptyBorder(i(5)));
			add(new EJBox(PAGE_AXIS) {{
				setAlignmentY(TOP_ALIGNMENT);
				
				add(new JLabel("Trackers"));
				add(trackersList);
				add(Box.createVerticalGlue());
			}});

			add(new EJBox(PAGE_AXIS) {{
				setAlignmentY(TOP_ALIGNMENT);
				add(new JLabel("Body proportions"));
				add(new SkeletonConfig(server, VRServerGUI.this));
				add(new JLabel("Skeleton data"));
				add(skeletonList);
				add(Box.createVerticalGlue());
			}});
		}});
		
		refresh();
		setLocationRelativeTo(null);
		
		server.addOnTick(trackersList::updateTrackers);
		server.addOnTick(skeletonList::updateBones);
	}
	
	// For now only changes font size, but should change fixed components size in the future too
	private void guiZoom() {
		if(zoom <= 1.0f) {
			zoom = 1.5f;
		} else if(zoom <= 1.5f) {
			zoom = 1.75f;
		} else if(zoom <= 1.75f) {
			zoom = 2.0f;
		} else if(zoom <= 2.0f) {
			zoom = 2.5f;
		} else {
			zoom = 1.0f;
		}
		processNewZoom(zoom / initZoom, pane);
		refresh();
		server.config.setProperty("zoom", zoom);
		server.saveConfig();
	}
	
	private static void processNewZoom(float zoom, Component comp) {
		if(comp.isFontSet()) {
			Font newFont = new ScalableFont(comp.getFont(), zoom);
			comp.setFont(newFont);
		}
		if(comp instanceof Container) {
			Container cont = (Container) comp;
			for(Component child : cont.getComponents())
				processNewZoom(zoom, child);
		}
	}
	
	private static void setDefaultFontSize(float zoom) {
		java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
		while(keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if(value instanceof javax.swing.plaf.FontUIResource) {
				javax.swing.plaf.FontUIResource f = (javax.swing.plaf.FontUIResource) value;
				javax.swing.plaf.FontUIResource f2 = new javax.swing.plaf.FontUIResource(f.deriveFont(f.getSize() * zoom));
				UIManager.put(key, f2);
			}
		}
	}
	
	@AWTThread
	private void reset() {
		ButtonTimer.runTimer(resetButton, 3, "RESET", server::resetTrackers);
	}
}
