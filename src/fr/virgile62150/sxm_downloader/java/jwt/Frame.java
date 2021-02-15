package fr.virgile62150.sxm_downloader.java.jwt;

import javax.swing.JFrame;

public class Frame extends JFrame {
	
	Panel panel;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3206619785787653691L;
	
	public Frame() {
		initWindow();
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
