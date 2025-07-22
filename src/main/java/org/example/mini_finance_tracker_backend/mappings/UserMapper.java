package org.example.mini_finance_tracker_backend.mappings;

import org.example.mini_finance_tracker_backend.dtos.UserDto;
import org.example.mini_finance_tracker_backend.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(UserEntity user);
    UserEntity userDtoToUser(UserDto userDto);
}
