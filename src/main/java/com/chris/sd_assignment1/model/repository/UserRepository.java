package com.chris.sd_assignment1.model.repository;

import com.chris.sd_assignment1.model.entities.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}