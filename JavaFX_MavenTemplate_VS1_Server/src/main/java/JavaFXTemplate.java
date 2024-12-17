import javax.script.ScriptException;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;


public class JavaFXTemplate extends Application {
	TextField portField;
	Button startButton, serverOnButton, serverOffButton;
	Scene startServerScene;
	Scene serverScene;
	HBox startRoot, onOffBox;
	BorderPane pane;
	static Text numClientsText;

	ListView<String> listItems= new ListView<String>();
	Server serverConnection;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	public static void updateClientCount(int size) {
		numClientsText.setText("No. Clients Connected: " + size);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		//sets up start menu
		BorderPane startPane = new BorderPane();
		startPane.setStyle("-fx-background-color: orange");
		portField = new TextField("Enter Port Number");
		startButton = new Button("Connect");

		numClientsText = new Text("No. Clients Connected: 0");

		startRoot = new HBox(portField, startButton);
		startRoot.setAlignment(Pos.CENTER);

		startPane.setCenter(startRoot);

		startServerScene = new Scene(startPane, 300, 100);

		primaryStage.setScene(startServerScene);
		primaryStage.setTitle("Server");
		primaryStage.show();
		//
		
		//Pressing this button initiates the server screen
		startButton.setOnAction(e->{
				serverConnection = new Server(data -> {
				Platform.runLater(()->{
					listItems.getItems().add(data.toString());
				});
			}, portField.getText());

			if (serverConnection.port < 0) {
				portField.setText("Invalid port, please try again.");
			}
			else {
				//Elements that must be visible:
				// How many clients are currently connected to the server
				//Things to add to the listview
				// The result of each game played by a client
				// how much a client bet
				// how much a client won or lost
				// if a client leaves the server
				// if a new client joins
				// if a client plays another hand
				pane = new BorderPane();
				pane.setPadding(new Insets(70));
				pane.setStyle("-fx-background-color: coral");

				serverOnButton = new Button("Enable Server");
				serverOnButton.setDisable(true);
					serverOnButton.setOnAction(f->{
						serverConnection.isRunning = true;
						serverOnButton.setDisable(true);
						serverOffButton.setDisable(false);
						listItems.getItems().add("Server enabled.");
					});
				serverOffButton = new Button("Disable Server");
					serverOffButton.setOnAction(f->{
						serverConnection.isRunning = false;
						serverOnButton.setDisable(false);
						serverOffButton.setDisable(true);
						listItems.getItems().add("Server disabled.");
					});

				onOffBox = new HBox(serverOnButton, serverOffButton);
				onOffBox.setAlignment(Pos.BOTTOM_CENTER);
				
				pane.setTop(numClientsText);
				pane.setCenter(listItems);
				pane.setBottom(onOffBox);

				serverScene = new Scene(pane, 750, 750);

				primaryStage.setScene(serverScene);
				primaryStage.setTitle("Server");
				primaryStage.show();
			}

			
		});
		
		
		
	}

}
