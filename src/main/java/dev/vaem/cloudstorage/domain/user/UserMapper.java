package dev.vaem.cloudstorage.domain.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.Instant.now())")
    User createRequestToEntity(UserCreateRequest userRequest);

    void updateUser(@MappingTarget User user, UserUpdateRequest userRequest);

    UserAccount entityToAccount(User user);

}
