package com.abc.framework.core.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.ndlan.framework.core.web.domain.Result;

public interface BusinessController <T extends Identifiable, Q extends T> {

	/**
	 * 根据结果集中的一列作为key，将结果集转换成Map
	 * @param <K> 返回Map的key类型
	 * @param <V> 返回Map的Value类型
	 * @param mapKey 返回结果List中‘mapKey’属性值作为Key
	 *  (The property to use as key for each value in the list.)
	 * @return Map containing key pair data. 
	 */
	public Result selectAll();
	/**
	 * 根据结果List
	 * @param mapKey 返回结果List中‘mapKey’属性值作为Key
	 *  (The property to use as key for each value in the list.)
	 * @return Map containing key pair data. 
	 */	
	public Result viewOne(String defualtId);
	
	

	/**
	 * 根据结果集中的List
	 * @param mapKey 返回结果List中‘mapKey’属性值作为Key
	 *  (The property to use as key for each value in the list.)
	 * @return Map containing key pair data. 
	 */
	public Result selectList( Q query);



	/**
	 * 根据结果集中的一列作为key，将结果集转换成Map
	 * @param <K> 返回Map的key类型
	 * @param <V> 返回Map的Value类型
	 * @param mapKey 返回结果List中‘mapKey’属性值作为Key
	 *  (The property to use as key for each value in the list.)
	 * @return Map containing key pair data. 
	 */
	public Result selectMap(Q query);

	
	/**
	 * 根据结果集中的一列作为key，将结果集转换成Map
	 * @param <K> 返回Map的key类型
	 * @param <V> 返回Map的Value类型
	 * @param query 查询参数,如果未null则查询所有对象
	 * @param mapKey 返回结果List中‘mapKey’属性值作为Key (The property to use as key for each value in the list.)
	 * @return Map 包含key属性值的Map对象
	 */
	public Result selectMap(Q query, String mapKey);

	/**
	 * 查询总记录数
	 * @return long 记录总数
	 * @date 2014年3月3日下午5:35:36
	 */
	public Result selectCount();

	/**
	 * 查询记录数
	 * @param query 查询对象，如果为null，则查询对象总数
	 * @return long 记录总数
	 */
	public Result selectCount(Q query);


	public Result addOne(T entity);
	

	/**
	 * 根据Id删除对象
	 * @param id  要删除的ID，不能为null
	 * @return int 受影响结果数
	 */
	public Result deleteById(String id);

	/**
	 * 删除所有
	 * @return int 受影响结果数
	 */
	public Result deleteAll();

	
	public Result deleteList( String[] ids) ;
	
	/**
	 * 删除对象
	 * @param entity 要删除的实体对象，不能为null
	 * @return int 受影响结果数
	 */
	public Result deleteOne( String defualtId);
	
	public Result deleteByQuery(Q query);

	
	/**
	 * 更新对象，对象必须设置ID
	 * @param entity 实体的Id不能为null
	 * @return int 受影响结果数
	 */
	public Result editOne(T entity) ;
	
	/**
	 * 更新对象中已设置的字段，未设置的字段不更新
	 * @param entity 要更新的实体对象，不能为null，切ID必须不为null
	 * @return int 受影响结果数
	 */
	public Result editSelective(T entity) ;
	
	
	/**
	 * 根据id，批量删除记录，如果传入的列表为null或为空列表则直接返回
	 * @param idList 批量删除ID列表
	 */
	public Result deleteByIdInBatch(List<String> idList);

	/**
	 * 批量插入，如果为空列表则直接返回
	 * @param entityList 需要批量插入的实体对象列表
	 */
	public Result addInBatch(List<T> entityList);

	/**
	 * 批量更新，改方法根据实体ID更新已设置的字段，未设置的字段不更新
	 * @param entityList 批量更新的实体对象列表
	 */
	public Result editInBatch(List<T> entityList);

		
}
