package com.abc.core.data.datasource.sample.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.abc.core.data.sample.domain.Role;
@Repository("roleDaoJpa")
public interface RoleRepository extends PagingAndSortingRepository<Role,Long> {
	
}
