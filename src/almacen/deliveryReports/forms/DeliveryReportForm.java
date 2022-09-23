package almacen.deliveryReports.forms;

import almacen.commons.enums.FilterEvent;
import almacen.commons.utilities.ConnectionDB;
import almacen.commons.utilities.Utility;
import almacen.deliveryReports.services.TaskChoferDeliveryRetrieveService;
import almacen.deliveryReports.services.TaskChoferDeliveryUpdateService;
import almacen.index.forms.IndexForm;
import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.ALREADY_AVAILABLE;
import static common.constants.ApplicationConstants.MESSAGE_UNEXPECTED_ERROR;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.EstadoEvento;
import common.model.TaskChoferDeliveryVO;
import common.model.Tipo;
import common.model.Usuario;
import common.services.EstadoEventoService;
import common.services.TipoEventoService;
import common.services.UserService;
import common.services.UtilityService;
import common.utilities.CheckBoxHeader;
import common.utilities.ItemListenerHeaderCheckbox;
import common.utilities.UtilityCommon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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
    private static final TaskChoferDeliveryRetrieveService taskChoferDeliveryRetrieveService = TaskChoferDeliveryRetrieveService.getInstance();
    private static TaskChoferDeliveryUpdateService taskChoferDeliveryUpdateService;

    public DeliveryReportForm() {
        this.setClosable(true);
        this.setTitle("REPORTES CHOFER");
        initComponents();
        init();
    }
    
    private Map<String, Object> getInitParameters () {

        Map<String,Object> map = new HashMap<>();
        map.put(FilterEvent.LIMIT.getKey(), LIMIT_RESULTS);
        map.put(FilterEvent.ATTEND_TYPE.getKey(), Arrays.asList(ApplicationConstants.UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString()));
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
        TASK_CREATED_AT(9,"Fecha tarea",String.class, false),
        STATUS_TASK(10,"Descripcion tarea",String.class, false),
        ATTENDED_TYPE(11,"Atendido",String.class, false),
        CHOFER(12,"Chofer",String.class, false),
        CHOFER_ID(13,"ID Chofer",String.class, false),
        PENDING_TO_PAY(14,"Esta pendiente por pagar",Boolean.class, false),
        PENDING_TO_PAY_DESCRIPTION(15,"Pendiente por pagar",String.class, false),
        TASK_ID(16,"task id", String.class,false)
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
        
        List<TaskChoferDeliveryVO> tasks;
        try {
            tasks = taskChoferDeliveryRetrieveService.getByParameters(map);
            lblInfo.setText("Eventos obtenidos: "+tasks.size()+". Límite de resultados: "+LIMIT_RESULTS);
        } catch (DataOriginException e) {
             JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        try {
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            for (TaskChoferDeliveryVO task : tasks) {
                Object row[] = {
                    false,
                    task.getRenta().getRentaId(),
                    task.getRenta().getFolio(),
                    task.getRenta().getDescripcion(),
                    simpleDateFormat.format(UtilityCommon.getFromString(task.getRenta().getFechaEvento(), PATTERN_STRING_DATE)),
                    simpleDateFormat.format(UtilityCommon.getFromString(task.getRenta().getFechaDevolucion(), PATTERN_STRING_DATE)),
                    task.getRenta().getCliente().getNombre()+" "+task.getRenta().getCliente().getApellidos(),
                    task.getRenta().getTipo().getTipo(),
                    task.getRenta().getEstado().getDescripcion(),
                    simpleDateFormat.format(task.getCreatedAt()),
                    task.getStatusAlmacenTaskCatalogVO().getDescription(),  
                    task.getAttendAlmacenTaskTypeCatalogVO().getDescription(),
                    task.getChofer().getNombre() +" "+ task.getChofer().getApellidos(),
                    task.getChofer().getUsuarioId(),
                    task.getPendingToPayEvent(),
                    (task.getPendingToPayEvent() ? "PENDIENTE POR PAGAR" : ""),
                    task.getId()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception e) {
          JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void formatTable() {
        
        // Colorear fila cuando este pendiente por pagar
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
        {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
            {
                final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Boolean pendingToPay = Boolean.parseBoolean(table.getValueAt(row, Column.PENDING_TO_PAY.getNumber()).toString());

                if (pendingToPay){
                   component.setBackground(Color.RED);
                } else if (isSelected){
                    
                } else {
                    component.setBackground(row % 2 == 0 ? new Color(240,240,240) : Color.WHITE);
                }
                return component;
            }
        });
        
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
            Column.TASK_CREATED_AT.getDescription(),
            Column.STATUS_TASK.getDescription(),
            Column.ATTENDED_TYPE.getDescription(),
            Column.CHOFER.getDescription(),
            Column.CHOFER_ID.getDescription(),
            Column.PENDING_TO_PAY.getDescription(),
            Column.PENDING_TO_PAY_DESCRIPTION.getDescription(),
            Column.TASK_ID.getDescription() 
            
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
            Column.TASK_CREATED_AT.getClazz(),
            Column.STATUS_TASK.getClazz(),
            Column.ATTENDED_TYPE.getClazz(),
            Column.CHOFER.getClazz(),
            Column.CHOFER_ID.getClazz(),
            Column.PENDING_TO_PAY.getClazz(),
            Column.PENDING_TO_PAY_DESCRIPTION.getClazz(),
            Column.TASK_ID.getClazz() 
            
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
            Column.TASK_CREATED_AT.getIsEditable(),
            Column.STATUS_TASK.getIsEditable(),   
            Column.ATTENDED_TYPE.getIsEditable(),
            Column.CHOFER.getIsEditable(),
            Column.CHOFER_ID.getIsEditable(),
            Column.PENDING_TO_PAY.getIsEditable(),
            Column.PENDING_TO_PAY_DESCRIPTION.getIsEditable(),
            Column.TASK_ID.getIsEditable()
            
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
       
     
       int[] anchos = {20,20,30,90,80,80,40,40,60,40,40,60,40,40,40,60,40};

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
       
       table.getColumnModel().getColumn(Column.PENDING_TO_PAY.getNumber()).setMaxWidth(0);
       table.getColumnModel().getColumn(Column.PENDING_TO_PAY.getNumber()).setMinWidth(0);
       table.getColumnModel().getColumn(Column.PENDING_TO_PAY.getNumber()).setPreferredWidth(0);
       
       table.getColumnModel().getColumn(Column.TASK_ID.getNumber()).setMaxWidth(0);
       table.getColumnModel().getColumn(Column.TASK_ID.getNumber()).setMinWidth(0);
       table.getColumnModel().getColumn(Column.TASK_ID.getNumber()).setPreferredWidth(0);
       
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
        btnSearch = new javax.swing.JButton();
        btnSearchByFolio = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        btnAttend = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        btnReload = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

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
        table.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tableKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(table);

        lblInfo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblInfo.setForeground(new java.awt.Color(204, 51, 0));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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

        btnAttend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/user-attend-24.png"))); // NOI18N
        btnAttend.setToolTipText("Marcar como atendido");
        btnAttend.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAttend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttendActionPerformed(evt);
            }
        });

        btnReload.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/refresh-24.png"))); // NOI18N
        btnReload.setToolTipText("Recargar");
        btnReload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReloadActionPerformed(evt);
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearchByFolio, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAttend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 34, Short.MAX_VALUE)
                    .addContainerGap()))
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
                .addGap(2, 2, 2)
                .addComponent(btnAttend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jButton1)
                .addContainerGap(88, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(195, 195, 195)
                    .addComponent(btnReload)
                    .addContainerGap(127, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            parameters.put(FilterEvent.ATTEND_TYPE.getKey(), Arrays.asList(ApplicationConstants.UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString(),ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString()));
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
    private static void openPDFReport (String rentaId, String choferName, String folio) throws BusinessException {
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
            throw new BusinessException(e.getMessage(), e);
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

    private List<String> getIdsSelected () {
       List<String> ids = new ArrayList<>();
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(table.getValueAt(i, Column.BOOLEAN.getNumber()).toString())) {
                ids.add(
                        table.getValueAt(i, Column.TASK_ID.getNumber()).toString()
                );
            }
        }
        return ids;
   }
    
    private void updateAttendType (String taskTypeCatalogId, String taskTypeCatalogDescription) {
        try{
            Utility.validateSelectCheckboxInTable(table, Column.BOOLEAN.getNumber());
            Map<String,Object> parameters = new HashMap<>();
            List<String> ids = getIdsSelected();
            parameters.put("ids", ids);
            parameters.put("taskTypeCatalogId", taskTypeCatalogId);
            int seleccion = JOptionPane.showOptionDialog(this, "Tareas seleccionadas: "+ids.size()+". Marcar como: "+taskTypeCatalogDescription+", ¿Deseas continuar?", "Mensaje", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (seleccion == 0) {//presiono que si
                taskChoferDeliveryUpdateService = TaskChoferDeliveryUpdateService.getInstance();
                taskChoferDeliveryUpdateService.updateTaskChoferDelivery(parameters);
                String message = "Tareas actualizadas: "+ids.size()+", IDs: ["+ids.stream().collect(Collectors.joining(","))+"]";
                Utility.pushNotification("Usuario: "+IndexForm.globalUser.getNombre()+" "+IndexForm.globalUser.getApellidos()+". "+message);
                init();
            }
        } catch (DataOriginException | BusinessException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);   
        }
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        utilityService = UtilityService.getInstance();
        utilityService.exportarExcel(table);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnAttendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttendActionPerformed
        updateAttendType(ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString(),ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG_DESCRIPTION);
    }//GEN-LAST:event_btnAttendActionPerformed

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed
        Utility.selectCheckBoxWhenKeyPressedIsSpace(evt,table,Column.BOOLEAN.getNumber());
    }//GEN-LAST:event_tableKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAttend;
    private javax.swing.JButton btnReload;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchByFolio;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    public static javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
