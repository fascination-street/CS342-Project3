import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;


public class JavaFXTemplate extends Application {

	TextField portField, IPField,
	          anteWagerField, ppWagerField;
	Text welcome,
		 c1, c2, c3,
	     d1, d2, d3,
		 ante, pp, play, cash, dealerCardLabel,
		 endScreenWinLoseText, endScreenWinLossMoney;
	Button b1, startButton,
		   dealButton, playButton, foldButton,
		   endGame, returnToGameButton, confirmButton;
	Client clientConnection;
	Scene startScene, startClientScene, clientScene, endGameScene;
	BorderPane startPane;
	VBox clientBox, littleDebugBox, endScreenClientBox;
	HBox startRoot, wagerFieldBox, cardButtonBox,
		 playerCardText, listBox, playerCardBox, dealerCardBox,
		 endScreenButtonsBox, endScreenTextBox;

	MenuBar menuBar;
	Menu options;
	MenuItem exit, freshStart, newLook;

	static PokerInfo clientPokerInfo;
	int roundCount = 1;

	ListView<String> listItems2;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	private void sendToEndScreen(Stage primaryStage) {
		if (clientPokerInfo.playerWon) {
			endScreenWinLoseText.setText("You win!\nPlay again?");
			endScreenWinLossMoney.setText("You earned $" + clientPokerInfo.winningsThisRound);
			endScreenClientBox.setStyle("-fx-background-color: #17B169");
		}
		else {
			endScreenWinLossMoney.setText("You lost $" + clientPokerInfo.winningsThisRound);
			endScreenWinLoseText.setText("You lost...\nPlay again?");
			endScreenClientBox.setStyle("-fx-background-color: #C60C30");
		}
		primaryStage.setScene(endGameScene);
		primaryStage.show();
	}

