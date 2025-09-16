package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
<<<<<<< HEAD
    // This method will automatically be implemented by Spring Data JPA
    Optional<Category> findByName(String name);
}
=======
}
>>>>>>> d894a8dc0c82e3b5bfd10bfc2ec9ee207806ca54
