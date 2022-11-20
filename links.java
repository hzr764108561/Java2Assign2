import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import javafx.scene.text.Text;
import javafx.stage.WindowEvent;

/**
 * 客户端
 * @author 花大侠
 *
 */
public class links implements Runnable {
  TextField ipText;
  TextField portText;
  TextField statusText;
  Button sendButton;
  List<Button> grids;
  String GameStatus;
  Boolean myTurn = false;
  Boolean oOrX = true;
  List<Grid> chess;
  ListView<String> clientListView;
  Text t;
  Text t2;
  Stage stage;
  Socket socket;
  TextField name;

  public links() {
    super();
  }

  public links(Socket socket, TextField ipText, TextField portText, List<Button> grids, Button sendButton, String GameStatus, List<Grid> chess, Text t,Text t2,Stage stage, TextField name) {
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
      }
      else if (back.charAt(1) == 'l'){
        t.setText("loss");
      }
      else {
        t.setText("draw");
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
      GameStatus = "Start";
      t.setText("Start");
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
          pWriter.write(socket.getLocalSocketAddress().toString().substring(1) + "," + "wait" + "," + "0" +"," +name+ "\r\n");
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