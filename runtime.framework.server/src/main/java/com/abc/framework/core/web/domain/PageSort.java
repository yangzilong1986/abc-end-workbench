package com.abc.framework.core.web.domain;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort;

public class PageSort implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private List<PageOrder> pageOrders=new ArrayList<PageOrder>();
	public PageSort(){
		
	}
	public PageSort(List<PageOrder> orders){
		this.pageOrders=orders;
	}
	public List<PageOrder> getPageOrders() {
		return pageOrders;
	}
	public void setPageOrders(List<PageOrder> pageOrders) {
		this.pageOrders = pageOrders;
	}
	
	public void addOrder(PageOrder pageOrder){
		this.pageOrders.add(pageOrder);
	}

	public Sort decodeSort(){
		List<Order> orders=new ArrayList<Order>();
		if(this.pageOrders!=null){
			for(PageOrder pageOrder: this.pageOrders){
				Order o=pageOrder.decodeOrder();
				if(o!=null){
					orders.add(o);
				}
			}
			if(!orders.isEmpty()){
				return new Sort(orders);
			}
		}
		return null;
	}
}
