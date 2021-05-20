import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable, Closeable {

    private final Socket socket;
    private String fileName = "file.pdf";
    DataInputStream is;
    DataOutputStream os;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Start listening...");
        try {
//            os.writeUTF("Connection success");
//            os.flush();
            receiveFile();
            //           os.writeUTF("Socket closed.");
//            os.flush();
            Thread.sleep(10);
        } catch (Exception e) {
            System.err.println("Connection was broken");
            try {
                close();
                System.out.println("Finish listening");
            } catch (Exception ioException) {
                System.err.println("Exception while socket close");
            }
        }
    }

    private void receiveFile() {
        byte[] b=new byte[3];
        try {// Определим входной поток,
            InputStream din = new DataInputStream (new BufferedInputStream (is)); // Создать файл для сохранения
            din.read(b);
            byte[] nameArr = "file.txt".getBytes();
            b = new byte[Integer.parseInt(new String(b))];
            din.read(b);
            fileName = new String(b);
//            byte[] searchedByte = ">".getBytes();
//            for (int i = 0; i < b.length; i++) {
//                if (b[i] == searchedByte[0]){
//                    nameArr = new byte[i-2];
//                    System.arraycopy(b, 2, nameArr, 0, i-2);
//                    System.arraycopy(b, i+1, b = new byte[b.length-i-1], 0, b.length-i-1);
//                    num = b.length;
//                    break;
//                }
//            }
            int num = din.read(b);
//            fileName = new String(nameArr, "UTF-8");
            System.out.println(fileName);
            File f = new File("Server/src/main/resources/", fileName);
            RandomAccessFile fw = new RandomAccessFile(f, "rw");
            do {// Записать 0 ~ num байтов в файл
                fw.write(b, 0, num); // Пропустить num байтов и снова записать в файл
                fw.skipBytes(num); // Чтение num байтов
                num = din.read(b);
            } while (num != -1); // Закрыть входной и выходной потоки
//                os.writeUTF("Read complete");
//                os.flush();
            System.out.println("Read complete");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void close() throws IOException {
        is.close();
        os.close();
        socket.close();
    }
}