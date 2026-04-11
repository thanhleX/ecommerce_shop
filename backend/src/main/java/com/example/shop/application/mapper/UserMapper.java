package com.example.shop.application.mapper;

import com.example.shop.application.dto.response.UserResponse;
import com.example.shop.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toUserResponse(User user);

    default java.util.Set<String> mapRoles(java.util.Set<com.example.shop.domain.entity.Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(r -> "ROLE_" + r.getName())
                .collect(java.util.stream.Collectors.toSet());
    }
}
