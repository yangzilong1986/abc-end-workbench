package com.abc.basic.algoritms.external;

import java.io.IOException;

/**
 * @author yovn
 *
 */
public class ExternalSorter {
	static String PATH_NAME=com.abc.basic.algoritms.algs4.utils.In.PATH_NAME;
		
	
	public void sort(int heapSize,RecordStore source,RunAcceptor mediator ,ResultAcceptor ra)throws IOException
	{
		MinHeap<Record> heap=new MinHeap<Record>(Record.class,heapSize);
		for(int i=0;i<heapSize;i++)
		{
			Record r=source.readNextRecord();
			if(r.isNull()){
				break;
			}
			heap.insert(r);
		}
		
		Record readR=source.readNextRecord();
		while(!readR.isNull()||!heap.isEmpty())
		{
		
			Record curR=null;
			//begin output one run
			mediator.startNewRun();
			while(!heap.isEmpty())
			{
				curR=heap.removeMin();
			
				mediator.acceptRecord(curR);
				
				if (!readR.isNull()) {
					if (readR.compareTo(curR) < 0) {
						heap.addToTail(readR);
					} else
						heap.insert(readR);
				}
				readR=source.readNextRecord();
				
			}
			//done one run
			mediator.closeRun();
			
			//prepare for next run
			heap.reverse();
			while(!heap.isFull()&&!readR.isNull())
			{
				
				heap.insert(readR);
				readR=source.readNextRecord();
				
			}
			
			
		}
		RecordStore[] stores=mediator.getProductedStores();
//		LoserTree  lt=new LoserTree(stores);
		WinnerTree  lt=new WinnerTree(stores);
		
		Record least=lt.nextLeastRecord();
		ra.start();
		while(!least.isNull())
		{
			ra.acceptRecord(least);
			least=lt.nextLeastRecord();
		}
		ra.end();
		
		for(int i=0;i<stores.length;i++)
		{
			stores[i].destroy();
		}
	}
	
	
	public static void main(String[] args) throws IOException
	{
//		RecordStore store=new MemRecordStore(60004,true);
//		RunAcceptor mediator=new MemRunAcceptor();
//		ResultAcceptor ra=new MemResultAcceptor();
		ExternalSorter sorter=new ExternalSorter();
			
		RecordStore store=new FileRecordStore(PATH_NAME+"test_sort1.txt");
		RunAcceptor mediator=new FileRunAcceptor(PATH_NAME+"test_sort2.txt");
		ResultAcceptor ra=new FileRecordStore(PATH_NAME+"test_sorted.txt");
		
		
		sorter.sort(70, store, mediator, ra);
	}
	
}
