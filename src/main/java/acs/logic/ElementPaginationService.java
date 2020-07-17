package acs.logic;

import java.util.List;

import acs.boundaries.ElementBoundary;

public interface ElementPaginationService extends ElementRelationshipService {

    public List<ElementBoundary> getAll(String userDomain, String userEmail, int size, int page);

    public List<ElementBoundary> getChildren(String userDomain, String userEmail, String elementDomain,
            String elementId, int size, int page);

    public List<ElementBoundary> getParents(String userDomain, String userEmail, String elementDomain, String elementId,
            int size, int page);

    public List<ElementBoundary> getAllByName(String userDomain, String userEmail, String name, int size, int page);

    public List<ElementBoundary> getAllByType(String userDomain, String userEmail, String type, int size, int page);

    public List<ElementBoundary> getAllByLocation(String userDomain, String userEmail, String lat, String lng,
            String distance, int size, int page);

}