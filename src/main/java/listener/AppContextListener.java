package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.ConnectionManager;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ConnectionManager.getDataSource();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionManager.close();

        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getName().equals("org.sqlite.JDBC")) {
                    DriverManager.deregisterDriver(driver);
                    System.out.println("JDBC Driver deregistered: " + driver.getClass().getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
