package dev.vaem.cloudstorage.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import dev.vaem.cloudstorage.domain.user.User;
import dev.vaem.cloudstorage.domain.user.UserAccount;
import dev.vaem.cloudstorage.domain.user.UserRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreated", expression = "java(java.time.Instant.now())")
    User createRequestToEntity(UserRequest userRequest);

    UserAccount entityToAccount(User user);

}
