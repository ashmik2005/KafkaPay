package com.jbdl.KafkaWallet;


import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {

    private final static String TXN_CREATE_TOPIC = "txn_create";
    private final TransactionRepository transactionRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public String createTxn(Transaction transaction) {
        transaction.setTxnId(UUID.randomUUID().toString());
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        // Save txn in db as PENDING
        transactionRepository.save(transaction);

        // Initiatite txn_created event
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sender", transaction.getSenderId());
        jsonObject.put("receiver", transaction.getReceiverId());
        jsonObject.put("amount", transaction.getAmount());
        jsonObject.put("txnId", transaction.getTxnId());

        kafkaTemplate.send(TXN_CREATE_TOPIC, jsonObject.toJSONString());

        return transaction.getTxnId();
    }


}
