package com.abc.framework.core.api;

import com.abc.framework.core.web.domain.ConditionLike;
import com.abc.framework.core.web.domain.ConditionOr;

public abstract class DefaultBeanIdentifiable implements Identifiable {

	private static final long serialVersionUID = 2035315960940308133L;

	private ConditionLike conditionLike;
	private ConditionOr conditionOr;

	@Override
	public String getDefaultId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultId(String defaultId) {
			
	}

	@Override
	public void setConditionLike(ConditionLike conditionLike) {
		this.conditionLike=conditionLike;
		
	}

	@Override
	public ConditionLike getConditionLike() {
		// TODO Auto-generated method stub
		return this.conditionLike;
	}
	
	/**
	 * 设置Or查询添加
	 * @param id
	 */
	public void setConditionOr(ConditionOr conditionOr){
		this.conditionOr=conditionOr;
	}
	
	/**
	 * 获取Or条件
	 * @author LiuJQ
	 * @return
	 */
	public ConditionOr getConditionOr(){
		return conditionOr;
	}

}
