package com.mwf9.projet.repository;

import com.mwf9.projet.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
