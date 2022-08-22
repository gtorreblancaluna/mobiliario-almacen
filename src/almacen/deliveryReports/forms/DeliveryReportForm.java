package almacen.deliveryReports.forms;

import almacen.commons.enums.FilterEvent;
import almacen.commons.utilities.ConnectionDB;
import almacen.commons.utilities.Utility;
import almacen.deliveryReports.services.RentaResultService;
import almacen.index.forms.IndexForm;
import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.ALREADY_AVAILABLE;
import static common.constants.ApplicationConstants.MESSAGE_UNEXPECTED_ERROR;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.EstadoEvento;
import common.model.Renta;
import common.model.Tipo;
import common.model.Usuario;
import common.services.EstadoEventoService;
import common.services.TipoEventoService;
import common.services.UserService;
import common.services.UtilityService;
import common.utilities.CheckBoxHeader;
import common.utilities.ItemListenerHeaderCheckbox;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.log4j.Logger;

public class DeliveryReportForm extends javax.swing.JInternalFrame {

    private DeliveryReportFilterForm deliveryReportFilterForm;
    private List<Tipo> typesGlobal = new ArrayList<>();
    private List<EstadoEvento> statusListGlobal = new ArrayList<>();
    private List<Usuario> choferes = new ArrayList<>();
    private EstadoEventoService estadoEventoService;
    private TipoEventoService tipoEventoService;
    private static UserService userService;
    private static ConnectionDB connectionDB;
    private final static Integer LIMIT_RESULTS = 1_000;
    private UtilityService utilityService;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final String PATTERN_STRING_DATE = "dd/MM/yyyy";
    private static final Logger LOGGER = Logger.getLogger(DeliveryReportForm.class.getName());
    private static final RentaResultService rentaResultService = RentaResultService.getInstance();
    

    public DeliveryReportForm() {
        this.setClosable(true);
        this.setTitle("REPORTES CHOFER");
        initComponents();
        init();
    }
    
    private Map<String, Object> getInitParameters () {

        Map<String,Object> map = new HashMap<>();
        map.put(FilterEvent.LIMIT.getKey(), LIMIT_RESULTS);
        map.put(FilterEvent.STATUS.getKey(), Arrays.asList(ApplicationConstants.ESTADO_APARTADO));
        map.put(FilterEvent.TYPE.getKey(), Arrays.asList(ApplicationConstants.TIPO_PEDIDO));
        map.put(FilterEvent.SYSTEM_DATE.getKey(), UtilityCommon.getSystemDate("/"));
        return map;
    }
    
