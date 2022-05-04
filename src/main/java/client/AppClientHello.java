package client;

import com.sun.corba.se.internal.CosNaming.BootstrapServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

public class AppClientHello {
    private final String host;
    private final int port;

    public AppClientHello(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
    public void run() throws Exception{
        //线程池
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            //客户端辅助启动类
            Bootstrap bs = new Bootstrap();
            bs.group(group)
                    .channel(NioSocketChannel.class) //实例化一个Channel
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new HandlerClientHello());
                        }
                    });
            //连接到远程节点；等待连接完成
            ChannelFuture future = bs.connect().sync();
            //发送消息到服务器端，编码格式是utf-8
            future.channel().writeAndFlush(Unpooled.copiedBuffer("hello netty", CharsetUtil.UTF_8));
            //阻塞操作
            future.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {

        new AppClientHello("127.0.0.1",18080).run();
    }
}
