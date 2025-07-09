package com.jobhunt.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContactInfoRequest {

  @Size(max = 100, message = "Country cannot exceed 100 characters")
  private String country;

  @Size(max = 100, message = "City cannot exceed 100 characters")
  private String city;

  @Size(max = 500, message = "Complete address cannot exceed 500 characters")
  private String completeAddress;

  @Size(max = 200, message = "Map location cannot exceed 200 characters")
  private String mapLocation;

  private BigDecimal latitude;

  private BigDecimal longitude;
}