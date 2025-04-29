package com.velixa.user.service;

import java.util.List;
import java.util.Optional;

import com.velixa.user.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);
    Optional<UserDTO> getUserById(String id);
    List<UserDTO> getAllUsers();
    void deleteUser(String id);
    Optional<UserDTO> updateUser(String id, UserDTO userDTO);
}
