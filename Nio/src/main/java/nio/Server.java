package nio;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class Server {

    private static int counter = 0;
    private final ByteBuffer buffer;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final DateTimeFormatter formatter;
    private static final Logger log = LoggerFactory.getLogger(Server.class);
    private static final String[][] commands = new String[][] {
            new String[]{"help", " - print commands list;\n"},
            new String[]{"ls", " - print files list;\n"},
            new String[]{"cat", " \"file_name\" - show data from file;\n"}
    };
    private final String path = "Nio\\src\\main";

    public Server() throws IOException {
        buffer = ByteBuffer.allocate(256);
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyy HH:mm:ss");
        serverChannel = ServerSocketChannel.open();
        log.debug("Server started...");
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(8189));
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (serverChannel.isOpen()) {
            selector.select(); // block
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) {
                    handleAccept(selectionKey);
                }
                if (selectionKey.isReadable()) {
                    handleRead(selectionKey);
                }
                iterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey selectionKey) {
        try {
            if (selectionKey.isValid()) {
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                String attachment = (String) selectionKey.attachment();

                StringBuilder reader = new StringBuilder();
                int read = 0;

                while (true) {
                    read = channel.read(buffer);
                    if (read == 0) {
                        break;
                    }
                    if (read == -1) {
                        channel.close();
                    }
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        reader.append((char) buffer.get());
                    }
                    buffer.clear();
                }

                String message = reader.toString();
                System.out.println(message);
                if (message.contains(commands[0][0])){
                    SocketChannel ch = (SocketChannel) selectionKey.channel();
                    for (int i = 0; i < commands.length; i++) {
                        ch.write(ByteBuffer.wrap((commands[i][0] + commands[i][1]).getBytes()));
                    }
                }else if (message.contains(commands[1][0])){
                    SocketChannel ch = (SocketChannel) selectionKey.channel();
                    ch.write(ByteBuffer.wrap(FileHandler.getFileList(path).getBytes()));
                } else if (message.contains(commands[2][0])){
                    String[] parts = message.split(" ");
                    String[] fileName = parts[1].split("\r");
                    SocketChannel ch = (SocketChannel) selectionKey.channel();
                    ch.write(ByteBuffer.wrap((FileHandler.fileToString(fileName[0], path)).getBytes()));
                }else {
                    log.debug("received from {} msg: {}", attachment, message);
                    String date = formatter.format(LocalDateTime.now());
                    for (SelectionKey key : selector.keys()) {
                        if (key.isValid() && key.channel() instanceof SocketChannel) {
                            SocketChannel ch = (SocketChannel) key.channel();
                            ch.write(ByteBuffer.wrap((date + " " + attachment + ": " + message)
                                    .getBytes(StandardCharsets.UTF_8)));
                        }
                    }
                }
            }
        } catch (Exception e) {
//            log.debug("Client {} disconnected!", selectionKey.attachment());
        }

    }

    private void handleAccept(SelectionKey selectionKey) throws IOException {
        counter++;
        log.debug("Client {} accepted...", "user" + counter);
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, "user" + counter);
    }

}