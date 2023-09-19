package ui.server;

import game.GameLogger;

class ServerStopCommand implements ServerCommand {
    public ServerStopCommand() {
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            GameLogger.getDefault().log("Ignored arguments in stop command: " + String.join(" ",args), GameLogger.Level.WARNING);
        }
        ServerCLI.getInstance().stop();
        System.exit(0);
    }
}
