package com.abc.framework.core.web.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class PageQuery extends PageRequest {

	private static final long serialVersionUID = 1028891669937942921L;

	private PageSort pageSort;
	//Page<UserModel> p =  ur.findAll(new 
	//PageRequest(0,2,new Sort(new Order(Direction. DESC,"uuid"))));
	public PageQuery(){
		this(0, 10);
	}
	
	public PageQuery(int page, int size) {
		super(page, size);
	}

	public PageQuery(int page, int size, PageSort pageSort) {
		super(page, size);
		this.pageSort=pageSort;
	}
	
	public PageQuery(int page, int size, Sort sort) {
		super(page, size, sort);
	}

	public PageQuery(int page, int size, Direction direction, String... properties) {
		super(page, size, direction, properties);
	}

	public PageSort getPageSort() {
		return pageSort;
	}

	public void setPageSort(PageSort pageSort) {
		this.pageSort = pageSort;
	}
	
	public Pageable decodePageRequest(){
		Sort sort=pageSort.decodeSort();
		if(sort!=null){
			return new PageRequest(this.getPageNumber(),
						this.getPageSize(),sort);
		}else{
			return new PageRequest(this.getPageNumber(),
					this.getPageSize());
		}
	}

}
