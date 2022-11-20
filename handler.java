
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * 处理线程
 * @author 花大侠
 *
 */
public class handler implements Runnable {

  Socket socket;
  TextArea sendMsgArea;
  TextField statusText;
  Button sendButton;
  TextArea receivedMsgArea;
  ObservableList<String> clients;
  ListView<String> clientListView;
  Map<String, PrintWriter> map;
  Map<String ,String> battle;
  List<String> wait;
  List<Integer> mychess = new ArrayList<>();

  public handler() {
    super();
  }

  public handler(Map<String, PrintWriter> map, Socket socket, TextArea sendMsgArea, TextField statusText, Button sendButton,
                 TextArea receivedMsgArea, ObservableList<String> clients, ListView<String> clientListView,Map<String ,String> battle,List<String> wait) {
    super();
    this.map = map;
    this.socket = socket;
    this.sendMsgArea = sendMsgArea;
    this.statusText = statusText;
    this.sendButton = sendButton;
    this.receivedMsgArea = receivedMsgArea;
    this.clients = clients;
    this.clientListView = clientListView;
    this.battle = battle;
    this.wait = wait;
  }

  /**
   * 接入客户端后，更新UI界面
   * 1.添加新接入客户端的地址信息
   * 2.receivedMsgarea打印成功连接信息
   * 3.statusText更新成功连接个数
   */
  public void updateForConnect(String remoteSocketAddress) {
    Platform.runLater(()->{
      clients.add(remoteSocketAddress);
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      receivedMsgArea.appendText(String.valueOf(clients.size()) + " Connected from " + remoteSocketAddress + " " + sdf.format(new Date()) + "\n");
      statusText.setText(String.valueOf(clients.size()) + " Connect success.");
    });
  }

  /**
   * 断开客户端后，更新UI界面
   * 1.移除断开客户端的地址信息
   * 2.receivedMsgarea打印断开连接信息
   * 3.statusText更新成功连接个数
   * 4.移除map中对应的remoteSocketAddress
   */
  public void updateForDisConnect(String remoteSocketAddress) {
    Platform.runLater(()->{
      clients.remove(remoteSocketAddress);
      statusText.setText(String.valueOf(clients.size()) + " Connect success.");
      receivedMsgArea.appendText(remoteSocketAddress + " out of connected.." + "\n");
      map.remove(remoteSocketAddress);
    });
  }

  /**
   * 单发及群发消息
   * 1.为clientListView设置监听器
   *   1.1获取已选择的项(IP:Port)
   *   1.2从映射表中取出对应printWriter放入printWriters集合
   * 2.为sendButton设置鼠标点击事件
   *   2.1遍历printWriters集合
   *   2.2写入待发送的消息
   */
  public void sendMessage() {
    Set<PrintWriter> printWriters = new HashSet<>();
    clientListView.getSelectionModel().selectedItemProperty().addListener(ov->{
      printWriters.clear();
      for(String key: clientListView.getSelectionModel().getSelectedItems()) {
        printWriters.add(map.get(key));
      }
    });
    sendButton.setOnAction(e->{
      for (PrintWriter printWriter : printWriters) {
        printWriter.write("127.0.0.1:9999" + "  " + sendMsgArea.getText() + "\r\n");
        printWriter.flush();
      }
    });
  }

  public void booleanEnd(String[] list){
    if (mychess.size()>=3) {
      if (mychess.contains(1)){
        if (mychess.contains(4)){
          if (mychess.contains(7)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
      }
      if (mychess.contains(3)){
        if (mychess.contains(4)){
          if (mychess.contains(5)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
      }
      if (mychess.contains(6)){
        if (mychess.contains(7)){
          if (mychess.contains(8)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
      }
      if (mychess.contains(0)) {
        if (mychess.contains(1)) {
          if (mychess.contains(2)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
        if (mychess.contains(3)) {
          if (mychess.contains(6)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
        if (mychess.contains(4)) {
          if (mychess.contains(8)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
      }
      if (mychess.contains(2)) {
        if (mychess.contains(5)) {
          if (mychess.contains(8)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
        if (mychess.contains(4)) {
          if (mychess.contains(6)) {
            list[1] = "w";
            sendMessages(list, "End");
          }
        }
      }
      if (mychess.size() == 5) {
        list[1] = "d";
        sendMessages(list, "End");
      }
    }
  }

  public void sendMessages(String[] list, String status){
    List<PrintWriter> printWriters = new ArrayList<>();
    if (status.equals("Start")){
      System.out.println(list[0]);
      for (String key:list){
        printWriters.add(map.get(key));
      }
      printWriters.get(0).write("2o" + "\r\n");
      printWriters.get(0).flush();
      printWriters.get(1).write("2x" + "\r\n");
      printWriters.get(1).flush();
    }
    else if (status.equals("Step")){
      printWriters.add(map.get(battle.get(list[0])));
      printWriters.get(0).write("1" +list[1]+ "\r\n");
      printWriters.get(0).flush();
      mychess.add(Integer.parseInt(list[1]));
      booleanEnd(list);
    }
    else if (status.equals("End")){
      printWriters.add(map.get(battle.get(list[0])));
      printWriters.add(map.get(list[0]));
      if (list[1] .equals("d") ) {
        printWriters.get(0).write("0" + list[1] + "\r\n");
        printWriters.get(0).flush();
        printWriters.get(1).write("0" + list[1] + "\r\n");
        printWriters.get(1).flush();
      }
      else {
        printWriters.get(0).write("0" + "l" + "\r\n");
        printWriters.get(0).flush();
        printWriters.get(1).write("0" + "w" + "\r\n");
        printWriters.get(1).flush();
        String z = battle.get(list[0]);
        battle.remove(list[0]);
        battle.remove(z);
      }
    }
    else if (status.equals("clear")){
      mychess.clear();
    }
    else {
      if (battle.containsKey(list[0])){
        printWriters.add(map.get(battle.get(list[0])));
        printWriters.get(0).write("3"  + "\r\n");
        printWriters.get(0).flush();
        String z = battle.get(list[0]);
        battle.remove(list[0]);
        battle.remove(z);
      }
      if (wait.contains(list[0])){
        wait.remove(list[0]);
      }
    }
  }

  public void back(String message) throws IOException {
    String[] back = message.split(",");
    if (back[1] .equals("wait")){
      mychess.clear();
      if (wait.size()!=0){
        battle.put(back[0],wait.get(0));
        battle.put(wait.get(0),back[0]);
        String a = wait.get(0);
        wait.remove(0);
        sendMessages(new String[]{a, back[0]},"Start");
      }
      else {
        wait.add(back[0]);
      }
    }
    else if(back[1] .equals("step")){
      sendMessages(new String[]{back[0], back[2]},"Step");
    }
    else {
      sendMessages(new String[]{back[0]},"close");
      socket.close();
    }
  }

  @Override
  public void run() {
    String remoteSocketAddress = socket.getRemoteSocketAddress().toString().substring(1);
    updateForConnect(remoteSocketAddress);
    try {
      InputStream in = socket.getInputStream();
      BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
      OutputStream out = socket.getOutputStream();
      PrintWriter pWriter = new PrintWriter(out);
      map.put(remoteSocketAddress, pWriter);
      //发消息
      sendMessage();
      //收消息
      String message;
      while(true) {
        message = bReader.readLine();
        receivedMsgArea.appendText(message + "\n");
        back(message);
      }
    } catch (IOException e) {
      updateForDisConnect(remoteSocketAddress);
    }
  }

}