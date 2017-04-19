package com.abc.core.data.datasource.sample.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.abc.core.data.sample.domain.Authority;
@Repository
public interface AuthorityRepository extends PagingAndSortingRepository<Authority,Long> {
}
