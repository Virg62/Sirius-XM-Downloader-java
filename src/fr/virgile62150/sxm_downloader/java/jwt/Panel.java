package fr.virgile62150.sxm_downloader.java.jwt;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import fr.virgile62150.sxm_downloader.java.API.API;
import fr.virgile62150.sxm_downloader.java.obj.Music;
import fr.virgile62150.sxm_downloader.java.obj.Radio;

public class Panel extends JPanel implements ActionListener{

	private ArrayList<Component> comp_default = new ArrayList<>();
	private ArrayList<Component> comp_radio = new ArrayList<>();
	private ArrayList<Component> comp_dl = new ArrayList<>();
	
	private ArrayList<Component> was_on_screen = new ArrayList<>();
	private ArrayList<Component> was_on_screen_radio = new ArrayList<>();
	
	JButton load_stations;
	
	JLabel statusBarmsg = new JLabel("Bienvenue dans Sirius XM Downloader");
	
	HashMap<JButton, String> radio_btn = new HashMap<>();
	HashMap<JButton, String> music_btn = new HashMap<>();
	
	ArrayList<JButton> on_screen = new ArrayList<>();
	ArrayList<JButton> oos_left = new ArrayList<>();
	ArrayList<JButton> oos_right = new ArrayList<>();
	
	JButton dte;
	JButton gch;
	
	JButton back;
	JButton back_dl;
	
	ArrayList<Radio> radio_list = new ArrayList<>();
	ArrayList<Music> music_list = new ArrayList<>();
	
	Radio r_chosen;
	Music m_chosen;
	
	JProgressBar dl_bar;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7220679159236141045L;

	public Panel() {
		this.setLayout(null);
		this.setSize(1280,720);
		initStatusBar();
		initDefault();
		initRadio();
		initDL();
	}
	
	
	private Font getTitleFont(Font def) {
		return def.deriveFont(def.getStyle(), 30);
	}
	
	
	private void initStatusBar() {
		statusBarmsg.setBounds(0, this.getHeight()-statusBarmsg.getFont().getSize()-50, this.getWidth(), statusBarmsg.getFont().getSize()+10);
		this.add(statusBarmsg);
	}
	
	private void hideDefault() {
		
		was_on_screen = new ArrayList<>();
		was_on_screen.addAll(comp_default);
		for (JButton os : on_screen) {
			was_on_screen.add(os);
		}
		
		// cache tout 
		for (Component e : was_on_screen) {
			e.setVisible(false);
		}
	}
	
	private void showDefault() {
		// A executer après hideDefault !!
		for (Component c : was_on_screen) {
			c.setVisible(true);
		}
	}
	
