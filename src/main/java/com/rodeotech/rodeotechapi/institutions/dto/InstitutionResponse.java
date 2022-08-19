package com.rodeotech.rodeotechapi.institutions.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionResponse {
    private Long id;
    private String name;
    private String adminName;
}
