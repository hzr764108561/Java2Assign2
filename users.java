import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class users extends Application implements Runnable {
  TextArea receivedMsgArea;
  TextField ipText;
  TextField portText;
  TextField statusText;
  Button sendButton;
  TextField name;
  String GameStatus = "end";
  Stage stage;
  ObservableList<String> clients = FXCollections.observableArrayList();
  ListView<String> clientListView = new ListView<>(clients);

  public users() {
    super();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Group rootNode = new Group();
    Line[] linels = new Line[4];
    linels[0] = new Line(200, 0, 200, 600);
    linels[1] = new Line(400, 0, 400, 600);
    linels[2] = new Line(0, 200, 600, 200);
    linels[3] = new Line(0, 400, 600, 400);
    for (int i = 0; i < linels.length; i++) {
      linels[i].setFill(Color.WHITE);
      linels[i].setStroke(Color.BLACK);
      linels[i].setStrokeWidth(2);
    }
    primaryStage.setTitle(name.getText());
    rootNode.getChildren().setAll(linels);
    GridPane leftPane2 = new GridPane();
    leftPane2.setPadding(new Insets(11.5, 700, 20, 610));
    leftPane2.setHgap(5.5);
    leftPane2.setVgap(5.5);
    leftPane2.add(new Label("Choose Client:"), 0, 0);
    clientListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    clientListView.setMaxHeight(80);
    clientListView.setMaxWidth(150);
    leftPane2.add(clientListView, 0, 1);
    //左边 Send Message

    rootNode.getChildren().add(leftPane2);


    Button btn = new Button();
    btn.setText("Start");
    btn.setMinSize(50, 20);
    btn.setTranslateX(610);
    btn.setTranslateY(180);
    rootNode.getChildren().add(btn);
    Button freshen = new Button();
    freshen.setText("freshen");
    freshen.setMinSize(20, 20);
    freshen.setTranslateX(610);
    freshen.setTranslateY(130);
    rootNode.getChildren().add(freshen);
    Button match = new Button();
    match.setText("Match");
    match.setMinSize(20, 20);
    match.setTranslateX(680);
    match.setTranslateY(180);
    rootNode.getChildren().add(match);
    List<Button> grids = new ArrayList<>();
    List<Grid> chess = new ArrayList<>();
    addBoard(rootNode, grids, chess);
    Text t = new Text(600, 350, "end");
    t.setVisible(true);
    rootNode.getChildren().add(t);
    Text t2 = new Text(600, 390, "");
    t2.setVisible(true);
    rootNode.getChildren().add(t2);
    primaryStage.setScene(new Scene(rootNode, 800, 600));
    try {
      Socket socket = new Socket("LocalHost", 9999);
      stage.close();
      primaryStage.show();
      Thread a = new Thread(new links(socket, ipText, portText, grids, btn, GameStatus, chess,
              t, t2, primaryStage, name, freshen, match, clients, clientListView));
      a.start();
      OutputStream out = socket.getOutputStream();
      PrintWriter pWriter = new PrintWriter(out);
      primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
          a.stop();
          pWriter.write(socket.getLocalSocketAddress().toString().substring(1)
                  + "," + "close" + "," + "0" + "\r\n");
          pWriter.flush();
        }
      });
    } catch (IOException e) {
      System.out.println("serve is not open");
    }
  }

  public void addBoard(Group group, List<Button> grid, List<Grid> chess) {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        Grid a = new Grid();
        chess.add(a);
        Button btn = new Button();
        btn.setText("");
        btn.setMinSize(200, 200);
        btn.setTranslateX(i * 200);
        btn.setTranslateY(j * 200);
        btn.setStyle("-fx-background-color: transparent;-fx-font-size:"
                +" 12em;-fx-text-alignment:center");
        group.getChildren().add(btn);
        grid.add(btn);
      }
    }
  }
  public users(Stage stage, TextField ipText, TextField portText,
               TextField Name, Button sendButton) {
    super();
    this.ipText = ipText;
    this.portText = portText;
    this.sendButton = sendButton;
    this.name = Name;
    this.stage = stage;
  }

  public void run() {
      Platform.runLater(() -> {
        users users = new users(stage, ipText, portText, name, sendButton);
        try {
          users.start(new Stage());
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
  }
}

class Grid {
  boolean has_chess = false;
}
