
package almacen.orders.forms;

import almacen.commons.service.UserService;
import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.ALREADY_AVAILABLE;
import static common.constants.ApplicationConstants.ESTADO_APARTADO;
import static common.constants.ApplicationConstants.ESTADO_EN_RENTA;
import static common.constants.ApplicationConstants.MESSAGE_UNEXPECTED_ERROR;
import static common.constants.ApplicationConstants.PUESTO_CHOFER;
import static common.constants.ApplicationConstants.SELECT_A_ROW_TO_GENERATE_REPORT;
import static common.constants.ApplicationConstants.TIPO_PEDIDO;
import common.exceptions.DataOriginException;
import common.model.EstadoEvento;
import common.model.Tipo;
import common.model.Usuario;
import common.services.UtilityService;
import common.utilities.UtilityCommon;
import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.log4j.Logger;
import almacen.index.forms.IndexForm;
import static almacen.index.forms.IndexForm.rootPanel;
import almacen.orders.models.OrderWarehouseVO;
import almacen.orders.services.OrderWarehouseService;
import almacen.commons.utilities.ConnectionDB;
import almacen.commons.utilities.Utility;

public class OrdersForm extends javax.swing.JInternalFrame {

    private static OrderWarehouseService orderWarehouseService;
    // variables gloables para reutilizar en los filtros y combos
    private final List<Tipo> typesGlobal = new ArrayList<>();
    private final List<EstadoEvento> statusListGlobal = new ArrayList<>();
    private final List<Usuario> choferes = new ArrayList<>();
    private OrdersFilterForm ordersFilterForm;
    private final UtilityService utilityService = UtilityService.getInstance();
    private static ConnectionDB connectionDB;
    private static final Logger LOGGER = Logger.getLogger(OrdersForm.class.getName());
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
    private static final String PATTERN_STRING_DATE = "dd/MM/yyyy";
    private static final UserService userService = UserService.getInstance();
    
    public OrdersForm() {
        initComponents();
        this.setClosable(true);
        orderWarehouseService = OrderWarehouseService.getInstance();
        init();
    }
    
    private Map<String, Object> getInitParameters () {
        String puestoUserId = IndexForm.globalUser.getPuesto().getPuestoId()+"";
        String puestoChoferId = PUESTO_CHOFER+"";
        Map<String,Object> map = new HashMap<>();
       
        if (puestoUserId.equals(puestoChoferId) && !IndexForm.globalUser.getAdministrador().equals("1")) {
            btnReport.setVisible(false);
            map.put(Filter.DRIVER_ID.getValue(), IndexForm.globalUser.getUsuarioId());
        } else if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            btnDeliveryReport.setVisible(false);
        }
        List<String> status = new ArrayList<>();
        status.add(ESTADO_APARTADO);
        status.add(ESTADO_EN_RENTA);
        
        List<String> types = new ArrayList<>();
        types.add(TIPO_PEDIDO);
        
        
        map.put(Filter.SYSTEM_DATE.getValue(), UtilityCommon.getSystemDate("/") );
        map.put(Filter.LIMIT.getValue(), 1000);
        map.put(Filter.TYPE.getValue(), types);
        map.put(Filter.STATUS.getValue(), status);
        
