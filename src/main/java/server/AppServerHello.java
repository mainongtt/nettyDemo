package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class AppServerHello {
    private Integer port;

    public AppServerHello(Integer port) {
        this.port = port;
    }
    public void run() throws Exception{
        //Netty的Reactor线程池，初始化了一个NioEventLoop数组，用来处理I/O操作,如接收新的连接和读/写数据
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap(); //用于NIO服务
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class) //通过工厂方法实例化一个channel
                    .localAddress(new InetSocketAddress(port)) //监听端口
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new HandlerServerHello());
                        }
                    });
            //绑定服务器，该实例将提供有关IO操作的结果或状态的信息
            ChannelFuture channelFuture = bootstrap.bind().sync();
            System.out.println(channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();
        }finally {
            //关闭EventLoopGroup并释放所有资源，包括所有创建的线程
            group.shutdownGracefully().sync();
        }

    }

    public static void main(String[] args) throws Exception {
        new AppServerHello(18080).run();
    }
}
