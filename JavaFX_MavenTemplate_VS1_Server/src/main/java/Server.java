import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

//The basis of this code is built off the code from the GUIServer Server.java file used in Homework 5
//I used it as a jumping off point
public class Server {
    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;
    int port = -1;

    boolean isRunning = true;

    Server(Consumer<Serializable> call) {
        callback = call;
        server = new TheServer();
        server.start();
        port = 5555;
    }

    Server(Consumer<Serializable> call, String text) {
        callback = call;
        
        try {
            port = Integer.parseInt(text);
        }
        catch (NumberFormatException e){
            port = -1;
            return;
        }

        server = new TheServer();
        server.start();
    }

    //May want to make a new constructor to customize ports and whatnot

    public class TheServer extends Thread {
        public void run() {
            try (ServerSocket socket = new ServerSocket(port);) {

            while(true) {
                ClientThread cli = new ClientThread(socket.accept(), count);
                callback.accept("Client #" + count + " connected.");
                clients.add(cli);
                cli.start();
                count++;

                JavaFXTemplate.updateClientCount(clients.size());
                
                }
            }
                catch(Exception e) {
                    callback.accept("Server did not launch");
                }
            }
        }


        class ClientThread extends Thread{
            Socket connection;
            int count;
            ObjectInputStream in;
            ObjectOutputStream out;

            ClientThread(Socket s, int count) {
                this.connection = s;
                this.count = count;
            }

            public synchronized void run() {
                Player player = new Player();
                Dealer dealer = new Dealer();
                try {
                    in = new ObjectInputStream(connection.getInputStream());
                    out = new ObjectOutputStream(connection.getOutputStream());
                    connection.setTcpNoDelay(true);
                }
                catch (Exception e) {
                    System.out.println("not open");
                }

                while(isRunning) {
                    //It seems like this is where most of the game logic will occur, depending on what button the client presses
                    try {
                        
                        PokerInfo data = (PokerInfo)in.readObject();
                        //System.out.println("fail?after");
                        //The only times the client will be sending info is when it presses one of the buttons... so choose what to do based on that!
                        if (data.buttonPressed == 0) {
                            //System.out.println("fail?66");
                            callback.accept("Client " + count + " deals.");
                            player.hand = dealer.dealHand();
                            dealer.dealersHand = dealer.dealHand();

                            player.anteBet = data.ante;
                            player.pairPlusBet = data.pairPlus;
                            player.totalWinnings = player.totalWinnings - player.pairPlusBet - player.anteBet;
                            data.cash = player.totalWinnings;
                            callback.accept("Client " + count + " bet $" + player.anteBet + " for ante and $" + player.pairPlusBet + " for pair plus.");
                            
                            try {out.reset();}
                            catch (IOException e) { System.out.println("fail?2");}
                            
                            //updates the card strings to be output to the client
                            data.card1 = ("" + player.hand.get(0).suit + player.hand.get(0).value); 
                            data.card2 = ("" + player.hand.get(1).suit + player.hand.get(1).value); 
                            data.card3 = ("" + player.hand.get(2).suit + player.hand.get(2).value);

                            data.dCard1 = ("" + dealer.dealersHand.get(0).suit + dealer.dealersHand.get(0).value); 
                            data.dCard2 = ("" + dealer.dealersHand.get(1).suit + dealer.dealersHand.get(1).value); 
                            data.dCard3 = ("" + dealer.dealersHand.get(2).suit + dealer.dealersHand.get(2).value);

                            

                            data.hang = false;
                            //System.out.println("fail?25");
                            out.writeObject(data);
                        }
                        else if (data.buttonPressed == 1) {
                            callback.accept("Client " + count + " plays.");
                            //this is where we actually check who won, as well as display the dealer's cards
                            data.dHandVal = ThreeCardLogic.evalHand(dealer.dealersHand).name;
                            data.pHandVal = ThreeCardLogic.evalHand(player.hand).name;
                            data.winner = ThreeCardLogic.compareHands(dealer.dealersHand, player.hand);

                            player.playBet = player.anteBet;
                            data.play = player.playBet;

                            if (data.winner == 1) {
                                data.winningsThisRound = player.anteBet + player.pairPlusBet + player.playBet;
                                player.totalWinnings -= player.playBet;
                                data.playerWon = false;
                                callback.accept("Client " + count + " has lost $" + data.winningsThisRound + " this round.");
                                
                            } 
                            else if (data.winner == 2) {
                                data.playerWon = true;
                                data.winningsThisRound = player.anteBet + player.playBet + ThreeCardLogic.evalPPWinnings(player.hand, player.pairPlusBet);
                                player.totalWinnings = player.totalWinnings + data.winningsThisRound;
                                callback.accept("Client " + count + " has won $" + data.winningsThisRound + " this round.");
                            }
                            data.cash = player.totalWinnings;
                            
                            callback.accept("Client " + count + " has $" + data.cash + " after this round.");

                            data.card1 = null; 
                            data.card2 = null; 
                            data.card3 = null;

                            data.playOver = true;
                            //data.dCard1 = ("" + dealer.dealersHand.get(0).suit + dealer.dealersHand.get(0).value); 
                            //data.dCard2 = ("" + dealer.dealersHand.get(1).suit + dealer.dealersHand.get(1).value); 
                            //data.dCard3 = ("" + dealer.dealersHand.get(2).suit + dealer.dealersHand.get(2).value);

                            out.writeObject(data);
                        }
                        else if (data.buttonPressed == 2) {
                            callback.accept("Client " + count + " folds.");
                            data.winningsThisRound = player.anteBet + player.pairPlusBet;
                            callback.accept("Client " + count + " has lost $" + data.winningsThisRound + " this round.");
                        }

                        if (data.newRound) {
                            callback.accept("Client " + count + " has started a new round.");
                            //data.newRound = false;
                        }
                        
                        //callback.accept("data a = " + data.ante);
                        //System.out.println(data);
                    }
                    catch(Exception e) {
                        callback.accept("Client " + count + " disconnected.");
                        clients.remove(this);
                        JavaFXTemplate.updateClientCount(clients.size());
                        break;
                    }
                }
            }
        }
    }
     

