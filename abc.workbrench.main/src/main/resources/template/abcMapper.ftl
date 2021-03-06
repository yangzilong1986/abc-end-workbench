<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${packageBase}.${packageModel}.${tableBean.tableNameCapitalized}${classSuffix}">
	
	<resultMap id="BaseResultMap" type="${packageBase}.${packageModel}.${tableBean.tableNameCapitalized}${classSuffix}">
	<#list tableBean.columnBeanList as columnBean>
		<#if columnBean.columnKey == "PRI" &&'' != columnBean.columnType>
		<id column="${columnBean.columnName}" property="${columnBean.columnNameNoDash}" jdbcType="${columnBean.columnDBType}" />
		</#if>
	</#list>
	
	<#list tableBean.columnBeanList as columnBean>
		<#if '' != columnBean.columnType && columnBean.columnKey != "PRI">
    
		<result column="${columnBean.columnName}" property="${columnBean.columnNameNoDash}" jdbcType="${columnBean.columnDBType}" />
		</#if>
	</#list>
	</resultMap>
	
	<sql id="Base_Column_List">
	<#list tableBean.columnBeanList as columnBean>
		${columnBean.columnName} as ${columnBean.columnNameNoDash} <#if columnBean_has_next>,</#if>
	</#list>
	</sql>
	
	
	<sql id="Base_Where_Clause">
		<where>
			<trim prefixOverrides="and">
			<#list tableBean.columnBeanList as columnBean>
		  	    <if test="${columnBean.columnNameNoDash} != null"> and ${columnBean.columnName} = #\{${columnBean.columnNameNoDash}\}</if>
				
			</#list>
				
			</trim>
		</where>
		<if test="sorting != null">order by $\{sorting\}</if>
		<if test="offset != null and limit != null">
			limit #\{offset\}, #\{limit\}
		</if>
	</sql>
	
	<!-- 查询总数 -->
	<select id="selectCount" resultType="java.lang.Long" parameterType="java.util.Map">
		select count(${tableBean.pkColumn})
		from ${tableBean.tableName}
		<include refid="Base_Where_Clause" />
	</select>
	
	<!-- 查询 -->
	<select id="select" resultMap="BaseResultMap" parameterType="java.util.Map">
		select
		<include refid="Base_Column_List" />
		from ${tableBean.tableName}
		<include refid="Base_Where_Clause" />
	</select>
	
	<!-- 根据ID查询 -->
	<select id="selectById" resultMap="BaseResultMap" parameterType="java.lang.String">
		select
		<include refid="Base_Column_List" />
		from ${tableBean.tableName}
		where ${tableBean.pkColumn} = #\{${tableBean.pkColumnName}\}
	</select>
	
	<!-- 根据ID删除 -->
	<delete id="deleteById" parameterType="java.lang.String">
		delete from ${tableBean.tableName}
		where  ${tableBean.pkColumn} = #\{${tableBean.pkColumnName}\}
	</delete>
	
	<!-- 删除 -->
	<delete id="delete" parameterType="java.util.Map">
		delete from sys_dictionary
		<include refid="Base_Where_Clause" />
	</delete>

	<!-- 添加 -->	
	<insert id="insert" parameterType="${packageBase}.${packageModel}.${tableBean.tableNameCapitalized}${classSuffix}">
		insert into ${tableBean.tableName}(
		<#list tableBean.columnBeanList as columnBean>
		  	    ${columnBean.columnName}  <#if columnBean_has_next>,</#if>
	
			</#list>
			
		)
		values (
		<#list tableBean.columnBeanList as columnBean>
		  	    #\{${columnBean.columnNameNoDash} \} <#if columnBean_has_next>,</#if>
	    </#list>
		)
	</insert>
	
	
	<!-- 通过ID更新 -->
	<update id="updateByIdSelective" parameterType="${packageBase}.${packageModel}.${tableBean.tableNameCapitalized}${classSuffix}">
		update ${tableBean.tableName}
		<set>
		<#list tableBean.columnBeanList as columnBean >
		    <#if columnBean.columnKey != "PRI">
		  	<if test=\"${columnBean.columnNameNoDash} != null\">  ${columnBean.columnName} = #\{${columnBean.columnNameNoDash}\} <#if columnBean_has_next>,</#if></if>
			</#if>
		</#list>
		</set>
		where  ${tableBean.pkColumn} = #\{${tableBean.pkColumnName}\}
	</update>
	
	<update id="updateById" parameterType="${packageBase}.${packageModel}.${tableBean.tableNameCapitalized}${classSuffix}">
		update ${tableBean.tableName}

		set 
		<#list tableBean.columnBeanList as columnBean >
		    <#if columnBean.columnKey != "PRI">
		  	 ${columnBean.columnName} = #\{${columnBean.columnNameNoDash}\} <#if columnBean_has_next>,</#if>
			</#if>
		</#list>
	
		where  ${tableBean.pkColumn} = #\{${tableBean.pkColumnName}\}
	</update>
	
</mapper>