package acs.logic;

import java.util.List;

import acs.boundaries.ActionBoundary;

public interface ActionPaginationService extends ActionService {

    public List<ActionBoundary> getAllActions(String adminDomain, String adminEmail, int size, int page);

}