        return map;
    }
    
    private void init () {
        Map<String, Object> map = getInitParameters();
        searchAndFillTable(map);
    }
    
       
    public static void searchAndFillTable (Map<String,Object> map) {
        formatTable();
         
         
        String puestoUserId = IndexForm.globalUser.getPuesto().getPuestoId()+"";
        String puestoChoferId = PUESTO_CHOFER+"";
        
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            map.put(OrdersForm.Filter.STATUS.getValue(), Arrays.asList(ESTADO_APARTADO,ESTADO_EN_RENTA));  
            map.put(OrdersForm.Filter.TYPE.getValue(), Arrays.asList(TIPO_PEDIDO));
            if (puestoUserId.equals(puestoChoferId)) {
                map.put(OrdersForm.Filter.DRIVER_ID.getValue(), IndexForm.globalUser.getUsuarioId());
            } else {
                map.put(OrdersForm.Filter.FILTER_BY_CATEGORY_USER.getValue(), IndexForm.globalUser.getUsuarioId());
            }
        }
         
         List<OrderWarehouseVO> orders;
         try {
             orders = orderWarehouseService.getByParameters(map);
             if (orders.isEmpty()) {
                lblInfo.setText("No se obtuvieron resultados :( ");
             } else {
                 lblInfo.setText("Total de eventos: "+orders.size());
             }
             
         } catch (DataOriginException e) {
             JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
             return;
         }
         
         try {
            
             DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            for (OrderWarehouseVO order : orders) {
                Object row[] = {
                    order.getEventId(),
                    order.getFolio(),
                    order.getAddressEvent(),
                    simpleDateFormat.format(UtilityCommon.getFromString(order.getEventDate(), PATTERN_STRING_DATE)),
                    simpleDateFormat.format(UtilityCommon.getFromString(order.getDeliveryDate(), PATTERN_STRING_DATE)) + " - " + order.getDeliveryHour(),
                    order.getCustomer(),
                    order.getEventType(),
                    order.getEventStatus(),
                    order.getChoferName()
                };
                tableModel.addRow(row);
            }
         } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
         }
         
    }
    
    private enum Colum{
        ID(0,"id"),
        FOLIO(1,"Folio"),
        DESCRIPTION_EVENT(2,"Dirección"),
        EVENT_DATE(3,"Fecha evento"),
        DELIVERY_DATE(4,"Fecha entrega"),
        CUSTOMER(5,"Cliente"),
        EVENT_TYPE(6,"Tipo"),
        EVENT_STATUS(7,"Estatus Pedido"),
        CHOFER(8,"Chofer")
        ;
        
        Colum (Integer number, String description) {
            this.number = number;
            this.description = description;
        }
        private final Integer number;
        private final String description;

        public Integer getNumber() {
            return number;
        }
        
        public String getDescription () {
            return description;
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
        FILTER_BY_CATEGORY_USER("filterByCategoryUser"),
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
        Object[][] data = {{"","","","","","","",""}};
        String[] columnNames = {          
                        Colum.ID.getDescription(),
                        Colum.FOLIO.getDescription(), 
                        Colum.DESCRIPTION_EVENT.getDescription(),
                        Colum.EVENT_DATE.getDescription(), 
                        Colum.DELIVERY_DATE.getDescription(),                        
                        Colum.CUSTOMER.getDescription(),
                        Colum.EVENT_TYPE.getDescription(),
                        Colum.EVENT_STATUS.getDescription(),
                        Colum.CHOFER.getDescription()
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        table.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        table.setRowSorter(ordenarTabla);

        int[] anchos = {20,60,180,140,140,140,80,80,70};

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
        
        table.getColumnModel().getColumn(Colum.CHOFER.getNumber()).setMaxWidth(0);
        table.getColumnModel().getColumn(Colum.CHOFER.getNumber()).setMinWidth(0);
        table.getColumnModel().getColumn(Colum.CHOFER.getNumber()).setPreferredWidth(0);
        
        
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

        table.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
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
        table.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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
        btnSearchByFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchByFolioActionPerformed(evt);
            }
        });

        btnReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReport.setText("Reporte por categorias");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        btnDeliveryReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnDeliveryReport.setText("Reporte entregas");
        btnDeliveryReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDeliveryReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeliveryReportActionPerformed(evt);
            }
        });

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
                .addGap(7, 7, 7)
                .addComponent(btnReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDeliveryReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1085, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDescriptionFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 481, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        utilityService.exportarExcel(table);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnDeliveryReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeliveryReportActionPerformed
        // TODO add your handling code here:
        if (!verifyIfOneRowIsSelected()) {return;}
        
        String rentaId = table.getValueAt(table.getSelectedRow(), Colum.ID.getNumber()).toString();
        String chofer = table.getValueAt(table.getSelectedRow(), Colum.CHOFER.getNumber()).toString();
        
        try {      
            connectionDB = ConnectionDB.getInstance();
            JasperPrint jasperPrint;
            String pathLocation = Utility.getPathLocation();
           
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+ApplicationConstants.RUTA_REPORTE_ENTREGAS);  
            // enviamos los parametros
            Map<String,Object> map = new HashMap<>();
            map.put("id_renta", rentaId);
            map.put("chofer", chofer);
            map.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA);

            jasperPrint = JasperFillManager.fillReport(masterReport, map, connectionDB.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS);
            
            Desktop.getDesktop().open(file2);
            generateLogGeneratePDFPush("reporte de entregas");

        } catch (Exception e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }//GEN-LAST:event_btnDeliveryReportActionPerformed

    private static boolean verifyIfOneRowIsSelected () {
        
        if (table.getSelectedRow() == - 1){
            JOptionPane.showMessageDialog(null, SELECT_A_ROW_TO_GENERATE_REPORT, "Reporte", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    public static void generateReportByCategories (Usuario user) {
    
        if (!verifyIfOneRowIsSelected()) {return;}
        
        String rentaId = table.getValueAt(table.getSelectedRow(), Colum.ID.getNumber()).toString();
        
        try {
            connectionDB = ConnectionDB.getInstance();
            JasperPrint jasperPrint;
            String archivo = ApplicationConstants.RUTA_REPORTE_CATEGORIAS;
            String pathLocation = Utility.getPathLocation();
            System.out.println("Cargando desde: " + archivo);
            if (archivo == null) {
                JOptionPane.showMessageDialog(null, "No se encuentra el Archivo jasper");
            }
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+archivo);
            
            Map<String,Object> parametro = new HashMap<>();
            //guardamos el parametro

            parametro.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            parametro.put("ID_RENTA",rentaId);
            parametro.put("ID_USUARIO",user.getUsuarioId());
            parametro.put("NOMBRE_ENCARGADO_AREA",user.getNombre() + " " + user.getApellidos());
            
            jasperPrint = JasperFillManager.fillReport(masterReport, parametro, connectionDB.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathLocation+ApplicationConstants.NOMBRE_REPORTE_CATEGORIAS);
            File file2 = new File(pathLocation+ApplicationConstants.NOMBRE_REPORTE_CATEGORIAS);
            
            Desktop.getDesktop().open(file2);
            
            generateLogGeneratePDFPush("reporte por categorías");

        } catch (Exception e) {
            System.out.println("Mensaje de Error:" + e.toString());
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    
    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        if (!verifyIfOneRowIsSelected()) {return;}

        if (IndexForm.globalUser.getAdministrador().equals("1")) {
            String rentaId = table.getValueAt(table.getSelectedRow(), Colum.ID.getNumber()).toString();
            String folio = table.getValueAt(table.getSelectedRow(), Colum.FOLIO.getNumber()).toString();
            
            try {
              final List<Usuario> usersInCategoriesAlmacen = userService.getUsersInCategoriesAlmacenAndEvent(Integer.parseInt(rentaId));
              if (usersInCategoriesAlmacen.isEmpty()) {
                  JOptionPane.showMessageDialog(this, "Ops!, no se puede generar el reporte, por que no existen usuarios que tengan categorias asignadas al folio: "+folio, "Error", JOptionPane.ERROR_MESSAGE);
              } else if (usersInCategoriesAlmacen.size() == 1) {
                  generateReportByCategories(usersInCategoriesAlmacen.get(0));
              } else {
                  final SelectUserGenerateReportByCategoriesDialog win = new SelectUserGenerateReportByCategoriesDialog(null, true, usersInCategoriesAlmacen);
                  win.setLocationRelativeTo(null);
                  win.setVisible(true);
              }

            } catch (DataOriginException e) {
                JOptionPane.showMessageDialog(this,e,"Error",JOptionPane.ERROR_MESSAGE);
                LOGGER.error(e);
            }
              
        } else {
            generateReportByCategories(IndexForm.globalUser);
        }
        
        
    }//GEN-LAST:event_btnReportActionPerformed

    private static void generateLogGeneratePDFPush (String report) {
        String logMessage = IndexForm.globalUser.getNombre() + " " + IndexForm.globalUser.getApellidos()+", generó con éxito PDF ["+report+"] ";
        LOGGER.info(logMessage);
        Utility.pushNotification(logMessage);
    }
    private void btnSearchByFolioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchByFolioActionPerformed
        String folio = JOptionPane.showInputDialog("Ingresa el folio");
        if (folio == null) {
            return;
        }
        try {
            Integer number = Integer.parseInt(folio);
            Map<String, Object> parameters = getInitParameters();
            parameters.put("folio", number);
            searchAndFillTable(parameters);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Folio no válido, ingresa un número válido para continuar ", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchByFolioActionPerformed


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
