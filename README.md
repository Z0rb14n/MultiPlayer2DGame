# MultiPlayer2D Game

A (WIP) multiplayer Java game based on the xkcd comic [Tapetum Lucidium](https://xkcd.com/2770/).
This project was mainly to learn about how to build a basic 2D physics engine and integrate it with a networking system which I based off of [Processing's Networking Library](https://github.com/processing/processing/tree/master/java/libraries/net).

## Requirements
- Java 8 (including Swing)
- (Tests) JUnit 5

There are no other dependencies, as the physics engine and networking was built from scratch (physics) or modified from source (TCP/IP library).

## How to Run
There's no external build tool - I used IntelliJ to run specific files.

1. Clone the repository
2. Start the server by running `src/main/ui/server/ServerCLI.java`
3. Start the client by running `src/main/ui/client/GameFrame.java`

### Running Tests
Unit tests are within the test folder.

There exist some graphical tests/visualizations of smaller components of the physics engine located in `src/main/physics/vis`.
