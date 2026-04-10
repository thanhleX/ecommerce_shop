package com.example.shop.application.mapper;

import com.example.shop.application.dto.response.UserResponse;
import com.example.shop.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "role.name", target = "role")
    UserResponse toUserResponse(User user);
}
