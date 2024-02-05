package almacen.form.task;

import almacen.commons.enums.FilterEvent;
import common.services.EstadoEventoService;
import common.services.TipoEventoService;
import common.constants.ApplicationConstants;
import static common.constants.ApplicationConstants.ALREADY_AVAILABLE;
import static common.constants.ApplicationConstants.MESSAGE_UNEXPECTED_ERROR;
import common.exceptions.DataOriginException;
import common.model.EstadoEvento;
import common.model.Tipo;
import common.services.UtilityService;
import common.utilities.UtilityCommon;
import java.text.SimpleDateFormat;
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
import org.apache.log4j.Logger;
import almacen.form.index.IndexForm;
import static almacen.form.index.IndexForm.rootPanel;
import almacen.service.task.TaskAlmacenRetrieveService;
import common.utilities.ConnectionDB;
import almacen.commons.utilities.Utility;
import almacen.service.task.TaskAlmacenUpdateService;
import common.exceptions.BusinessException;
import common.model.Renta;
import common.model.TaskAlmacenVO;
import common.model.Usuario;
import common.services.RentaService;
import common.services.UserService;
import common.utilities.CheckBoxHeader;
import common.utilities.ItemListenerHeaderCheckbox;
import common.utilities.JasperPrintUtility;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.swing.table.TableColumn;

public class TasksAlmacenForm extends javax.swing.JInternalFrame {
    
    private static TaskAlmacenRetrieveService taskAlmacenRetrieveService;
    // variables gloables para reutilizar en los filtros y combos
    private List<Tipo> typesGlobal = new ArrayList<>();
    private List<EstadoEvento> statusListGlobal = new ArrayList<>();
    private List<Usuario> usersInCategoriesAlmacen = new ArrayList<>();
    private TasksAlmacenFilterForm ordersFilterForm;
    private final UtilityService utilityService = UtilityService.getInstance();
    private static ConnectionDB connectionDB;
    private static final Logger LOGGER = Logger.getLogger(TasksAlmacenForm.class.getName());
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final String PATTERN_STRING_DATE = "dd/MM/yyyy";
    private EstadoEventoService estadoEventoService;
    private TipoEventoService tipoEventoService;
    private UserService userService;
    private RentaService rentaService;
    private final TaskAlmacenUpdateService taskAlmacenUpdateService = TaskAlmacenUpdateService.getInstance();
    private final static Integer LIMIT_RESULTS = 1_000;
    
    
    public TasksAlmacenForm() {
        initComponents();
        this.setClosable(true);
        this.setTitle("TAREAS ALMACEN");
        taskAlmacenRetrieveService = TaskAlmacenRetrieveService.getInstance();
        rentaService = RentaService.getInstance();
        init();
    }
    
    private Map<String, Object> getInitParameters () {

        Map<String,Object> map = new HashMap<>();
        map.put(FilterEvent.LIMIT.getKey(), LIMIT_RESULTS);
        map.put(FilterEvent.ATTEND_TYPE.getKey(), Arrays.asList(ApplicationConstants.UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString()));
        return map;
    }
    
    private void init () {
        Map<String, Object> map = getInitParameters();
        searchAndFillTable(map);
    }
    
    private static void showFiltersApplied (Map<String,Object> map) {
        List<String> filters = new ArrayList<>();
        map.entrySet().forEach(entry -> {
            for (FilterEvent filter : FilterEvent.values()) {
                if (entry.getValue() != null && !entry.getValue().equals("null") && !entry.getValue().equals("") && entry.getKey().equals(filter.getKey())) {
                    if (entry.getKey().equals(FilterEvent.ATTEND_TYPE.getKey())) {
                        List<String> array = (List<String>) entry.getValue();
                        List<String> result = new ArrayList<>();
                        for (String attendType : array) {
                            if (attendType.equals(ApplicationConstants.UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString())) {
                                result.add(ApplicationConstants.UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG_DESCRIPTION.toLowerCase());
                            } else if (attendType.equals(ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString())){
                                result.add(ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG_DESCRIPTION.toLowerCase());
                            }
                        }
                        filters.add(filter.getDescription()+"=["+result.stream().collect(Collectors.joining(","))+"]");
                    } else {
                        filters.add(filter.getDescription()+"="+entry.getValue());
                    }
                }
            }
        });
        if (!filters.isEmpty()) {
            lblDescriptionFilters.setText("Filtros: "+filters.stream().collect(Collectors.joining(",")));
        } else {
            lblDescriptionFilters.setText("No se aplicaron filtros");
        }
    }
    
       
    public static void searchAndFillTable (Map<String,Object> map) {
        
        formatTable();
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            map.put(FilterEvent.USER_BY_CATEGORY_ID.getKey(), IndexForm.globalUser.getUsuarioId());
        }
         
