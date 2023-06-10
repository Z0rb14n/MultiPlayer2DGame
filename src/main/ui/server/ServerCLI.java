package ui.server;

import javax.swing.*;
import java.util.HashMap;
import java.util.Scanner;

public class ServerCLI {
    private static ServerCLI singleton;
    private final Timer timer;
    private final HashMap<String,ServerCommand> commands = new HashMap<>();
    public static ServerCLI getInstance() {
        if (singleton == null) singleton = new ServerCLI();
        return singleton;
    }

    private ServerCLI() {
        timer = new Timer(Math.floorDiv(1000,165), ae -> this.update());
        timer.start();

        commands.put("stop",new ServerStopCommand());
    }

    private void update() {

    }

    public boolean isActive() {
        return timer.isRunning();
    }

    public void stop() {
        timer.stop();
    }

    private void sendCommand(String command) {
        String[] split = command.split(" ");
        if (commands.containsKey(split[0])) {
            // remove the command name from the args
            String[] args = new String[split.length - 1];
            System.arraycopy(split,1,args,0,split.length - 1);
            commands.get(split[0]).call(args);
        } else {
            System.out.println("Command not found: " + split[0]);
        }
    }


    public static void main(String[] args) {
        ServerCLI cli = ServerCLI.getInstance();
        Scanner scanner = new Scanner(System.in);
        while (cli.isActive()) {
            String command = scanner.nextLine().trim();
            if (command.isEmpty()) continue;
            cli.sendCommand(command);
        }
    }
}
