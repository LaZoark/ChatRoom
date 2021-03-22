import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * 伺服器執行緒，主要來處理多個客戶端的請求
 */
public class ServerThread extends Servers implements Runnable{

    Socket socket;
    String socketName;
    Format df = new SimpleDateFormat("<yyyy-MM-dd HH:mm:ss>");  // showing timestamp
//    Date today = Calendar.getInstance().getTime();
//    String dateNow = df.format(today);

    public ServerThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //設定該客戶端的端點地址
            socketName = socket.getRemoteSocketAddress().toString();
            System.out.println(df.format(Calendar.getInstance().getTime()) + socketName+" 已加入聊天");
            print(df.format(Calendar.getInstance().getTime()) + socketName+" 已加入聊天");
            boolean flag = true;
            while (flag)
            {
                //阻塞，等待該客戶端的輸出流
                String line = reader.readLine();
                //若客戶端退出，則退出連線。
                if (line == null){
                    flag = false;
                    continue;
                }
                String msg = df.format(Calendar.getInstance().getTime()) + socketName+" : "+line;
                System.out.println(msg);
                //向線上客戶端輸出資訊
                print(msg);
            }
            closeConnect();
        } catch (IOException e) {
            try {
                closeConnect();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    /**
     * 向所有線上客戶端socket轉發訊息
     * @param msg
     * @throws IOException
     */
    private void print(String msg) throws IOException {
        PrintWriter out = null;
        synchronized (sockets){
            for (Socket sc : sockets){
                out = new PrintWriter(sc.getOutputStream());
                out.println(msg);
                out.flush();
            }
        }
    }
    /**
     * 關閉該socket的連線
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        System.out.println(df.format(Calendar.getInstance().getTime()) + socketName+" 已退出聊天");
        print(df.format(Calendar.getInstance().getTime()) + socketName+" 已退出聊天");
        //移除沒連線上的客戶端
        synchronized (sockets){
            sockets.remove(socket);
        }
        socket.close();
    }
}