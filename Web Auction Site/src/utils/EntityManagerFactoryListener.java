package utils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class EntityManagerFactoryListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        EntityManagerHelper.closeEntityManagerFactory();
    }

    @Override
    public void contextInitialized(ServletContextEvent e) {}

}