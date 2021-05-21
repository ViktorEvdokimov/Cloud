package nio;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileHandler {
    public static String getFileList(String path) throws IOException {
        if(Files.isExecutable(Paths.get(path))) {
            StringBuilder sb = new StringBuilder();
            Files.walkFileTree(Paths.get(path), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    sb.append("dir: " + dir + "\n");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    sb.append("file: " + file + "\n");
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    //               System.out.println("visit file failed: " + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    //               System.out.println("post visit directory: " + dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            return sb.toString();
        } else return "Error when connect to file";
    }

    public static String fileToString (String fileName, String path) {
        Path p = Paths.get(getPathByName(fileName, path));
        if(Files.exists(p)){
            RandomAccessFile aFile = null;
            StringBuilder sb = new StringBuilder();
            try {
                aFile = new RandomAccessFile(getPathByName(fileName, path), "rw");
                FileChannel inChannel = aFile.getChannel();
                ByteBuffer buf = ByteBuffer.allocate(48);
                int bytesRead = inChannel.read(buf);
                while (bytesRead != -1) {
                    buf.flip();
                    while(buf.hasRemaining()){
                        sb.append((char) buf.get());
                    }
                    buf.clear();
                    bytesRead = inChannel.read(buf);
                }
                aFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sb.toString();
        } else return "file not found";
    }

    private static String getPathByName (String fileName, String path){
        Path rootPath = Paths.get(path);
        String fileToFind = File.separator + fileName;
        final String[] result = {null};
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String fileString = file.toAbsolutePath().toString();
                    //System.out.println("pathString = " + fileString);

                    if (fileString.endsWith(fileToFind)) {
                        result[0] = String.valueOf(file);
                        System.out.println("file found at path: " + result[0]);
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result[0];
    }
}



