package acs.logic;

import java.util.List;

import acs.data.ActionEntity;
import acs.data.ElementEntity;
import acs.data.UserEntity;

public interface DatabaseService {

    public List<UserEntity> getAllUsers();

    public List<ElementEntity> getAllElements();

    public List<ActionEntity> getAllActions();
}