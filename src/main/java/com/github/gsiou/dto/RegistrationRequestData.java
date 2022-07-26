package com.github.gsiou.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RegistrationRequestData {

  private String userid;
  @ToString.Exclude
  private String password1;
  @ToString.Exclude
  private String password2;
  private String email;
  private String address;
  private String country;
  private String phone;
  private String trn;
  private String longitude;
  private String latitude;

  public static RegistrationRequestData from(HttpServletRequest request) {
    RegistrationRequestData dto = new RegistrationRequestData();
    dto.userid = request.getParameter("Username");
    dto.password1 = request.getParameter("Password");
    dto.password2 = request.getParameter("Password_conf");
    dto.email = request.getParameter("Email");
    dto.address = request.getParameter("Address");
    dto.country = request.getParameter("Country");
    dto.phone = request.getParameter("Phone");
    dto.trn = request.getParameter("Trn");
    dto.longitude = request.getParameter("Longitude");
    dto.latitude = request.getParameter("Latitude");
    return dto;
  }

}
