package com.abc.framework.core.utils;

import org.springframework.data.domain.Pageable;

import com.abc.framework.core.web.domain.ConditionLike;
import com.abc.framework.core.web.domain.ConditionOr;
import com.abc.framework.core.web.domain.PageQuery;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

/**
 * 对Bean进行操作的相关工具方法
 * @author LiuJQ
 *
 */
public class BeanUtils {
	/**
	 * 将Bean对象转换成Map对象，将忽略掉值为null或size=0的属性
	 * @param obj 对象
	 * @return 若给定对象为null则返回size=0的map对象
	 */
	public static Map<String, Object> toMap(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj == null) {
			return map;
		}
		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			// 转换时会将类名也转换成属性，此处去掉
			if (value != null && !name.equals("class")) {
				map.put(name, value);
			}
		}
		return map;
	}
	
	public static Boolean isEmpty(Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj == null) {
			return false;
		}
		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			// 转换时会将类名也转换成属性，此处去掉
			if (value != null && !name.equals("class")) {
				return false;
			}
		}
		return true;
	}
	public static Map<String, Object> toFilterConditionOr(Object obj,String key,String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj == null||key==null||condition==null) {
			return map;
		}
		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		ConditionOr conditionOr=null;
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			// 转换时会将类名也转换成属性，此处去掉
			if (value != null && !name.equals("class")&&name.contains(condition)) {
				if(conditionOr==null){
					conditionOr=new ConditionOr(name,value);
				}else{
					conditionOr.addConditionOr(name, value);
				}
				
			}
		}
		if(conditionOr!=null){
			map.put(key, conditionOr);
			return map;
		}
		return null;
	}
	
	public static Map<String, Object> toFilterConditionLike(Object obj,String key,String condition) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (obj == null||key==null||condition==null) {
			return map;
		}
		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		ConditionLike conditionLike=null;
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			// 转换时会将类名也转换成属性，此处去掉
			if (value != null && !name.equals("class")&&name.contains(condition)) {
				if(conditionLike==null){
					conditionLike=new ConditionLike(name,value);
				}else{
					conditionLike.addConditionLike(name, value);
				}
				
			}
		}
		if(conditionLike!=null){
			map.put(key, conditionLike);
			return map;
		}
		return null;
	}
	/**
	 * 获取某个对象指定类的值
	 * @param obj
	 * @param clzz
	 * @return
	 */
	public static Object gainChildObject(Object obj,Class<?> clzz) {
		
		if (obj == null) {
			return null;
		}
		BeanMap beanMap = new BeanMap(obj);
		Iterator<String> it = beanMap.keyIterator();
		while (it.hasNext()) {
			String name = it.next();
			Object value = beanMap.get(name);
			if(value==null){
				continue;
			}
			if(value instanceof Pageable){
				System.out.println("需要分页处理");
			}
			Class c=value.getClass();
			if(value != null &&c.equals(clzz)){
				return value;
			}

		}
		return null;
	}

	/**
	 * 该方法将给定的所有对象参数列表转换合并生成一个Map，对于同名属性，依次由后面替换前面的对象属性
	 * @param objs 对象列表
	 * @return 对于值为null的对象将忽略掉
	 */
	public static Map<String, Object> toMap(Object... objs) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Object object : objs) {
			if (object != null) {
				map.putAll(toMap(object));
			}
		}
		return map;
	}

	/**
	 * 获取接口的泛型类型，如果不存在则返回null
	 * @param clazz
	 * @return
	 */
	public static Class<?> getGenericClass(Class<?> clazz) {
		Type t = clazz.getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			Type[] p = ((ParameterizedType) t).getActualTypeArguments();
			return ((Class<?>) p[0]);
		}
		return null;
	}
}