package warehouse.services;

import common.exceptions.DataOriginException;
import common.model.Usuario;
import org.apache.log4j.Logger;
import warehouse.daos.UserDAO;

public class UserService {
    
    private final static Logger LOG = Logger.getLogger(UserService.class.getName());
    private final UserDAO usuariosDao;
    private static UserService INSTANCE = null;
    
    private UserService(){
        usuariosDao = UserDAO.getInstance();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new UserService();
        }
    }
    
    public static UserService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public Usuario getByPassword(String psw) throws DataOriginException{
         return usuariosDao.getByPassword(psw);
    }
}
