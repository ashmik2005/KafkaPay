package com.jbdl.KafkaWallet;

import lombok.AllArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {


    private static final String USER_CREATE_TOPIC = "user_create";
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public User createUser(User user) {
       user = userRepository.save(user);

        // TODO: Add code for wallet addition

        // Create a JSON Object with data we want to publish
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", user.getId());
        jsonObject.put("userEmail", user.getEmail());
        jsonObject.put("userContact", user.getContact());

        kafkaTemplate.send(USER_CREATE_TOPIC, jsonObject.toJSONString());

        return user;
    }
}