	private void initDefault() {
		// liste des stations
		
		JLabel l = new JLabel("Sirius XM Downloader", SwingConstants.CENTER);
		l.setFont(getTitleFont(l.getFont()));
		l.setBounds(0, 0,this.getWidth(), l.getFont().getSize());
		this.add(l);
		//comp_default.add(l);
		
		JLabel info = new JLabel("Pour afficher la liste des stations, appuyez sur le bouton ci-dessous :");
		info.setBounds(0, 60, this.getWidth(), info.getFont().getSize());
		this.add(info);
		comp_default.add(info);
		
		load_stations = new JButton("Charger");
		load_stations.setBounds(0, 55+info.getHeight()+10,100,30);
		load_stations.addActionListener(this);
		this.add(load_stations);
		comp_default.add(load_stations);
		
		try {
			API.getInstance().init();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			showErrorDialog(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showErrorDialog(e);
			e.printStackTrace();
		}
		
		
	}
	
	private void initRadio() {
		JLabel info = new JLabel("Liste des titres proposés par la radio : ");
		info.setBounds(0, 60, this.getWidth(), info.getFont().getSize());
		
		comp_radio.add(info);
		
		back = new JButton("Retour");
		back.addActionListener(this);
		back.setBounds(0, 55+info.getHeight()+10,100,30);
		comp_radio.add(back);
		
		
		info.setVisible(false);
		back.setVisible(false);
		this.add(info);
		this.add(back);
		
	}
	
	private void initDL() {
		JLabel info = new JLabel("Téléchargement de votre titre en cours ...");
		info.setBounds(0, 60, this.getWidth(), info.getFont().getSize());
		
		comp_dl.add(info);
		
		back_dl = new JButton("Retour");
		back_dl.addActionListener(this);
		back_dl.setBounds(0, 55+info.getHeight()+10,100,30);
		comp_dl.add(back_dl);
		
		dl_bar = new JProgressBar(0,100);
		
		dl_bar.setBounds(0, 385-40, this.getWidth(), 40);
		dl_bar.setStringPainted(true);
		comp_dl.add(dl_bar);
		
		
		info.setVisible(false);
		back.setVisible(false);
		dl_bar.setVisible(false);
		
		
		
		this.add(info);
		this.add(back_dl);
		this.add(dl_bar);
	}
	
	private void hideRadio() {
		was_on_screen_radio = new ArrayList<>();
		was_on_screen_radio.addAll(comp_radio);
		
		for (Map.Entry<JButton, String> entry : music_btn.entrySet()) {
			was_on_screen_radio.add(entry.getKey());
		}
		
		for (Component c : was_on_screen_radio) {
			c.setVisible(false);
		}
	}
	
	
	private void reshowRadio() {
		for (Component c : was_on_screen_radio) {
			c.setVisible(true);
		}
	}
	
	private void showRadio(String key) {
		hideDefault();
		
		for (Component c : comp_radio)  {
			c.setVisible(true);
		}
		statusBarmsg.setText("Chargement des titres proposés par la radio en cours...");
		long start = System.currentTimeMillis();
		music_btn = new HashMap<>();
		try {
			music_list = API.getInstance().getTrackList(key);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			showErrorDialog(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showErrorDialog(e);
			e.printStackTrace();
		}
		showTracks();
		long duration = System.currentTimeMillis() - start;
		
		statusBarmsg.setText("Titres proposés par la radio "+r_chosen.getName() +" ("+ music_list.size()+" titres chargés en "+duration+" ms.)");
		
		
	}
	
	private void showTracks() {
		int btn_count = 0, total_count= 0;
		int initial_x = 0, initial_y = 301;
		for (Music m : music_list) {
			String full_title = m.getArtist()+" - "+m.getTitle();
			JButton b = new JButton(full_title);
			b.setToolTipText(full_title);
			b.addActionListener(this);
			music_btn.put(b,m.getUUID());
			b.setBounds(initial_x, initial_y, 253, 108);
			
			initial_x+= 253+2;
			this.add(b);
		}
	}
	
	private void showDL() {
		for (Component c : comp_dl) {
			c.setVisible(true);
		}
		this.repaint();
		
		back_dl.setEnabled(false);
		this.statusBarmsg.setText("Téléchargement de "+m_chosen.getTitle()+" de "+m_chosen.getArtist()+" en cours...");
		API.getInstance().choseMusic(m_chosen);
		Thread th = new Thread(API.getInstance());
		th.start();
		
		
	}
	
	public void setPBPercentage(float percent) {
		dl_bar.setValue((int) (percent*100));
		if (percent == 1) {
			back_dl.setEnabled(true);
			this.statusBarmsg.setText("Téléchargement de "+m_chosen.getTitle()+" de "+m_chosen.getArtist()+" terminé");
		}
		this.repaint();
	}
	
	private void hideDL() {
		for (Component c : comp_dl) {
			c.setVisible(false);
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == load_stations) {
			loadRadios();
		} else if (e.getSource() == dte) {
			showRight();
		} else if (e.getSource() == gch) {
			showLeft();
		} else if (e.getSource() == back) {
			hideRadio();
			showDefault();
		} else if (e.getSource() == back_dl) {
			hideDL();
			reshowRadio();
		} else {
			if (!findRadio(e)) {
				findMusic(e);
			}
		}
		// TODO Auto-generated method stub
		
	}
	
	private boolean findRadio(ActionEvent e) {
		for (Map.Entry<JButton, String> entry : radio_btn.entrySet()) {
			if (entry.getKey() == e.getSource()) {
				// On la met en mémoire
				for (Radio r : radio_list) {
					if (r.getId().equals(entry.getValue())) {
						r_chosen = r;
						break;
					}
				}
							
				showRadio(entry.getValue());
				return true;
			}
		}
		return false;
	}
	
	private boolean findMusic(ActionEvent e) {
		for (Map.Entry<JButton, String> entry : music_btn.entrySet()) {
			if (e.getSource() == entry.getKey()) {
				for (Music m : music_list) {
					if (m.getUUID().equals(entry.getValue())) {
						m_chosen = m;
						break;
					}
				}
				//JOptionPane.showMessageDialog(this, "Musique choisie : "+m_chosen.getArtist()+" - "+m_chosen.getTitle(),"Succès",JOptionPane.INFORMATION_MESSAGE);
				hideRadio();
				showDL();
				
				return true;
			}
		}
		return false;
	}
	
	private void loadRadios() {
		long start = System.currentTimeMillis();
		statusBarmsg.setText("Chargement des radio en cours... Merci de patienter");
		load_stations.setEnabled(false);
		try {
			radio_list = API.getInstance().getChannels();
			
			int btn_count = 0, total_count= 0;
			int initial_x = 0, initial_y = 110;
			
			
			for(Radio r : radio_list) {
				JButton b = new JButton(r.getName());
				b.addActionListener(this);
				radio_btn.put(b,r.getId());
				
				if (total_count<=24) {
				
				b.setBounds(initial_x, initial_y, 253, 108);
				
				initial_x+= 253+2;
				
				if (btn_count == 4) {
					initial_x = 0;
					initial_y += 108+2;
					btn_count = 0;
				} else {
					btn_count++;
				}
				on_screen.add(b);
				
				this.add(b);
				
				} else {
					oos_right.add(b);
				}
				
				
				total_count+=1;
			}
			
			long duration = System.currentTimeMillis() - start;
			statusBarmsg.setText("Radios Chargées. Nombre de radios : "+radio_list.size()+" chargées en "+duration+" ms");
			
			dte = new JButton(">");
			gch = new JButton("<");
			
			dte.setBounds(1165,55+statusBarmsg.getFont().getSize()+10 , 100, 30);
			gch.setBounds(1065, 55+statusBarmsg.getFont().getSize()+10, 100, 30);
			this.add(dte); this.add(gch);
			dte.addActionListener(this); gch.addActionListener(this);
			
			comp_default.add(dte); comp_default.add(gch);
			
			check_rad_btn();
			
			this.repaint();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			showErrorDialog(e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showErrorDialog(e);
			e.printStackTrace();
		}
		
	}
	
	private void check_rad_btn() {
		if (oos_left.size() <= 0) 
			gch.setEnabled(false);
		else
			gch.setEnabled(true);
		
		if (oos_right.size() <= 0) 
			dte.setEnabled(false);
		else
			dte.setEnabled(true);
	}
	
	
	private void showLeft() {
		if (oos_left.size() == 0) {return;}
		for (JButton b : on_screen) {
			oos_right.add(0, b);
			b.setVisible(false);
		}
		on_screen = new ArrayList<>();
		
		for (JButton b : oos_left) {
			if (on_screen.size() < 25) {
				on_screen.add(b);
			} else {
				break;
			}
		}
		
		for (JButton b : on_screen) {
			oos_left.remove(b);
		}
		
		int btn_count = 0;
		int initial_x = 0, initial_y = 110;
		
		
		for (JButton b : on_screen) {
			b.setBounds(initial_x, initial_y, 253, 108);
			b.setVisible(true);
			this.add(b);
			
			if (btn_count == 4) {
				initial_x = 0;
				initial_y += 108+2;
				btn_count = 0;
			} else {
				initial_x+= 253+2;
				btn_count++;
			}
			
		}
		
		check_rad_btn();
		this.repaint();
	} 
	
	private void showRight() {
		if (oos_right.size() == 0) {return;}
		for (JButton b : on_screen) {
			oos_left.add(0,b);
			b.setVisible(false);
		}
		on_screen = new ArrayList<>();
		
		for (JButton b : oos_right) {
			if (on_screen.size() < 25) {
				on_screen.add(b);
			} else {
				break;
			}
		}
		
		for (JButton b : on_screen) {
			oos_right.remove(b);
		}
		 
		
		int btn_count = 0;
		int initial_x = 0, initial_y = 110;
		
		
		for (JButton b : on_screen) {
			b.setBounds(initial_x, initial_y, 253, 108);
			b.setVisible(true);
			this.add(b);
			
			if (btn_count == 4) {
				initial_x = 0;
				initial_y += 108+2;
				btn_count = 0;
			} else {
				initial_x+= 253+2;
				btn_count++;
			}
			
		}
		
		check_rad_btn();
		this.repaint();
	}
	
	
	public void showErrorDialog(Exception e) {
		JOptionPane.showMessageDialog(this, "Une erreur est survenue :\n"+e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
	}
	
}
