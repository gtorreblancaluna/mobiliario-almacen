package almacen.rentas.daos;

import common.exceptions.DataOriginException;
import common.model.Renta;
import common.utilities.MyBatisConnectionFactory;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;

public class RentaDAO {
    
    private static final Logger LOGGER = Logger.getLogger(RentaDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;


    private RentaDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    private static final RentaDAO SINGLE_INSTANCE = null;

    public static RentaDAO getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new RentaDAO();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Renta> getEventsBetweenDeliveryDate (Map<String,Object> parameters) throws DataOriginException {
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           return (List<Renta>) session.selectList("MapperRentas.getEventsBetweenDeliveryDate",parameters);
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
           return (List<Renta>) session.selectList("MapperRentas.getByParameters",parameters);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }  
    
    public List<Renta> getByIds (List<String> ids) throws DataOriginException {
        SqlSession session = null;
        try {
           session = sqlSessionFactory.openSession();
           return (List<Renta>) session.selectList("MapperRentas.getByIds",ids);
        } catch (Exception e) {
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void updateStatusFromApartadoToEnRenta(Map<String,Object> parameters) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            session.update("MapperRentas.updateStatusFromApartadoToEnRenta",parameters);
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