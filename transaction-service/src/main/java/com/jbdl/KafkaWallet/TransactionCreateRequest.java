package com.jbdl.KafkaWallet;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionCreateRequest {

    @NotNull
    private Long senderId;

    @NotNull
    private Long receiverId;

    @NotNull
    private Double amount;

    private String purpose;

    public Transaction to() {
        return Transaction.builder()
                .senderId(this.senderId)
                .receiverId(this.receiverId)
                .amount(this.amount)
                .purpose(this.purpose)
                .build();
    }



}
