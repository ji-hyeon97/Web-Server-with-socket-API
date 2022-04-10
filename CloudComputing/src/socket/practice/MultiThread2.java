package socket.practice;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class MultiThread2 extends Thread{
    // 파일 요청이 없을 경우의 기본 파일
    private static final String DEFAULT_FILE_PATH = "index.html";
    private static final String REQUEST_FILE_PATH = "process.html";

    // 클라이언트와의 접속 소켓
    private Socket connectionSocket;

    // @param connectionSocket 클라이언트와의 통신을 위한 소켓
    public MultiThread2(Socket connectionSocket){
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run(){
        System.out.println("WebServer Thread Created");
        BufferedReader inFromClient = null;
        DataOutputStream outToClient = null;

        try{
            // 클라이언트와 통신을 위한 입/출력 2개의 스트림을 생성한다.
            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            // 클라이언트로의 메시지중 첫번째 줄을 읽어들인다.
            String requestMessageLine = inFromClient.readLine();
            System.out.println(requestMessageLine);

            // 파싱을 위한 토큰을 생성한다.

            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine);
            // 첫번째 토큰이 GET 으로 시작하는가? ex) GET /green.jpg

            //System.out.println(tokenizedLine.nextToken());
            String firstToken = tokenizedLine.nextToken();

            if(firstToken.equals("GET")) {
                // 다음의 토큰은 파일명이다.
                String fileName = tokenizedLine.nextToken();
                // 기본적으로 루트(/)로부터 주소가 시작하므로 제거한다.
                if (fileName.startsWith("/") == true) {
                    if (fileName.length() > 1) {
                        fileName = fileName.substring(1);
                    }
                    // 파일명을 따로 입력하지 않았을 경우 기본 파일을 출력한다.
                    else {
                        fileName = DEFAULT_FILE_PATH;
                    }
                }
                File file = new File(fileName);

                // 요청한 파일이 존재하는가?
                if (file.exists()) {
                    // 파일의 바이트수를 찾아온다.
                    int numOfBytes = (int) file.length();

                    // 파일을 스트림을 읽어들일 준비를 한다.
                    FileInputStream inFile = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);

                    // 정상적으로 처리가 되었음을 나타내는 200 코드를 출력한다.
                    outToClient.writeBytes("HTTP/1.0 200 Document Follows \r\n");

                    // 출력할 컨텐츠의 길이를 출력
                    outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    outToClient.writeBytes("\r\n");

                    // 요청 파일을 출력한다.
                    outToClient.write(fileInBytes, 0, numOfBytes);
                }
            }

            if(firstToken.equals("POST")) {
                // 다음의 토큰은 파일명이다.
                String fileName = REQUEST_FILE_PATH;

                File file = new File(fileName);

                // 요청한 파일이 존재하는가?
                if (file.exists()) {
                    // 파일의 바이트수를 찾아온다.
                    int numOfBytes = (int) file.length();

                    // 파일을 스트림을 읽어들일 준비를 한다.
                    FileInputStream inFile = new FileInputStream(fileName);
                    byte[] fileInBytes = new byte[numOfBytes];
                    inFile.read(fileInBytes);

                    // 정상적으로 처리가 되었음을 나타내는 200 코드를 출력한다.
                    outToClient.writeBytes("HTTP/1.0 200 Document Follows \r\n");

                    // 출력할 컨텐츠의 길이를 출력
                    outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
                    outToClient.writeBytes("\r\n");

                    // 요청 파일을 출력한다.
                    outToClient.write(fileInBytes, 0, numOfBytes);
                }
            }
            connectionSocket.close();
            System.out.println("Connection Closed");
        }
        // 예외 처리
        catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
