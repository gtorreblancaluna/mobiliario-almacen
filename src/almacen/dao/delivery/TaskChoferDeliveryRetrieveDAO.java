package almacen.dao.delivery;

import common.exceptions.DataOriginException;
import common.model.TaskChoferDeliveryVO;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class TaskChoferDeliveryRetrieveDAO {
    
    private static final Logger LOGGER = Logger.getLogger(TaskChoferDeliveryRetrieveDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;


    private TaskChoferDeliveryRetrieveDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    private static final TaskChoferDeliveryRetrieveDAO SINGLE_INSTANCE = null;

    public static TaskChoferDeliveryRetrieveDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new TaskChoferDeliveryRetrieveDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<TaskChoferDeliveryVO> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           return (List<TaskChoferDeliveryVO>) session.selectList("MapperTaskChoferDelivery.getTasksChoferDelivery",parameters);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }   
}