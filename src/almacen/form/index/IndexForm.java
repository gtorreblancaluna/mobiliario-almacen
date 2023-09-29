package almacen.form.index;

import common.exceptions.DataOriginException;
import common.model.Usuario;
import common.utilities.UtilityCommon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import almacen.form.task.TasksAlmacenForm;
import almacen.commons.utilities.Utility;
import static almacen.commons.utilities.Utility.getCloseWindowAction;
import almacen.form.delivery.DeliveryReportForm;
import almacen.form.providers.ViewOrdersProvidersForm;
import almacen.form.rentas.RentasForm;
import almacen.inventory.forms.ItemsForm;
import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.ALREADY_AVAILABLE;
import common.exceptions.InvalidDataException;
import common.model.DatosGenerales;
import common.services.PropertiesService;
import common.services.UserService;
import common.utilities.InactivityListener;
import common.utilities.RequestFocusListener;
import javax.swing.JPasswordField;

public class IndexForm extends javax.swing.JFrame {

    public static List<String> listNotifications = new ArrayList<>();
    public static Usuario globalUser;
    private TasksAlmacenForm ordersForm;
    private DeliveryReportForm deliveryReportForm;
    private ItemsForm itemsForm;
    private RentasForm eventsForm;

    private static final UserService userService = UserService.getInstance();
    private static final PropertiesService propertiesService = PropertiesService.getInstance();
    private static final Logger LOGGER = Logger.getLogger(IndexForm.class.getName());
    
    public static DatosGenerales generalDataGlobal = null;
    
