package com.cognizant.ecommerce.dao;

import com.cognizant.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
<<<<<<< HEAD
    Optional<Product> findByName(String name);
}
=======
}
>>>>>>> d894a8dc0c82e3b5bfd10bfc2ec9ee207806ca54
