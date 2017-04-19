package com.abc.framework.core.api;

import java.io.Serializable;

import com.abc.framework.core.web.domain.ConditionLike;
import com.abc.framework.core.web.domain.ConditionOr;

/**
 * 主键标识
 * @author LiuJQ
 */
public interface Identifiable extends Serializable {
	/**
	 * 获取主键
	 * @author LiuJQ
	 * @return
	 */
	public String getDefaultId();

	
	/**
	 * 设置ID属性
	 * @param id
	 */
	public void setDefaultId(String defaultId);
	
	/**
	 * 设置Like查询添加
	 * @param id
	 */
	public void setConditionLike(ConditionLike conditionLike);
	
	/**
	 * 获取Like条件
	 * @author LiuJQ
	 * @return
	 */
	public ConditionLike getConditionLike();
	
	/**
	 * 设置Or查询添加
	 * @param id
	 */
	public void setConditionOr(ConditionOr conditionOr);
	
	/**
	 * 获取Or条件
	 * @author LiuJQ
	 * @return
	 */
	public ConditionOr getConditionOr();
}
