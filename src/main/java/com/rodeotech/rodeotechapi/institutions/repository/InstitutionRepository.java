package com.rodeotech.rodeotechapi.institutions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rodeotech.rodeotechapi.institutions.models.Institution;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long> {

    Institution findByName(String name);
}
