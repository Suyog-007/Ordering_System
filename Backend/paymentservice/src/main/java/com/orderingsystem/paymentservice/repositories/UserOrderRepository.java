package com.orderingsystem.paymentservice.repositories;

import com.orderingsystem.paymentservice.entities.UserOrder;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrderRepository extends MongoRepository<UserOrder, String> {
    UserOrder findByUserId(String userId);
}

