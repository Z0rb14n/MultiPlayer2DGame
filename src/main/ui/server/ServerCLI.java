package ui.server;

import game.net.GameServer;
import game.net.NetworkConstants;

import java.util.Timer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TimerTask;

public class ServerCLI {
    private static ServerCLI singleton;
    private Timer timer;
    private final HashMap<String,ServerCommand> commands = new HashMap<>();
    private final GameServer server;
    private final ServerCLITask task = new ServerCLITask();
    public static ServerCLI getInstance() {
        if (singleton == null) singleton = new ServerCLI();
        return singleton;
    }

    private ServerCLI() {
        try {
            server = new GameServer();
            System.out.println("Server started on port " + NetworkConstants.PORT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        commands.put("stop",new ServerStopCommand());

        timer = new Timer("ServerUpdateTimerThread",true);
        timer.scheduleAtFixedRate(task,0, Math.floorDiv(1000,165));
    }

    private void update() {
        server.update();
    }

    public boolean isActive() {
        return timer != null;
    }

    public void stop() {
        task.cancel();
        timer.cancel();
        timer = null;
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

    private class ServerCLITask extends TimerTask {
        @Override
        public void run() {
            update();
        }
    }
}
