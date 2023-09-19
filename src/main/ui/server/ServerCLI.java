package ui.server;

import game.GameLogger;
import game.net.UDPGameServer;
import game.net.NetworkConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class ServerCLI {
    private static ServerCLI singleton;
    private final HashMap<String,ServerCommand> commands = new HashMap<>();
    private final UDPGameServer server;
    private final ServerCLITask task = new ServerCLITask(Math.floorDiv(1000,165));
    public static ServerCLI getInstance() {
        if (singleton == null) singleton = new ServerCLI();
        return singleton;
    }

    private ServerCLI() {
        try {
            server = new UDPGameServer();
            GameLogger.getDefault().log("Server started on port " + NetworkConstants.PORT, GameLogger.Category.GAME);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        commands.put("stop",new ServerStopCommand());

        task.start();
    }

    private void update() {
        server.update();
    }

    public boolean isActive() {
        return task.active;
    }

    public void stop() {
        task.stopServer();
    }

    private void sendCommand(String command) {
        String[] split = command.split(" ");
        if (commands.containsKey(split[0])) {
            // remove the command name from the args
            String[] args = new String[split.length - 1];
            System.arraycopy(split,1,args,0,split.length - 1);
            commands.get(split[0]).call(args);
        } else {
            GameLogger.getDefault().log("Command not found: " + split[0]);
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

    private class ServerCLITask extends Thread {
        private boolean active = true;
        private final long period;
        public ServerCLITask(long period) {
            super("ServerCLIThread");
            this.period = period;
        }

        public void stopServer() {
            active = false;
        }
        @Override
        public void run() {
            while (active) {
                update();
                try {
                    Thread.sleep(period);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
