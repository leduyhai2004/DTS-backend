package com.example.usermanagement.service;

import com.example.usermanagement.dto.request.CreateRoleRequest;
import com.example.usermanagement.dto.request.UpdateRoleRequest;
import com.example.usermanagement.dto.response.RoleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(CreateRoleRequest request);
    RoleResponse updateRole(Long id, UpdateRoleRequest request);
    void deleteRole(Long id);
    RoleResponse getRoleById(Long id);
    RoleResponse getRoleByName(String name);
    List<RoleResponse> getAllRoles();
    Page<RoleResponse> getAllRoles(Pageable pageable);
    boolean existsByName(String name);
}
