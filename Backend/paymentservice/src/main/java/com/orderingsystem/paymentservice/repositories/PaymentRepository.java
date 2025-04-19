package com.orderingsystem.paymentservice.repositories;

import com.orderingsystem.paymentservice.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByUserId(String userId);
}
