package almacen.deliveryReports.dao;

import common.exceptions.DataOriginException;
import common.model.Renta;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class RentaResultDAO {
    
    private static final Logger LOGGER = Logger.getLogger(RentaResultDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;


    private RentaResultDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    private static final RentaResultDAO SINGLE_INSTANCE = null;

    public static RentaResultDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new RentaResultDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Renta> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           return (List<Renta>) session.selectList("MapperRentasResult.getByParameters",parameters);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
}
