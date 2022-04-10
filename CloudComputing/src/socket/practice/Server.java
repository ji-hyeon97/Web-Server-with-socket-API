package socket.practice;


import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server {
    public Server() {
        try {
            final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(12345));

            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel ch, Void att) {
                    listener.accept(null, this); // Accept the next connection.

                    ch.write(ByteBuffer.wrap("Hello world\n".getBytes())); // Send hello message.

                    ByteBuffer byteBuffer = ByteBuffer.allocate(4096); // Create read buffer.
                    try {
                        boolean running = true;
                        int bytesRead = ch.read(byteBuffer).get(20, TimeUnit.SECONDS); // Read, timeout 20sec.

                        while (bytesRead != -1 && running) {
                            if (byteBuffer.position() > 2) {
                                byteBuffer.flip(); // Ready to read.

                                // Buffer data to string.
                                byte[] lineBytes = new byte[bytesRead];
                                byteBuffer.get(lineBytes, 0, bytesRead);
                                String line = new String(lineBytes);
                                System.out.println("ECHO: " + line);

                                ch.write(ByteBuffer.wrap(line.getBytes())); // ECHO

                                byteBuffer.clear();

                            } else {
                                running = false;
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        ch.write(ByteBuffer.wrap("Good Bye\n".getBytes())); // Send timeout message.
                    }

                    try {
                        if (ch.isOpen()) {
                            ch.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void failed(Throwable exc, Void att) {
                    //
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Server();
        System.out.println("Started.");

        try {
            Thread.sleep(60 * 1000);
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("Aborted.");
    }
}