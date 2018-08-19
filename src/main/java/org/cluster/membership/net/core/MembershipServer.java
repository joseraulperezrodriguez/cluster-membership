package org.cluster.membership.net.core;

import org.cluster.membership.Config;
import org.cluster.membership.net.RequestReceiver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MembershipServer {
	
	private int port;
	
	private MembershipServerHandler membershipServerHandler;
	
	private static ObjectEncoder objectEncoder = new ObjectEncoder();
	
	public MembershipServer(RequestReceiver requestReceiver, int port) {
		this.port = port;
		this.membershipServerHandler = new MembershipServerHandler(requestReceiver);
	}
	
	
	public void listen() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();                 
                    p.addLast(new JdkZlibEncoder(),
                  		  	  new JdkZlibDecoder());
                    p.addLast(
                            objectEncoder,
                            new ObjectDecoder(Config.MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)),
                            membershipServerHandler);
                }
             });

            // Bind and start to accept incoming connections.
            b.bind(port).sync().channel().closeFuture().sync();
        }
        catch (InterruptedException e) {    
        	e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            listen();
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}

}
