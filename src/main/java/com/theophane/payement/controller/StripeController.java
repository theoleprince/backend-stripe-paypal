package com.theophane.payement.controller;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
@CrossOrigin(origins = "*")  // Permet l'accès depuis Angular
public class StripeController {

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> request) {
        try {
            // Conversion explicite de 'amount' et 'quantity' en Long
            Long amount = Long.valueOf((Integer) request.get("amount"));  // amount doit être en centimes, par exemple : 5000 = 50.00 EUR
            Long quantity = Long.valueOf((Integer) request.get("quantity"));  // quantity doit être de type Long

            // Créer les données du produit
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName((String) request.get("productName"))
                            .build();

            // Créer les informations de prix
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency((String) request.get("currency"))
                            .setUnitAmount(amount)  // Montant du produit
                            .setProductData(productData)
                            .build();

            // Créer un élément de ligne avec la quantité
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(quantity)  // Quantité en Long
                            .setPriceData(priceData)
                            .build();

            // Créer les paramètres de la session Stripe
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl((String) request.get("successUrl"))  // URL de succès
                            .setCancelUrl((String) request.get("cancelUrl"))    // URL d'annulation
                            .addLineItem(lineItem)
                            .build();

            // Créer la session Stripe
            Session session = Session.create(params);

            // Retourner l'ID de la session pour la redirection
            Map<String, String> response = new HashMap<>();
            response.put("id", session.getId());
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Erreur Stripe : " + e.getMessage());
        }
    }
}
