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

/**
 * Servlet Filter implementation class EntityManagerInterceptor
 */
@WebFilter("/EntityManagerInterceptor")
public class EntityManagerInterceptor implements Filter {

    /**
     * Default constructor. 
     */
    public EntityManagerInterceptor() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			EntityManagerHelper.beginTransaction();
			chain.doFilter(request, response);
			EntityManagerHelper.commit();
		} 
		catch (RuntimeException e) 
		{
			EntityTransaction tx = EntityManagerHelper.getTransaction();
			if (tx != null && tx.isActive()) 
				EntityManagerHelper.rollback();
		    throw e;
			
		} 
		finally {
			EntityManagerHelper.closeEntityManager();
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}