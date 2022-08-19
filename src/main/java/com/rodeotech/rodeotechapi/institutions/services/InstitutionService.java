package com.rodeotech.rodeotechapi.institutions.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rodeotech.rodeotechapi.institutions.dto.CreateInstitutionRequest;
import com.rodeotech.rodeotechapi.institutions.dto.InstitutionResponse;
import com.rodeotech.rodeotechapi.institutions.exceptions.InstitutionExistsException;
import com.rodeotech.rodeotechapi.institutions.models.Institution;
import com.rodeotech.rodeotechapi.institutions.repository.InstitutionRepository;
import com.rodeotech.rodeotechapi.users.exceptions.UserNotFoundException;
import com.rodeotech.rodeotechapi.users.models.User;
import com.rodeotech.rodeotechapi.users.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class InstitutionService {
    private final InstitutionRepository institutionRepository;
    private final UserService userService;

    public List<InstitutionResponse> getAllInstitutions() throws Exception {
        log.info("getAllInstitutions called");
        List<Institution> institutions = this.institutionRepository.findAll();
        return institutions.stream().map(this::convertToResponse).toList();
    }

    public InstitutionResponse create(Long adminId, CreateInstitutionRequest institutionRequest) throws Exception {
        log.info("Received institutionRequest {} for adminId {}", institutionRequest.getName(), adminId);
        Institution prevInstitution = this.institutionRepository.findByName(institutionRequest.getName());
        if (prevInstitution != null) {
            throw new InstitutionExistsException(institutionRequest.getName());
        }
        User adminUser = this.userService.getUser(adminId);
        if (adminUser == null) {
            throw new UserNotFoundException(adminId);
        }
        Institution institution = this.institutionRepository.save(
                Institution
                        .builder()
                        .name(institutionRequest.getName())
                        .admin(adminUser)
                        .build());
        return this.convertToResponse(institution);
    }

    private InstitutionResponse convertToResponse(Institution institution) {
        User adminUser = institution.getAdmin();
        String adminName = null;
        if (adminUser != null) {
            adminName = adminUser.getUsername();
        }
        log.info("convertToResponse adminName {} found for institution {}", adminName, institution.getName());
        return InstitutionResponse
                .builder()
                .id(institution.getId())
                .name(institution.getName())
                .adminName(adminName)
                .build();
    }
}
