package client;

public class ScrapedPage {
    private static final int TOP_N_COMMADS = 5;

    private String title;
    private String[] commands;

    public ScrapedPage() {
        title = null;
        commands = new String[5];
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public String getCommand(int index) {
        if (index >= TOP_N_COMMADS) {
            return null;
        }
        return commands[index];
    }

    public void setCommand(String command, int index) {
        if (index < TOP_N_COMMADS) {
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
        for (int i = 0; i < TOP_N_COMMADS; i++) {
            retString += commands[i] + "\n";
        }
        return retString;
    }
}
