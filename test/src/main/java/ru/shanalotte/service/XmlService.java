package ru.shanalotte.service;

import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;
import ru.shanalotte.xmlentities.Items;

@Service
public class XmlService {
  public Optional<Marshaller> createXmlMarshaller() {
    try {
      JAXBContext jaxbContext = null;
      Marshaller marshaller = null;
      jaxbContext = JAXBContext.newInstance(Items.class);
      marshaller = jaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      return Optional.of(marshaller);
    } catch (JAXBException ex) {
      ex.printStackTrace();
      return Optional.empty();
    }
  }


}