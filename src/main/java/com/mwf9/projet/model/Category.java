package com.mwf9.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** A product category. One category has many products (OneToMany). */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must be at most 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
}
