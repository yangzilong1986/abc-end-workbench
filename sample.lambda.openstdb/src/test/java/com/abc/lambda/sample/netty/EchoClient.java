package com.abc.lambda.sample.netty;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.ssl.SslContext;
import org.jboss.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Sends one message when a connection is open and echoes back any received
 * data to the server.  Simply put, the echo client initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public final class EchoClient {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.git
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        // Configure the bootstrap.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        try {
            bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
                public ChannelPipeline getPipeline() {
                    ChannelPipeline p = Channels.pipeline();
                    if (sslCtx != null) {
                        p.addLast("ssl", sslCtx.newHandler(HOST, PORT));
                    }
                    p.addLast("echo", new EchoClientHandler());
                    return p;
                }
            });
            bootstrap.setOption("tcpNoDelay", true);
            bootstrap.setOption("receiveBufferSize", 1048576);
            bootstrap.setOption("sendBufferSize", 1048576);

            // Start the connection attempt.
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(HOST, PORT));

            // Wait until the connection is closed or the connection attempt fails.
            future.getChannel().getCloseFuture().sync();
        } finally {
            // Shut down thread pools to exit.
            bootstrap.releaseExternalResources();
        }
    }
}

