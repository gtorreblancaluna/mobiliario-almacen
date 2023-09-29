package almacen.service.task;

import almacen.dao.task.TaskAlmacenRetrieveDAO;
import common.exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import common.model.TaskAlmacenVO;


public class TaskAlmacenRetrieveService {
    
    private final TaskAlmacenRetrieveDAO taskAlmacenRetrieveDAO;
    private static TaskAlmacenRetrieveService INSTANCE = null;
    
    private TaskAlmacenRetrieveService(){
        taskAlmacenRetrieveDAO = TaskAlmacenRetrieveDAO.getInstance();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TaskAlmacenRetrieveService();
        }
    }
    
    public static TaskAlmacenRetrieveService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<TaskAlmacenVO> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        return taskAlmacenRetrieveDAO.getByParameters(parameters);
    }
}
