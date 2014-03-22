package maptool;

public class StatusMessageHandler {

		final static String STATUS_MESSAGE_DOWNLOADING_1 = "Map does not exist. Login in..";
	final static String STATUS_MESSAGE_DOWNLOADING_2 = "Downloading the map..";
	final static String STATUS_MESSAGE_DOWNLOADING_3 = "Done. Proceeding with the next step";
	
	final static String STATUS_MESSAGE_NOT_DOWNLOADING = "Map does already exist. Proceeding..";
	
	final static String STATUS_MESSAGE_SVG_1 = "Reading the .svg. This may take some time";
	final static String STATUS_MESSAGE_SVG_2 = "Finished reading the .svg. Fetching elements..";
	final static String STATUS_MESSAGE_SVG_3 = "Ensuring that the \"svg\" directory does exit..";
	final static String STATUS_MESSAGE_SVG_4 = "Renaming and moving the map..";
	
	final static String STATUS_MESSAGE_PLANETLIST_1 = "Creating the actual planets from the parsed data";
	final static String STATUS_MESSAGE_PLANETLIST_2 = "Done. Proceeding..";
	
	final static String STATUS_MESSAGE_HTML_1 = "Ensuring that the \"html\" directory does exit..";
	final static String STATUS_MESSAGE_HTML_2 = "Writing the html..";
	final static String STATUS_MESSAGE_HTML_3 = "Finished with writing the html";
	
	final static String STATUS_MESSAGE_FETCHING_WARPLANETS = "Warplanets: Fetching info from Kosmor..";
	final static String STATUS_MESSAGE_FETCHING_PLANETS_1 = "Planets: Fetching info from Kosmor..";
	final static String STATUS_MESSAGE_FETCHING_PLANETS_2 = "This may take several minutes.";
	
	final static String STATUS_MESSAGE_CHECKING_FOR_UPDATE = "Checking for updates..";
	final static String STATUS_MESSAGE_NEW_VERSION_AVAILABLE = "New version available";
	final static String STATUS_MESSAGE_NEW_VERSION_DOWNLOADING = "Downloading the new version. Please wait a moment..";
	final static String STATUS_MESSAGE_NEW_VERSION_FINISHED_DOWNLOADING = "Download finished. Please restart. Exiting..";
	final static String STATUS_MESSAGE_NO_NEW_VERSION = "No new version available. Proceeding..";
	
	static String[] statusMessages = new String[]{
		STATUS_MESSAGE_DOWNLOADING_1, 						// 0
		STATUS_MESSAGE_DOWNLOADING_2, 						// 1
		STATUS_MESSAGE_DOWNLOADING_3, 						// 2
		STATUS_MESSAGE_NOT_DOWNLOADING,						// 3
		STATUS_MESSAGE_SVG_1,								// 4
		STATUS_MESSAGE_SVG_2,								// 5
		STATUS_MESSAGE_SVG_3,								// 6
		STATUS_MESSAGE_SVG_4,								// 7
		STATUS_MESSAGE_PLANETLIST_1,						// 8
		STATUS_MESSAGE_PLANETLIST_2,						// 9
		STATUS_MESSAGE_HTML_1,								// 10
		STATUS_MESSAGE_HTML_2,								// 11
		STATUS_MESSAGE_FETCHING_WARPLANETS,					// 12
		STATUS_MESSAGE_FETCHING_PLANETS_1,					// 13
		STATUS_MESSAGE_FETCHING_PLANETS_2,					// 14
		STATUS_MESSAGE_HTML_3,								// 15
		STATUS_MESSAGE_CHECKING_FOR_UPDATE,					// 16
		STATUS_MESSAGE_NEW_VERSION_AVAILABLE,				// 17
		STATUS_MESSAGE_NEW_VERSION_DOWNLOADING,				// 18
		STATUS_MESSAGE_NEW_VERSION_FINISHED_DOWNLOADING,	// 19
		STATUS_MESSAGE_NO_NEW_VERSION						// 20
	};
	
	
	public static void postStatusMessages(int i){
		if(MapTool.GUI){
			setText(statusMessages[i]);
		}
		else setConsoleText(statusMessages[i]);
	}
	
	public static void setConsoleText(String STATUS_MESSAGE) {
		System.out.println(MapTool.getTime()+": "+STATUS_MESSAGE);
	}

	public static void setText(String STATUS_MESSAGE) {
		MapTool.textArea.setText(MapTool.textArea.getText()+"\n"+MapTool.getTime()+": "+STATUS_MESSAGE);
	}
}
