package com.abc.core.data.datasource.sample.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.abc.core.data.sample.domain.User;
@Repository("userDaoJpa")
public interface UserRepository  extends PagingAndSortingRepository<User,Long> {


}
