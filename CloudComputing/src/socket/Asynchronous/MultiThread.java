package socket.Asynchronous;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class MultiThread extends Thread{

    private static final String DEFAULT_FILE_PATH = "index.html";
    private static final String REQUEST_FILE_PATH = "process.html";

    private Socket connectionSocket;

    public MultiThread(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run(){
        System.out.println("WebServer Thread Created");

        BufferedReader inFromClient = null;
        DataOutputStream outToClient = null;

        try {

            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            String requestMessageLine = inFromClient.readLine();

            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);

            //System.out.println(tokenizedLine.nextToken());
            String firstToken = tokenizedLine.nextToken();

            if (firstToken.equals("GET")) {

                String fileName = tokenizedLine.nextToken();

                if (fileName.startsWith("/") == true) {
                    if (fileName.length() > 1) {
                        fileName = fileName.substring(1);
                    } else {
                        fileName = DEFAULT_FILE_PATH;
                    }
                }
                File file = new File(fileName);

                if (file.exists()) {
                    int numOfBytes = (int) file.length();

                    FileInputStream inFile = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);

                    outToClient.writeBytes("HTTP/1.0 200 Document Follows \r\n");
                    outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    outToClient.writeBytes("\r\n");

                    outToClient.write(fileInBytes, 0, numOfBytes);
                }
            }

            if (firstToken.equals("POST")) {

                String fileName = REQUEST_FILE_PATH;

                File file = new File(fileName);

                if (file.exists()) {
                    int numOfBytes = (int) file.length();

                    FileInputStream inFile = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);

                    outToClient.writeBytes("HTTP/1.0 200 Document Follows \r\n");
                    outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    outToClient.writeBytes("\r\n");
                    outToClient.write(fileInBytes, 0, numOfBytes);
                }
            }
            connectionSocket.close();
            System.out.println("Connection Closed");
        }
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}