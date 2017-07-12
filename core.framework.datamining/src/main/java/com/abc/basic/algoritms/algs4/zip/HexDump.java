package com.abc.basic.algoritms.algs4.zip;

import com.abc.basic.algoritms.algs4.utils.BinaryStdIn;
import com.abc.basic.algoritms.algs4.utils.StdOut;

/**
 *  The {@code HexDump} class provides a client for displaying the contents
 *  of a binary file in hexadecimal.
 *  <p>
 */
public class HexDump {

    // Do not instantiate.
    private HexDump() { }

    /**
     * Reads in a sequence of bytes from standard input and writes
     * them to standard output using hexademical notation, k hex digits
     * per line, where k is given as a command-line integer (defaults
     * to 16 if no integer is specified); also writes the number
     * of bits.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int bytesPerLine = 16;
        if (args.length == 1) {
            bytesPerLine = Integer.parseInt(args[0]);
        }

        int i;
        for (i = 0; !BinaryStdIn.isEmpty(); i++) {
            if (bytesPerLine == 0) {
                BinaryStdIn.readChar();
                continue;
            }
            if (i == 0) StdOut.printf("");
            else if (i % bytesPerLine == 0) StdOut.printf("\n", i);
            else StdOut.print(" ");
            char c = BinaryStdIn.readChar();
            StdOut.printf("%02x", c & 0xff);
        }
        if (bytesPerLine != 0) StdOut.println();
        StdOut.println((i*8) + " bits");
    }
}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
