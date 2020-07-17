package acs.dal;

import org.springframework.data.repository.PagingAndSortingRepository;

import acs.data.UserEntity;
import acs.data.sub.UserIdPk;

public interface UserDao extends PagingAndSortingRepository<UserEntity, UserIdPk> {
}