package almacen.service.delivery;

import almacen.dao.delivery.TaskChoferDeliveryUpdateDAO;
import common.exceptions.DataOriginException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class TaskChoferDeliveryUpdateService {
    
    private final TaskChoferDeliveryUpdateDAO taskChoferDeliveryUpdateDAO;
    private static TaskChoferDeliveryUpdateService INSTANCE = null;
    
    private TaskChoferDeliveryUpdateService(){
        taskChoferDeliveryUpdateDAO = TaskChoferDeliveryUpdateDAO.getInstance();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TaskChoferDeliveryUpdateService();
        }
    }
    
    public static TaskChoferDeliveryUpdateService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public void updateTaskChoferDelivery (Map<String,Object> parameters) throws DataOriginException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        parameters.put("updatedAt", LocalDateTime.now().format(formatter));
        taskChoferDeliveryUpdateDAO.updateTaskChoferDelivery(parameters);
    }
}
