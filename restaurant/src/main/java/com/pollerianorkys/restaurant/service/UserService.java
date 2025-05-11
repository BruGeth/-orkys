package com.pollerianorkys.restaurant.service;

import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.User;

/**
 *
 * @author Brunoo
 */
public interface UserService {

    User registerUser(UserRegistrationDto dto);

}
