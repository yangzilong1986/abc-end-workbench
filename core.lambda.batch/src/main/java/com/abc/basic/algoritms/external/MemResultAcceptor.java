package com.abc.basic.algoritms.external;

import java.io.IOException;

public class MemResultAcceptor implements ResultAcceptor {

	int count=0;
	
	Record prev=null;

	@Override
	public void acceptRecord(Record rec) throws IOException {
	    count++;
	    if(prev==null)
	    {
	    	prev=rec;
	    }
	    else if(prev.compareTo(rec)>0)
	    {
	    	System.err.println(" sorted error!!!");
	    	System.exit(-1);
	    }
	    prev=rec;


	}

	@Override
	public void end() throws IOException {
		System.out.println("\nend,totally :"+count+" records!");

	}

	@Override
	public void start() throws IOException {
		System.out.println("begin:");

	}

}
