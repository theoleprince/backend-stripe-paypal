package com.theophane.payement.controller;

import com.theophane.payement.entity.License;
import com.theophane.payement.services.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/licenses")
public class LicenseController {
    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping("/generate")
    public ResponseEntity<License> generateLicense(@RequestBody Map<String, String> request) throws Exception {
        String clientName = request.get("clientName");
        String product = request.get("product");
        Long nbrUser = Long.valueOf(request.get("nbrUser"));
        String expiryDate = request.get("expiryDate");

        License license = licenseService.generateLicense(clientName, product, nbrUser, expiryDate);
        return ResponseEntity.ok(license);
    }

    @PostMapping("/validate")
    public ResponseEntity<License> validateLicense(@RequestBody Map<String, String> request) throws Exception {
        String licenseKey = request.get("licenseKey");

        License license = licenseService.validateLicense(licenseKey);
        return ResponseEntity.ok(license);
    }

    // Déchiffrer une licence
    @PostMapping("/decrypt")
    public License decryptLicense(@RequestBody Map<String, String> request) throws Exception {
        String licenseKey = request.get("licenseKey");
        return licenseService.decryptLicense(licenseKey);
    }
}

