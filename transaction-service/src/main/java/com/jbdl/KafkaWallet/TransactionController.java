package com.jbdl.KafkaWallet;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/txn")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public String createTxn(@Valid TransactionCreateRequest transactionCreateRequest) {
        // return external txn id
        String txnId =  transactionService.createTxn(transactionCreateRequest.to());
        return "Transaction created with id: " + txnId;
    }

}
