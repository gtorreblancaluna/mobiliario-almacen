package almacen.events.services;

import almacen.events.daos.EventsDAO;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.Renta;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsService {
    
    private final EventsDAO eventesDao = EventsDAO.getInstance();

    private EventsService() {}

    private static final EventsService SINGLE_INSTANCE = null;

    public static EventsService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new EventsService();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Renta> getByParameters (Map<String,Object> parameters) throws DataOriginException, BusinessException {
        return eventesDao.getByParameters(parameters);
    }
    
    public List<Renta> getEventsByNumbersOfWeeks (Integer numbersWeek, Integer userByCategoryId) throws DataOriginException, BusinessException {        
        
        if (numbersWeek == null || numbersWeek >= 3 || numbersWeek <= -3) {
            throw new BusinessException("Numero de semanas no permitidas");
        }
        
        Map<String,Object> parameters = new HashMap<>();
        
        parameters.put("type", ApplicationConstants.TIPO_PEDIDO);
        parameters.put("statusId", Arrays.asList( 
                        ApplicationConstants.ESTADO_APARTADO,
                        ApplicationConstants.ESTADO_EN_RENTA
                    ));
        parameters.put("userByCategoryId", userByCategoryId);
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String initDeliveryDate;
        String endDeliveryDate;
        
        if (numbersWeek > 0) {
            initDeliveryDate = format.format(LocalDate.now().atStartOfDay());
            endDeliveryDate = format.format(LocalDate.now().plusWeeks(numbersWeek).with(DayOfWeek.MONDAY).minusDays(1));
        } else {
            endDeliveryDate = format.format(LocalDate.now().atStartOfDay());
            initDeliveryDate = format.format(LocalDate.now().plusWeeks(numbersWeek).with(DayOfWeek.MONDAY).minusDays(1));
        }
        
        parameters.put("initDeliveryDate", initDeliveryDate);
        parameters.put("endDeliveryDate", endDeliveryDate);
        return eventesDao.getEventsBetweenDeliveryDate(parameters);
    }
    
}
