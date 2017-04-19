package com.abc.lambda.sample.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Handler implementation for the echo client.  It initiates the ping-pong
 * traffic between the echo client and server by sending the first message to
 * the server.
 */
public class EchoClientHandler extends SimpleChannelUpstreamHandler {

    private final ChannelBuffer firstMessage;
    private final AtomicLong transferredBytes = new AtomicLong();

    /**
     * Creates a client-side handler.
     */
    public EchoClientHandler() {
        firstMessage = ChannelBuffers.buffer(EchoClient.SIZE);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }
    }

    public long getTransferredBytes() {
        return transferredBytes.get();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        // Send the first message.  Server will not send anything here
        // because the firstMessage's capacity is 0.
        e.getChannel().write(firstMessage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // Send back the received message to the remote peer.
        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
        e.getChannel().write(e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        // Close the connection when an exception is raised.
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}

