package cn.attackme.nettydemo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author 高语越 (Gao Yuyue)
 * @email gaoyuyue@outlook.com
 */
public class ServerHandler extends SimpleChannelInboundHandler<Protocol.Message> {
    public static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol.Message msg) throws Exception {
        System.out.println(msg.getSessionId()+"say: "+msg.getContent());
        channels.forEach(c -> c.writeAndFlush(msg));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println("a client add");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        System.out.println("a client remove");
        super.handlerRemoved(ctx);
    }
}
