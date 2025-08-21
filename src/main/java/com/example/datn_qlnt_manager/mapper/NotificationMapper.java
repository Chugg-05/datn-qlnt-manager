package com.example.datn_qlnt_manager.mapper;

import org.mapstruct.*;

import com.example.datn_qlnt_manager.dto.request.notification.NotificationCreationRequest;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;
import com.example.datn_qlnt_manager.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "notificationUsers", ignore = true)
    @Mapping(target = "user", ignore = true)
    Notification toNotification(NotificationCreationRequest request);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    NotificationResponse toResponse(Notification entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotificationFromRequest(NotificationUpdateRequest request, @MappingTarget Notification notification);
}
