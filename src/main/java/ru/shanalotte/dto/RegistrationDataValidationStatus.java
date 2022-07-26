package ru.shanalotte.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class RegistrationDataValidationStatus {
  private final boolean validated;
  private final String validationErrorMessage;
}
