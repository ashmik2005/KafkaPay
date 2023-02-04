package com.jbdl.KafkaWallet;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    private static final String USER_CREATE_TOPIC = "user_create";

    @Autowired
    WalletRepository walletRepository;

    @Value("${user.onboarding.amount}")
    Double onboardingAmount;

    @KafkaListener(topics = {USER_CREATE_TOPIC}, groupId = "jbdl27_grp")
    public void walletCreate(String message) throws Exception {
        // message = data produced by USER CREATE event
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(message);

        if (!jsonObject.containsKey("userId")) {
            throw new Exception("userId does not exist, hence can't create wallet");
        }


        Long userId = (Long)jsonObject.get("userId");

        // Now we know for which user the wallet has to be created
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(onboardingAmount)
                .build();

        walletRepository.save(wallet);

    }

}
