package maptool.configurationTool;

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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class ConfigurationTool extends JFrame implements ItemListener {

	private static final long serialVersionUID = 1L;
	static JCheckBox checkBoxWarplanets = new JCheckBox();
	static JCheckBox checkBoxPlanets = new JCheckBox();
	static JCheckBox checkBoxNeutral = new JCheckBox();
	static JCheckBox checkBoxLogin = new JCheckBox();
	static JTextField textFieldUsername = new JTextField();
	static JTextField textFieldPassword = new JTextField();
	
	static String username;
	static String password;

	static boolean writeWarplanets;
	static boolean writePlanets;
	static boolean writeNeutral;
	static boolean doLogin;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ConfigurationTool();

	}

	ConfigurationTool() {
		JPanel panel = new JPanel();
		JButton save = new JButton();
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter("settings.ini"));
				out.write("Write warplanets="+String.valueOf(writeWarplanets));
				out.newLine();
				out.write("Write planets="+String.valueOf(writePlanets));
				out.newLine();
				out.write("Write neutral="+String.valueOf(writeNeutral));
				out.newLine();
				out.write("Auto login="+String.valueOf(doLogin));
				out.newLine();
				out.write("Username="+textFieldUsername.getText());
				out.newLine();
				out.write("Password="+textFieldPassword.getText());
				out.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			}
		});
		checkBoxWarplanets.setText("Write warplanets");
		checkBoxWarplanets.addItemListener(this);
		checkBoxPlanets.setText("Write planets");
		checkBoxPlanets.addItemListener(this);
		checkBoxNeutral.setText("Write neutral");
		checkBoxNeutral.addItemListener(this);
		checkBoxLogin.setText("Use auto login");
		checkBoxLogin.addItemListener(this);
		
		textFieldUsername.setPreferredSize(new Dimension(150,25));
		textFieldUsername.setEditable(false);
		textFieldPassword.setPreferredSize(new Dimension(150,25));
		textFieldPassword.setEditable(false);
		
		panel.add(checkBoxWarplanets);
		panel.add(checkBoxPlanets);
		panel.add(checkBoxNeutral);
		panel.add(checkBoxLogin);
		panel.add(textFieldUsername);
		panel.add(textFieldPassword);
		
		save.setText("Save settings");
		panel.add(save);		
		
		this.add(panel);
		File file = new File("settings.ini");
		if (file.exists()) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("Warplanets")) {
						writeWarplanets = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("Planets")) {
						writePlanets = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("Neutral")) {
						writeNeutral = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("doLogin")) {
						doLogin = Boolean.valueOf(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("Username")) {
						textFieldUsername.setText(line.substring(line.indexOf("=")+1));
					}
					if (line.startsWith("Password")) {
						textFieldPassword.setText(line.substring(line.indexOf("=")+1));
					}
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		setSize(new Dimension(275, 200));
		//setResizable(false);
		setTitle("Map Tool Configuration Tool");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == checkBoxWarplanets) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				writeWarplanets = true;
			} else
				writeWarplanets = false;
		}
		if (source == checkBoxPlanets) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				writePlanets = true;
			} else
				writePlanets = false;
		}
		if (source == checkBoxNeutral) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				writeNeutral = true;
			} else
				writeNeutral = false;
		}
		if (source == checkBoxLogin){
			if(e.getStateChange() == ItemEvent.SELECTED){
				doLogin = true;
				textFieldUsername.setEditable(doLogin);
				textFieldPassword.setEditable(doLogin);
			}
			else {
				doLogin = false;
				textFieldUsername.setText("");
				textFieldUsername.setEditable(doLogin);
				textFieldPassword.setText("");
				textFieldPassword.setEditable(doLogin);
				
			}
		}
	}

}
