package com.abc.basic.algoritms.external;

import java.io.IOException;


public class MemRunAcceptor extends BaseRunAcceptor {

	int count=0;
	
	public MemRunAcceptor()
	{
		stores=new MemRecordStore[50];
		
	}
	
	@Override
	public void closeRun() {
		System.out.println("\nend run:("+(index+1)+")total:"+count);
		((MemRecordStore)stores[index]).compact();
	}


	@Override
	public void acceptRecord(Record rec) throws IOException {
		//System.out.print(rec+",");
		count++;
		((MemRecordStore)stores[index]).storeRecord(rec);
		
	}




	@Override
	protected void increamentStoresBy(int len) {
		RecordStore[] tmp=new RecordStore[len+stores.length];
		System.arraycopy(stores, 0, tmp, 0, stores.length);
		stores=tmp;
		
	}

	@Override
	protected void initStore(int i) {
		
		System.out.println("start new run:");
		stores[i]=new MemRecordStore();
		
	}

}
