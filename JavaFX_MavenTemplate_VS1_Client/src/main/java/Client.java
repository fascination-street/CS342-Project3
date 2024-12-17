import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


//I used the code from the Client.java file part of the JavaFX_GUIServer_Maven project used in Homework 5 as a starting point
public class Client extends Thread{
    Socket socketClient;
    String IP;
    int port;
    boolean successfulConnection = false;
    //boolean keepGoing = false;

    //private PokerInfo pInfo;

    ObjectInputStream in;
    ObjectOutputStream out;

    private Consumer<Serializable> callback;

    Client(Consumer<Serializable> call) {
        callback = call;
    }

    Client (Consumer<Serializable> call, String IP, String port) {
        callback = call;
        this.IP = IP;
        try {
            this.port = Integer.parseInt(port);
        }
        catch (NumberFormatException e) {
            this.port = -1;
            return; 
        }
    }

    public void run() {
        try {
            socketClient = new Socket(IP,port); 
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        
           successfulConnection = true;

           callback.accept("Successfully connected to server.");
            
        }
        catch(Exception e) {
            successfulConnection = false;
            System.out.println("Fail here");
        }

        while(true) {
            try {
                //What should the client be expecting from the server?
                // Communication occurs when deal, play, or fold are pressed
                // After the server deals, the client should expect:
                //  Its cards (Player's hand)
                //  Updated text for cash, ante pp play wagers
                // 
                
                JavaFXTemplate.clientPokerInfo = (PokerInfo)in.readObject(); 

                
                //callback.accept(data.card1);
                //callback.accept(data.card2);
                //callback.accept(data.card3);
                if (JavaFXTemplate.clientPokerInfo.buttonPressed == 1) {
                    if (JavaFXTemplate.clientPokerInfo.winner == 1) {
                        callback.accept("Dealer wins with " + JavaFXTemplate.clientPokerInfo.dHandVal);
                    }
                    else if (JavaFXTemplate.clientPokerInfo.winner == 2) {
                        callback.accept("Player wins with " + JavaFXTemplate.clientPokerInfo.pHandVal);
                    }
                }
               
                
                //keepGoing = true;
                
            }
            catch(Exception e) {}
        }
    }

    public void send(PokerInfo pInfo) { 
        try {
            //try {out.reset();}
			//catch (IOException a) {System.out.println("fail?1");}
            out.writeObject(pInfo);
        }
        catch (IOException e) {
            System.out.println("hmm...");
            e.printStackTrace();
        }
    }

    public void send(String string) { 
        try {
            out.writeObject(string);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