         List<TaskAlmacenVO> orders;
         try {
             orders = taskAlmacenRetrieveService.getByParameters(map);
             if (orders.isEmpty()) {
                lblInfo.setText("No se obtuvieron resultados :( ");
             } else {
                lblInfo.setText("Tareas obtenidas: "+orders.size()+", Limite de resultados: "+LIMIT_RESULTS);
             }
             
         } catch (DataOriginException e) {
             JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
             return;
         }
         
         try {
            
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            for (TaskAlmacenVO task : orders) {
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
                    task.getUserByCategory().getNombre()+" "+task.getUserByCategory().getApellidos(),
                    task.getUserByCategory().getUsuarioId(),
                    task.getId()
                };
                tableModel.addRow(row);
            }
            
            showFiltersApplied(map);
         } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, MESSAGE_UNEXPECTED_ERROR, JOptionPane.ERROR_MESSAGE);
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
        USER_NAME(12,"Encargado",String.class,false),
        USER_ID(13,"usuario id", String.class,false),
        TASK_ID(14,"task id", String.class,false)
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
            Column.TASK_CREATED_AT.getDescription(),
            Column.STATUS_TASK.getDescription(),
            Column.ATTENDED_TYPE.getDescription(),
            Column.USER_NAME.getDescription(),
            Column.USER_ID.getDescription(),
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
            Column.USER_NAME.getClazz(),
            Column.USER_ID.getClazz(),
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
            Column.USER_NAME.getIsEditable(),
            Column.USER_ID.getIsEditable(),
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
       
     
       int[] anchos = {20,20,40,90,100,100,80,90,90,90,90,90,90,90,40,40};

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
       
       table.getColumnModel().getColumn(Column.USER_ID.getNumber()).setMaxWidth(0);
       table.getColumnModel().getColumn(Column.USER_ID.getNumber()).setMinWidth(0);
       table.getColumnModel().getColumn(Column.USER_ID.getNumber()).setPreferredWidth(0);
       
       table.getColumnModel().getColumn(Column.TASK_ID.getNumber()).setMaxWidth(0);
       table.getColumnModel().getColumn(Column.TASK_ID.getNumber()).setMinWidth(0);
       table.getColumnModel().getColumn(Column.TASK_ID.getNumber()).setPreferredWidth(0);
       
       // adding checkbox in header table
       TableColumn tc = table.getColumnModel().getColumn(Column.BOOLEAN.getNumber());
       tc.setCellEditor(table.getDefaultEditor(Boolean.class)); 
       tc.setHeaderRenderer(new CheckBoxHeader(new ItemListenerHeaderCheckbox(Column.BOOLEAN.getNumber(),table)));
       
