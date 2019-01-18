package org.cluster.membership.protocol.net.test;

import java.util.concurrent.TimeUnit;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.protocol.Config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
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

public class MembershipClientTest {
		
	public static void connect(Node to, ChannelInboundHandlerAdapter handler, Config config) {
		
		EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {                	
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new ReadTimeoutHandler(config.getConnectionTimeOutMs() * 1, TimeUnit.MILLISECONDS));
                    
                    p.addLast(new JdkZlibEncoder(),
                  		  	  new JdkZlibDecoder());
                    p.addLast(                    		
                            new ObjectEncoder(),
                            new ObjectDecoder(config.getMaxObjectSize(),ClassResolvers.cacheDisabled(null)),
                            handler);                    
                }
             });

            // Start the connection attempt.
            b.connect(to.getAddress(), to.getProtocolPort()).sync().channel().closeFuture().sync();
        }
        catch(InterruptedException e) {
            group.shutdownGracefully();
        	e.printStackTrace();        	
        	assert(false);
        }
        finally {
            group.shutdownGracefully();
        }
		
	}	
	

}
