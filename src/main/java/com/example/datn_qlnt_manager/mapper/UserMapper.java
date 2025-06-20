package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateForAdminRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserResponse;
import com.example.datn_qlnt_manager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userStatus", ignore = true) // bỏ qua trường này khi ánh xạ
    @Mapping(target = "profilePicture", ignore = true)
    User toUser(UserCreationRequest request); // chuyển đổi UserCreationRequest sang User

    UserResponse toUserResponse(User user);

    @Mapping(source = "roles", target = "roles")
    UserDetailResponse toUserDetailResponse(User user); // chuyển đổi từ  User sang UserDetailResponse

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "email", ignore = true)
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE) // nếu thuộc tinh trong request là null thì khong cập nhật
    void updateUser(
            UserUpdateRequest request,
            @MappingTarget User user); // @MappingTarget: chỉ định đối tượng sẽ được cập nhật thay vì tạo mới

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) // nếu thuộc tính trong request là null thì không cập nhật
    void updateUserForAdmin(UserUpdateForAdminRequest request, @MappingTarget User user);
}
