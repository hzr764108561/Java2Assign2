
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ServerUI extends Application{

  TextArea receivedMsgArea = new TextArea();
  Button sendButton = new Button(" Send ");

  public void start(Stage primaryStage) throws Exception {

    //右边 Received Message
    GridPane rightPane = new GridPane();
    rightPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
    rightPane.setHgap(5.5);
    rightPane.setVgap(5.5);
    rightPane.add(new Label("Received Message:"), 0, 0);
    receivedMsgArea.setWrapText(true);
    receivedMsgArea.setEditable(false);
    receivedMsgArea.setMaxWidth(350);
    receivedMsgArea.setPrefHeight(410);
    rightPane.add(receivedMsgArea, 0, 1);
    VBox vBox = new VBox();
    HBox hBox = new HBox();
    hBox.getChildren().addAll(vBox, rightPane);

    Scene scene = new Scene(hBox);
    primaryStage.setTitle("server");
    primaryStage.setScene(scene);
    //关闭UI线程时同时关闭各子线程
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        System.exit(0);
      }
    });
    primaryStage.show();
    //启动server线程
    new Thread(new MyServer(sendButton, receivedMsgArea)).start();
  }

}
