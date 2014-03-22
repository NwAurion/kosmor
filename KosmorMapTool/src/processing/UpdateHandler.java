package processing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

public class UpdateHandler {
	final static int size = 1024;
	static String versionURLAsString = "http://dl.dropbox.com/u/1398050/Kosmor/index.html";
	static String downloadURLAsString = "http://dl.dropbox.com/u/1398050/Kosmor/tools/MapTool.jar";
	static boolean downloadNewVersion = false;
	
	public static boolean checkForUpdate(String checkForUpdate, double currentVersion) {
		boolean newVersionAvailable = false;
		try {
			MapTool.getTextArea().setText(StatusMessageHandler.STATUS_MESSAGE_CHECKING_FOR_UPDATE);
			URL versionUrl = new URL(versionURLAsString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(versionUrl.openStream()));
			String line = reader.readLine();
			double availableVersion = Double.parseDouble(line.substring(
					line.indexOf("<version =") + 11, line.indexOf(">")));
			if (currentVersion < availableVersion) {
				newVersionAvailable = true;
				StatusMessageHandler.postStatusMessages(17);
				checkStatus(checkForUpdate);
				if(downloadNewVersion) { downloadNewVersion(); System.exit(0); }
			}
			else StatusMessageHandler.postStatusMessages(20);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newVersionAvailable;
	}

	public static void checkStatus(String checkForUpdate) throws IOException {
		if (checkForUpdate.equals("ask")) showUpdateQuestions();
		else if (checkForUpdate.equals("info")) showChangeLogInBrowser();
		else if (checkForUpdate.equals("download")) downloadNewVersion = true;
		else if (checkForUpdate.equals("all")) showChangeLogInBrowser(); downloadNewVersion = true;
	}

	public static void showUpdateQuestions() throws IOException {
		if (MapTool.GUI) {
			int response = JOptionPane.showConfirmDialog(null,
					"Would you like to view the changelog?",
					"New version available",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION) showChangeLogInBrowser();
			response = JOptionPane.showConfirmDialog(null,
					"New version available",
					"Would you like to download the new version?",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION) downloadNewVersion = true;
		} else {
			System.out.println("New version available.");
			System.out.println("Press Y to view the changelog");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String answer  = reader.readLine();
			if (answer.startsWith("Y") || answer.startsWith("y")) showChangeLogInBrowser();
			System.out.println("Press Y to download the new version");
			answer  = reader.readLine();
			if (answer.startsWith("Y") || answer.startsWith("y")) downloadNewVersion = true;
		}
	}
	
	public static void showChangeLogInBrowser(){
		if( java.awt.Desktop.isDesktopSupported() ) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
			try {
				java.net.URI uri = new java.net.URI("http://dl.dropbox.com/u/1398050/Kosmor/other/MapTool%20Changelog.txt");
				desktop.browse(uri);
			}
			catch ( Exception e ) {
				System.err.println( e.getMessage() );
			}
		}
	}

	public static void downloadNewVersion() {
		String fAddress = downloadURLAsString;
		String destinationDir = "./";
		int slashIndex = fAddress.lastIndexOf('/');
		int periodIndex = fAddress.lastIndexOf('.');

		String fileName = fAddress.substring(slashIndex + 1);
		if (periodIndex >= 1 && slashIndex >= 0
				&& slashIndex < fAddress.length() - 1) {
			fileUrl(fAddress, fileName, destinationDir);
		} else {
			System.err.println("path or file name.");
		}
	}

	public static void fileUrl(String fAddress, String localFileName,
			String destinationDir) {
		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		try {
			URL Url;
			byte[] buf;
			int ByteRead; // No longer neeeded -> ByteWritten = 0; ?
			Url = new URL(fAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(
					destinationDir + "\\" + localFileName));

			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((ByteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, ByteRead);
				//ByteWritten += ByteRead; <- No longer needed?
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
