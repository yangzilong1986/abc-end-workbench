package com.abc.framework.core.web.domain;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
public class PageOrder implements java.io.Serializable {

	private static final long serialVersionUID = 8485365139834399011L;
	private  Direction direction;
	private  String property;
	public PageOrder(){
		
	}
	public PageOrder(String property){
		this.property=property;
	}
	public PageOrder(Direction direction,String property){
		this.direction=direction;
		this.property=property;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
   
	public Order decodeOrder(){
		if(this.property!=null&&this.property!=null){
			return new Order(this.direction,this.property);
		}else if(this.property!=null){
			return new Order(this.property);
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "PageOrder [direction=" + direction + ", property=" + property + "]";
	}

}
