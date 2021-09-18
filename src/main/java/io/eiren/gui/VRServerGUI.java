package io.eiren.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import io.eiren.util.MacOSX;
import io.eiren.util.OperatingSystem;
import io.eiren.util.StringUtils;
import io.eiren.util.ann.AWTThread;
import io.eiren.vr.Main;
import io.eiren.vr.VRServer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.BoxLayout.PAGE_AXIS;
import static javax.swing.BoxLayout.LINE_AXIS;

public class VRServerGUI extends JFrame {
	
	public static final String TITLE = "SlimeVR Server (" + Main.VERSION + ")";
	
	public final VRServer server;
	private final TrackersList trackersList;
	private final SkeletonList skeletonList;
	private JButton resetButton;
	private EJBox pane;
	
	private float zoom = 1.5f;
	private float initZoom = zoom;
	
	@AWTThread
	public VRServerGUI(VRServer server) {
		super(TITLE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(OperatingSystem.getCurrentPlatform() == OperatingSystem.OSX)
			MacOSX.setTitle(TITLE);
		try {
			List<BufferedImage> images = new ArrayList<BufferedImage>(6);
			images.add(ImageIO.read(VRServerGUI.class.getResource("/icon16.png")));
			images.add(ImageIO.read(VRServerGUI.class.getResource("/icon32.png")));
			images.add(ImageIO.read(VRServerGUI.class.getResource("/icon48.png")));
			images.add(ImageIO.read(VRServerGUI.class.getResource("/icon64.png")));
			images.add(ImageIO.read(VRServerGUI.class.getResource("/icon128.png")));
			images.add(ImageIO.read(VRServerGUI.class.getResource("/icon256.png")));
			setIconImages(images);
			if(OperatingSystem.getCurrentPlatform() == OperatingSystem.OSX) {
				MacOSX.setIcons(images);
			}
		} catch(IOException e1) {
			e1.printStackTrace();
		}
		
		this.server = server;
		
		this.zoom = server.config.getFloat("zoom", zoom);
		this.initZoom = zoom;
		setDefaultFontSize(zoom);
		// All components should be constructed to the current zoom level by default
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setLayout(new BoxLayout(getContentPane(), PAGE_AXIS));
		
		this.trackersList = new TrackersList(server, this);
		this.skeletonList = new SkeletonList(server, this);
		
		add(new JScrollPane(pane = new EJBox(PAGE_AXIS), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		GraphicsConfiguration gc = getGraphicsConfiguration();
		Rectangle screenBounds = gc.getBounds();
		setMinimumSize(new Dimension(100, 100));
		setSize(Math.min(server.config.getInt("window.width", 800), screenBounds.width), Math.min(server.config.getInt("window.height", 800), screenBounds.height));
		setLocation(server.config.getInt("window.posx", screenBounds.x + (screenBounds.width - getSize().width) / 2), screenBounds.y + server.config.getInt("window.posy", (screenBounds.height - getSize().height) / 2));
		
		// Resize and close listeners to save position and size betwen launcher starts
		addComponentListener(new AbstractComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				saveFrameInfo();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				saveFrameInfo();
			}
		});
		
		build();
	}
	
	protected void saveFrameInfo() {
		Rectangle b = getBounds();
		server.config.setProperty("window.width", b.width);
		server.config.setProperty("window.height", b.height);
		server.config.setProperty("window.posx", b.x);
		server.config.setProperty("window.posy", b.y);
		server.saveConfig();
	}
	
	public float getZoom() {
		return this.zoom;
	}
	
	public void refresh() {
		// Pack and display
		//pack();
		setVisible(true);
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}
	
	@AWTThread
	private void build() {
		pane.removeAll();
		
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
			add(Box.createHorizontalStrut(10));
			add(new JButton("Fast Reset") {{
				addMouseListener(new MouseInputAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						resetFast();
					}
				});
			}});
			add(Box.createHorizontalGlue());
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
			add(new JButton("WiFi") {{
				addMouseListener(new MouseInputAdapter() {
					@SuppressWarnings("unused")
					@Override
					public void mouseClicked(MouseEvent e) {
						new WiFiWindow(VRServerGUI.this);
					}
				});
			}});
			add(Box.createHorizontalStrut(10));
		}});
		
		pane.add(new EJBox(LINE_AXIS) {{
			setBorder(new EmptyBorder(i(5)));
			add(new EJBox(PAGE_AXIS) {{
				setAlignmentY(TOP_ALIGNMENT);
				add(new JLabel("SteamVR Trackers:"));
				JComboBox<String> trackersSelect;
				add(trackersSelect = new JComboBox<>());
				trackersSelect.addItem("Waist");
				trackersSelect.addItem("Waist + Legs");
				trackersSelect.addItem("Waist + Legs + Chest");
				trackersSelect.addItem("Waist + Legs + Knees");
				trackersSelect.addItem("Waist + Legs + Chest + Knees");
				switch(server.config.getInt("virtualtrackers", 3)) {
				case 1:
					trackersSelect.setSelectedIndex(0);
					break;
				case 3:
					trackersSelect.setSelectedIndex(1);
					break;
				case 4:
					trackersSelect.setSelectedIndex(2);
					break;
				case 5:
					trackersSelect.setSelectedIndex(3);
					break;
				case 6:
					trackersSelect.setSelectedIndex(4);
					break;
				}
				trackersSelect.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						switch(trackersSelect.getSelectedIndex()) {
						case 0:
							server.config.setProperty("virtualtrackers", 1);
							break;
						case 1:
							server.config.setProperty("virtualtrackers", 3);
							break;
						case 2:
							server.config.setProperty("virtualtrackers", 4);
							break;
						case 3:
							server.config.setProperty("virtualtrackers", 5);
							break;
						case 4:
							server.config.setProperty("virtualtrackers", 6);
							break;
						}
						server.saveConfig();
					}
				});
				add(Box.createHorizontalStrut(10));
				
				add(new JLabel("Trackers list"));
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
	private void resetFast() {
		server.resetTrackersYaw();
	}
	
	@AWTThread
	private void reset() {
		ButtonTimer.runTimer(resetButton, 3, "RESET", server::resetTrackers);
	}
}
