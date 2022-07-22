package ru.shanalotte.config;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class MessageManager {

  private final MessageSource messageSource;

  @Autowired
  public MessageManager(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public String getMessage(String messageCode) {
    return messageSource.getMessage(messageCode, null, Locale.getDefault());
  }
}
