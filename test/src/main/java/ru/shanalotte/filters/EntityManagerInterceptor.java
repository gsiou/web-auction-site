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
import ru.shanalotte.utils.EntityManagerHelper;

@WebFilter("/EntityManagerInterceptor")
public class EntityManagerInterceptor implements Filter {

  public EntityManagerInterceptor() {
  }

  public void init(FilterConfig fConfig) throws ServletException {
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    try {
      EntityManagerHelper.beginTransaction();
      chain.doFilter(request, response);
      EntityManagerHelper.commit();
    } catch (RuntimeException e) {
      EntityTransaction tx = EntityManagerHelper.getTransaction();
      if (tx != null && tx.isActive())
        EntityManagerHelper.rollback();
      throw e;
    } finally {
      EntityManagerHelper.closeEntityManager();
    }
  }

}