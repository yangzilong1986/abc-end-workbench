package com.abc.basic.algoritms.external;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Random;

public class CreateFile {

	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		OutputStream out=new FileOutputStream(ExternalSorter.PATH_NAME+"test_sort1.txt");
		PrintStream ps=new PrintStream(new BufferedOutputStream(out,12*1024));

		Random r=new Random();
		
		for(int i=0;i<1000;i++)
		{
			ps.println(r.nextInt(10000000)+"             this  a line line bbbbb!");
		}
		ps.close();
	}

}
