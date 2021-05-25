import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import model.FileData;
import model.Message;

@Slf4j
public class NettyNetwork implements Runnable {

    private SocketChannel clientChannel;
    private Callback callback;
    private static NettyNetwork network;

    public static NettyNetwork getInstance() {
        if (network == null) {
            network = new NettyNetwork();
        }
        return network;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    @Override
    public void run() {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            clientChannel = channel;
                            channel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new FileHandler(callback)
                            );
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8189).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            ClientApplication.log.debug("", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    public void writeFile(FileData fileData) {
        clientChannel.writeAndFlush(fileData);
    }
}