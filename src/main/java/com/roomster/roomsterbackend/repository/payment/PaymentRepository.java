package com.roomster.roomsterbackend.repository.payment;

import com.roomster.roomsterbackend.entity.PaymentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    List<PaymentEntity> findAllByUserPayment_IdOrderByCreatedDate(Long userId , Pageable pageable);
    Long countPaymentEntitiesByUserPayment_Id(Long userId);

    @Query(value = "Select month(pt.tran_date) as month, sum(p.paid_amount) as totalPrice \n" +
            "From payment as p inner join payment_transaction as pt\n" +
            "where p.id = pt.payment_id and pt.tran_status = 0\n" +
            "Group by Month(pt.tran_date)\n" +
            "Order by month(pt.tran_date)", nativeQuery = true)
    List<Object[]> getTotalPaymentTransactionByMonth();
}
