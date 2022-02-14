package br.com.mac.menu.repository;

import br.com.mac.menu.entity.ProductComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductComponentsRepository extends JpaRepository<ProductComponents, Integer> {
    List<ProductComponents> findByChildProducts(@Param("id") Integer id);
}
