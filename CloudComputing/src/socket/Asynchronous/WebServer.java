package socket.Asynchronous;

import java.net.ServerSocket;
import java.net.Socket;

class WebServer{
    public static void main(String argv[]) throws Exception {

        ServerSocket listenSocket = new ServerSocket(8085);
        System.out.println("WebServer Socket Created");

        Socket connectionSocket;
        MultiThread serverThread;

        while((connectionSocket = listenSocket.accept()) != null){

            try {
                serverThread = new MultiThread(connectionSocket);
                serverThread.start();
            }catch (Exception e){
                throw e;
            }
        }
    }
}
