package warehouse.orders.services;

import common.exceptions.DataOriginException;
import java.util.List;
import java.util.Map;
import warehouse.orders.dao.OrderWarehouseDAO;
import warehouse.orders.models.OrderWarehouseVO;


public class OrderWarehouseService {
    
    private final OrderWarehouseDAO orderWarehouseDAO;
    private static OrderWarehouseService INSTANCE = null;
    
    private OrderWarehouseService(){
        orderWarehouseDAO = OrderWarehouseDAO.getInstance();
    }
    
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new OrderWarehouseService();
        }
    }
    
    public static OrderWarehouseService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<OrderWarehouseVO> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        return orderWarehouseDAO.getByParameters(parameters);
    }
}
