package almacen.commons.service;


import almacen.commons.daos.TipoEventoDAO;
import common.exceptions.DataOriginException;
import java.util.List;
import common.model.Tipo;

public class TipoEventoService {
        
    private static TipoEventoService INSTANCE = null;
    private final TipoEventoDAO tipoEventoDao = TipoEventoDAO.getInstance();

    // Private constructor suppresses 
    private TipoEventoService(){}

    // creador sincronizado para protegerse de posibles problemas  multi-hilo
    // otra prueba para evitar instanciación múltiple 
    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new TipoEventoService();
        }
    }

    public static TipoEventoService getInstance() {
        if (INSTANCE == null) createInstance();
            return INSTANCE;
    }
    
    public List<Tipo> get ()  throws DataOriginException {
        return tipoEventoDao.get();
    }
}
