package client;

public class ScrapedPage {
	// TODO: Automatically populate these fields based on an
	// initializer with a URL parameter. Maybe? 
	
	// TODO: User never interacts with these methods, but it
	// may be a good idea to give more warning via a return value
	// on functions that don't do anything on invalid inputs.
	
    private static final int TOP_N_COMMADS = 5;

    private String title;
    private String[] commands;

    public ScrapedPage() {
        title = null;
        commands = new String[TOP_N_COMMADS];
    }

    /** 
     * @return the title of this question.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param newTitle set the title of this question.
     */
    public void setTitle(String newTitle) {
        title = newTitle;
    }

    /**
     * @param index the index of the commands to retrieve.
     * requires -1 < index < TOP_N_COMMANDS 
     * @return the commands at the specified index.
     */
    public String getCommands(int index) {
        if (index >= TOP_N_COMMADS || index <= -1) {
            return null;
        }
        return commands[index];
    }

    /**
     * Sets the command at the specified index to the passed command.
     * @param command to set the command at index to.
     * @param index of the command field to set to command.
     * requires -1 < index < TOP_N_COMMANDS
     */
    public void setCommand(String command, int index) {
        if (index > -1 && index < TOP_N_COMMADS) {
            commands[index] = command;
        }
    }

    /**
     * @return a copy of the commands contained in this page.
     */
    public String[] getCommands() {
        return commands.clone();
    }

    /**
     * @param newCommands set the current commands to an array of commands
     */
    public void setCommands(String[] newCommands) {
        if (commands.length == TOP_N_COMMADS)
        	commands = newCommands;
    }

    public String toString() {
        String retString = "";
        retString += title + "\n";
        for (int i = 0; i < TOP_N_COMMADS; i++) {
            retString += commands[i] + "\n";
        }
        return retString;
    }
}
