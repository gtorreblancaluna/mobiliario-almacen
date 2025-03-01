package almacen.service;

import almacen.commons.utilities.Utility;
import almacen.form.index.IndexForm;
import common.dao.SystemDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.DatosGenerales;

public class SystemService {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemService.class.getName());
    
    private SystemService(){}
    
    private static final SystemService SINGLE_INSTANCE = null;
    
    public static SystemService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new SystemService();
        }
        return SINGLE_INSTANCE;
    } 
    
    private final SystemDAO systemDao = SystemDAO.getInstance();

    public String getDataConfigurationByKey(String key)throws BusinessException{
        String result;
        try{
            result = systemDao.getDataConfigurationByKey(key);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
        
        return result;
    }
    
    public DatosGenerales getGeneralData(){
        if (IndexForm.generalDataGlobal == null ) {
            DatosGenerales generalData = systemDao.getGeneralData();
            IndexForm.generalDataGlobal = generalData;
            Utility.pushNotification("Datos generales del sistema obtenidos de la base de datos.");
        }
        return IndexForm.generalDataGlobal;
    }
    
    public void saveDatosGenerales(DatosGenerales datosGenerales){
        systemDao.saveDatosGenerales(datosGenerales);
        DatosGenerales generalData = systemDao.getGeneralData();
        IndexForm.generalDataGlobal = generalData;
    }
    
    public void updateInfoPDFSummary(DatosGenerales datosGenerales) throws DataOriginException{
        systemDao.updateInfoPDFSummary(datosGenerales);
    }
    
    
}
