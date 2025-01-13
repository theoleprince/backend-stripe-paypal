package com.theophane.payement.services;

import com.theophane.payement.entity.License;
import com.theophane.payement.repository.LicenseRepository;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LicenseService {

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private AESService aesService;

    // Générer une licence
    public License generateLicense(String clientName, String product, Long nbrUser, String expiryDate) throws Exception {
        License license = new License();
        license.setClientName(clientName);
        license.setProduct(product);
        license.setNbrUser(nbrUser);
        license.setExpiryDate(expiryDate);
        license.setActivated(false);

        // Chiffrer l'objet License en clé de licence
        String licenseKey = aesService.encrypt(license);

        license.setLicenseKey(licenseKey);
        return licenseRepository.save(license);
    }

    // Valider une licence
    public License validateLicense(String licenseKey) throws Exception {
        License license = licenseRepository.findByLicenseKey(licenseKey)
                .orElseThrow(() -> new RuntimeException("Licence invalide"));

        if (license.isActivated()) {
            throw new RuntimeException("Licence déjà activée.");
        }

        // Déchiffrer et vérifier l'objet
        License decryptedLicense = aesService.decrypt(licenseKey, License.class);

        // Valider les données (exemple : comparaison clientName ou produit)
        if (!license.getClientName().equals(decryptedLicense.getClientName())) {
            throw new RuntimeException("Données de licence non valides.");
        }

        license.setActivated(true);
        licenseRepository.save(license);
        return license;
    }

    // Déchiffrer une licence
    public License decryptLicense(String licenseKey) throws Exception {
        return aesService.decrypt(licenseKey, License.class);
    }
}
