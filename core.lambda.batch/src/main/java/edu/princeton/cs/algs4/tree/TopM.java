package edu.princeton.cs.algs4.tree;

import edu.princeton.cs.algs4.col.Stack;
import edu.princeton.cs.algs4.utils.StdIn;
import edu.princeton.cs.algs4.utils.StdOut;
import edu.princeton.cs.algs4.Transaction;

import java.util.Date;

public class TopM {

    // This class should not be instantiated.
    private TopM() { }

    public static void main(String[] args) {
        int m =10;// Integer.parseInt(args[0]);
        MinPQ<Transaction> pq = new MinPQ<Transaction>(m+1);
        Date d=new Date();
        String line = "Turing 6/17/1990 644.0 ";

        Transaction ts = new Transaction(line);
        pq.insert(ts);
        line = "BS 4/17/1990 644.0 ";
        ts = new Transaction(line);
        pq.insert(ts);

        line = "von 3/17/1990 234.0 ";
        ts = new Transaction(line);
        pq.insert(ts);
        if (pq.size() > m)
            pq.delMin();
        // print entries on PQ in reverse order
        Stack<Transaction> stack = new Stack<Transaction>();
        for (Transaction transaction : pq)
            stack.push(transaction);
        for (Transaction transaction : stack)
            StdOut.println(transaction);
    }

    public static void testFile() {
        int m = 10;
        MinPQ<Transaction> pq = new MinPQ<Transaction>(m+1);

        while (StdIn.hasNextLine()) {
            // Create an entry from the next line and put on the PQ.
            String line = StdIn.readLine();
            Transaction transaction = new Transaction(line);
            pq.insert(transaction);

            // remove minimum if m+1 entries on the PQ
            if (pq.size() > m)
                pq.delMin();
        }   // top m entries are on the PQ

        // print entries on PQ in reverse order
        Stack<Transaction> stack = new Stack<Transaction>();
        for (Transaction transaction : pq)
            stack.push(transaction);
        for (Transaction transaction : stack)
            StdOut.println(transaction);
    }
}