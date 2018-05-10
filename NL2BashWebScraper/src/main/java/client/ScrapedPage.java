package client;

public class ScrapedPage {
	private String title;
	private String[] commands; 
	
	public ScrapedPage() {
		title = null;
		commands = new String[] {null, null, null, null, null};
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String newTitle) {
		title = newTitle;
	}

	public String getCommand(int index) {
		if (index >= 5) {
			return null;
		}
		return commands[index];
	}
	
	public void setCommand(String command, int index) {
		if (index < 5) {
			commands[index] = command;
		}
	}
	
	public String[] getCommands() {
		return commands.clone();
	}
	
	public void setCommands(String[] newCommands) {
		commands = newCommands;
	}
	
	public String toString() {
		String retString = "";
		retString += title + "\n";
		retString += commands[0] + "\n";
		retString += commands[1] + "\n";
		retString += commands[2] + "\n";
		retString += commands[3] + "\n";
		retString += commands[4];
		return retString;
	}
}
