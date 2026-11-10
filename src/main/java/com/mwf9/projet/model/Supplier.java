package com.mwf9.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/** A supplier. Suppliers and products are linked by a ManyToMany relationship. */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Supplier email is required")
    @Email(message = "Supplier email must be valid")
    @Column(nullable = false)
    private String email;

    @Size(max = 30)
    private String phone;

    @ManyToMany(mappedBy = "suppliers")
    @JsonIgnore
    private Set<Product> products = new HashSet<>();
}
