package org.cluster.membership.protocol.net.core;

import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.net.RequestReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Component
public class MembershipServer extends Thread {
	
	private MembershipServerHandler membershipServerHandler;
	
	private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    private RequestReceiver requestReceiver;

	
	private static ObjectEncoder objectEncoder = new ObjectEncoder();

	@Autowired
	public MembershipServer(RequestReceiver requestReceiver) {
		this.requestReceiver = requestReceiver;
	}
	
	public void pause(long millis) {
		if(this.membershipServerHandler == null || 
				!(membershipServerHandler instanceof MembershipServerHandlerDebug)) return;
		
		MembershipServerHandlerDebug debugHandler = (MembershipServerHandlerDebug)membershipServerHandler;
		debugHandler.pause(millis);
	}
	
	public void run() {
        try {
    		this.membershipServerHandler = (
    				Config.MODE[0].equals("DEBUG") ? new MembershipServerHandlerDebug(requestReceiver) : 
    					new MembershipServerHandler(requestReceiver));

        	
        	bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup();

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();                 
                    /*p.addLast(new JdkZlibEncoder(),
                  		  	  new JdkZlibDecoder());*/
                    p.addLast(
                    		//new ReadTimeoutHandler(Config.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS),
                            objectEncoder,
                            new ObjectDecoder(Config.MAX_OBJECT_SIZE, ClassResolvers.cacheDisabled(null)),
                            membershipServerHandler);
                }
             });

            // Bind and start to accept incoming connections.
            b.bind(Config.THIS_PEER.getProtocolPort()).sync().channel().closeFuture().sync();
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
	
	public void listen() {
		this.start();
	}
	
	public void shutdown() {
		if(bossGroup != null) bossGroup.shutdownGracefully();
		if(workerGroup != null) workerGroup.shutdownGracefully();
	}

}
