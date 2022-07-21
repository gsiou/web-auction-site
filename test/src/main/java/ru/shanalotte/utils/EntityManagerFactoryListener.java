package ru.shanalotte.utils;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class EntityManagerFactoryListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        EntityManagerHelper.closeEntityManagerFactory();
    }

    @Override
    public void contextInitialized(ServletContextEvent e) {}

}