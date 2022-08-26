package almacen.deliveryReports.dao;

import common.exceptions.DataOriginException;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import common.utilities.MyBatisConnectionFactory;


public class TaskChoferDeliveryUpdateDAO {
    
    private final static Logger LOGGER = Logger.getLogger(TaskChoferDeliveryUpdateDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static TaskChoferDeliveryUpdateDAO INSTANCE = null;
    
    private TaskChoferDeliveryUpdateDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TaskChoferDeliveryUpdateDAO();
        }
    }
    
     public static TaskChoferDeliveryUpdateDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
     
    @SuppressWarnings("unchecked")
    public void updateTaskChoferDelivery(Map<String,Object> parameters) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            session.update("MapperTaskChoferDeliveryUpdate.updateTaskChoferDelivery",parameters);
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
