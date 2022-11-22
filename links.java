import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class links implements Runnable {
  TextField ipText;
  TextField portText;
  TextField statusText;
  Button sendButton;
  Button match;
  Button freshen;
  ObservableList<String> clients;
  ListView<String> clientListView;
  List<Button> grids;
  String GameStatus;
  Boolean myTurn = false;
  Boolean oOrX = true;
  List<Grid> chess;
  Text t;
  Text t2;
  Stage stage;
  Socket socket;
  TextField name;

  public links() {
    super();
  }

  public links(Socket socket, TextField ipText, TextField portText, List<Button> grids, Button sendButton, String GameStatus, List<Grid> chess, Text t,Text t2,Stage stage, TextField name
                ,Button freshen, Button match, ObservableList<String> clients, ListView<String> clientListView) {
    super();
    this.ipText = ipText;
    this.portText = portText;
    this.sendButton = sendButton;
    this.grids = grids;
    this.GameStatus = GameStatus;
    this.chess = chess;
    this.t = t;
    this.t2 = t2;
    this.stage = stage;
    this.socket = socket;
    this.name = name;
    this.freshen = freshen;
    this.match = match;
    this.clients = clients;
    this.clientListView = clientListView;
  }

  /**
   * 更新UI界面的IP和端口
   */
  public void updateIpAndPort(Socket socket) {
    Platform.runLater(()->{
      ipText.setText(socket.getLocalAddress().toString().substring(1));
      portText.setText(String.valueOf(socket.getLocalPort()));
    });
  }

  public void update(Boolean a,Button button){
    if (a){
      button.setText("O");
    }
    else {
      button.setText("X");
    }
  }

  public void back(String back) throws IOException {
    if (back.charAt(0) == '0'&&GameStatus.equals("Start")){
      GameStatus = "end";
      if (back.charAt(1) == 'w'){
        t.setText("win");
        t2.setText("");
        myTurn = false;
      }
      else if (back.charAt(1) == 'l'){
        t.setText("loss");
        t2.setText("");
        myTurn = false;
      }
      else {
        t.setText("draw");
        t2.setText("");
        myTurn = false;
      }
    }
    else if (back.charAt(0) == '1'&&GameStatus.equals("Start")) {
      char a = back.charAt(1);
      int temp_int=a-'0';
      chess.get(temp_int).has_chess = true;
      update(!oOrX,grids.get(temp_int));
      myTurn = true;
      t2.setText("Your turn");
    }
    else if (back.charAt(0) == '2'&&GameStatus.equals("Wait")){
      char a = back.charAt(1);
      String[] player = back.split(",");
      GameStatus = "Start";
      t.setText("Start with " + player[1]);
      if (a == 'o'){
        t2.setText("Your turn");
        myTurn = true;
      }
      else {
        myTurn = false;
        oOrX = false;
      }
    }
    else {
      t.setText("your opponent is disconnected, so you win");
      myTurn = false;
      GameStatus = "end";
    }
  }

  @Override
  public void run() {
    try {
      updateIpAndPort(socket);
      InputStream in = socket.getInputStream();
      BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
      OutputStream out = socket.getOutputStream();
      PrintWriter pWriter = new PrintWriter(out);
      //发消息
      for (int i = 0; i < grids.size(); i++) {
        int a = i;
        grids.get(i).setOnAction(e->{
          boolean z = chess.get(a).has_chess;
          if (myTurn&&!z){
            myTurn = false;
            System.out.println(a);
            update(oOrX,grids.get(a));
            chess.get(a).has_chess = true;
            String s = String.valueOf(a);
            pWriter.write(socket.getLocalSocketAddress().toString().substring(1) + "," + "step"+","+s+ "\r\n");
            pWriter.flush();
            t2.setText("");
          }
        });
      }
      sendButton.setOnAction(e->{
        if (GameStatus.equals("end")) {
          oOrX = true;
          myTurn = false;
          for (int i = 0; i < grids.size(); i++) {
            grids.get(i).setText("");
            chess.get(i).has_chess = false;
          }
          t.setText("wait");
          GameStatus = "Wait";
          pWriter.write(socket.getLocalSocketAddress().toString().substring(1) + "," + "wait" + "," + "0" +"," +name.getText()+ "\r\n");
          pWriter.flush();
        }
      });
      //收消息
      String message;
      while(true) {
        message = bReader.readLine();
        String finalMessage = message;
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            try {
              back(finalMessage);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }

        });
      }

    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("No serve");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
