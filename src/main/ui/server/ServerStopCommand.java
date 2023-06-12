package ui.server;

class ServerStopCommand implements ServerCommand {
    public ServerStopCommand() {
    }

    @Override
    public void call(String[] args) {
        if (args.length > 0) {
            System.out.println("Ignored arguments in stop command: " + String.join(" ",args));
        }
        ServerCLI.getInstance().stop();
        System.exit(0);
    }
}
