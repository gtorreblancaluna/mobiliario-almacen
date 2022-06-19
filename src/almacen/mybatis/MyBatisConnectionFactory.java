package almacen.mybatis;

import almacen.commons.service.PropertiesService;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class MyBatisConnectionFactory {
    
    private final static Logger LOGGER = Logger.getLogger(MyBatisConnectionFactory.class.getName());
    private static SqlSessionFactory sqlSessionFactory;
    private final static PropertiesService propertiesService = PropertiesService.getInstance();
    
    static {
        try {
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(
                        Resources.getResourceAsReader(propertiesService.getProperty("mybatis.resource"))
                );
            }
        } catch (FileNotFoundException fileNotFoundException) {
            LOGGER.error(fileNotFoundException);
        } catch (IOException iOException) {
            LOGGER.error(iOException);
        }
    }
 
    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
    
}
