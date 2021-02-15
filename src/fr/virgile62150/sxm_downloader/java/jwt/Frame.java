package fr.virgile62150.sxm_downloader.java.jwt;

import javax.swing.JFrame;

public class Frame extends JFrame {
	
	Panel panel;

	static Frame instance;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3206619785787653691L;
	
	public static Frame getInstance() {
		if (instance == null) instance = new Frame();
		return instance;
	}
	
	
	private Frame() {
		initWindow();
	}
	
	public Panel getPanel() {
		return panel;
	}
	
	private void initWindow() {
		this.setTitle("Sirius XM Downloader jwt");
		this.setContentPane(panel = new Panel());
		this.setResizable(false);
		this.setSize(1280, 720);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
	}

}
