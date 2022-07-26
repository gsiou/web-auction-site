package ru.shanalotte.filters;

import java.io.IOException;


import jakarta.persistence.*;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.shanalotte.utils.EntityManagerHelper;

@WebFilter("/EntityManagerInterceptor")
public class EntityManagerInterceptor implements Filter {

  @Autowired
  private EntityManagerHelper entityManagerHelper;

  public EntityManagerInterceptor() {
  }

  public void init(FilterConfig fConfig) {
    ApplicationContext applicationContext = (AnnotationConfigApplicationContext) fConfig.getServletContext().getAttribute("springcontext");
    final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    beanFactory.autowireBean(this);
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      entityManagerHelper.beginTransaction();
      chain.doFilter(request, response);
      entityManagerHelper.commit();
    } catch (RuntimeException e) {
      EntityTransaction tx = entityManagerHelper.getTransaction();
      if (tx != null && tx.isActive())
        entityManagerHelper.rollback();
      throw e;
    } finally {
      entityManagerHelper.closeEntityManager();
    }
  }

}