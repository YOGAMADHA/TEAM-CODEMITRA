package com.aicte.service;

import com.aicte.dto.request.CreateInstitutionRequest;
import com.aicte.dto.response.InstitutionDto;
import com.aicte.entity.Institution;
import com.aicte.exception.DuplicateResourceException;
import com.aicte.exception.ResourceNotFoundException;
import com.aicte.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final AuditService auditService;

    @Transactional
    public InstitutionDto createInstitution(CreateInstitutionRequest request, String createdBy) {
        if (institutionRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Institution already exists with code: " + request.getCode());
        }

        Institution institution = Institution.builder()
                .name(request.getName())
                .code(request.getCode())
                .type(request.getType())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPinCode())
                .phone(request.getPhone())
                .email(request.getEmail())
                .website(request.getWebsite())
                .affiliationDetails(request.getUniversityAffiliation())
                .active(true)
                .build();
        institution.setCreatedBy(createdBy);

        Institution saved = institutionRepository.save(institution);
        auditService.log("Institution", saved.getId(), "CREATE", createdBy, null, saved.getName(), null);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public InstitutionDto getInstitutionById(UUID id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with ID: " + id));
        return mapToDto(institution);
    }

    @Transactional(readOnly = true)
    public InstitutionDto getInstitutionByCode(String code) {
        Institution institution = institutionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with code: " + code));
        return mapToDto(institution);
    }

    @Transactional(readOnly = true)
    public Page<InstitutionDto> getAllInstitutions(Pageable pageable) {
        return institutionRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public InstitutionDto updateInstitution(UUID id, CreateInstitutionRequest request, String updatedBy) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with ID: " + id));

        institution.setName(request.getName());
        institution.setType(request.getType());
        institution.setAddress(request.getAddress());
        institution.setCity(request.getCity());
        institution.setState(request.getState());
        institution.setPincode(request.getPinCode());
        institution.setPhone(request.getPhone());
        institution.setEmail(request.getEmail());
        institution.setWebsite(request.getWebsite());
        institution.setAffiliationDetails(request.getUniversityAffiliation());
        institution.setUpdatedBy(updatedBy);

        Institution saved = institutionRepository.save(institution);
        auditService.log("Institution", saved.getId(), "UPDATE", updatedBy, null, saved.getName(), null);
        return mapToDto(saved);
    }

    public InstitutionDto mapToDto(Institution inst) {
        return InstitutionDto.builder()
                .id(inst.getId())
                .name(inst.getName())
                .code(inst.getCode())
                .type(inst.getType())
                .address(inst.getAddress())
                .city(inst.getCity())
                .state(inst.getState())
                .pinCode(inst.getPincode())
                .phone(inst.getPhone())
                .email(inst.getEmail())
                .website(inst.getWebsite())
                .universityAffiliation(inst.getAffiliationDetails())
                .createdAt(inst.getCreatedAt())
                .updatedAt(inst.getUpdatedAt())
                .build();
    }
}
