package almacen.form.providers;

import almacen.commons.utilities.Utility;
import almacen.form.index.IndexForm;
import common.tables.TableStatusProveedorBitacora;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.Renta;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import common.model.providers.CatalogStatusProvider;
import common.model.providers.StatusProviderByRenta;
import common.services.providers.ProviderStatusBitacoraService;
import common.utilities.UtilityCommon;

public class ProviderStatusBitacoraDialog extends javax.swing.JDialog {
    
    private final TableStatusProveedorBitacora tableStatusProveedorBitacora;
    private final ProviderStatusBitacoraService providerStatusBitacoraService;
    private final Long rentaId;
    private final String folio;

    public ProviderStatusBitacoraDialog(java.awt.Frame parent, boolean modal, Long rentaId, String folio) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Bitacora status proveedor");
        tableStatusProveedorBitacora = new TableStatusProveedorBitacora();
        UtilityCommon.addJtableToPane(400, 400, this.panelTable, tableStatusProveedorBitacora);
        providerStatusBitacoraService = ProviderStatusBitacoraService.getInstance();
        fillCombo();
        this.rentaId = rentaId;
        this.folio = folio;
        fillTable();
        this.lblTitle.setText("Seguimiento de ordenes al proveedor para el folio: "+folio);
        this.txtComment.requestFocus();
        addEscapeListener();
        
    }
    
    // close dialog when esc is pressed.
    private void addEscapeListener() {
        ActionListener escListener = (ActionEvent e) -> {
            setVisible(false);
            dispose();
        };

        this.getRootPane().registerKeyboardAction(escListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    }
    
    private void fillTable () {
        
        try {
            List<StatusProviderByRenta> statusProviderByRentas = 
                providerStatusBitacoraService.getStatusProviderByRenta(this.rentaId);
        
            tableStatusProveedorBitacora.format();
            DefaultTableModel tableModel = (DefaultTableModel) tableStatusProveedorBitacora.getModel();
            for (StatusProviderByRenta statusProviderByRenta : statusProviderByRentas) {
                Object fila[] = {                                          
                    statusProviderByRenta.getId(),
                    statusProviderByRenta.getCatalogStatusProvider().getDescription(),
                    statusProviderByRenta.getComment(),
                    statusProviderByRenta.getUser().getNombre() + " " + statusProviderByRenta.getUser().getApellidos(),
                    statusProviderByRenta.getCreatedAt()                        
              };
              tableModel.addRow(fila);
            }
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } finally{
           Toolkit.getDefaultToolkit().beep();
        }
    }
    
    private void save () {
        String error = null;
        
        CatalogStatusProvider catalogStatusProvider
                = (CatalogStatusProvider) cmbStatus.getSelectedItem();
        
        if (catalogStatusProvider.getId().equals(0L)) {
            error = "Selecciona un estatus.";
        }
        
        if (!txtComment.getText().isEmpty() && txtComment.getText().length() > 455) {
            error = "Comentario no debe exceder los 455 caracteres.";
        }
        
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
            return;
        }
        
        StatusProviderByRenta statusProviderByRenta = new StatusProviderByRenta();
        Renta renta = new Renta();
        renta.setRentaId(Integer.parseInt(String.valueOf(this.rentaId)));
        statusProviderByRenta.setRenta(renta);
        
        statusProviderByRenta.setCatalogStatusProvider(catalogStatusProvider);
        
        statusProviderByRenta.setUser(IndexForm.globalUser);
        
        statusProviderByRenta.setComment(txtComment.getText().trim());
        
        try {
            providerStatusBitacoraService.insertStatusProvicerByRenta(statusProviderByRenta);
            fillTable();
            Utility.pushNotification(String.format("Folio: %s, Usuario %s,  Estatus al orden proveedor registrado [%s]"
                    , this.folio
                    , IndexForm.globalUser.getNombre() + " " + IndexForm.globalUser.getApellidos()
                    , catalogStatusProvider.getDescription().toUpperCase()
            ));
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
        }
                
        
    }
    
    private void fillCombo () {
        
        cmbStatus.removeAllItems();
        cmbStatus.addItem(
                new CatalogStatusProvider(0L, ApplicationConstants.CMB_SELECCIONE)
        );
        try {
            List<CatalogStatusProvider> catalogStatusProviders
                    = providerStatusBitacoraService.getAll();
            
            catalogStatusProviders.stream().forEach(t -> {
                cmbStatus.addItem(t);
            });
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);  
        }
        
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        txtComment = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        panelTable = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        txtComment.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCommentKeyPressed(evt);
            }
        });

        jLabel1.setText("Comentario:");

        jLabel2.setText("Estatus:");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/guardar-24.png"))); // NOI18N
        jButton1.setToolTipText("Agregar");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblTitle.setText("jLabel3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtComment, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(98, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 20, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 198, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTable, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        save();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtCommentKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCommentKeyPressed
        if (evt.getKeyCode() == 10 ) {
            this.save();
        } 
    }//GEN-LAST:event_txtCommentKeyPressed

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
            java.util.logging.Logger.getLogger(ProviderStatusBitacoraDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ProviderStatusBitacoraDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ProviderStatusBitacoraDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ProviderStatusBitacoraDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ProviderStatusBitacoraDialog dialog = new ProviderStatusBitacoraDialog(new javax.swing.JFrame(), true, null, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<CatalogStatusProvider> cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panelTable;
    private javax.swing.JTextField txtComment;
    // End of variables declaration//GEN-END:variables
}
