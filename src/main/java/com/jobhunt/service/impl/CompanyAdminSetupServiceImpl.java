package com.jobhunt.service.impl;

import com.jobhunt.exception.BadRequestException;
import com.jobhunt.mapper.CompanyMapper;
import com.jobhunt.model.entity.Company;
import com.jobhunt.model.entity.CompanyMember;
import com.jobhunt.model.entity.User;
import com.jobhunt.model.request.CompanyAdminSetupRequest;
import com.jobhunt.model.response.CompanyResponse;
import com.jobhunt.repository.CompanyMemberRepository;
import com.jobhunt.repository.CompanyRepository;
import com.jobhunt.repository.UserRepository;
import com.jobhunt.service.CompanyAdminSetupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyAdminSetupServiceImpl implements CompanyAdminSetupService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyMemberRepository companyMemberRepository;
  private final CompanyMapper companyMapper;

  @Value("${keycloak.auth-server-url}")
  private String authServerUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Override
  @Transactional
  public CompanyResponse setupCompanyWithAdmin(CompanyAdminSetupRequest request) {

    // Validate unique constraints
    if (userRepository.findByEmail(request.getAdminEmail()).isPresent()) {
      throw new BadRequestException("Admin email already exists");
    }

    if (userRepository.findByUsername(request.getAdminUsername()).isPresent()) {
      throw new BadRequestException("Admin username already exists");
    }

    if (companyRepository.existsByTaxId(request.getTaxId())) {
      throw new BadRequestException("Company with this Tax ID already exists");
    }

    try {
      // 1. Create admin user in Keycloak
      String keycloakUserId = createUserInKeycloak(request);

      // 2. Create admin user in database
      User adminUser = createAdminUser(request, keycloakUserId);

      // 3. Create company with admin user assigned
      Company company = createCompanyWithAdmin(request, adminUser);

      // 4. Create company member record with ADMIN role
      createCompanyMember(company, adminUser);

      log.info("Successfully setup company '{}' with admin user '{}'",
          company.getName(), adminUser.getEmail());

      return companyMapper.toResponse(company);

    } catch (Exception e) {
      log.error("Failed to setup company with admin: {}", e.getMessage(), e);
      throw new BadRequestException("Failed to setup company: " + e.getMessage());
    }
  }

  private String createUserInKeycloak(CompanyAdminSetupRequest request) throws Exception {
    Keycloak keycloak = getAdminKeycloak();
    RealmResource realmResource = keycloak.realm(realm);
    UsersResource usersResource = realmResource.users();

    // Create user in Keycloak
    UserRepresentation user = new UserRepresentation();
    user.setEnabled(true);
    user.setUsername(request.getAdminUsername());
    user.setEmail(request.getAdminEmail());
    user.setFirstName(request.getAdminFirstName());
    user.setLastName(request.getAdminLastName());
    user.setEmailVerified(true); // Admin users are pre-verified

    Response response = usersResource.create(user);

    if (response.getStatus() != 201) {
      String body = null;
      try {
        body = response.readEntity(String.class);
      } catch (Exception ignored) {
      }
      log.error("Keycloak user creation failed: {} – {}", response.getStatus(), body);
      throw new BadRequestException("Failed to create user in Keycloak: " + response.getStatus());
    }

    // Get user ID
    String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

    // Set password
    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(request.getAdminPassword());

    usersResource.get(userId).resetPassword(passwordCred);

    // Assign EMPLOYER role
    try {
      var roleRepresentation = realmResource.roles().get("EMPLOYER").toRepresentation();
      usersResource.get(userId).roles().realmLevel().add(List.of(roleRepresentation));
    } catch (Exception e) {
      log.warn("Failed to assign EMPLOYER role in Keycloak: {}", e.getMessage());
    }

    return userId;
  }

  private User createAdminUser(CompanyAdminSetupRequest request, String keycloakUserId) {
    User adminUser = new User();
    adminUser.setUsername(request.getAdminUsername());
    adminUser.setEmail(request.getAdminEmail());
    adminUser.setFirstName(request.getAdminFirstName());
    adminUser.setLastName(request.getAdminLastName());
    adminUser.setPhoneNumber(request.getAdminPhoneNumber());
    adminUser.setKeycloakId(keycloakUserId);
    adminUser.setRole(User.UserRole.EMPLOYER);
    adminUser.setProfilePictureUrl(request.getAdminProfilePictureUrl());

    return userRepository.save(adminUser);
  }

  private Company createCompanyWithAdmin(CompanyAdminSetupRequest request, User adminUser) {
    Company company = new Company();
    company.setName(request.getCompanyName());
    company.setEmail(request.getCompanyEmail());
    company.setPhoneNumber(request.getCompanyPhone());
    company.setWebsiteUrl(request.getCompanyWebsite());
    company.setEstablishmentYear(request.getEstablishmentYear());
    company.setTeamSize(request.getTeamSize());
    company.setIndustryType(request.getIndustryType());
    company.setAbout(request.getCompanyAbout());
    company.setCountry(request.getCountry());
    company.setCity(request.getCity());
    company.setAddress(request.getAddress());
    company.setTaxId(request.getTaxId());

    // Set both user relationships
    company.setUser(adminUser); // Owner/Creator
    company.setAdminUser(adminUser); // Current Admin
    company.setActive(true);

    return companyRepository.save(company);
  }

  private void createCompanyMember(Company company, User adminUser) {
    CompanyMember member = new CompanyMember();
    member.setUser(adminUser);
    member.setCompany(company);
    member.setRole(CompanyMember.MemberRole.ADMIN);
    member.setDepartment("Management");
    member.setStatus(CompanyMember.MemberStatus.ACTIVE);

    companyMemberRepository.save(member);
  }

  private Keycloak getAdminKeycloak() {
    return KeycloakBuilder.builder()
        .serverUrl(authServerUrl)
        .realm("master")
        .clientId("admin-cli")
        .username("admin")
        .password("admin")
        .build();
  }
}