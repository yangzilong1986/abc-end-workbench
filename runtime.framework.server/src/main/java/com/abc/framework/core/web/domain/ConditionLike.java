package com.abc.framework.core.web.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConditionLike implements Serializable{

	private Map<String,Object> conditionLikes=new HashMap<String,Object>();

	public ConditionLike(String likeKey,Object likeValue){
		conditionLikes.put(likeKey,likeValue);
	}
	
	public void addConditionLike(String likeKey,Object likeValue){
		conditionLikes.put(likeKey,likeValue);
	}

	public Map<String, Object> getConditionLikes() {
		return conditionLikes;
	}

	public void setConditionLikes(Map<String, Object> conditionLikes) {
		this.conditionLikes = conditionLikes;
	}

	public String decodeConditionLike(){
		if(conditionLikes.isEmpty()){
			return null;
		}
		Iterator<String> interator=conditionLikes.keySet().iterator();
		StringBuilder builder=null;
		boolean isFirst=true;
		while(interator.hasNext()){
			if(isFirst){
				builder=new StringBuilder();
				isFirst=false;
			}
			String key=interator.next();
			Object value=conditionLikes.get(key);
			if(value!=null){
				builder.append(key);
				builder.append(" like '%");
				builder.append(value);
				builder.append("%' and ");
			}
		}
		if(builder!=null){
			String like=builder.toString();
			like=like.substring(0, like.lastIndexOf(" and "));
			return like;
		}
		return null;
	}
}
