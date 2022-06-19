package almacen.commons.daos;

import almacen.mybatis.MyBatisConnectionFactory;
import common.exceptions.DataOriginException;
import common.model.Tipo;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class TipoEventoDAO {
    
    private static TipoEventoDAO INSTANCE = null;
    private final SqlSessionFactory sqlSessionFactory;
    
    // Private constructor suppresses 
    private TipoEventoDAO(){
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TipoEventoDAO();
        }
    }

    public static TipoEventoDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<Tipo> get () throws DataOriginException {
        SqlSession session = null;
        try{
            session = sqlSessionFactory.openSession();
            return (List<Tipo>) session.selectList("MapperTipoEvento.getTipoEvento");
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
}
