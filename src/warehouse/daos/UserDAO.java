package warehouse.daos;

import common.exceptions.DataOriginException;
import common.model.Usuario;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import warehouse.mybatis.MyBatisConnectionFactory;


public class UserDAO {
    private final static Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final SqlSessionFactory sqlSessionFactory;
    private static UserDAO INSTANCE = null;
    
    private UserDAO() {
        sqlSessionFactory = MyBatisConnectionFactory.getSqlSessionFactory();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new UserDAO();
        }
    }
    
    public static UserDAO getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    @SuppressWarnings("unchecked")
    public Usuario getByPassword(String password) throws DataOriginException {
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            Usuario usuario = (Usuario) session.selectOne("MapperUsuarios.getByPassword",password);
            if(usuario != null)
                LOGGER.debug("usuario obtenido es: "+usuario.getNombre()+" "+usuario.getApellidos());
            else
                LOGGER.debug("usuario no econtrado para la contrasenia: "+password);
            return usuario;
        } catch(Exception e){           
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
}
