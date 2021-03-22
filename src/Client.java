import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class  Client{

    public static void main(String[] args) throws IOException {
        //和指定的IP以及PORT建立連線
        Socket socket = new Socket("120.101.8.185",5000);
        //獲取系統標準輸入流
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //建立一個執行緒用於讀取伺服器的資訊
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true || in.readLine()!=null){
                        System.out.println(in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //寫資訊給客戶端
        String  line = reader.readLine();
        while (!"end".equalsIgnoreCase(line)){
            //把鍵盤輸入的字串傳送給伺服器
            out.println(line);
            out.flush();
            //顯示輸入的資訊
            line = reader.readLine();
        }
        out.close();
        in.close();
        socket.close();

    }
}