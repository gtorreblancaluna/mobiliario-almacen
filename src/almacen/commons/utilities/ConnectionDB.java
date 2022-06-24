package almacen.commons.utilities;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import common.services.PropertiesService;

public class ConnectionDB {
    
    private static final ConnectionDB SINGLE_INSTANCE = null;
    
    private ConnectionDB() throws Exception {
        
        prop = PropertiesService.getInstance();
        this.bd = prop.getProperty("db.database.name");
        this.user = prop.getProperty("db.username");
        this.password = prop.getProperty("db.password");
        this.url = prop.getProperty("db.url");
        this.driver = prop.getProperty("db.driver");
        connect();

    }
    
    public static ConnectionDB getInstance() throws Exception{
        
        if (SINGLE_INSTANCE == null) {
            return new ConnectionDB();
        }
        return SINGLE_INSTANCE;
    } 
    
    
    private final PropertiesService prop;
    private final String bd;
    private final String user; 
    private final String password;
    private final String url;
    private final String driver;
    private java.sql.Connection connection;

    
    private void connect() throws Exception{  
        
        try {
            //obtenemos el driver de para mysql
            
            Class.forName(driver);
            //obtenemos la conexión
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Conexion a base de datos " + bd + ". listo");
            }
        
        } catch (SQLNonTransientConnectionException e) {
           System.out.println("la conexion se ha cerrado "+e);
           throw new SQLNonTransientConnectionException(e);
        } catch (SQLException e) {
            System.out.println(e);
            throw new SQLException(e);

        } catch (ClassNotFoundException e) {
            System.out.println(e);
            throw new ClassNotFoundException(e.toString());
        }catch (Exception e) {
            throw new Exception(e.toString());
        }
    }
    

    public java.sql.Connection getConnection() {
        return connection;
    }
    
    public void desconectar() {
        connection = null;
        System.out.println("La conexion a la  base de datos " + bd + " a terminado");
    }
    
}
