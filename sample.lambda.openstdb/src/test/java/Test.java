import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class Test {
    public static void encode(byte[] in, byte[] out, int password) {
        int len = in.length;
        int seed = password ^ 0x3e1e25e6;
        for (int i = 0; i < len; ++i) {
            byte a = (byte) ((in[i] ^ seed) >> 3);
            //说明①: in[i]的高5位给了a的低5位
            byte b = (byte) (((((int) in[i]) <<18) ^seed) >> (18 - 5));
            //说明②: in[i]的低3位给了b的高3位
            a&= 0x1f;
            //0x1f=16+15=31=2^5-1=00011111;
            b&= 0xe0;
            //0xe0=11100000;
            out[i] = (byte) (a | b);
            seed = (seed * 84723701^ seed ^ out[i]);
        }
    }


    public static void decode(byte[] in, byte[] out, int password) {
        int len = in.length;// encode中的out[i]是这里decode中的in[i]
        int seed = password ^ 0x3e1e25e6;
        for (int i = 0; i < len; ++i) {
            byte a = (byte) (in[i] & 0x1f);
            //参照式⑤，还原输出结果，取in[i]的低5位
            byte b = (byte) (in[i] & 0xe0);
            //参照式⑤，还原输出结果，取in[i]的高3位
            a = (byte) (((a <<3) ^ seed) & 248);
            //参照定理三B ^ A^ A = B，参照式①byte a = (byte) ((in[i] ^seed) >>> 3)
            //式①中的in[i]相当于B，seed相当于A，(a<<3)相当 B^A 故((a<<3) ^ seed)表示in[i]高5
            //位的这5个数字，为了将这5个数字放入高5位的位置上，还需&11111000，即&248。
            //11111000=2^7+2^6+2^5+2^4+2^3=128+64+32+16+8=248
            b = (byte) ((((((int) b) << (18 - 5)) ^ seed) >> 18) & 7);
            //类似地，逆向式②，得到的结果将放入out[i]的低3位，故&00000111，即&7。
            //00000111=2^0+2^1+2^2=1+2+4=7
            out[i] = (byte) (a | b);
            seed = (seed * 84723701 ^ seed ^ in[i]);
        }
    }


    public static void main(String[] args) throws Exception {
        int password = 0xfdb4d0e9;
        byte[] buf1 = { -5, 9, -62, -122, 50, 122, -86, 119, -101, 25, -64,
                -97, -128, 95, 85, 62, 99, 98, -94, 76, 12, 127, 121, -32,
                -125, -126, 15, 18, 100, 104, -32, -111, -122, 110, -4, 60, 57,
                21, 36, -82, };
//        byte[] buf2 = new byte[buf1.length];
//        encode(buf1, buf2, password);
//        System.out.println(new String(buf2, "GBK"));
//        decode(buf1, buf2, password);
//        System.out.println(new String(buf2, "GBK"));
           byte b = (byte) 0127;    // 0b1000 1000
        //1000 11111 01000
        //900  11100 00100
        //9    1001
        //127  1111111
        //-1    11111111111111111111111111111111
        //-127  1111 1111 1111 1111 1111 1111 1000 0001

        //-112  1111 1111 1111 1111 1111 1111 1001 0000
        //-113  1111 1111 1111 1111 1111 1111 1000 1111
        //-114  1111 1111 1111 1111 1111 1111 1000 1110
        //-115  1111 1111 1111 1111 1111 1111 1000 1101
        //-116  1111 1111 1111 1111 1111 1111 1000 1100
        //-117  1111 1111 1111 1111 1111 1111 1000 1011
        //-118  1111 1111 1111 1111 1111 1111 1000 1010
        //-119  1111 1111 1111 1111 1111 1111 1000 1001

        //-120  1111 1111 1111 1111 1111 1111 1000 1000
        //-121  1111 1111 1111 1111 1111 1111 1000 0111
        //-122  1111 1111 1111 1111 1111 1111 1000 0110
        //-123  1111 1111 1111 1111 1111 1111 1000 0101
        //-124  1111 1111 1111 1111 1111 1111 1000 0100

        //-9    1111 1111 1111 1111 1111 1111 1111 0111
        //-900  1111 1111 1111 1111 1111 1100 0111 1100
        //512   10 0000 0000
        //87       0101 0111
        //88       0101 1000
        //90       1011010
//        System.out.println(Byte.toString(b));
        System.out.println(Integer.toBinaryString(-128));
//        {
//            long tmp = 512;
//            do {
//                System.out.println("tmp -> ");
//                System.out.println(tmp);
//                System.out.println("b -> ");
//                System.out.println(b);
//                tmp >>>= 8;//除法，八位位移
//                b--;
//            } while (tmp != 0);
//        }
//        ChannelBuffer buffer = ChannelBuffers.buffer( 10 );
//
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
//
//        buffer.writeInt( 10 );
//        System.out.println("after write one integer");
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
//
//        buffer.writeInt( 10 );
//        System.out.println("after write two integer");
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
//
//        int i = buffer.readInt( );
//        System.out.println("after read one integer: " + i);
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
//
//        buffer.discardReadBytes( );
//        System.out.println("after discard read bytes");
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
//
//        buffer.resetReaderIndex( );
//        System.out.println("after reset reader index");
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
//
//        buffer.resetWriterIndex( );
//        System.out.println("after reset writer index");
//        System.out.println("readable bytes: " + buffer.readableBytes( ));
//        System.out.println("readable index: " + buffer.readerIndex( ));
//        System.out.println("writable bytes: " + buffer.writableBytes( ));
//        System.out.println("writable index: " + buffer.writerIndex( ));
    }
}
