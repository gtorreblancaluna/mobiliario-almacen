package almacen.commons.service;

import almacen.commons.daos.EstadoEventoDAO;
import common.exceptions.DataOriginException;
import java.util.List;
import common.model.EstadoEvento;

public class EstadoEventoService {
        
    private static EstadoEventoService INSTANCE = null;
    private final EstadoEventoDAO estadoEventoDao = EstadoEventoDAO.getInstance();

    // Private constructor suppresses 
    private EstadoEventoService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new EstadoEventoService();
        }
    }

    public static EstadoEventoService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<EstadoEvento> get () throws DataOriginException {
        return estadoEventoDao.get();
    }
}
