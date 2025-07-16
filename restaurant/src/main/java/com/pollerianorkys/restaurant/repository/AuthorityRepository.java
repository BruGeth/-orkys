package com.pollerianorkys.restaurant.repository;

import com.pollerianorkys.restaurant.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority,Long> {

    Authority getAuthorityById(Long id);
}
