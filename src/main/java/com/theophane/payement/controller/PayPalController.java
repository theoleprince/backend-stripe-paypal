package com.theophane.payement.controller;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@CrossOrigin(origins = "*")
public class PayPalController {

    @Autowired
    private APIContext apiContext;

    @PostMapping("/create-payment")
    public Map<String, String> createPayment(@RequestBody Map<String, Object> request) {
        try {
            Amount amount = new Amount();
            amount.setCurrency((String) request.get("currency"));

            // Convert amount to double before formatting
            double amountValue = Double.parseDouble(request.get("amount").toString());
            amount.setTotal(String.format("%.2f", amountValue));

            Transaction transaction = new Transaction();
            transaction.setDescription((String) request.get("productName")); // Fixing key to match your request body
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl((String) request.get("cancelUrl"));
            redirectUrls.setReturnUrl((String) request.get("successUrl"));
            payment.setRedirectUrls(redirectUrls);

            Payment createdPayment = payment.create(apiContext);
            Map<String, String> response = new HashMap<>();
            response.put("link", createdPayment.getLinks().get(1).getHref());
            return response; // Return approval URL

        } catch (PayPalRESTException e) {
            throw new RuntimeException("Erreur PayPal : " + e.getMessage());
        }
    }


    @GetMapping("/execute-payment")
    public String executePayment(@RequestParam String paymentId, @RequestParam String payerId) {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);

            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            return executedPayment.toJSON();
        } catch (PayPalRESTException e) {
            throw new RuntimeException("Erreur PayPal : " + e.getMessage());
        }
    }
}
