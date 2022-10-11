package almacen.events.daos;

import common.exceptions.DataOriginException;
import common.model.Renta;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class EventsDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EventsDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;


    private EventsDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    private static final EventsDAO SINGLE_INSTANCE = null;

    public static EventsDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new EventsDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Renta> getEventsBetweenDeliveryDate (Map<String,Object> parameters) throws DataOriginException {
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           return (List<Renta>) session.selectList("MapperEvents.getEventsBetweenDeliveryDate",parameters);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }   
    
    public List<Renta> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           return (List<Renta>) session.selectList("MapperEvents.getByParameters",parameters);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }   
}