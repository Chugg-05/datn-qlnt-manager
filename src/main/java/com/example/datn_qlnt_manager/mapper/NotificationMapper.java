package com.example.datn_qlnt_manager.mapper;

import com.example.datn_qlnt_manager.dto.request.notification.NotificationCreationRequest;
import com.example.datn_qlnt_manager.dto.request.notification.NotificationUpdateRequest;
import com.example.datn_qlnt_manager.dto.response.notification.NotificationResponse;
import com.example.datn_qlnt_manager.entity.Notification;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toNotification(NotificationCreationRequest request);

    @Mapping(source = "user.fullName", target = "fullName")
    NotificationResponse toResponse(Notification entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateNotificationFromRequest(NotificationUpdateRequest request, @MappingTarget Notification notification);
}

