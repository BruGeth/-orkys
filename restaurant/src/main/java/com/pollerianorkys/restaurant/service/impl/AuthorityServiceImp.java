package com.pollerianorkys.restaurant.service.impl;

import com.pollerianorkys.restaurant.model.Authority;
import com.pollerianorkys.restaurant.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorityServiceImp {

    private final AuthorityRepository authorityRepository;

    @Autowired
    public AuthorityServiceImp(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }


    public Authority getAuthorityById(Long id){
        return authorityRepository.getAuthorityById(id);
    }


}


