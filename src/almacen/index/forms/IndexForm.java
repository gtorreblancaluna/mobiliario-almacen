package almacen.index.forms;

import common.exceptions.DataOriginException;
import common.model.Usuario;
import common.utilities.UtilityCommon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import almacen.orders.forms.OrdersForm;
import almacen.commons.utilities.Utility;
import static almacen.commons.utilities.Utility.getCloseWindowAction;
import static common.constants.ApplicationConstants.ALREADY_AVAILABLE;
import common.services.PropertiesService;
import common.services.UserService;
import common.utilities.InactivityListener;

public class IndexForm extends javax.swing.JFrame {

    public static List<String> listNotifications = new ArrayList<>();
    public static Usuario globalUser;
    private OrdersForm ordersForm;

    private static final UserService userService = UserService.getInstance();
    private static final PropertiesService propertiesService = PropertiesService.getInstance();
    private static final Logger LOGGER = Logger.getLogger(IndexForm.class.getName());
    
    private IndexForm() {
        throw new RuntimeException("Deprecated constructor, please do use constructor with POJO Usuario arguments");
    }
    
    public IndexForm(Usuario user) {
        globalUser = user;
        initComponents();
        init();
        Integer timeToEndSession = Integer.parseInt(propertiesService.getProperty("time.to.end.session"));
        Utility.pushNotification("Inicio de sesión "+user.getNombre() + " " + user.getApellidos());
        Utility.pushNotification("Minutos para finalizar la sesión: "+timeToEndSession);
        this.setTitle("MOBILIARIO ALMACEN");
        new InactivityListener(this, getCloseWindowAction(), timeToEndSession ).start();
    }
    
    private void init () {
        this.setExtendedState(this.MAXIMIZED_BOTH);
        lblPuesto.setText(globalUser.getPuesto().getDescripcion().toUpperCase().trim());
        lbl_logueo.setText(globalUser.getNombre().trim().toUpperCase() + " " + globalUser.getApellidos().toUpperCase().trim());
    }
    
    public static boolean dataSessionUptade(String password){
        Usuario user;
        try {
            user = userService.getByPassword(password);
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        if(user != null && !user.getNombre().equals("")) {
            String msgUpdateSession = "Actualización sesión: "+user.getNombre()+" "+user.getApellidos();
            LOGGER.info(msgUpdateSession);
            Utility.pushNotification(msgUpdateSession);
            globalUser = user;
            lbl_logueo.setText(user.getNombre()+" "+user.getApellidos());
            lblPuesto.setText(user.getPuesto().getDescripcion());
            return true;
        }
        
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbl_logueo = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        lblPuesto = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaNotifications = new javax.swing.JTextArea();
        quickAccessPanel = new javax.swing.JPanel();
        btnShowOrdersForm = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuIndex = new javax.swing.JMenu();
        menuItemView = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/img/Admin-icon_48.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lbl_logueo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.add(lbl_logueo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 328, 22));

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, -1, 64));
        jPanel1.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 10));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/img/Apps-preferences-system-windows-actions-icon_48.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));
        jPanel1.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 760, 10));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("gtorreblancaluna@gmail.com");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 30, 300, 20));

        lblPuesto.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.add(lblPuesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 14, 330, 20));

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 0, 10, 60));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Versión 1.0.0");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 24, 150, 20));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Contacto: L.I. Gerardo Torreblanca Luna");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 300, 20));

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 10, 60));

        txtAreaNotifications.setEditable(false);
        txtAreaNotifications.setBackground(new java.awt.Color(226, 224, 224));
        txtAreaNotifications.setColumns(20);
        txtAreaNotifications.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAreaNotifications.setRows(5);
        txtAreaNotifications.setBorder(null);
        txtAreaNotifications.setEnabled(false);
        jScrollPane1.setViewportView(txtAreaNotifications);

        btnShowOrdersForm.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnShowOrdersForm.setText("Ver eventos");
        btnShowOrdersForm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnShowOrdersForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowOrdersFormActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout quickAccessPanelLayout = new javax.swing.GroupLayout(quickAccessPanel);
        quickAccessPanel.setLayout(quickAccessPanelLayout);
        quickAccessPanelLayout.setHorizontalGroup(
            quickAccessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quickAccessPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnShowOrdersForm, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(184, Short.MAX_VALUE))
        );
        quickAccessPanelLayout.setVerticalGroup(
            quickAccessPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quickAccessPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnShowOrdersForm, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rootPanel.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        rootPanel.setLayer(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        rootPanel.setLayer(quickAccessPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 9, Short.MAX_VALUE))
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(quickAccessPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                    .addComponent(quickAccessPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        menuIndex.setText("Eventos");

        menuItemView.setText("Ver");
        menuItemView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemViewActionPerformed(evt);
            }
        });
        menuIndex.add(menuItemView);

        jMenuBar1.add(menuIndex);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rootPanel)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rootPanel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void openOrdersForm () {
        
        if (UtilityCommon.verifyIfInternalFormIsOpen(ordersForm,IndexForm.rootPanel)) {
            if(!Utility.showWindowDataUpdateSession()){
                return;
            }
            ordersForm = new OrdersForm();
            ordersForm.setLocation(this.getWidth() / 2 - ordersForm.getWidth() / 2, this.getHeight() / 2 - ordersForm.getHeight() / 2 - 20);
            rootPanel.add(ordersForm);
            ordersForm.show();
        } else {
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }
    
    private void menuItemViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemViewActionPerformed
        openOrdersForm();
    }//GEN-LAST:event_menuItemViewActionPerformed

    private void btnShowOrdersFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowOrdersFormActionPerformed
        openOrdersForm();
    }//GEN-LAST:event_btnShowOrdersFormActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IndexForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IndexForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnShowOrdersForm;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    public static javax.swing.JLabel lblPuesto;
    public static javax.swing.JLabel lbl_logueo;
    private javax.swing.JMenu menuIndex;
    private javax.swing.JMenuItem menuItemView;
    private javax.swing.JPanel quickAccessPanel;
    public static javax.swing.JDesktopPane rootPanel;
    public static javax.swing.JTextArea txtAreaNotifications;
    // End of variables declaration//GEN-END:variables
}
