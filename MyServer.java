
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MyServer implements Runnable {

    //Server端监听的端口号
    public static final int PORT = 9999;
    //映射表 存放每个socket地址(IP:Port)及其对应的PrintWriter
    //为群发消息做准备
    Map<String, PrintWriter> map = new HashMap<>();
    Map<String ,String> battle= new HashMap<>();;
    List<String> wait = new ArrayList<>();
    //存放已连接socket地址(IP:Port)，用于clientListView

    Button sendButton;
    TextArea receivedMsgArea;

    public MyServer() {

    }

    public MyServer(Button sendButton, TextArea receivedMsgArea) {
        super();
        this.sendButton = sendButton;
        this.receivedMsgArea = receivedMsgArea;
    }

    /**
     * 更新UI界面的IP和Port
     */
    public void updateIpAndPort() {
        //用于在非UI线程更新UI界面
    }

    @Override
    public void run() {
        updateIpAndPort();
        ServerSocket server;
        Socket socket;
        try {
            server = new ServerSocket(PORT);
            while(true) {
                socket = server.accept();
                //一个客户端接入就启动一个handler线程去处理
                new Thread(new handler(map, socket,sendButton, receivedMsgArea,battle,wait)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
