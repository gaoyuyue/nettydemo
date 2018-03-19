package cn.attackme.nettydemo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 高语越 (Gao Yuyue)
 * @email gaoyuyue@outlook.com
 */
public class ClientHandler extends SimpleChannelInboundHandler<Protocol.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol.Message msg) throws Exception {
        System.out.println(msg.getSessionId()+"say: "+msg.getContent());
    }
}
