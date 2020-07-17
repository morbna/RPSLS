package acs.logic;

import java.util.List;

import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;

public interface ElementRelationshipService extends ElementService {

    public void bind(String managerDomain, String managerEmail, String elementDomain, String elementId,
            ElementIdBoundary elementIdBoundary);

    public List<ElementBoundary> getChildren(String userDomain, String userEmail, String elementDomain,
            String elementId);

    public List<ElementBoundary> getParents(String userDomain, String userEmail, String elementDomain,
            String elementId);
}
