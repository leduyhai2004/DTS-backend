package com.example.usermanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "permissions")
public class Permission {
    @Id
    String name;

    String description;
}
