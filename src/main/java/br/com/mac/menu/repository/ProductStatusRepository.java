package br.com.mac.menu.repository;

import br.com.mac.menu.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStatusRepository extends JpaRepository<ProductStatus, Integer> {
    @Query("SELECT p FROM ProductStatus p WHERE p.prdId IN (:ids)")
    List<ProductStatus> findByProductStatusIds(@Param("ids")List<Integer> ids);
}
