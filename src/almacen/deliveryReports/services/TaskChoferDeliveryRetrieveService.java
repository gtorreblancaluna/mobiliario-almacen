package almacen.deliveryReports.services;

import almacen.deliveryReports.dao.TaskChoferDeliveryRetrieveDAO;
import common.exceptions.DataOriginException;
import common.model.TaskChoferDeliveryVO;
import common.utilities.UtilityCommon;
import java.util.List;
import java.util.Map;


public class TaskChoferDeliveryRetrieveService {
    
    private final TaskChoferDeliveryRetrieveDAO taskChoferDeliveryRetrieveDAO;

    private TaskChoferDeliveryRetrieveService() {
        
        taskChoferDeliveryRetrieveDAO = TaskChoferDeliveryRetrieveDAO.getInstance();
    }

    private static final TaskChoferDeliveryRetrieveService SINGLE_INSTANCE = null;

    public static TaskChoferDeliveryRetrieveService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new TaskChoferDeliveryRetrieveService();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<TaskChoferDeliveryVO> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        
        List<TaskChoferDeliveryVO> tasks = taskChoferDeliveryRetrieveDAO.getByParameters(parameters);
        
        for (TaskChoferDeliveryVO task : tasks) {
            UtilityCommon.calcularTotalesPorRenta(task.getRenta());
            task.setPendingToPayEvent(task.getRenta().getTotal() > 0);
        }
        return tasks;
    }
    
}
