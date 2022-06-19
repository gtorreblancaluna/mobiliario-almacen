package almacen.commons.daos;

import almacen.mybatis.MyBatisConnectionFactory;
import common.exceptions.DataOriginException;
import common.model.EstadoEvento;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class EstadoEventoDAO {
    
    private static EstadoEventoDAO INSTANCE = null;
    private final SqlSessionFactory sqlSessionFactory;
    
    // Private constructor suppresses 
    private EstadoEventoDAO(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new EstadoEventoDAO();
        }
    }

    public static EstadoEventoDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<EstadoEvento> get () throws DataOriginException {
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            return (List<EstadoEvento>) session.selectList("MapperEstadoEvento.getEstadoEvento");
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
             if (session != null)
                session.close();
        }
    }
    
}
