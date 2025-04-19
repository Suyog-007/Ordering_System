package com.orderingsystem.orderservice.repository;




import com.orderingsystem.orderservice.entities.OrderRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderRequest, Long> {
    // Additional query methods can be added if necessary
}
