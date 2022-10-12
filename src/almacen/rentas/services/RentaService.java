package almacen.rentas.services;

import almacen.commons.utilities.Utility;
import almacen.rentas.daos.RentaDAO;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.NoDataFoundException;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.Tipo;
import common.model.Usuario;
import common.services.OrderStatusChangeService;
import common.services.TaskAlmacenUpdateService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentaService {
    
    private final RentaDAO rentaDao = RentaDAO.getInstance();
    private final TaskAlmacenUpdateService taskAlmacenUpdateService = TaskAlmacenUpdateService.getInstance();
    private final OrderStatusChangeService orderStatusChangeService = OrderStatusChangeService.getInstance();
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RentaService.class.getName());

    private RentaService() {}

    private static final RentaService SINGLE_INSTANCE = null;

    public static RentaService getInstance(){
        if (SINGLE_INSTANCE == null) {
            return new RentaService();
        }
        return SINGLE_INSTANCE;
    }
    
    public List<Renta> getByParameters (Map<String,Object> parameters) throws DataOriginException, BusinessException {
        return rentaDao.getByParameters(parameters);
    }
    
    public List<Renta> getByIds (List<String> ids) throws DataOriginException, BusinessException {
        return rentaDao.getByIds(ids);
    }
    
    public void updateStatusFromApartadoToEnRenta (List<String> ids, Usuario user) throws DataOriginException, BusinessException {
        if (ids.isEmpty()) {
            throw new BusinessException("No se recibieron parametros");
        }
        if (ids.size() > 20) {
            throw new BusinessException("Limite de operaciones permitidas [20]");
        }
        List<Renta> rentas = getByIds(ids);
        
        if (rentas.isEmpty()) {
            throw new BusinessException("No se obtuvieron eventos");
        }
        
        List<String> message = new ArrayList<>();
        for (Renta renta : rentas) {
            if (!renta.getEstado().getEstadoId().toString().equals(ApplicationConstants.ESTADO_APARTADO)) {
                message.add(String.format("El folio %s tiene estado [%s], debe tener estado [%s]",renta.getFolio(),renta.getEstado().getDescripcion(),ApplicationConstants.DS_ESTADO_APARTADO));
            }
            if (!renta.getTipo().getTipoId().toString().equals(ApplicationConstants.TIPO_PEDIDO)) {
                message.add(String.format("El folio %s tiene tipo [%s], debe tener tipo [%s]",renta.getFolio(),renta.getTipo().getTipo(),ApplicationConstants.DS_TIPO_PEDIDO));
            }
        }
        
        if (!message.isEmpty()) {
            throw new BusinessException(String.join("\n", message));
        }
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        parameters.put("estadoIdInRent", ApplicationConstants.ESTADO_EN_RENTA);
        
        rentaDao.updateStatusFromApartadoToEnRenta(parameters);
        final EstadoEvento estadoEventoSelected = new EstadoEvento(Integer.parseInt(ApplicationConstants.ESTADO_EN_RENTA));
        final Tipo tipoSelected = new Tipo(Integer.parseInt(ApplicationConstants.TIPO_PEDIDO));
        
        new Thread(() -> {
            for (Renta renta : rentas) {
            
                String messageSaveWhenEventIsUpdated;
                try {
                    messageSaveWhenEventIsUpdated = taskAlmacenUpdateService
                        .saveWhenEventIsUpdated(estadoEventoSelected, tipoSelected, renta, false, false, user.getUsuarioId().toString());
                } catch (NoDataFoundException e) {
                    messageSaveWhenEventIsUpdated = e.getMessage();
                    log.error(messageSaveWhenEventIsUpdated);
                } catch (DataOriginException e) {
                    log.error(e.getMessage(),e);
                    messageSaveWhenEventIsUpdated = "Ocurrió un error al generar la tarea a almacén, DETALLE: "+e.getMessage();
                }
                Utility.pushNotification(messageSaveWhenEventIsUpdated);
            }
        }).start();
        
        new Thread(() -> {
            for (Renta renta : rentas) {            
                String msg = String.format("Folio: %s, Usuario %s,  Realizó el cambio de Estado [%s] a [%s]",
                    renta.getFolio()+"",
                    user.getNombre() + " " + user.getApellidos(),
                    renta.getEstado().getDescripcion(),
                    estadoEventoSelected.getDescripcion()
                );
                try {
                    orderStatusChangeService.insert(renta.getRentaId(), renta.getEstado().getEstadoId() , estadoEventoSelected.getEstadoId(),user.getUsuarioId());
                    log.info(msg);
                    Utility.pushNotification(msg);
                } catch (BusinessException e) {
                    log.error(e.getMessage(),e);
                    Utility.pushNotification(e.getMessage());
                }
            }
        }).start();
        
        
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
        return rentaDao.getEventsBetweenDeliveryDate(parameters);
    }
    
}