    private void checkGlobalList(){
        
        if (!IndexForm.globalUser.getAdministrador().equals("1"))
            return;
        
        try {
            tipoEventoService = TipoEventoService.getInstance();
            estadoEventoService = EstadoEventoService.getInstance();
            userService = UserService.getInstance();
            
            if (typesGlobal.isEmpty()) {
                typesGlobal = tipoEventoService.get();
            }
            if (statusListGlobal.isEmpty()) {
                statusListGlobal = estadoEventoService.get();
            }
            if (choferes.isEmpty()) {
                choferes = userService.getChoferes();
            }
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e,"ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private enum Column{
        
        BOOLEAN(0,"",Boolean.class, true),
        ID(1,"id",String.class, false),
        FOLIO(2,"Folio",String.class, false),
        DESCRIPTION_EVENT(3,"Dirección",String.class, false),
        EVENT_DATE(4,"Fecha evento",String.class, false),
        DELIVERY_DATE(5,"Fecha entrega",String.class, false),
        CUSTOMER(6,"Cliente",String.class, false),
        EVENT_TYPE(7,"Tipo",String.class, false),
        EVENT_STATUS(8,"Estatus Pedido",String.class, false),
        CHOFER(9,"Chofer",String.class, false),
        CHOFER_ID(10,"ID Chofer",String.class, false)
        ;
        
        Column (Integer number, String description, Class clazz, Boolean isEditable) {
            this.number = number;
            this.description = description;
            this.clazz = clazz;
            this.isEditable = isEditable;
        }
        private final Integer number;
        private final String description;
        private final Class clazz;
        private final Boolean isEditable;
        
        public Boolean getIsEditable() {
            return isEditable;
        }
        
        public Class getClazz () {
            return clazz;
        }

        public Integer getNumber() {
            return number;
        }
        
        public String getDescription () {
            return description;
        }
        
    }
    
    public static void searchAndFillTable (Map<String,Object> map) {
        formatTable();
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            map.put(FilterEvent.CHOFER_ID.getKey(), IndexForm.globalUser.getUsuarioId());
        }
        
        List<Renta> rentas;
        try {
            rentas = rentaResultService.getByParameters(map);
            lblInfo.setText("Eventos obtenidos: "+rentas.size()+". Límite de resultados: "+LIMIT_RESULTS);
        } catch (DataOriginException e) {
             JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        try {
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            for (Renta renta : rentas) {
                Object row[] = {
                    false,
                    renta.getRentaId(),
                    renta.getFolio(),
                    renta.getDescripcion(),
                    simpleDateFormat.format(UtilityCommon.getFromString(renta.getFechaEvento(), PATTERN_STRING_DATE)),
                    simpleDateFormat.format(UtilityCommon.getFromString(renta.getFechaDevolucion(), PATTERN_STRING_DATE)),
                    renta.getCliente().getNombre()+" "+renta.getCliente().getApellidos(),
                    renta.getTipo().getTipo(),
                    renta.getEstado().getDescripcion(),
                    renta.getChofer().getNombre() +" "+ renta.getChofer().getApellidos(),
                    renta.getChofer().getUsuarioId()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void formatTable() {
        
        String[] columnNames = {
            
            Column.BOOLEAN.getDescription(),
            Column.ID.getDescription(),
            Column.FOLIO.getDescription(), 
            Column.DESCRIPTION_EVENT.getDescription(),
            Column.EVENT_DATE.getDescription(), 
            Column.DELIVERY_DATE.getDescription(),                        
            Column.CUSTOMER.getDescription(),
            Column.EVENT_TYPE.getDescription(),
            Column.EVENT_STATUS.getDescription(),
            Column.CHOFER.getDescription(),
            Column.CHOFER_ID.getDescription()
            
            
        };
        Class[] types = {
            
            Column.BOOLEAN.getClazz(),
            Column.ID.getClazz(),
            Column.FOLIO.getClazz(), 
            Column.DESCRIPTION_EVENT.getClazz(),
            Column.EVENT_DATE.getClazz(), 
            Column.DELIVERY_DATE.getClazz(),                        
            Column.CUSTOMER.getClazz(),
            Column.EVENT_TYPE.getClazz(),
            Column.EVENT_STATUS.getClazz(),
            Column.CHOFER.getClazz(),
            Column.CHOFER_ID.getClazz()
           
            
        };
        
        boolean[] editable = {
            
            Column.BOOLEAN.getIsEditable(),
            Column.ID.getIsEditable(),
            Column.FOLIO.getIsEditable(), 
            Column.DESCRIPTION_EVENT.getIsEditable(),
            Column.EVENT_DATE.getIsEditable(),
            Column.DELIVERY_DATE.getIsEditable(),                        
            Column.CUSTOMER.getIsEditable(),
            Column.EVENT_TYPE.getIsEditable(),
            Column.EVENT_STATUS.getIsEditable(),
            Column.CHOFER.getIsEditable(),
            Column.CHOFER_ID.getIsEditable()
           
            
        };
        
        // customize column types
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public Class getColumnClass(int column) {
                return types[column];
            }
            
            @Override
            public boolean isCellEditable (int row, int column) {
                return editable[column];
            }
        };
       
       table.setModel(tableModel);

       TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
       table.setRowSorter(ordenarTabla);
       
     
       int[] anchos = {20,20,40,90,100,100,80,80,90,90,90};

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

       table.getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(0);
       table.getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(0);
       table.getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(0);
       
       table.getColumnModel().getColumn(Column.CHOFER_ID.getNumber()).setMaxWidth(0);
       table.getColumnModel().getColumn(Column.CHOFER_ID.getNumber()).setMinWidth(0);
       table.getColumnModel().getColumn(Column.CHOFER_ID.getNumber()).setPreferredWidth(0);
              
       // adding checkbox in header table
       TableColumn tc = table.getColumnModel().getColumn(Column.BOOLEAN.getNumber());
       tc.setCellEditor(table.getDefaultEditor(Boolean.class)); 
       tc.setHeaderRenderer(new CheckBoxHeader(new ItemListenerHeaderCheckbox(Column.BOOLEAN.getNumber(),table)));
       
       table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                if (col == Column.BOOLEAN.getNumber()) {
                    //checkSelectRowsInTable();
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnReload = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnSearchByFolio = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

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
        jScrollPane1.setViewportView(table);

        lblInfo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblInfo.setForeground(new java.awt.Color(204, 51, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1053, Short.MAX_VALUE)
            .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnReload.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/refresh-24.png"))); // NOI18N
        btnReload.setToolTipText("Recargar");
        btnReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
            }
        });

        btnSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/search-24.png"))); // NOI18N
        btnSearch.setToolTipText("Busqueda");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSearchByFolio.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByFolio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/searching-24.png"))); // NOI18N
        btnSearchByFolio.setToolTipText("Buscar por folio");
        btnSearchByFolio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchByFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchByFolioActionPerformed(evt);
            }
        });

        btnReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/pdf-24.png"))); // NOI18N
        btnReport.setToolTipText("Reporte por categorias");
        btnReport.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/excel-24.png"))); // NOI18N
        jButton1.setToolTipText("Exportar a excel");
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnReload, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearchByFolio, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearchByFolio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(btnReload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(341, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        // TODO add your handling code here:
        init();
    }//GEN-LAST:event_btnReloadActionPerformed
    private void init () {
        Map<String, Object> map = getInitParameters();
        searchAndFillTable(map);
    }
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        if (UtilityCommon.verifyIfInternalFormIsOpen(deliveryReportFilterForm,IndexForm.rootPanel)) {
            checkGlobalList();
            deliveryReportFilterForm = new DeliveryReportFilterForm(typesGlobal,statusListGlobal,choferes);
            deliveryReportFilterForm.setLocation(this.getWidth() / 2 - deliveryReportFilterForm.getWidth() / 2, this.getHeight() / 2 - deliveryReportFilterForm.getHeight() / 2 - 20);
            IndexForm.rootPanel.add(deliveryReportFilterForm);
            deliveryReportFilterForm.show();
        } else {
            JOptionPane.showMessageDialog(this, ALREADY_AVAILABLE);
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnSearchByFolioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchByFolioActionPerformed
        String folio = JOptionPane.showInputDialog("Ingresa el folio");
        if (folio == null) {
            return;
        }
        try {
            Integer number = Integer.parseInt(folio);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("folio", number);
            parameters.put("limit", 1);
            searchAndFillTable(parameters);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Folio no válido, ingresa un número válido para continuar ", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchByFolioActionPerformed

    private static void generateLogGeneratePDFPush (String report) {
        String logMessage = IndexForm.globalUser.getNombre() + " " + IndexForm.globalUser.getApellidos()+", a creado el PDF ["+report+"] ";
        LOGGER.info(logMessage);
        Utility.pushNotification(logMessage);
    }
    private static void openPDFReport (String rentaId, String choferName, String folio) {
        try {
            connectionDB = ConnectionDB.getInstance();
            String pathLocation = Utility.getPathLocation();
            String reportName = pathLocation+ApplicationConstants.NOMBRE_REPORTE_ENTREGAS+"-"+folio+".pdf";
            JasperReport masterReport = (JasperReport) JRLoader.loadObjectFromFile(pathLocation+ApplicationConstants.RUTA_REPORTE_ENTREGAS);
            
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("id_renta", rentaId);
            parameters.put("chofer", choferName);
            parameters.put("URL_IMAGEN",pathLocation+ApplicationConstants.LOGO_EMPRESA );
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(masterReport, parameters, connectionDB.getConnection());
            JasperExportManager.exportReportToPdfFile(jasperPrint, reportName);

            Desktop.getDesktop().open(
                    new File(reportName)
            );
            generateLogGeneratePDFPush("reporte por entrega por chofer, folio: "+folio);

        } catch (Exception e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(null, e,"ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed

        try {
            Utility.validateSelectCheckboxInTable(table,Column.BOOLEAN.getNumber());
            for (int i = 0; i < table.getRowCount(); i++) {
                if (Boolean.parseBoolean(table.getValueAt(i, Column.BOOLEAN.getNumber()).toString())) {
                    String folio = table.getValueAt(i, Column.FOLIO.getNumber()).toString();
                    String rentaId = table.getValueAt(i, Column.ID.getNumber()).toString();
                    String choferName = table.getValueAt(i, Column.CHOFER.getNumber()).toString();
                    openPDFReport(rentaId,choferName,folio);
                }
            }
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Reporte", JOptionPane.INFORMATION_MESSAGE);
        }

    }//GEN-LAST:event_btnReportActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        utilityService = UtilityService.getInstance();
        utilityService.exportarExcel(table);
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchByFolio;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    public static javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
