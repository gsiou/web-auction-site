package com.github.gsiou.utils;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import com.github.gsiou.config.PropertiesHolder;

@Component
public class ApplicationStartListener implements ServletContextListener {

    @Autowired
    private EntityManagerHelper entityManagerHelper;

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        entityManagerHelper.closeEntityManagerFactory();
    }

    @Override
    public void contextInitialized(ServletContextEvent e) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext (this);
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.github.gsiou");
        e.getServletContext().setAttribute(PropertiesHolder.SPRINT_CONTEXT_ATTRIBUTE_NAME_IN_SERVLET_CONTEXT, applicationContext);
    }

}