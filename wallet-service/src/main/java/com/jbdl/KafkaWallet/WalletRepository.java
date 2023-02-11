package com.jbdl.KafkaWallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUserId(Long userId);

    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.user_id = :userId")
    void updateWallet(Long userId, Double amount);

}
