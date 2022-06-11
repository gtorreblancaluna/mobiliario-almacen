package warehouse.utilities;

import common.constants.ApplicationConstants;
import common.utilities.RequestFocusListener;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import warehouse.index.forms.IndexForm;


public abstract class Utility {
    
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    
    public static void pushNotification(final String notification){
        StringBuilder messages = new StringBuilder();
        
        String date = simpleDateFormat.format(new Timestamp(System.currentTimeMillis()));
        IndexForm.listNotifications.add(date+" >> "+notification);
        IndexForm.listNotifications.stream().forEach(t -> {
            messages.append(t);
            messages.append("\n");
        });
        
       
        IndexForm.txtAreaNotifications.setText(null);
        IndexForm.txtAreaNotifications.setText(messages+"");
    }     
    
   
    public static boolean showWindowDataUpdateSession(){
        
        JPasswordField pf = new JPasswordField(); 
        pf.addAncestorListener(new RequestFocusListener());
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Introduce tu contrase\u00F1a", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); 
       
        
        if (okCxl == JOptionPane.OK_OPTION) {
            String password = new String(pf.getPassword()); 
            System.out.println("You entered: " + password); 
            if(!IndexForm.dataSessionUptade(password)){
                JOptionPane.showMessageDialog(null, ApplicationConstants.DS_MESSAGE_FAIL_LOGIN, ApplicationConstants.TITLE_MESSAGE_FAIL_LOGIN, JOptionPane.ERROR_MESSAGE);
                return false;
            }else{
              return true;
            }
        }else{
            return false;
        }
    }

}
