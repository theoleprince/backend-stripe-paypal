package com.theophane.payement.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.theophane.payement.dto.PaymentResponse;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    public PaymentResponse parsePaymentResponse(JsonObject jsonResponse) {
        Gson gson = new Gson();
        return gson.fromJson(jsonResponse, PaymentResponse.class);
    }
}
