package com.theophane.payement.repository;


import com.theophane.payement.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByLicenseKey(String licenseKey);
}

