package org.cluster.membership.protocol.net;

import org.cluster.membership.common.model.util.Literals;
import org.cluster.membership.protocol.Config;
import org.cluster.membership.protocol.net.channel.handler.MembershipServerHandler;
import org.cluster.membership.protocol.net.core.RequestReceiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
public class MembershipServer extends Thread {
	
    private MembershipServerHandler membershipServerHandler;
	
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    
    private RequestReceiver requestReceiver;

	
	private ObjectEncoder objectEncoder = new ObjectEncoder();

	@Autowired
	private Config config;
	
	@Autowired
	public MembershipServer(RequestReceiver requestReceiver) {
		this.requestReceiver = requestReceiver;
	}
	
	public void pause(long millis) {
		if(this.membershipServerHandler == null || !(membershipServerHandler instanceof MembershipServerHandler.Debug)) return;
		
		MembershipServerHandler.Debug debugHandler = (MembershipServerHandler.Debug)membershipServerHandler;
		debugHandler.pause(millis);
	}
	
	public void run() {
          try {
    	    this.membershipServerHandler = (config.getMode().equals(Literals.APP_TEST_MODE) ? new MembershipServerHandler.Debug(requestReceiver) : new MembershipServerHandler(requestReceiver));        	
            bossGroup = new NioEventLoopGroup(config.getServerThreads());
            workerGroup = new NioEventLoopGroup(config.getServerThreads());

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
                            new ObjectDecoder(config.getMaxObjectSize(), ClassResolvers.cacheDisabled(null)),
                            membershipServerHandler);
                }
             });

            // Bind and start to accept incoming connections.
            b.bind(config.getThisPeer().getProtocolPort()).sync().channel().closeFuture().sync();
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
