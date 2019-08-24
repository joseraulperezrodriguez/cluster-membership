package org.cluster.membership.protocol.net.core;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.Config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.compression.JdkZlibDecoder;
import io.netty.handler.codec.compression.JdkZlibEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class MembershipClient {
	
	private static ObjectEncoder objectEncoder = new ObjectEncoder();
	
	private static Logger logger = Logger.getLogger(MembershipClient.class.getName());
		
	public static void connect(Node to, MembershipClientHandler handler, Config config) {
		
		EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new JdkZlibEncoder(),
                  		  	  new JdkZlibDecoder());
                    p.addLast(
                    		new ReadTimeoutHandler(config.getConnectionTimeOutMs(), TimeUnit.MILLISECONDS),
                            objectEncoder,
                            new ObjectDecoder(config.getMaxObjectSize(),ClassResolvers.cacheDisabled(null)),
                            handler);
                }
             });

            // Start the connection attempt.
            b.connect(to.getAddress(), to.getProtocolPort()).sync().channel().closeFuture().sync();
            
        }
        catch(Exception e) {
        	e.printStackTrace();
            group.shutdownGracefully();
            logger.severe("exception sending connection to " + to);
        	handler.getResponseHandler().addToFailed(to);
        	handler.getResponseHandler().restoreMessages(handler.getMessages());
        }
        finally {
            group.shutdownGracefully();
        }
		
	}	
	

}
