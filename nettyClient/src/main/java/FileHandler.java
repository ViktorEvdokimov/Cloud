import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.FileData;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FileHandler extends SimpleChannelInboundHandler<FileData> {

    private Callback callback;

    public FileHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileData fileData) throws Exception {
        callback.processMessage(fileData);
    }

    public static String[][] getFileList() {
        File[] paths;
        FileSystemView fsv = FileSystemView.getFileSystemView();

// returns pathnames for files and directory
        paths = File.listRoots();

// for each pathname in pathname array
        String[][] fileList = new String[paths.length][2];
        for(int i=0; i< paths.length; i++)
        {
            fileList[i][0] = "Drive Name: \t";        // description
            fileList[i][1] = paths[i].toString();    // file name

        }
        return fileList;
    }

    public static String[][] getFileList(String path) {

        // определяем объект для каталога
        File dir = new File(path);
        File[] paths = dir.listFiles();
        // если объект представляет каталог
        String[][] fileList = new String[paths.length][2];
        if (dir.isDirectory()) {
            // получаем все вложенные объекты в каталоге
            for (int i=0; i< paths.length;i++) {

                if (paths[i].isDirectory()) {
                    fileList[i][0] = "Directory: \t";
                } else {
                    fileList[i][0] = "File: \t\t\t";
                }
                fileList[i][1] = paths[i].toString();
            }
        }
        return fileList;
    }

    public static File getFile(String path){
        return new File(path);
    }
}