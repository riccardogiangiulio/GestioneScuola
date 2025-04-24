package com.riccardo.giangiulio.gestionescuola.dto.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import com.riccardo.giangiulio.gestionescuola.dto.model.UserDTO;
import com.riccardo.giangiulio.gestionescuola.model.User;

public class UserMapperDTO {

    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), RoleMapperDTO.toDTO(user.getRole()));
    }

    public static Set<UserDTO> toDTOSet(Set<User> users) {
        return users.stream()
            .map(UserMapperDTO::toDTO)
            .collect(Collectors.toSet());
    }
}
