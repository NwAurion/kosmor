package maptool.configurationTool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;


public class ConfigurationTool extends JFrame implements ItemListener {

	private static final long serialVersionUID = 1L;
	private static double VERSION = 1.03;
	
	static JCheckBox checkBoxWarplanets = new JCheckBox();
	static JCheckBox checkBoxPlanets = new JCheckBox();
	static JCheckBox checkBoxNeutral = new JCheckBox();
	static JCheckBox checkBoxfetchInfo = new JCheckBox();
	static JCheckBox checkBoxUpdateHTML = new JCheckBox();
	static JCheckBox checkBoxDate = new JCheckBox();
	static JCheckBox checkBoxGUI = new JCheckBox();
	
	static JCheckBox checkBoxAskAtUpdate = new JCheckBox();
	static JCheckBox checkBoxInfoAtUpdate = new JCheckBox();
	static JCheckBox checkBoxDownloadAtUpdate = new JCheckBox();
	
	static JTextField textFieldLogin = new JTextField();
	static JTextField textFieldPassword = new JTextField();
	
	static File settings = new File("settings.ini");
	
	static String login;
	static String password;

	static boolean writeWarplanets;
	static boolean writePlanets;
	static boolean writeNeutral;
	static boolean fetchInfo;
	static boolean updateHTML;
	static boolean date;
	static boolean gui;
	static boolean askAtUpdate;
	static boolean infoAtUpdate;
	static boolean downloadAtUpdate;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ConfigurationTool();

	}

	ConfigurationTool() {

		JPanel panel = new JPanel(new BorderLayout());
		JButton save = new JButton();
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter("settings.ini"));
					out.write("warplanets="+String.valueOf(writeWarplanets));
					out.newLine();
					out.write("planets="+String.valueOf(writePlanets));
					out.newLine();
					out.write("neutral="+String.valueOf(writeNeutral));
					out.newLine();
					out.write("fetchInfo="+String.valueOf(fetchInfo));
					out.newLine();
					if(textFieldLogin.getText().length()>1){
						out.write("login="+textFieldLogin.getText());
						out.newLine();
					}
					if (textFieldPassword.getText().length()>1){
						out.write("password="+textFieldPassword.getText());
						out.newLine();
					}
					out.write("gui="+String.valueOf(gui));
					out.newLine();
					out.write("update="+String.valueOf(updateHTML));
					out.newLine();
					out.write("date="+String.valueOf(date));
					out.newLine();
					if(askAtUpdate) out.write("checkforupdate=ask");
					if(infoAtUpdate && downloadAtUpdate) out.write("checkforupdate=all");
					else if (infoAtUpdate) out.write("checkforupdate=info");
					else if(downloadAtUpdate) out.write("checkforupdate=download");
					out.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		initGUI();
		
		addGUI(panel);
		
		save.setText("Save settings");
		panel.add(save);		
		
		this.add(panel);
		
		if (settings.exists()) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(settings));
				String line;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("warplanets")) {
						writeWarplanets = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("planets")) {
						writePlanets = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("neutral")) {
						writeNeutral = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("login")) {
						login = line.substring(line.indexOf("login")+6);
					}
					if (line.startsWith("password")) {
						password = line.substring(line.indexOf("password")+9);
					}
					if (line.startsWith("fetchinfo")) {
						fetchInfo = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("gui")){
						gui = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("update")) {
						updateHTML = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("date")) {
						date = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("checkforupdate")) {
						if (line.contains("ask")) askAtUpdate = true;
						else if (line.contains("info")) infoAtUpdate = true;
						else if (line.contains("download")) downloadAtUpdate = true;
						else if (line.contains("all")) infoAtUpdate = downloadAtUpdate = true;
					}
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			checkBoxWarplanets.setSelected(writeWarplanets);
			checkBoxPlanets.setSelected(writePlanets);
			checkBoxNeutral.setSelected(writeNeutral);
			checkBoxfetchInfo.setSelected(fetchInfo);
			checkBoxGUI.setSelected(gui);
			checkBoxUpdateHTML.setSelected(updateHTML);
			checkBoxDate.setSelected(date);
			
			checkBoxAskAtUpdate.setSelected(askAtUpdate);
			checkBoxInfoAtUpdate.setSelected(infoAtUpdate);
			checkBoxDownloadAtUpdate.setSelected(downloadAtUpdate);
			
			textFieldLogin.setText(login);
			textFieldPassword.setText(password);

		}
		
		setSize(new Dimension(480, 170)); // Width, Height
		setTitle("Version "+VERSION);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void addGUI(JPanel panel) { // TODO Implementing a nice layout
		panel.add(checkBoxWarplanets);
		panel.add(checkBoxPlanets);
		panel.add(checkBoxNeutral);
		panel.add(checkBoxfetchInfo);
		panel.add(checkBoxGUI);
		panel.add(checkBoxUpdateHTML);
		panel.add(checkBoxDate);
		panel.add(textFieldLogin);
		panel.add(textFieldPassword);
		panel.add(checkBoxAskAtUpdate);
		panel.add(checkBoxInfoAtUpdate);
		panel.add(checkBoxDownloadAtUpdate);
	}

	public void initGUI() {
		checkBoxWarplanets.setText("Write warplanets");
		checkBoxWarplanets.addItemListener(this);
		
		checkBoxPlanets.setText("Write planets");
		checkBoxPlanets.addItemListener(this);
		
		checkBoxNeutral.setText("Write neutral");
		checkBoxNeutral.addItemListener(this);
		
		checkBoxfetchInfo.setText("Fetch housenames");
		checkBoxfetchInfo.addItemListener(this);
		
		checkBoxGUI.setText("Show GUI");
		checkBoxGUI.addItemListener(this);
		
		checkBoxUpdateHTML.setText("Update old files");
		checkBoxUpdateHTML.addItemListener(this);
		
		checkBoxDate.setText("Use custom date");
		checkBoxDate.addItemListener(this);
		
		checkBoxAskAtUpdate.setText("Ask what to do");
		checkBoxAskAtUpdate.addItemListener(this);
		
		checkBoxInfoAtUpdate.setText("Show changelog");
		checkBoxInfoAtUpdate.addItemListener(this);
		
		checkBoxDownloadAtUpdate.setText("Automatically download");
		checkBoxDownloadAtUpdate.addItemListener(this);
		
		textFieldLogin.setPreferredSize(new Dimension(150,25));
		textFieldPassword.setPreferredSize(new Dimension(150,25));
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == checkBoxWarplanets) {
			if (e.getStateChange() == ItemEvent.SELECTED) writeWarplanets = true;
			 else writeWarplanets = false;
		}
		if (source == checkBoxPlanets) {
			if (e.getStateChange() == ItemEvent.SELECTED) writePlanets = true;
			 else writePlanets = false;
		}
		if (source == checkBoxNeutral) {
			if (e.getStateChange() == ItemEvent.SELECTED) writeNeutral = true;
			else writeNeutral = false;
		}
		if (source == checkBoxfetchInfo) {
			if (e.getStateChange() == ItemEvent.SELECTED) fetchInfo = true;
			else fetchInfo = false;
		}
		if (source == checkBoxGUI) {
			if (e.getStateChange() == ItemEvent.SELECTED) gui = true;
			else gui = false;
		}
		if (source == checkBoxUpdateHTML) {
			if (e.getStateChange() == ItemEvent.SELECTED) updateHTML = true;
			else updateHTML = false;
		}
		if (source == checkBoxDate) {
			if (e.getStateChange() == ItemEvent.SELECTED) date = true;
			else date = false;
		}
		if (source == checkBoxAskAtUpdate) {
			if (e.getStateChange() == ItemEvent.SELECTED) { 
				askAtUpdate = true;
				infoAtUpdate = downloadAtUpdate = false;
				checkBoxInfoAtUpdate.setSelected(false);
				checkBoxDownloadAtUpdate.setSelected(false);
			}
			else askAtUpdate = false;
		}
		if (source == checkBoxInfoAtUpdate) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				infoAtUpdate = true;
				askAtUpdate = false;
				checkBoxAskAtUpdate.setSelected(false);
			}
			else infoAtUpdate = false;
		}
		if (source == checkBoxDownloadAtUpdate) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				downloadAtUpdate = true;
				askAtUpdate = false;
				checkBoxAskAtUpdate.setSelected(false);
			}
			else downloadAtUpdate = false;
		}
	}
}
