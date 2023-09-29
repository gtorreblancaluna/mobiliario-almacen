
package almacen.dao.task;

import common.exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import common.utilities.MyBatisConnectionFactory;
import common.model.TaskAlmacenVO;


public class TaskAlmacenRetrieveDAO {
    
    private final static Logger LOGGER = Logger.getLogger(TaskAlmacenRetrieveDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static TaskAlmacenRetrieveDAO INSTANCE = null;
    
    private TaskAlmacenRetrieveDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TaskAlmacenRetrieveDAO();
        }
    }
    
     public static TaskAlmacenRetrieveDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
     
     @SuppressWarnings("unchecked")
    public List<TaskAlmacenVO> getByParameters(Map<String,Object> parameters) throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        try {
            return (List<TaskAlmacenVO>) session.selectList("MapperTaskAlmacenRetrieve.getByParameters",parameters);
        }catch(Exception e){
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
}
