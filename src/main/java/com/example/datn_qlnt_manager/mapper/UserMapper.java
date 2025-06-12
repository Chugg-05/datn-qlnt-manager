package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.UserCreationRequest;
import com.example.datn_qlnt_manager.dto.request.UserUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.UserDetailResponse;
import com.example.datn_qlnt_manager.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "userStatus", ignore = true) // bỏ qua trường này khi ánh xạ
    @Mapping(target = "profilePicture", ignore = true)
    User toUser(UserCreationRequest request); // chuyển đổi UserCreationRequest sang User

    @Mapping(source = "roles", target = "roles")
    UserDetailResponse toUserResponse(User user); // chuyển đổi từ  User sang UserDetailResponse

    //    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "email", ignore = true)
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE) // nếu thuộc tinh trong request là null thì khong cập nhật
    void updateUser(
            UserUpdateRequest request,
            @MappingTarget User user); // @MappingTarget: chỉ định đối tượng sẽ được cập nhật thay vì tạo mới
}
