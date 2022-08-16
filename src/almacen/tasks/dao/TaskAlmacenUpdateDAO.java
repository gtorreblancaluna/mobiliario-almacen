
package almacen.tasks.dao;

import common.exceptions.DataOriginException;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import common.utilities.MyBatisConnectionFactory;


public class TaskAlmacenUpdateDAO {
    
    private final static Logger LOGGER = Logger.getLogger(TaskAlmacenUpdateDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static TaskAlmacenUpdateDAO INSTANCE = null;
    
    private TaskAlmacenUpdateDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TaskAlmacenUpdateDAO();
        }
    }
    
     public static TaskAlmacenUpdateDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
     
     @SuppressWarnings("unchecked")
    public void updateTypeAttend(Map<String,Object> parameters) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            session.update("MapperTaskAlmacenUpdate.updateTypeAttend",parameters);
            session.commit();
        }catch(Exception e){
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
}
