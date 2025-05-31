package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(UserUpdateRequest request, @MappingTarget User user);

    @Mapping(source = "roles", target = "roles")
    UserDetailResponse toUserResponse(User user);
}
