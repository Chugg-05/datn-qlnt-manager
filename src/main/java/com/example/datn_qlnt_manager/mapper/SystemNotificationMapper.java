package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.response.systemnotification.SystemNotificationResponse;
import com.example.datn_qlnt_manager.entity.SystemNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SystemNotificationMapper {
    @Mapping(source = "user.id", target = "userId")
    SystemNotificationResponse toResponse(SystemNotification entity);
}
