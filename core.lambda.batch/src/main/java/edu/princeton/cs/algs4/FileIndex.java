package edu.princeton.cs.algs4;

import edu.princeton.cs.algs4.tree.SET;
import edu.princeton.cs.algs4.tree.ST;
import edu.princeton.cs.algs4.utils.In;

import java.io.File;

/**
 *  The {@code FileIndex} class provides a client for indexing a set of files,
 *  specified as command-line arguments. It takes queries from standard input
 *  and prints each file that contains the given query.
 */
public class FileIndex { 

    // Do not instantiate.
    private FileIndex() { }

    public static void main(String[] args) {

        // key = word, value = set of files containing that word
        ST<String, SET<File>> st = new ST<String, SET<File>>();

        // create inverted index of all files
        StdOut.println("Indexing files");
        for (String filename : args) {
            StdOut.println("  " + filename);
            File file = new File(filename);
            In in = new In(file);
            while (!in.isEmpty()) {
                String word = in.readString();
                if (!st.contains(word)) st.put(word, new SET<File>());
                SET<File> set = st.get(word);
                set.add(file);
            }
        }


        // read queries from standard input, one per line
        while (!StdIn.isEmpty()) {
            String query = StdIn.readString();
            if (st.contains(query)) {
                SET<File> set = st.get(query);
                for (File file : set) {
                    StdOut.println("  " + file.getName());
                }
            }
        }

    }

}