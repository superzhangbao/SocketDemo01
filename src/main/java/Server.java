import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2001);

        System.out.println("服务器准备就绪~");
        System.out.println("服务端信息：" + serverSocket.getInetAddress() + "P:" + serverSocket.getLocalPort());

        //等待客户端连接
        while (true) {
            //得到客户端
            Socket client = serverSocket.accept();
            //客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            //启动线程
            clientHandler.start();
        }
    }

    /**
     * 客户端消息处理
     */
    private static class ClientHandler extends Thread {
        private Socket mSocket;
        private boolean flag = true;

        public ClientHandler(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接："+ mSocket.getInetAddress()+"  P:"+mSocket.getPort());
            try {
                //得到socket输出流，并转换为打印流,用于数据输出；服务器回送数据使用
                OutputStream outputStream = mSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                //得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                do {
                    String s = socketInput.readLine();
                    if ("bye".equalsIgnoreCase(s)) {
                        flag = false;
                        //回送
                        printStream.println("bye");
                    }else {
                        //打印到屏幕。并回送数据长度
                        printStream.println("回送："+s.length());
                    }
                    System.out.println("客户端数据："+s);
                }while (flag);

                //释放资源
                printStream.close();
                socketInput.close();
            }catch (Exception e) {
                System.out.println("连接异常断开");
            }finally {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已退出："+mSocket.getInetAddress()+"  P:"+mSocket.getPort());
        }
    }
}
