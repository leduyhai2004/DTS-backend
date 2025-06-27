package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.request.CreateRoleRequest;
import com.example.usermanagement.dto.request.UpdateRoleRequest;
import com.example.usermanagement.dto.response.RoleResponse;
import com.example.usermanagement.entities.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public Role toEntity(CreateRoleRequest request) {
        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public Role toEntity(UpdateRoleRequest request) {
        return Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public RoleResponse toResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }

    public List<RoleResponse> toResponseList(List<Role> roles) {
        return roles.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
