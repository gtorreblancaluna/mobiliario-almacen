package almacen.tasks.services;

import almacen.tasks.dao.TaskAlmacenUpdateDAO;
import common.exceptions.DataOriginException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class TaskAlmacenUpdateService {
    
    private final TaskAlmacenUpdateDAO taskAlmacenUpdateDAO;
    private static TaskAlmacenUpdateService INSTANCE = null;
    
    private TaskAlmacenUpdateService(){
        taskAlmacenUpdateDAO = TaskAlmacenUpdateDAO.getInstance();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TaskAlmacenUpdateService();
        }
    }
    
    public static TaskAlmacenUpdateService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public void updateTypeAttend (Map<String,Object> parameters) throws DataOriginException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        parameters.put("updatedAt", LocalDateTime.now().format(formatter));
        taskAlmacenUpdateDAO.updateTypeAttend(parameters);
    }
}
