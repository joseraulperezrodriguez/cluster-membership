package org.cluster.membership.protocol.net.test;

import org.cluster.membership.protocol.Config;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
import io.netty.util.concurrent.Future;

public class MembershipServerTest {
	
	private int port;
	
	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelInboundHandlerAdapter serverHandler;
    private Config config;
	
	public MembershipServerTest(int port, ChannelInboundHandlerAdapter serverHandler, Config config) {
		this.port = port;
		this.bossGroup = new NioEventLoopGroup(1);
	    this.workerGroup = new NioEventLoopGroup();
	    this.serverHandler = serverHandler;
	    this.config = config;
	}
	
	
	public void listen() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    //p.addLast(new ReadTimeoutHandler(Config.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS));
                    
                    p.addLast(new JdkZlibEncoder(),
                    		  new JdkZlibDecoder());
                    p.addLast(                    		
                            new ObjectEncoder(),
                            new ObjectDecoder(config.getMaxObjectSize(), ClassResolvers.cacheDisabled(null)),
                            serverHandler);
                    
                }
             });

            // Bind and start to accept incoming connections.
            b.bind(port).sync().channel().closeFuture().sync();
        }
        catch (InterruptedException e) {    
        	e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            assert(false);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}
	
	public void shutdownAsync() {
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
	}
	
	public void shutdownSync() {
		Future<?> endBossGroup = bossGroup.shutdownGracefully();
		Future<?> endWorkerGroup = workerGroup.shutdownGracefully();
		
		while(!endBossGroup.isDone() || !endWorkerGroup.isDone());
	}

}
