package com.theophane.payement.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theophane.payement.dto.PayRequestDTO;
import com.theophane.payement.dto.PaymentResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class OrangeMoneyService {

    private static final String TOKEN_URL = "https://api-s1.orange.cm/token";
    private static final String INIT_PAYMENT_URL = "https://api-s1.orange.cm/omcoreapis/1.0.2/mp/init";
    private static final String PAY_URL = "https://api-s1.orange.cm/omcoreapis/1.0.2/mp/pay";
    private static final String STATUS_URL = "https://api-s1.orange.cm/omcoreapis/1.0.2/mp/paymentstatus/";

    private static final String USERNAME = "aV5hY7sYfekhKBzHpdXivC4xqW4a";
    private static final String PASSWORD = "9WsyI8TB9tXNttJ23XBauR4OHgUa";
    private static final String X_AUTH_TOKEN = "WU5PVEVIRUFEMjpAWU5vVGVIRUBEMlBST0RBUEk=";
    private static final String CHANNEL_USER_MSISDN = "659925094";
    private static final String PIN = "9523";

    public String getAccessToken() throws IOException {
        String authHeader = Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());

        HttpPost post = new HttpPost(TOKEN_URL);
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Authorization", "Basic " + authHeader);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
        post.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String json = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            return jsonObject.get("access_token").getAsString();
        }
    }


    public String initializePayment(String accessToken) throws IOException {
        HttpPost post = new HttpPost(INIT_PAYMENT_URL);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("X-AUTH-TOKEN", X_AUTH_TOKEN);
        post.setHeader("Content-Type", "application/json");

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String json = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonObject dataToken = jsonObject.getAsJsonObject("data");
            String payToken = dataToken.get("payToken").getAsString();

            return payToken;
        }
    }

    public PaymentResponse pay(String accessToken, PayRequestDTO payRequestDTO) throws IOException {
        HttpPost post = new HttpPost(PAY_URL);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("X-AUTH-TOKEN", X_AUTH_TOKEN);
        post.setHeader("Content-Type", "application/json");

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("notifUrl", payRequestDTO.getNotifUrl());
        requestBody.addProperty("channelUserMsisdn", CHANNEL_USER_MSISDN);
        requestBody.addProperty("amount", payRequestDTO.getAmount());
        requestBody.addProperty("subscriberMsisdn", payRequestDTO.getSubscriberMsisdn());
        requestBody.addProperty("pin", PIN);
        requestBody.addProperty("orderId", payRequestDTO.getOrderId());
        requestBody.addProperty("description", payRequestDTO.getDescription());
        requestBody.addProperty("payToken", payRequestDTO.getPayToken());

        post.setEntity(new StringEntity(requestBody.toString()));

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(post)) {

            String json = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(json).getAsJsonObject();

            // Convertir la réponse JSON en PaymentResponse
            PaymentService paymentService = new PaymentService();
            return paymentService.parsePaymentResponse(jsonResponse);
        }
    }

    public PaymentResponse getPaymentStatus(String accessToken, String payToken) throws IOException {
        String url = STATUS_URL + payToken;

        HttpGet get = new HttpGet(url);
        get.setHeader("Authorization", "Bearer " + accessToken);
        get.setHeader("X-AUTH-TOKEN", X_AUTH_TOKEN);

        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(get)) {

            String json = EntityUtils.toString(response.getEntity());
            JsonObject jsonResponse = JsonParser.parseString(json).getAsJsonObject();

            // Convertir la réponse JSON en PaymentResponse
            PaymentService paymentService = new PaymentService();
            return paymentService.parsePaymentResponse(jsonResponse);
        }
    }

}
