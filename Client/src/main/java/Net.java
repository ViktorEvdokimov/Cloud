import com.sun.javafx.binding.StringFormatter;

import java.io.*;
import java.net.Socket;

public class Net implements Closeable {

    private int port;
    private Socket socket;
    //    private DataInputStream is;
    private DataOutputStream os;
    private final ClientController cn;

    private Net(int port, ClientController cn) throws IOException {
        this.cn = cn;
        socket = new Socket("localhost", port);
//        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }

//    private void listenServer (){
//        try {
//            String msg;
//            while (true) {
//                msg = is.readUTF();
//                cn.printServerMessage(msg);
//            }
//        } catch (IOException e) {
//            System.out.println("Connected loss");
//        }
//    }

    public static Net start(int port, ClientController cn) throws IOException {
        return new Net(port, cn);
    }

    public void sendFile(String path) throws IOException {
//        new Thread(this::listenServer).start();
        byte[] b=new byte[1024];
        File f = new File(path);
        System.out.println(f.getName());
        try {// Поток вывода данных
            DataOutputStream dout = new DataOutputStream (new BufferedOutputStream (os)); // Поток чтения файла
            InputStream ins=new FileInputStream(f);
            String mess = f.getName();
            String t = String.format("%03d", mess.getBytes().length);
            dout.write(t.getBytes());
            dout.write(mess.getBytes());
            int n = ins.read(b);
            while (n != -1) {// Запись данных в сеть
                dout.write (b); // Отправить содержимое файла
                dout.flush (); // снова прочитать n байтов
                n = ins.read(b);
            } // Закрыть поток
            ins.close();
            dout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






    @Override
    public void close() throws IOException {
        os.close();
//        is.close();
//        socket.close();
    }
}