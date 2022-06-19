package almacen.orders.forms;

import common.constants.ApplicationConstants;
import common.model.EstadoEvento;
import common.model.Tipo;
import common.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import almacen.index.forms.IndexForm;
import static almacen.orders.forms.OrdersForm.Filter.END_CREATED_DATE;
import static almacen.orders.forms.OrdersForm.Filter.INIT_CREATED_DATE;
import static almacen.orders.forms.OrdersForm.Filter.LIMIT;


public class OrdersFilterForm extends javax.swing.JInternalFrame {
    
    private List<Tipo> typesGlobal;
    private List<EstadoEvento> statusListGlobal;
    private List<Usuario> choferes;
    
    public OrdersFilterForm(List<Tipo> typesGlobal, List<EstadoEvento> statusListGlobal, List<Usuario> choferes) {
        initComponents();
        this.setClosable(true);
        this.typesGlobal = typesGlobal;
        this.statusListGlobal = statusListGlobal;
        this.choferes = choferes;
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            lblStatus.setVisible(false);
            lblDriver.setVisible(false);
            lblType.setVisible(false);
            cmbStatus.setVisible(false);
            cmbEventType.setVisible(false);
            cmbDriver.setVisible(false);
        }
        
        initInfo();
    }
    
    public OrdersFilterForm() {
        initComponents();
        this.setClosable(true);
    }
    
    private void initInfo () {
        
        cmbStatus.removeAllItems();
        cmbStatus.addItem(
                new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmbEventType.removeAllItems();       
        cmbEventType.addItem(
                new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
        );
        cmbDriver.removeAllItems();        
        cmbDriver.addItem(
                new Usuario(0, ApplicationConstants.CMB_SELECCIONE)
        );
        
        
        choferes.stream().forEach(t -> {
            cmbDriver.addItem(t);
        });
        
        statusListGlobal.stream().forEach(t -> {
            cmbStatus.addItem(t);
        });
        
        typesGlobal.stream().forEach(t -> {
            cmbEventType.addItem(t);
        });
        
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDeliveryInitDate = new com.toedter.calendar.JDateChooser();
        txtDeliveryEndDate = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        txtEventInitDate = new com.toedter.calendar.JDateChooser();
        txtEventEndDate = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        txtCreatedInitDate = new com.toedter.calendar.JDateChooser();
        txtCreatedEndDate = new com.toedter.calendar.JDateChooser();
        lblStatus = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblType = new javax.swing.JLabel();
        cmbEventType = new javax.swing.JComboBox<>();
        btnApply = new javax.swing.JButton();
        cmbDriver = new javax.swing.JComboBox<>();
        lblDriver = new javax.swing.JLabel();

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Cliente:");

        txtCustomer.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCustomerActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Fecha de entrega: (es necesario indicar fecha inicial y fecha final)");

        txtDeliveryInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtDeliveryInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDeliveryInitDateMouseClicked(evt);
            }
        });
        txtDeliveryInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDeliveryInitDateKeyPressed(evt);
            }
        });

        txtDeliveryEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtDeliveryEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDeliveryEndDateMouseClicked(evt);
            }
        });
        txtDeliveryEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDeliveryEndDateKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel6.setText("Fecha del evento: (es necesario indicar fecha inicial y fecha final)");

        txtEventInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtEventInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtEventInitDateMouseClicked(evt);
            }
        });
        txtEventInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEventInitDateKeyPressed(evt);
            }
        });

        txtEventEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtEventEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtEventEndDateMouseClicked(evt);
            }
        });
        txtEventEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEventEndDateKeyPressed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel7.setText("Fecha de elaboración: (es necesario indicar fecha inicial y fecha final)");

        txtCreatedInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCreatedInitDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCreatedInitDateMouseClicked(evt);
            }
        });
        txtCreatedInitDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCreatedInitDateKeyPressed(evt);
            }
        });

        txtCreatedEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtCreatedEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCreatedEndDateMouseClicked(evt);
            }
        });
        txtCreatedEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCreatedEndDateKeyPressed(evt);
            }
        });

        lblStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblStatus.setText("Estado:");

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblType.setText("Tipo de evento:");

        cmbEventType.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbEventType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnApply.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnApply.setText("Aplicar filtro");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        cmbDriver.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        cmbDriver.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        lblDriver.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lblDriver.setText("Chofer:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 94, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnApply, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCustomer, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(txtDeliveryInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDeliveryEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(txtEventInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtEventEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(txtCreatedInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtCreatedEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDeliveryInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDeliveryEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEventInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEventEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCreatedInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCreatedEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(lblType))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDriver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbDriver, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApply))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCustomerActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCustomerActionPerformed

    private void txtDeliveryInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDeliveryInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryInitDateMouseClicked

    private void txtDeliveryInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDeliveryInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryInitDateKeyPressed

    private void txtDeliveryEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDeliveryEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryEndDateMouseClicked

    private void txtDeliveryEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDeliveryEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDeliveryEndDateKeyPressed

    private void txtEventInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEventInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventInitDateMouseClicked

    private void txtEventInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEventInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventInitDateKeyPressed

    private void txtEventEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtEventEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventEndDateMouseClicked

    private void txtEventEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEventEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEventEndDateKeyPressed

    private void txtCreatedInitDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCreatedInitDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedInitDateMouseClicked

    private void txtCreatedInitDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCreatedInitDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedInitDateKeyPressed

    private void txtCreatedEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCreatedEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedEndDateMouseClicked

    private void txtCreatedEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCreatedEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedEndDateKeyPressed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        try {

            final String FORMAT_DATE = "dd/MM/yyyy";
            Usuario chofer = (Usuario) cmbDriver.getModel().getSelectedItem();
            EstadoEvento estadoEvento = (EstadoEvento) cmbStatus.getModel().getSelectedItem();
            
           
            Tipo eventType = (Tipo) cmbEventType.getModel().getSelectedItem();
            
            String customer = txtCustomer.getText().trim();
            String initDeliveryDate = txtDeliveryInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDeliveryInitDate.getDate()) : null;
            String endDeliveryDate = txtDeliveryEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDeliveryEndDate.getDate()) : null;
            String initEventDate = txtEventInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEventInitDate.getDate()) : null;
            String endEventDate = txtEventEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEventEndDate.getDate()) : null;
            String initCreatedDate = txtCreatedInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtCreatedInitDate.getDate()) : null;
            String endCreatedDate = txtCreatedEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtCreatedEndDate.getDate()) : null;
      
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(INIT_CREATED_DATE.getValue(), initCreatedDate);
            parameters.put(END_CREATED_DATE.getValue(), endCreatedDate);
            parameters.put(LIMIT.getValue(), 1000);
            parameters.put(OrdersForm.Filter.CUSTOMER.getValue(), customer);
            parameters.put(OrdersForm.Filter.INIT_DELIVERY_DATE.getValue(), initDeliveryDate);
            parameters.put(OrdersForm.Filter.END_DELIVERY_DATE.getValue(), endDeliveryDate);
            parameters.put(OrdersForm.Filter.INIT_EVENT_DATE.getValue(), initEventDate);
            parameters.put(OrdersForm.Filter.END_EVENT_DATE.getValue(), endEventDate);
            
            if (!customer.isEmpty() && customer.length()>1000) {
                JOptionPane.showMessageDialog(null, "Valor no permitido para el nombre del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            if (IndexForm.globalUser.getAdministrador().equals("1")) {
                parameters.put(OrdersForm.Filter.TYPE.getValue(), eventType.getTipoId().equals(0) ? null : Arrays.asList(eventType.getTipoId()));
                parameters.put(OrdersForm.Filter.STATUS.getValue(), estadoEvento.getEstadoId().equals(0) ? null : Arrays.asList(estadoEvento.getEstadoId()));
            }

            OrdersForm.searchAndFillTable(parameters);
            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnApplyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JComboBox<Usuario> cmbDriver;
    private javax.swing.JComboBox<Tipo> cmbEventType;
    private javax.swing.JComboBox<EstadoEvento> cmbStatus;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblDriver;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblType;
    private com.toedter.calendar.JDateChooser txtCreatedEndDate;
    private com.toedter.calendar.JDateChooser txtCreatedInitDate;
    private javax.swing.JTextField txtCustomer;
    private com.toedter.calendar.JDateChooser txtDeliveryEndDate;
    private com.toedter.calendar.JDateChooser txtDeliveryInitDate;
    private com.toedter.calendar.JDateChooser txtEventEndDate;
    private com.toedter.calendar.JDateChooser txtEventInitDate;
    // End of variables declaration//GEN-END:variables
}
