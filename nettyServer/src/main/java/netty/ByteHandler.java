package netty;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Server.log.debug("Client connected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        Server.log.debug("buffer: {}", buf);
        StringBuilder sb = new StringBuilder();
        while (buf.isReadable()) {
            sb.append((char) buf.readByte());
        }
        Server.log.debug("received: {}", sb);
        ByteBuf send = ctx.alloc().buffer();
        send.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(send);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Server.log.error("", cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Server.log.debug("Client disconnected");
    }
}