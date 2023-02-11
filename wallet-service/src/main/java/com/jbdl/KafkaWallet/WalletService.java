package com.jbdl.KafkaWallet;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private static final String USER_CREATE_TOPIC = "user_create";
    private static final String TXN_CREATE_TOPIC = "txn_create";
    private static final String WALLET_UPDATE_TOPIC = "wallet_update";

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Value("${user.onboarding.amount}")
    Double onboardingAmount;

    @KafkaListener(topics = {USER_CREATE_TOPIC}, groupId = "jbdl27_grp")
    public void walletCreate(String message) throws Exception {
        // message = data produced by USER CREATE event
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);

        if (!jsonObject.containsKey("userId")) {
            throw new Exception("userId does not exist, hence can't create wallet");
        }


        Long userId = (Long) jsonObject.get("userId");

        // Now we know for which user the wallet has to be created
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(onboardingAmount)
                .build();

        walletRepository.save(wallet);

    }

    @KafkaListener(topics = {TXN_CREATE_TOPIC}, groupId = "jdbl27_grp")
    public void walletUpdate(String message) throws Exception {

        JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);

        if (!jsonObject.containsKey("sender") ||
                !jsonObject.containsKey("receiver") ||
                !jsonObject.containsKey("amount") ||
                !jsonObject.containsKey("txnId")) {
            throw new Exception("Some details from txn_create event are missing");
        }

        Long senderId   =  (Long) jsonObject.get("sender");
        Long receiverId =  (Long) jsonObject.get("receiver");
        Double amount   =  (Double) jsonObject.get("amount");
        String txnId    =  (String) jsonObject.get("txnId");

        // Here, we also have to publish an event -> WALLET_UPDATE
        JSONObject walletUpdateEvent = new JSONObject();
        walletUpdateEvent.put("txnId", txnId);

        Wallet senderWallet = walletRepository.findByUserId(senderId);

        if (senderWallet.getBalance() < amount) {
            // wallet update failed, fire event to txn_service
            walletUpdateEvent.put("status", "FAILED");

        } else {
            // wallet update successful, fire event to txn_service
            walletRepository.updateWallet(senderId, 0  -amount);
            walletRepository.updateWallet(receiverId, amount);
            walletUpdateEvent.put("status", "SUCCESS");
        }

        kafkaTemplate.send(WALLET_UPDATE_TOPIC, walletUpdateEvent.toJSONString());


    }

}
