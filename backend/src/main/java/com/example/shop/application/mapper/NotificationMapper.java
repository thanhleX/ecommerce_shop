package com.example.shop.application.mapper;

import com.example.shop.application.dto.response.NotificationResponse;
import com.example.shop.domain.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}
