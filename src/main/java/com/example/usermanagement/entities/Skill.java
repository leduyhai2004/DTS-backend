package com.example.usermanagement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "skills")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message="Name must not be blank")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "skills")
    @JsonIgnore
    private Set<User> users;
}
