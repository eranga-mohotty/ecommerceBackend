package com.emarket.emarket.services;

import com.emarket.emarket.entities.OrderHeader;
import com.emarket.emarket.entities.User;
import com.emarket.emarket.repositories.OrderHeaderRepository;
import com.emarket.emarket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    OrderHeaderRepository orderHeaderRepository;
    @Autowired
    @Value("${env.SQUARE_API_KEY}")
    private String SQUARE_ACCESS_TOKEN;


    public String handleAttemptCharge(String email, String password, String nonce){

        final String SQUARE_API_URL = "https://connect.squareupsandbox.com/v2/payments";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + SQUARE_ACCESS_TOKEN);

        // Create the request body
        String idempotencyKey = UUID.randomUUID().toString(); //replace with orderHeaderID
        String requestJson = String.format("{\"idempotency_key\": \"%s\", \"source_id\": \"%s\", \"amount_money\": {\"amount\": %s, \"currency\": \"AUD\"}}",
                idempotencyKey, nonce, 1); //Replace price with actual price from order Header
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

        // Make the POST request
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(SQUARE_API_URL, requestEntity, String.class);


        System.out.println("RESPONSE BODY = \n\n\n"+responseEntity.getBody());

        // Handle the response
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            // Payment successful, you can process the responseEntity.getBody()

            User user = userRepository.findByEmailAndPassword(email, password);
            Optional<List<OrderHeader>> orderHeaders = orderHeaderRepository.findByUserOrderByDateTimeDesc(user);
            OrderHeader orderHeader = orderHeaders.get().get(0);
            orderHeader.setPaid(true);
            orderHeaderRepository.save(orderHeader);

            return "Payment was Successful.";


        } else {
            // Payment failed, handle the error
            return "Payment failed.";
        }

    }

}
