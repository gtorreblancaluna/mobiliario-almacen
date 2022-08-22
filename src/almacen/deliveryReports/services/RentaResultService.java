package almacen.deliveryReports.services;

import almacen.deliveryReports.dao.RentaResultDAO;
import common.exceptions.DataOriginException;
import common.model.Renta;
import java.util.List;
import java.util.Map;


public class RentaResultService {
    
    private final RentaResultDAO rentaResultDAO;

    private RentaResultService() {
        
        rentaResultDAO = RentaResultDAO.getInstance();
    }

    private static final RentaResultService SINGLE_INSTANCE = null;

    public static RentaResultService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new RentaResultService();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Renta> getByParameters (Map<String,Object> parameters) throws DataOriginException {
        return rentaResultDAO.getByParameters(parameters);
    }
    
}
