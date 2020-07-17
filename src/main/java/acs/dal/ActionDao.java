package acs.dal;

import org.springframework.data.repository.PagingAndSortingRepository;

import acs.data.ActionEntity;
import acs.data.sub.ActionIdPk;

public interface ActionDao extends PagingAndSortingRepository<ActionEntity, ActionIdPk> {

}
