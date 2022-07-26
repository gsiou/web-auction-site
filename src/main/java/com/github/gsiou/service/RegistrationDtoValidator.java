package com.github.gsiou.service;

import org.springframework.stereotype.Service;
import com.github.gsiou.dto.RegistrationRequestData;
import com.github.gsiou.dto.RegistrationDataValidationStatus;

@Service
public class RegistrationDtoValidator {

  private static final int minPasswordLength = 4;

  public RegistrationDataValidationStatus validate(RegistrationRequestData dto) {
    String registrationMessage = "";
    if (dto.getUserid() == null) {
      registrationMessage = "Username cannot be empty.";
    } else if (dto.getPassword1() == null) {
      registrationMessage = "Password cannot be empty.";
    } else if (dto.getPassword2() == null) {
      registrationMessage = "You have to verify your password.";
    } else if (dto.getEmail() == null) {
      registrationMessage = "Email cannot be empty.";
    } else if (dto.getAddress() == null) {
      registrationMessage = "Address cannot be empty.";
    } else if (dto.getCountry() == null) {
      registrationMessage = "Country cannot be empty.";
    } else if (dto.getPhone() == null) {
      registrationMessage = "Phone cannot be empty.";
    } else if (dto.getTrn() == null) {
      registrationMessage = "Tax Registration Number cannot be empty.";
    } else {
      if (dto.getPassword1().length() < RegistrationDtoValidator.minPasswordLength) {
        registrationMessage = "Password must be at least " +
            minPasswordLength +
            " characters long.";
      } else if (!dto.getPassword1().equals(dto.getPassword2())) {
        registrationMessage = "Passwords do not match";
      }
    }
    if (registrationMessage.equals("")) {
      return new RegistrationDataValidationStatus(true, "");
    } else {
      return new RegistrationDataValidationStatus(false, registrationMessage);
    }
  }
}
