package warehouse.mybatis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

public class MyBatisConnectionFactory {
    
    private static Logger LOGGER = Logger.getLogger(MyBatisConnectionFactory.class.getName());
    private static SqlSessionFactory sqlSessionFactory;
    
 
    static {
        try { 
            String resource = "warehouse/mybatis/mybatis-config.xml";
            Reader reader = Resources.getResourceAsReader(resource);
 
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            LOGGER.error(fileNotFoundException);
        } catch (IOException iOException) {
             iOException.printStackTrace();
             LOGGER.error(iOException);
        }
    }
 
    public static SqlSessionFactory getSqlSessionFactory() {
 
        return sqlSessionFactory;
    }
    
}
