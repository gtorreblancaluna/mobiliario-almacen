
package almacen.orders.dao;

import common.exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import common.utilities.MyBatisConnectionFactory;
import almacen.orders.models.OrderWarehouseVO;


public class OrderWarehouseDAO {
    
    private final static Logger LOGGER = Logger.getLogger(OrderWarehouseDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static OrderWarehouseDAO INSTANCE = null;
    
    private OrderWarehouseDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new OrderWarehouseDAO();
        }
    }
    
     public static OrderWarehouseDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
     
     @SuppressWarnings("unchecked")
    public List<OrderWarehouseVO> getByParameters(Map<String,Object> parameters) throws DataOriginException{
        SqlSession session = sqlSessionFactory.openSession();
        
        try {
            return (List<OrderWarehouseVO>) session.selectList("MapperOrderWarehouse.getByParameters",parameters);
        }catch(Exception e){
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            session.close();
        }
    }
}
