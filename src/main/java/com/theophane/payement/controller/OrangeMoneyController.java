package com.theophane.payement.controller;

import com.google.gson.JsonObject;
import com.theophane.payement.dto.PayRequestDTO;
import com.theophane.payement.dto.PaymentResponse;
import com.theophane.payement.services.OrangeMoneyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
public class OrangeMoneyController {

    private final OrangeMoneyService orangeMoneyService;

    public OrangeMoneyController(OrangeMoneyService orangeMoneyService) {
        this.orangeMoneyService = orangeMoneyService;
    }

    @PostMapping("/pay")
    public ResponseEntity<?> handlePayment(@RequestBody PayRequestDTO payRequestDTO) {
        try {
            String accessToken = orangeMoneyService.getAccessToken();
            String payToken = orangeMoneyService.initializePayment(accessToken);
            payRequestDTO.setPayToken(payToken);
            PaymentResponse paymentResponse = orangeMoneyService.pay(accessToken, payRequestDTO);

            return ResponseEntity.ok(paymentResponse);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/statut/{payToken}")
    public ResponseEntity<?> getStatus(@PathVariable String payToken) {
        try {
            // Obtenir le token d'accès
            String accessToken = orangeMoneyService.getAccessToken();

            // Récupérer le statut du paiement
            PaymentResponse status = orangeMoneyService.getPaymentStatus(accessToken, payToken);
            return ResponseEntity.ok(status);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de la récupération du statut: " + e.getMessage());
        }
    }
}
