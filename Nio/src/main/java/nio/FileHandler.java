package nio;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileHandler {
    public static String getFileList(Path path) throws IOException {
        if(Files.isExecutable(path)) {
            StringBuilder sb = new StringBuilder();
            Files.walkFileTree(path, new FileVisitor<Path>() {
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
}