	private void returnToGame(Stage primaryStage) {
		dealButton.setDisable(false);
		playButton.setDisable(true);
		foldButton.setDisable(true);

		c1.setText("");
		c2.setText("");
		c3.setText("");
		d1.setText("");
		d2.setText("");
		d3.setText("");
		dealerCardLabel.setVisible(false);

		clientPokerInfo.play = 0;
		play.setText("Play: $0");

		anteWagerField.setDisable(false);
		ppWagerField.setDisable(false);

		roundCount++;
		listItems2.getItems().add("ROUND " + roundCount);

		clientPokerInfo.newRound = true;
		clientPokerInfo.buttonPressed = -1;
		clientConnection.send(clientPokerInfo);

		clientPokerInfo.newRound = false;

		confirmButton.setVisible(false);
		primaryStage.setScene(clientScene);
		primaryStage.show();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		//Pretty much everything here should end up being adapted to the Poker game I made in Project 2
		//sets up start menu
		BorderPane startPane = new BorderPane();
		startPane.setStyle("-fx-background-color: linear-gradient(to bottom, #42c9e4, #090979);");
		portField = new TextField("5555");
		IPField = new TextField("127.0.0.1"); 
		startButton = new Button("Connect");
		endGame = new Button("Exit");
		welcome = new Text("WELCOME TO POKER!!!!");
			welcome.setStyle("-fx-font-size: 30px; -fx-font-weight: Bold;");

		portField.setPrefWidth(65);
		IPField.setPrefWidth(150);
		startRoot = new HBox(IPField, portField, startButton);
		startRoot.setAlignment(Pos.CENTER);
		VBox starterRoot = new VBox(100,welcome, startRoot);
		starterRoot.setAlignment(Pos.CENTER);

		startPane.setCenter(starterRoot);

		startClientScene = new Scene(startPane, 500, 400);

		primaryStage.setScene(startClientScene);
		primaryStage.setTitle("Client");
		primaryStage.show();
		//

		startButton.setOnAction(e->{
			

			clientConnection = new Client(data->{
				Platform.runLater(()->{listItems2.getItems().add(data.toString());
				});
			}, IPField.getText(), portField.getText());

			if (clientConnection.port < 0) {
				portField.setText("Invalid");
			}
			else {
				clientConnection.start();
				primaryStage.setTitle("client");
				primaryStage.setScene(clientScene);
				primaryStage.show();
			}
		});

		endGame.setOnAction(e->{
			Platform.exit();
		});

		//EVERYTHING HERE BELONGS TO THE CLIENTSCENE, AKA MAIN GAMEPLAY// 
		{
			 
			clientPokerInfo = new PokerInfo(0, 0, 100);
				//clientConnection.start();
				
				listItems2 = new ListView<String>();
				listItems2.setPrefWidth(375);
				
				anteWagerField = new TextField("5");
					anteWagerField.setPrefWidth(50);
				ppWagerField = new TextField("0");
					ppWagerField.setPrefWidth(50);

				wagerFieldBox = new HBox(anteWagerField, ppWagerField);
					wagerFieldBox.setAlignment(Pos.BOTTOM_CENTER);

				c1 = new Text("");
					c1.setStyle("-fx-font-size: 20px");
				c2 = new Text("");
					c2.setStyle("-fx-font-size: 20px");
				c3 = new Text("");
					c3.setStyle("-fx-font-size: 20px");

				playerCardBox = new HBox(10,c1, c2, c3);
					playerCardBox.setAlignment(Pos.BOTTOM_CENTER);

				d1 = new Text("");
					d1.setStyle("-fx-font-size: 20px");
				d2 = new Text("");
					d2.setStyle("-fx-font-size: 20px");
				d3 = new Text("");
					d3.setStyle("-fx-font-size: 20px");
				dealerCardLabel = new Text("Dealer Cards: ");
					dealerCardLabel.setStyle("-fx-font-size: 20px");
					dealerCardLabel.setVisible(false);
				dealerCardBox = new HBox(10, dealerCardLabel, d1, d2, d3);
					dealerCardBox.setAlignment(Pos.TOP_CENTER);
				
				cash = new Text("$" + clientPokerInfo.cash);
				ante = new Text("Ante: $0");
				pp = new Text("Pair Plus: $0");
				play = new Text("Play: $0");

				HBox betsBox = new HBox(10, ante, pp, play);
				betsBox.setAlignment(Pos.BOTTOM_CENTER);

				VBox moneyBox = new VBox(cash, betsBox);
				moneyBox.setAlignment(Pos.BOTTOM_CENTER);
				moneyBox.setStyle("-fx-font-size: 18px");

				playButton = new Button("Play");
					playButton.setOnAction(d->{
						try {
							clientConnection.out.reset();
							clientPokerInfo.play = clientPokerInfo.ante;
							listItems2.getItems().add("Player plays; bets another $" + clientPokerInfo.play);
						}
						catch (IOException a) {}
						clientPokerInfo.buttonPressed = 1;
						
						clientConnection.send(clientPokerInfo);

						while (clientPokerInfo.playOver == false) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException m) {
								m.printStackTrace();
							}
						}

						Platform.runLater(()->{
							clientPokerInfo.playOver = false;
							d1.setText(clientPokerInfo.dCard1);
							d2.setText(clientPokerInfo.dCard2);
							d3.setText(clientPokerInfo.dCard3);
							dealerCardLabel.setVisible(true);

							cash.setText("$" + clientPokerInfo.cash);
							play.setText("Play: $" + clientPokerInfo.play);
							//dealButton.setDisable(false);
							playButton.setDisable(true);
							foldButton.setDisable(true);



						

							confirmButton.setVisible(true);
						});
						
						
					});
				dealButton = new Button("Deal");
					dealButton.setOnAction(b->{
						try {
							clientConnection.out.reset();
							clientPokerInfo.ante = Integer.parseInt(anteWagerField.getText());
							clientPokerInfo.pairPlus = Integer.parseInt(ppWagerField.getText());

							anteWagerField.setDisable(true);
							ppWagerField.setDisable(true);
							//System.out.println("good?");
						}
						catch (NumberFormatException n) {
							System.out.println("bad number");
							return;
						}
						catch (IOException io) {}
						
						clientPokerInfo.buttonPressed = 0;
						clientConnection.send(clientPokerInfo);

						while (clientPokerInfo.card1 == null || clientPokerInfo.card2 == null || clientPokerInfo.card3 == null) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException m) {
								m.printStackTrace();
							}
						}
						
