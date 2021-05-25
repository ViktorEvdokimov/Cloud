package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.FileData;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class FileHandler extends SimpleChannelInboundHandler<FileData> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileData fileData) throws Exception {
        String path = "nettyServer/src/mail/resources/";
        String fileName = fileData.getName();
        File f = new File(path, fileName);
        RandomAccessFile fw = new RandomAccessFile(f, "rw");
        ArrayList<byte[]> data = fileData.getData();
        for(int i=0; i< data.size(); i++){
            byte[] b = data.get(i);
            int num = b.length;
            fw.write(b, 0, num); // Пропустить num байтов и снова записать в файл
            fw.skipBytes(num); // Чтение num байтов
        }
        System.out.println("Read complete");

    }
}