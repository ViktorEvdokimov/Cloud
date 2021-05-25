package model;

import lombok.Builder;
import lombok.Data;

import java.io.*;
import java.util.ArrayList;

@Data
@Builder
public class FileData {
    private final String name;
    private final ArrayList<byte[]> data;

    public String getName() {
        return name;
    }

    public ArrayList<byte[]> getData() {
        return data;
    }

    public FileData(String path) {
        File file = new File(path);
        this.name = file.getName();
        data = new ArrayList<byte[]>();

        byte[] b=new byte[1024];
        File f = new File(path);
        System.out.println(f.getName());
        try {// Поток вывода данных
            InputStream ins=new FileInputStream(f);
            int n = ins.read(b);
            while (n != -1) {// Запись данных в сеть
                data.add(b); // записываем файл в лист
                n = ins.read(b);
            } // Закрыть поток
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