						Platform.runLater(() -> {
							listItems2.getItems().add("Player bets $" + clientPokerInfo.ante + " for ante and $" + clientPokerInfo.pairPlus + " for pair plus.");
							c1.setText(clientPokerInfo.card1);
							c2.setText(clientPokerInfo.card2);
							c3.setText(clientPokerInfo.card3);

							ante.setText("Ante: $" + clientPokerInfo.ante);
							pp.setText("Pair Plus: $" + clientPokerInfo.pairPlus);
							cash.setText("$" + clientPokerInfo.cash);
							
							dealButton.setDisable(true);
							playButton.setDisable(false);
							foldButton.setDisable(false);
						});
						
					});
				foldButton = new Button("Fold");
					foldButton.setOnAction(c->{
						try {clientConnection.out.reset();}
						catch (IOException a) {}
						clientPokerInfo.buttonPressed = 2;
						clientConnection.send(clientPokerInfo);
						//dealButton.setDisable(false);
						playButton.setDisable(true);
						foldButton.setDisable(true);
						listItems2.getItems().add("Player folds.");

						confirmButton.setVisible(true);
					});
				
				dealButton.setDisable(false);
					dealButton.setPrefSize(85,45);
					dealButton.setStyle("-fx-font-size: 18px; -fx-font-weight: Bold");
				playButton.setDisable(true);
					playButton.setPrefSize(85,45);
					playButton.setStyle("-fx-font-size: 18px; -fx-font-weight: Bold");
				foldButton.setDisable(true);
					foldButton.setPrefSize(85,45);
					foldButton.setStyle("-fx-font-size: 18px; -fx-font-weight: Bold");

				confirmButton = new Button("Confirm");
					confirmButton.setOnAction(c->{
						sendToEndScreen(primaryStage);
					});
					confirmButton.setVisible(false);
					confirmButton.setAlignment(Pos.BOTTOM_CENTER);

				cardButtonBox = new HBox(dealButton, playButton, foldButton);
					cardButtonBox.setAlignment(Pos.BOTTOM_CENTER);
				
				VBox allButtonsBox = new VBox(cardButtonBox, confirmButton);
					allButtonsBox.setAlignment(Pos.BOTTOM_CENTER);
				listBox = new HBox(listItems2);
					listBox.setAlignment(Pos.CENTER);
				
				exit = new MenuItem("Exit");
					exit.setOnAction(e->{
						Platform.exit();
					});
				freshStart = new MenuItem("Fresh Start");
					freshStart.setOnAction(e->{
						clientPokerInfo = new PokerInfo();
						roundCount = 0;
						listItems2.getItems().clear();
						cash.setText("$" + clientPokerInfo.cash);
						ante.setText("Ante: $0");
						pp.setText("Pair Plus: $0");
						play.setText("Play: $0");
						returnToGame(primaryStage);
					});
				newLook = new MenuItem("New Look");
					newLook.setOnAction(e->{
						clientBox.setStyle("-fx-background-color: pink; -fx-font-weight: Bold;");
					});

				options = new Menu("Options",null, exit, freshStart, newLook);
				menuBar = new MenuBar(options);
				clientBox = new VBox(25, menuBar, dealerCardBox, listBox, playerCardBox, wagerFieldBox, allButtonsBox, moneyBox);
				clientBox.setStyle("-fx-background-color: green;");

				//clientBox.setAlignment(Pos.BOTTOM_LEFT);

				clientScene = new Scene(clientBox, 800, 800);
				
			
			
			
		
		}
		//END OF CLIENT SCREEN STUFF
		
		//WIN/LOSS SCREEN STUFF
		returnToGameButton = new Button("Return");
			returnToGameButton.setOnAction(e->{
				returnToGame(primaryStage);
			});
			returnToGameButton.setPrefSize(100,70);

		endGame.setPrefSize(100, 70);
		endScreenButtonsBox = new HBox(50,returnToGameButton, endGame);
			endScreenButtonsBox.setPrefSize(100, 70);
			endScreenButtonsBox.setAlignment(Pos.CENTER);
			//This should hold more info 
		
		endScreenWinLoseText = new Text();
			endScreenWinLoseText.setStyle("-fx-font-size: 20px;");
		endScreenWinLossMoney = new Text();
			endScreenWinLossMoney.setStyle("-fx-font-size: 20px;");

		endScreenTextBox = new HBox(50,endScreenWinLoseText, endScreenWinLossMoney);
		endScreenTextBox.setAlignment(Pos.CENTER);

		endScreenClientBox = new VBox(endScreenTextBox,endScreenButtonsBox);
			endScreenClientBox.setAlignment(Pos.CENTER);
			

		endGameScene = new Scene(endScreenClientBox, 500, 200);
	}

}