    private IndexForm() {
        throw new RuntimeException("Deprecated constructor");
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
        String choferPuestoId = ApplicationConstants.PUESTO_CHOFER+"";
        String userPuestId = globalUser.getPuesto().getPuestoId()+"";
        if (!globalUser.getAdministrador().equals("1") && !choferPuestoId.equals(userPuestId)) {
            panelMenuChoferDelivery.setVisible(false);
        }
        if (!globalUser.getAdministrador().equals("1") && choferPuestoId.equals(userPuestId)) {
            panelMenuInventory.setVisible(false);
        }
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
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelEvents = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        panelMenuChoferDelivery = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        panelMenuInventory = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        panelMenuRentas = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        panelMenuProviders = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaNotifications = new javax.swing.JTextArea();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelEvents.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelEvents.setOpaque(false);
        panelEvents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelEventsMouseClicked(evt);
            }
        });
        panelEvents.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Tareas almacen");
        panelEvents.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 130, 30));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/lista-de-quehaceres-24.png"))); // NOI18N
        panelEvents.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel2.add(panelEvents, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 180, 30));

        panelMenuChoferDelivery.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenuChoferDelivery.setOpaque(false);
        panelMenuChoferDelivery.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMenuChoferDeliveryMouseClicked(evt);
            }
        });
        panelMenuChoferDelivery.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Reportes entregas");
        panelMenuChoferDelivery.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 130, 30));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/delivery-24.png"))); // NOI18N
        panelMenuChoferDelivery.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel2.add(panelMenuChoferDelivery, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 180, 40));

        panelMenuInventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenuInventory.setOpaque(false);
        panelMenuInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMenuInventoryMouseClicked(evt);
            }
        });
        panelMenuInventory.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel10.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Inventario");
        panelMenuInventory.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 130, 30));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/warehouse-24.png"))); // NOI18N
        panelMenuInventory.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel2.add(panelMenuInventory, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 180, 40));

        panelMenuRentas.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenuRentas.setOpaque(false);
        panelMenuRentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMenuRentasMouseClicked(evt);
            }
        });
        panelMenuRentas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel12.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Folios");
        panelMenuRentas.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 130, 30));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/folder-24.png"))); // NOI18N
        panelMenuRentas.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel2.add(panelMenuRentas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 180, 40));

        panelMenuProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenuProviders.setOpaque(false);
        panelMenuProviders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMenuProvidersMouseClicked(evt);
            }
        });
        panelMenuProviders.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel14.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Proveedores");
        panelMenuProviders.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 0, 130, 30));

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/proveedor-24.png"))); // NOI18N
        panelMenuProviders.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 30, 30));

        jPanel2.add(panelMenuProviders, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, 180, 40));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        txtAreaNotifications.setEditable(false);
        txtAreaNotifications.setColumns(20);
        txtAreaNotifications.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtAreaNotifications.setRows(5);
        txtAreaNotifications.setBorder(null);
        txtAreaNotifications.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAreaNotifications.setEnabled(false);
        jScrollPane1.setViewportView(txtAreaNotifications);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/img/Admin-icon_48.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lbl_logueo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.add(lbl_logueo, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, 328, 22));

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator6, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 0, -1, 90));
        jPanel1.add(jSeparator7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 10));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/img/Apps-preferences-system-windows-actions-icon_48.png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));
        jPanel1.add(jSeparator8, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 0, 900, 10));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("gtorreblancaluna@gmail.com");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 30, 300, 20));

        lblPuesto.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jPanel1.add(lblPuesto, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 14, 330, 20));

        jSeparator9.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 0, 10, 90));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Versión 1.0.0");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 24, 150, 20));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Contacto: L.I. Gerardo Torreblanca Luna");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 10, 300, 20));

        jSeparator10.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel1.add(jSeparator10, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 10, 90));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1296, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        rootPanel.setLayer(jPanel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rootPanel)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public void openInventoryForm () {
        if (UtilityCommon.verifyIfInternalFormIsOpen(itemsForm,IndexForm.rootPanel)) {
            try {
                showWindowDataUpdateSessionAndJobIdNotAccess(ApplicationConstants.PUESTO_CHOFER+"",true);
            } catch (InvalidDataException | DataOriginException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }
            itemsForm = new ItemsForm();
            itemsForm.setLocation(this.getWidth() / 2 - itemsForm.getWidth() / 2, this.getHeight() / 2 - itemsForm.getHeight() / 2 - 20);
            rootPanel.add(itemsForm);
            itemsForm.show();
        } else {
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }
    
    private void openProvidersForm () {
        
        try {
            showWindowDataUpdateSessionAndJobIdNotAccess(ApplicationConstants.PUESTO_CHOFER+"",true);
        } catch (InvalidDataException | DataOriginException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
        ViewOrdersProvidersForm form = new ViewOrdersProvidersForm();
        form.setLocation(this.getWidth() / 2 - form.getWidth() / 2, this.getHeight() / 2 - form.getHeight() / 2 - 20);
        rootPanel.add(form);
        form.show();
        
    }
    
    public void openEventsForm () {
        if (UtilityCommon.verifyIfInternalFormIsOpen(eventsForm,IndexForm.rootPanel)) {
            try {
                showWindowDataUpdateSessionAndJobIdNotAccess(ApplicationConstants.PUESTO_CHOFER+"",true);
            } catch (InvalidDataException | DataOriginException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }
            eventsForm = new RentasForm();
            eventsForm.setLocation(this.getWidth() / 2 - eventsForm.getWidth() / 2, this.getHeight() / 2 - eventsForm.getHeight() / 2 - 20);
            rootPanel.add(eventsForm);
            eventsForm.show();
        } else {
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }
    
    public void openDeliveryReportForm () {
        if (UtilityCommon.verifyIfInternalFormIsOpen(deliveryReportForm,IndexForm.rootPanel)) {
            try {
                showWindowDataUpdateSessionAndJobIdNotAccess(ApplicationConstants.PUESTO_CHOFER+"", false);
            } catch (InvalidDataException | DataOriginException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }
            deliveryReportForm = new DeliveryReportForm();
            deliveryReportForm.setLocation(this.getWidth() / 2 - deliveryReportForm.getWidth() / 2, this.getHeight() / 2 - deliveryReportForm.getHeight() / 2 - 20);
            rootPanel.add(deliveryReportForm);
            deliveryReportForm.show();
        } else {
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }
    
    public static void showWindowDataUpdateSessionAndJobIdNotAccess(String jobId, Boolean isEqual) throws DataOriginException, InvalidDataException {
        
        JPasswordField pf = new JPasswordField(); 
        pf.addAncestorListener(new RequestFocusListener());
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Introduce tu contrase\u00F1a", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); 
        if (okCxl != JOptionPane.OK_OPTION) {
           throw new InvalidDataException("Operacion cancelada");
        } 
        String password = new String(pf.getPassword());
        Usuario user = userService.getByPassword(password);
        if(user == null || user.getNombre().isEmpty()) {
            String msg = "Ususario no encontrado";
            Utility.pushNotification(msg);
            throw new InvalidDataException(msg);
        }
        
        String userJobId = user.getPuesto().getPuestoId()+"";
        String msg = "";
        if (isEqual && !user.getAdministrador().equals("1") && userJobId.equals(jobId)
                ||
                (!isEqual && !user.getAdministrador().equals("1") && !userJobId.equals(jobId))) {
            msg = String.format("Usuario: %s, Tu puesto: %s, no esta autorizado para acceder a esta ventana\nAYUDA. Reinicia el sistema para actualizar la sesión", 
                    String.format("%s %s", user.getNombre(),user.getApellidos()),
                    user.getPuesto().getDescripcion());
        }
        
        if (!msg.isEmpty()) {
            Utility.pushNotification(msg);
            throw new InvalidDataException(msg);
        }
        String msgUpdateSession = "Actualización sesión: "+user.getNombre()+" "+user.getApellidos()+", PUESTO: "+user.getPuesto().getDescripcion().toUpperCase().trim();
        LOGGER.info(msgUpdateSession);
        Utility.pushNotification(msgUpdateSession);
        globalUser = user;
        lbl_logueo.setText(user.getNombre()+" "+user.getApellidos());
        lblPuesto.setText(user.getPuesto().getDescripcion());
        
    }
    
    public void openOrdersForm () {
        
        if (UtilityCommon.verifyIfInternalFormIsOpen(ordersForm,IndexForm.rootPanel)) {
            try {
                showWindowDataUpdateSessionAndJobIdNotAccess(ApplicationConstants.PUESTO_CHOFER+"", true);
            } catch (InvalidDataException | DataOriginException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }
            ordersForm = new TasksAlmacenForm();
            ordersForm.setLocation(this.getWidth() / 2 - ordersForm.getWidth() / 2, this.getHeight() / 2 - ordersForm.getHeight() / 2 - 20);
            rootPanel.add(ordersForm);
            ordersForm.show();
        } else {
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }
    
    private void panelEventsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelEventsMouseClicked
        openOrdersForm();
    }//GEN-LAST:event_panelEventsMouseClicked

    private void panelMenuChoferDeliveryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuChoferDeliveryMouseClicked
       openDeliveryReportForm();
    }//GEN-LAST:event_panelMenuChoferDeliveryMouseClicked

    private void panelMenuInventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuInventoryMouseClicked
        openInventoryForm();
    }//GEN-LAST:event_panelMenuInventoryMouseClicked

    private void panelMenuRentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuRentasMouseClicked
        openEventsForm();
    }//GEN-LAST:event_panelMenuRentasMouseClicked

    private void panelMenuProvidersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMenuProvidersMouseClicked
        openProvidersForm();
    }//GEN-LAST:event_panelMenuProvidersMouseClicked

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    public static javax.swing.JLabel lblPuesto;
    public static javax.swing.JLabel lbl_logueo;
    private javax.swing.JPanel panelEvents;
    private javax.swing.JPanel panelMenuChoferDelivery;
    private javax.swing.JPanel panelMenuInventory;
    private javax.swing.JPanel panelMenuProviders;
    private javax.swing.JPanel panelMenuRentas;
    public static javax.swing.JDesktopPane rootPanel;
    public static javax.swing.JTextArea txtAreaNotifications;
    // End of variables declaration//GEN-END:variables
}
