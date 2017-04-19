package org.hbase.async;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * Created by admin on 2017/3/31.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        char c='册';
        ChannelBuffer buffer = ChannelBuffers.buffer( 9 );
        Long l=8999999999999L;//8 8 3 19
        HBaseRpc.writeVLong(buffer,l);

    }
}
