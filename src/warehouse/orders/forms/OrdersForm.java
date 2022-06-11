
package warehouse.orders.forms;

import common.constants.ApplicationConstants;
import common.exceptions.DataOriginException;
import common.model.EstadoEvento;
import common.model.Tipo;
import common.model.Usuario;
import common.services.UtilityService;
import common.utilities.UtilityCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import warehouse.index.forms.IndexForm;
import static warehouse.index.forms.IndexForm.rootPanel;
import warehouse.orders.models.OrderWarehouseVO;
import warehouse.orders.services.OrderWarehouseService;

public class OrdersForm extends javax.swing.JInternalFrame {

    private static OrderWarehouseService orderWarehouseService;
    // variables gloables para reutilizar en los filtros y combos
    private List<Tipo> typesGlobal = new ArrayList<>();
    private List<EstadoEvento> statusListGlobal = new ArrayList<>();
    private List<Usuario> choferes = new ArrayList<>();
    private OrdersFilterForm ordersFilterForm;
    private final UtilityService utilityService = UtilityService.getInstance();

    public OrdersForm() {
        initComponents();
        this.setClosable(true);
        orderWarehouseService = OrderWarehouseService.getInstance();
        init();
    }
    
    
    private void init () {
        String puestoUserId = IndexForm.globalUser.getPuesto().getPuestoId()+"";
        String puestoChoferId = ApplicationConstants.PUESTO_CHOFER+"";
       
        if (puestoUserId.equals(puestoChoferId) && !IndexForm.globalUser.getAdministrador().equals("1")) {
            btnReport.setVisible(false);
        } else if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            btnDeliveryReport.setVisible(false);
        }
        Map<String,Object> map = new HashMap<>();
        map.put(Filter.SYSTEM_DATE.getValue(), UtilityCommon.getSystemDate("/") );
        map.put(Filter.LIMIT.getValue(), 1000);
        searchAndFillTable(map);
    }
    
       
    public static void searchAndFillTable (Map<String,Object> map) {
         formatTable();
         
         List<OrderWarehouseVO> orders;
         try {
             orders = orderWarehouseService.getByParameters(map);
             if (orders.isEmpty()) {
                lblInfo.setText("No se obtuvieron resultados :( ");
             } else {
                 lblInfo.setText("Total de eventos: "+orders.size());
             }
             
         } catch (DataOriginException e) {
             JOptionPane.showMessageDialog(null, e, ApplicationConstants.MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
             return;
         }
         DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
         for (OrderWarehouseVO order : orders) {
             Object row[] = {
                 order.getEventId(),
                 order.getFolio(),
                 order.getAddressEvent(),
                 order.getEventDate(),
                 order.getDeliveryDate() + " " + order.getDeliveryHour(),
                 order.getCustomer(),
                 ""
             };
             tableModel.addRow(row);
         }
         
    }
    
    private enum Colum{
        ID(0),
        FOLIO(1),
        DESCRIPTION_EVENT(2),
        EVENT_DATE(3),
        DELIVERY_DATE(4),
        CUSTOMER(5);
        
        Colum (Integer number) {
            this.number = number;
        }
        private final Integer number;

        public Integer getNumber() {
            return number;
        }
    }
    
    
    public enum Filter {
        
        CUSTOMER("customer"),
        SYSTEM_DATE("systemDate"),
        LIMIT("limit"),
        DRIVER_ID("driverId"),
        TYPE("type"),
        STATUS("statusId"),
        INIT_DELIVERY_DATE("initDeliveryDate"),
        END_DELIVERY_DATE("endDeliveryDate"),
        INIT_CREATED_DATE("initCreatedDate"),
        END_CREATED_DATE("endCreatedDate"),
        INIT_EVENT_DATE("initEventDate"),
        END_EVENT_DATE("endEventDate"),
        FOLIO("folio");
        
        Filter (String value) {
            this.value = value;
        }
        
        private final String value;
        
        public String getValue(){
            return value;
        }
    }
    
    private static void formatTable() {
        Object[][] data = {{"","","","","",""}};
        String[] columnNames = {          
                        "id",
                        "Folio", 
                        "Dirección",
                        "Fecha evento", 
                        "Fecha entrega",                        
                        "Cliente",
                        "Status"
                       
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        table.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        table.setRowSorter(ordenarTabla);

        int[] anchos = {20,60,180,140,140,140,80};

        for (int inn = 0; inn < table.getColumnCount(); inn++) {
            table.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) table.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        table.getColumnModel().getColumn(Colum.ID.getNumber()).setMaxWidth(0);
        table.getColumnModel().getColumn(Colum.ID.getNumber()).setMinWidth(0);
        table.getColumnModel().getColumn(Colum.ID.getNumber()).setPreferredWidth(0);    
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        jPanel1 = new javax.swing.JPanel();
        btnReload = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnSearchByFolio = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        btnDeliveryReport = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        lblInfo = new javax.swing.JLabel();
        lblDescriptionFilters = new javax.swing.JLabel();

        table.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        table.setRowHeight(14);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table);

        btnReload.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReload.setText("Recargar");
        btnReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });

        btnSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearch.setText("Busqueda");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSearchByFolio.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByFolio.setText("Buscar por folio");
        btnSearchByFolio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReport.setText("Reporte");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnDeliveryReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnDeliveryReport.setText("Reporte entregas");
        btnDeliveryReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton1.setText("Exportar Excel");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeliveryReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 422, Short.MAX_VALUE)
                .addComponent(btnSearchByFolio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReload)
                .addGap(17, 17, 17))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReport)
                    .addComponent(btnDeliveryReport)
                    .addComponent(btnSearchByFolio)
                    .addComponent(btnSearch)
                    .addComponent(btnReload)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        lblInfo.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        lblDescriptionFilters.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDescriptionFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescriptionFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 448, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_tableMouseClicked

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        // TODO add your handling code here:
        init();
    }//GEN-LAST:event_btnReloadActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        if (UtilityCommon.verifyIfInternalFormIsOpen(ordersFilterForm,IndexForm.rootPanel)) {
            
            ordersFilterForm = new OrdersFilterForm(typesGlobal,statusListGlobal,choferes);
            ordersFilterForm.setLocation(this.getWidth() / 2 - ordersFilterForm.getWidth() / 2, this.getHeight() / 2 - ordersFilterForm.getHeight() / 2 - 20);
            rootPanel.add(ordersFilterForm);
            ordersFilterForm.show();
        } else {
            JOptionPane.showMessageDialog(this, "La ventana ya se encuentra disponible");
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        utilityService.exportarExcel(table);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeliveryReport;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchByFolio;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDescriptionFilters;
    public static javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
