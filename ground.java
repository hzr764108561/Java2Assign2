
import javafx.application.Platform;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 客户端UI
 * @author 花大侠
 *
 */
public class ground extends Application{
  TextField name = new TextField();
  final TextField ipText = new TextField();
  final TextField portText = new TextField();
  final Button sendButton = new Button(" login ");

  @Override
  public void start(Stage primaryStage) throws Exception {

    //左边 IPAdress+Port
    GridPane leftPane1 = new GridPane();
    leftPane1.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
    leftPane1.setHgap(5.5);
    leftPane1.setVgap(5.5);
    leftPane1.add(new Label("IPAdress:"), 0, 0);
    ipText.setEditable(false);
    leftPane1.add(ipText, 1, 0);
    leftPane1.add(new Label("Port:"), 0, 1);
    portText.setEditable(false);
    leftPane1.add(portText, 1, 1);
    leftPane1.add(new Label("Name:"), 0, 2);
    portText.setEditable(true);
    leftPane1.add(name, 1, 2);

    //左边 Connect Status + button
    GridPane leftPane3 = new GridPane();
    leftPane3.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
    leftPane3.setHgap(5.5);
    leftPane3.setVgap(5.5);
    leftPane3.add(sendButton, 0, 0);

    //组合
    VBox vBox = new VBox();
    vBox.getChildren().addAll(leftPane1,leftPane3);
    HBox hBox = new HBox();
    hBox.getChildren().addAll(vBox);

    Scene scene = new Scene(hBox);
    primaryStage.setTitle("client");
    primaryStage.setScene(scene);
    //关闭UI线程时同时关闭各子线程
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent event) {
        System.exit(0);
      }
    });
    primaryStage.show();

    //启动Client线程
    sendButton.setOnAction(e->{
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          new Thread(new users(ipText, portText,name,sendButton)).start();//更新JavaFX的主线程的代码放在此处
        }
      });

    });
  }
}