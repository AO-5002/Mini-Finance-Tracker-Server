package org.example.mini_finance_tracker_backend.repositories;

import org.example.mini_finance_tracker_backend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByAuth0Id(String auth0Id);
}