//       JTableHeader header = table.getTableHeader();
//       header.addMouseListener(new java.awt.event.MouseAdapter() {
//           @Override
//            public void mouseClicked(MouseEvent event) {
//                Point point = event.getPoint();
//                int col = table.columnAtPoint(point);
//                if (col == Column.BOOLEAN.getNumber()) {
//                    setLabelRowsSelected();
//                }
//            }
//       });
              
       table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());
                String name = table.getColumnName(col);
                System.out.println("Column index selected " + col + " " + name);
                if (col == Column.BOOLEAN.getNumber()) {
                    setLabelRowsSelected();
                }
            }
        });
    }
           
   private static void setLabelRowsSelected () {
       int selectRows = 0;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(table.getValueAt(i, Column.BOOLEAN.getNumber()).toString())) {
                selectRows++;
            }
        }
        
        lblCountTable.setText(selectRows <= 0 ? "" : selectRows+"");
   }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnReload = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnSearchByFolio = new javax.swing.JButton();
        btnReport = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnAttend = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        lblCountTable = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblInfo = new javax.swing.JLabel();
        lblDescriptionFilters = new javax.swing.JLabel();

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
        btnSearch.setMnemonic('b');
        btnSearch.setToolTipText("[Alt+B] Busqueda");
        btnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnSearchByFolio.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByFolio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/searching-24.png"))); // NOI18N
        btnSearchByFolio.setMnemonic('f');
        btnSearchByFolio.setToolTipText("[Alt+F] Buscar por folio");
        btnSearchByFolio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchByFolio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchByFolioActionPerformed(evt);
            }
        });

        btnReport.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/pdf-24.png"))); // NOI18N
        btnReport.setMnemonic('p');
        btnReport.setToolTipText("[Alt+P] Generar PDF");
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

        btnAttend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/user-attend-24.png"))); // NOI18N
        btnAttend.setMnemonic('a');
        btnAttend.setToolTipText("[Alt+A] Marcar como atendido");
        btnAttend.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAttend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttendActionPerformed(evt);
            }
        });

        lblCountTable.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(btnAttend, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnSearchByFolio, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnReport, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnReload, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblCountTable, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btnReport)
                .addGap(9, 9, 9)
                .addComponent(btnSearchByFolio)
                .addGap(4, 4, 4)
                .addComponent(btnSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAttend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnReload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCountTable, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        lblDescriptionFilters.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDescriptionFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(lblDescriptionFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(23, 23, 23)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnReloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReloadActionPerformed
        // TODO add your handling code here:
        init();
    }//GEN-LAST:event_btnReloadActionPerformed

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
            if (usersInCategoriesAlmacen.isEmpty()) {
                usersInCategoriesAlmacen = userService.getUsersInCategoriesAlmacen();
            }
        } catch (DataOriginException e) {
            JOptionPane.showMessageDialog(this, e,"ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        if (UtilityCommon.verifyIfInternalFormIsOpen(ordersFilterForm,IndexForm.rootPanel)) {
            checkGlobalList();
            ordersFilterForm = new TasksAlmacenFilterForm(typesGlobal,statusListGlobal,usersInCategoriesAlmacen);
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
        
    private void btnReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        
        try {
            UtilityCommon.validateSelectCheckboxInTable(table,Column.BOOLEAN.getNumber());
            for (int i = 0; i < table.getRowCount(); i++) {
                if (Boolean.parseBoolean(table.getValueAt(i, Column.BOOLEAN.getNumber()).toString())) {
                    String folio = table.getValueAt(i, Column.FOLIO.getNumber()).toString();
                    String rentaId = table.getValueAt(i, Column.ID.getNumber()).toString();
                    String userId = table.getValueAt(i, Column.USER_ID.getNumber()).toString();
                    String userName = table.getValueAt(i, Column.USER_NAME.getNumber()).toString();
                    
                    final Renta renta = rentaService.getById(Integer.parseInt(rentaId));
                    
                    JasperPrintUtility.openPDFReportByCategories(userId,userName,renta,
                            connectionDB, Utility.getPathLocation());
                    generateLogGeneratePDFPush("reporte por categorías, folio: "+folio);
                }
            }
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), "Reporte", JOptionPane.INFORMATION_MESSAGE);  
        }
        
    }//GEN-LAST:event_btnReportActionPerformed

    private static void generateLogGeneratePDFPush (String report) {
        String logMessage = IndexForm.globalUser.getNombre() + " " + IndexForm.globalUser.getApellidos()+", a creado el PDF ["+report+"] ";
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
            parameters.put(FilterEvent.ATTEND_TYPE.getKey(), Arrays.asList(ApplicationConstants.UN_ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString(),ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString()));
            searchAndFillTable(parameters);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Folio no válido, ingresa un número válido para continuar ", "ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSearchByFolioActionPerformed

    private void updateAttendType (String taskTypeCatalogId, String taskTypeCatalogDescription) {
        try{
            UtilityCommon.validateSelectCheckboxInTable(table, Column.BOOLEAN.getNumber());
            Map<String,Object> parameters = new HashMap<>();
            List<String> ids = UtilityCommon.getIdsSelected(table, Column.BOOLEAN.getNumber(), Column.TASK_ID.getNumber());
            parameters.put("ids", ids);
            parameters.put("taskTypeCatalogId", taskTypeCatalogId);
            int seleccion = JOptionPane.showOptionDialog(this, "Tareas seleccionadas: "+ids.size()+". Marcar como: "+taskTypeCatalogDescription+", ¿Deseas continuar?", "Mensaje", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (seleccion == 0) {//presiono que si
                taskAlmacenUpdateService.updateTypeAttend(parameters);
                String message = "Tareas actualizadas: "+ids.size()+", IDs: ["+ids.stream().collect(Collectors.joining(","))+"]";
                Utility.pushNotification("Usuario: "+IndexForm.globalUser.getNombre()+" "+IndexForm.globalUser.getApellidos()+". "+message);
                init();
            }
        } catch (DataOriginException | BusinessException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);   
        }
    }
    
    private void btnAttendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttendActionPerformed
        updateAttendType(ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG.toString(),ApplicationConstants.ATTEND_ALMACEN_TASK_TYPE_CATALOG_DESCRIPTION);
    }//GEN-LAST:event_btnAttendActionPerformed

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed
        UtilityCommon.selectCheckBoxWhenKeyPressedIsSpace(evt,table,Column.BOOLEAN.getNumber());
        setLabelRowsSelected();
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private static javax.swing.JLabel lblCountTable;
    public static javax.swing.JLabel lblDescriptionFilters;
    public static javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
