package com.abc.core.data.sample.domain;

public class Order  implements java.io.Serializable{

	private String item;
	private String number;
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	@Override
	public String toString() {
		return "Order [item=" + item + ", number=" + number + "]";
	}

}
