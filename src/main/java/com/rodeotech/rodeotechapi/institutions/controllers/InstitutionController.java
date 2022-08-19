package com.rodeotech.rodeotechapi.institutions.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rodeotech.rodeotechapi.institutions.dto.CreateInstitutionRequest;
import com.rodeotech.rodeotechapi.institutions.dto.InstitutionResponse;
import com.rodeotech.rodeotechapi.institutions.services.InstitutionService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/api/institutions")
@AllArgsConstructor
public class InstitutionController {
    private final InstitutionService institutionService;

    @GetMapping("/find")
    public List<InstitutionResponse> getAllInstitutions() throws Exception {
        return this.institutionService.getAllInstitutions();
    }

    @PreAuthorize(value = "@userSecurity.userSpecificRequest(authentication, #adminId)")
    @PostMapping("/{adminId}")
    public InstitutionResponse createInstitution(@PathVariable("adminId") Long adminId,
            @RequestBody CreateInstitutionRequest institutionRequest) throws Exception {
        return this.institutionService.create(adminId, institutionRequest);
    }
}
