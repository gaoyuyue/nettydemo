package cn.attackme.nettydemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author 高语越 (Gao Yuyue)
 * @email gaoyuyue@outlook.com
 */
public class ClientApplication {
    private String address;
    private int port;
    private long sessionId = System.currentTimeMillis();

    public ClientApplication(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void start(){
        NioEventLoopGroup executors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(executors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                        ch.pipeline().addLast(new ProtobufDecoder(Protocol.Message.getDefaultInstance()));
                        ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                        ch.pipeline().addLast(new ProtobufEncoder());
                        ch.pipeline().addLast(new ClientHandler());
                    }
                });
        try {
            ChannelFuture cf = bootstrap.connect(address,port).sync();
            while (true){
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in));
                String input = reader.readLine();
                if (input.equals("quit")){
                    break;
                }
                Protocol.Message.Builder builder = Protocol.Message.newBuilder();
                builder.setSessionId(sessionId).setContent(input);
                cf.channel().writeAndFlush(builder.build());
            }
            cf.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ClientApplication("127.0.0.1",8099).start();
    }
}
