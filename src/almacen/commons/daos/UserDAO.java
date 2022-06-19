package almacen.commons.daos;

import common.exceptions.DataOriginException;
import common.model.Usuario;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import almacen.mybatis.MyBatisConnectionFactory;
import common.constants.ApplicationConstants;
import java.util.List;


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
            Usuario usuario = (Usuario) session.selectOne("MapperUsuarios.obtenerUsuarioPorPassword",password);
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
    
    @SuppressWarnings("unchecked")
    public List<Usuario> getChoferes() throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return (List<Usuario>) session.selectList("MapperUsuarios.getChoferes",ApplicationConstants.PUESTO_CHOFER);
        }catch(Exception e){           
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Usuario> getUsersInCategoriesAlmacenAndEvent(Integer eventId) throws DataOriginException{
        SqlSession session = null;
        try {
            session = sqlSessionFactory.openSession();
            return (List<Usuario>) session.selectList("MapperUsuarios.getUsersInCategoriesAlmacenAndEvent",eventId);
        }catch(Exception e){           
            LOGGER.error(e);
            throw new DataOriginException(e.getMessage(),e);
        } finally {
            if (session != null)
                session.close();
        }
    }
}
