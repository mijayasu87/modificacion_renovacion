package senadi.gob.ec.mod.listener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class InitListener implements ServletContextListener {

    public InitListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DataSource fuenteDatos = null;
        Context ctx;
        try {
            ServletContext sc = sce.getServletContext();
            ctx = new InitialContext();

//			fuenteDatos = (DataSource) ctx.lookup("java:comp/env/jdbc/pagosoffline");			
//			Connection cn = fuenteDatos.getConnection();			
//			sc.setAttribute("datasource", cn);
//            EntityManagerUtil.getEntityManager();

        } catch (NamingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
