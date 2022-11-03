package almacen.commons.utilities;

import almacen.commons.forms.UpdateSessionDialogForm;
import common.constants.ApplicationConstants;
import common.utilities.RequestFocusListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import almacen.index.forms.IndexForm;
import common.exceptions.BusinessException;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import static common.constants.ApplicationConstants.LIMIT_GENERATE_PDF;


public abstract class Utility {
    
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void selectCheckBoxWhenKeyPressedIsSpace (java.awt.event.KeyEvent evt, JTable table, int columnBoolean) {
        
        if(evt.getKeyCode() != 32) {
            return;
        }
        
        int rowSelect = table.getSelectedRow();
        
        if (rowSelect >= 0) {
            boolean isSelected = (Boolean) table.getValueAt(rowSelect, columnBoolean);
            table.setValueAt(!isSelected, rowSelect, columnBoolean);
        }
    }
    public static void validateSelectCheckboxInTable(JTable table, Integer columNumber) throws BusinessException {
        
        int selectRows = 0;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(table.getValueAt(i, columNumber).toString())) {
                selectRows++;
            }
        }
        
        if (selectRows > LIMIT_GENERATE_PDF) {
            throw new BusinessException ("Limite excedido de operaciones ["+ LIMIT_GENERATE_PDF +"]");
        }
        
        if (selectRows <= 0) {
            throw new BusinessException ("Marca el CHECKBOX de una o mas filas para continuar");
        }
    }
    
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
    public static Action getCloseWindowAction () {
        return new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {        
                showWindowDataForceUpdateSession();
            }
        };
     }
         
    public static void showWindowDataForceUpdateSession(){
        System.out.println("SHOW showWindowDataForceUpdateSession");
        UpdateSessionDialogForm win = new UpdateSessionDialogForm(null,true);
        win.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        win.setLocationRelativeTo(null);
        win.setVisible(true);
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
    
    public static String getPathLocation()throws IOException,URISyntaxException{
   
        File file = new File(Utility.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParentFile();
        
        return file+"";
    
    }

}
