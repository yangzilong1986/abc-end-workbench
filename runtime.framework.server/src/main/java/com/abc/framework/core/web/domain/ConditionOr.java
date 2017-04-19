package com.abc.framework.core.web.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConditionOr implements Serializable {

	private static final long serialVersionUID = 5379594794116152511L;
	private Map<String,Object> conditionOrs=new HashMap<String,Object>();

	public ConditionOr() {
		// TODO Auto-generated constructor stub
	}
	
	public ConditionOr(String likeKey,Object likeValue){
		conditionOrs.put(likeKey,likeValue);
	}
	
	public void addConditionOr(String likeKey,Object likeValue){
		conditionOrs.put(likeKey,likeValue);
	}

	public String decodeConditionOr(){
		if(conditionOrs.isEmpty()){
			return null;
		}
		Iterator<String> interator=conditionOrs.keySet().iterator();
		StringBuilder builder=null;
		boolean isFirst=true;
		while(interator.hasNext()){
			if(isFirst){
				builder=new StringBuilder();
				isFirst=false;
			}
			String key=interator.next();
			Object value=conditionOrs.get(key);
			if(value!=null){
				builder.append(key);
				builder.append(" = '");
				builder.append(value);
				builder.append("' or ");
			}
		}
		if(builder!=null){
			String or=builder.toString();
			or=or.substring(0, or.lastIndexOf(" or "));
			return or;
		}
		return null;
	}
}
