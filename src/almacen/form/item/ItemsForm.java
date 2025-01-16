package almacen.form.item;

import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.InvalidDataException;
import common.form.items.AgregarArticuloDisponibilidadDialog;
import common.form.items.VerDisponibilidadArticulos;
import common.model.Articulo;
import common.model.EstadoEvento;
import common.model.ItemByFolioResultQuery;
import common.model.SearchItemByFolioParams;
import common.model.Tipo;
import common.services.EstadoEventoService;
import common.services.ItemService;
import common.services.TipoEventoService;
import common.services.UtilityService;
import common.tables.TableDisponibilidadArticulosShow;
import common.tables.TableIDetailtems;
import common.tables.TableItemsByFolio;
import common.utilities.UtilityCommon;
import java.awt.Component;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ItemsForm extends javax.swing.JInternalFrame {
    
    final ItemService itemService = ItemService.getInstance();
    List<Articulo> items;
    private static final DecimalFormat decimalFormat = 
            new DecimalFormat( ApplicationConstants.DECIMAL_FORMAT_SHORT );
    private static final DecimalFormat integerFormat = 
            new DecimalFormat( ApplicationConstants.INTEGER_FORMAT );
    private TableDisponibilidadArticulosShow tablaDisponibilidadArticulos;
    private final TableItemsByFolio tableItemsByFolio;
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ItemsForm.class.getName());
    private UtilityService utilityService;
    private List<Tipo> eventTypes = new ArrayList<>();
    private List<EstadoEvento> eventStatus = new ArrayList<>();
    private final EstadoEventoService estadoEventoService = EstadoEventoService.getInstance();
    private final TipoEventoService tipoEventoService = TipoEventoService.getInstance();
    private final TableIDetailtems tableIDetailtems;

    public ItemsForm() {
        initComponents();
        tableIDetailtems = new TableIDetailtems();
        UtilityCommon.addJtableToPane(940, 940, panelTableDetailItems, tableIDetailtems);
        init();
        tableItemsByFolio = new TableItemsByFolio();
        UtilityCommon.addJtableToPane(937, 305, panelTableItemsByFolio, tableItemsByFolio);
        eventListenerTabGeneral();
        this.setMaximizable(true);
        this.setResizable(true);
        
    }
    
    private void setCmbLimit () {
        cmbLimit.removeAllItems();
        cmbLimit.addItem("100");
        cmbLimit.addItem("1000");
        cmbLimit.addItem("5000");
        cmbLimit.addItem("10000");
    }
    
    private void eventListenerTabGeneral () {
        tabGeneral.addMouseListener(new MouseAdapter(){
        @Override
        public void mousePressed(MouseEvent e) {
            Component c = tabGeneral.getComponentAt(new Point(e.getX(), e.getY()));
                //TODO Find the right label and print it! :-)
                System.out.println("Selected Index: "+tabGeneral.getSelectedIndex());
                if (tabGeneral.getSelectedIndex() == 2 ) {
                    setCmbLimit();
                    fillParametersSearchByItemsByFolio();
                }
            }
        });
    }
    
    private void fillParametersSearchByItemsByFolio () {
        
        log.info("In fillParametersSearchByItemsByFolio..");
            if (eventTypes.isEmpty()) {
                new Thread(() -> {
                    try {
                        eventTypes = tipoEventoService.get();
                        cmbEventType.removeAllItems();
                        cmbEventType.addItem(
                            new Tipo(0, ApplicationConstants.CMB_SELECCIONE)
                        );
                        eventTypes.stream().forEach(t -> {
                            cmbEventType.addItem(t);
                        });
                    } catch (DataOriginException e) {
                        log.error(e.getMessage(),e);
                        JOptionPane.showMessageDialog(this, e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
                    }
                }).start();
            }

            if (eventStatus.isEmpty()) {
                new Thread(() -> {
                    try {
                        eventStatus = estadoEventoService.get();
                        cmbStatus.removeAllItems();
                        cmbStatus.addItem(
                            new EstadoEvento(0, ApplicationConstants.CMB_SELECCIONE)
                        );
                        eventStatus.stream().forEach(t -> {
                            cmbStatus.addItem(t);
                        });
                    } catch (DataOriginException e) {
                        log.error(e.getMessage(),e);
                        JOptionPane.showMessageDialog(this, e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
                    }
                }).start();
            }
        
        
        
    }
    
    private void initFillTable () {
        lblInfo.setText("Obteniendo artículos de la base de datos...");
        txtSearch.setEnabled(false);
        btnUpdateStock.setEnabled(false);
        new Thread(() -> {
            getItemsAndFillTable();
        }).start();
    }
    
    private void init () {
        
        initFillTable();
        this.setTitle("INVENTARIO");
        this.setClosable(true);        
        
        txtDisponibilidadFechaInicial.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoStatusChange();
            }
        });
        txtDisponibilidadFechaFinal.getDateEditor().addPropertyChangeListener((PropertyChangeEvent e) -> {
            if ("date".equals(e.getPropertyName())) {
                setLblInfoStatusChange();
            }
        });
        tablaDisponibilidadArticulos = new TableDisponibilidadArticulosShow();
        UtilityCommon.addJtableToPane(950, 400, jPanel6, tablaDisponibilidadArticulos);
        
        tableIDetailtems.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {                
                if (evt.getClickCount() == 2) {
                    editStock(tableIDetailtems.getSelectedRow());
                }
            }
        });
        
        tableIDetailtems.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                
                if(evt.getKeyCode() == KeyEvent.VK_ENTER && tableIDetailtems.getRowCount() > 0) {
                    editStock(tableIDetailtems.getSelectedRow());
                } else if (evt.getKeyCode() == KeyEvent.VK_UP && tableIDetailtems.getSelectedRow() == 0) {
                    txtSearch.requestFocus();
                    txtSearch.selectAll();
                }
            }
        });
        
    }
    
    private void setLblInfoStatusChange () {
        
        final String FORMAT_DATE = ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT; 
        int rowCount = tablaDisponibilidadArticulos.getRowCount();
        String initDate = txtDisponibilidadFechaInicial.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaInicial.getDate()) : null;
        String endDate = txtDisponibilidadFechaFinal.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaFinal.getDate()) : null;
        
        String message = ApplicationConstants.EMPTY_STRING;
        
        
        if (initDate != null && endDate != null) {
            if (txtDisponibilidadFechaInicial.getDate().compareTo(txtDisponibilidadFechaFinal.getDate()) > 0) {
                message = "ERROR. Fecha inicial debe ser menor que fecha final.";
            } else if (rowCount > 0) {
                message = String.format("Articulos a mostrar: [%s] entre el día [%s] y [%s]",rowCount,initDate,endDate);
            } else {
                message = String.format("Mostrar todos los artículos entre el día %s y %s", initDate,endDate);
            }
        }
        
        lblInfoConsultarDisponibilidad.setText(message);
        
    }
    
    private void fillTable (List<Articulo> items) {
        if (items == null) {
            return;
        }
        if (items.isEmpty()) {
            lblInfo.setText("No se encontraron artículos, puedes buscar por CÓDIGO, DESCRIPCIÓN o COLOR");
        } else {
            lblInfo.setText("Total artículos: "+integerFormat.format(items.size()));
        }
        
        for(Articulo articulo : items){
            
            DefaultTableModel temp = (DefaultTableModel) tableIDetailtems.getModel();
            Object fila[] = {
                  articulo.getArticuloId(),
                  articulo.getCodigo(),
                  articulo.getCantidad() != 0 ? integerFormat.format(articulo.getCantidad()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getRentados() != 0 ? integerFormat.format(articulo.getRentados()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getFaltantes() != 0 ? integerFormat.format(articulo.getFaltantes()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getReparacion() != 0 ? integerFormat.format(articulo.getReparacion()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getAccidenteTrabajo() != 0 ? integerFormat.format(articulo.getAccidenteTrabajo()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getDevolucion() != 0 ? integerFormat.format(articulo.getDevolucion()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getTotalCompras() != 0 ? integerFormat.format(articulo.getTotalCompras()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getUtiles() != 0 ? integerFormat.format(articulo.getUtiles()) : ApplicationConstants.EMPTY_STRING,
                  articulo.getCategoria().getDescripcion(),
                  articulo.getDescripcion(),
                  articulo.getColor().getColor(),
                  articulo.getFechaUltimaModificacion()
               };
               temp.addRow(fila);
        }
    }
    
    private boolean checkIfItemIdExist (Integer itemId) {
        
        boolean existItemId = false;
        
        for (int i = 0; i < tablaDisponibilidadArticulos.getRowCount(); i++) {            
            Integer itemIdInTable = Integer.parseInt(
                    tablaDisponibilidadArticulos.getValueAt(
                            i, TableDisponibilidadArticulosShow.Column.ID.getNumber()).toString());
            if (itemIdInTable.equals(itemId)) {
                existItemId = true;
                break;
            }
        }
            
        return existItemId;
    }
    
    private void addItemFromDisponibilidadDialog (List<Integer> itemsId) {
        
        for (Integer itemId : itemsId) {
            if (!checkIfItemIdExist(itemId)) {
                
                Articulo item = itemService.obtenerArticuloPorId(itemId);
        
                if(item == null){
                    return;
                }
        
                DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
                Object row[] = {
                      false,
                      item.getArticuloId(),
                      item.getCodigo(),
                      item.getCategoria().getDescripcion(),
                      item.getDescripcion(),
                      item.getColor().getColor(),
                      item.getPrecioRenta(),
                      item.getCantidad()
                };
                temp.addRow(row);
                setLblInfoStatusChange();
            }
        }
    }
    
    private void showAndSelectItems () {
        
        AgregarArticuloDisponibilidadDialog dialog = 
                new AgregarArticuloDisponibilidadDialog(null, true, items);
        List<Integer> items = dialog.showDialog();
        
        if (items != null) {
            addItemFromDisponibilidadDialog(items);
        }

        
    }
    
    private void getItemsAndFillTable () {
        tableIDetailtems.format();
        
        try {
            items = itemService.obtenerArticulosBusquedaInventario(new HashMap<>());
            txtSearch.setEnabled(true);
            txtSearch.requestFocus();
            fillTable(items);
            btnUpdateStock.setEnabled(true);
        } catch (Exception e) {
            Logger.getLogger(ItemsForm.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(this, "Ocurrio un inesperado\n "+e, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
            lblInfo.setText(e.getMessage());
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
    }    

        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tabGeneral = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblInfo = new javax.swing.JLabel();
        btnExportItemTableToExcel = new javax.swing.JButton();
        btnUpdateStock = new javax.swing.JButton();
        panelTableDetailItems = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtDisponibilidadFechaInicial = new com.toedter.calendar.JDateChooser();
        txtDisponibilidadFechaFinal = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        radioBtnTodos = new javax.swing.JRadioButton();
        radioBtnFechaEntrega = new javax.swing.JRadioButton();
        radioBtnFechaDevolucion = new javax.swing.JRadioButton();
        check_solo_negativos = new javax.swing.JCheckBox();
        btnAddItem = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        btnShowAvailivity = new javax.swing.JButton();
        lblInfoConsultarDisponibilidad = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jbtnSearch = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        txtSearchInitialDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndDate = new com.toedter.calendar.JDateChooser();
        txtSearchInitialEventDate = new com.toedter.calendar.JDateChooser();
        jLabel14 = new javax.swing.JLabel();
        txtSearchEndEventDate = new com.toedter.calendar.JDateChooser();
        jLabel16 = new javax.swing.JLabel();
        txtSearchFolioRenta = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        cmbEventType = new javax.swing.JComboBox<>();
        jLabel19 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        txtSearchLikeItemDescription = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        panelTableItemsByFolio = new javax.swing.JPanel();
        lblInfoGeneral = new javax.swing.JLabel();

        jPanel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        lblInfo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        btnExportItemTableToExcel.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnExportItemTableToExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/excel-24.png"))); // NOI18N
        btnExportItemTableToExcel.setToolTipText("Exportar Excel");
        btnExportItemTableToExcel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExportItemTableToExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportItemTableToExcelActionPerformed(evt);
            }
        });

        btnUpdateStock.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnUpdateStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/edit-24.png"))); // NOI18N
        btnUpdateStock.setToolTipText("Editar stock");
        btnUpdateStock.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
                .addComponent(btnUpdateStock, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExportItemTableToExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportItemTableToExcel)
                    .addComponent(btnUpdateStock))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelTableDetailItemsLayout = new javax.swing.GroupLayout(panelTableDetailItems);
        panelTableDetailItems.setLayout(panelTableDetailItemsLayout);
        panelTableDetailItemsLayout.setHorizontalGroup(
            panelTableDetailItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelTableDetailItemsLayout.setVerticalGroup(
            panelTableDetailItemsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 518, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTableDetailItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTableDetailItems, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Artículos", jPanel1);

        jPanel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText("Fecha inicial");

        txtDisponibilidadFechaInicial.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txtDisponibilidadFechaInicial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDisponibilidadFechaInicialMouseClicked(evt);
            }
        });
        txtDisponibilidadFechaInicial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDisponibilidadFechaInicialKeyPressed(evt);
            }
        });

        txtDisponibilidadFechaFinal.setFont(new java.awt.Font("Microsoft Sans Serif", 0, 12)); // NOI18N
        txtDisponibilidadFechaFinal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDisponibilidadFechaFinalMouseClicked(evt);
            }
        });
        txtDisponibilidadFechaFinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDisponibilidadFechaFinalKeyPressed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText("Fecha final");

        buttonGroup1.add(radioBtnTodos);
        radioBtnTodos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        radioBtnTodos.setSelected(true);
        radioBtnTodos.setText("Ver todos los traslapes");
        radioBtnTodos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        radioBtnTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnTodosActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioBtnFechaEntrega);
        radioBtnFechaEntrega.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        radioBtnFechaEntrega.setText("Ver por fecha de entrega");
        radioBtnFechaEntrega.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        radioBtnFechaEntrega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnFechaEntregaActionPerformed(evt);
            }
        });

        buttonGroup1.add(radioBtnFechaDevolucion);
        radioBtnFechaDevolucion.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        radioBtnFechaDevolucion.setText("Ver por fecha de devolución");
        radioBtnFechaDevolucion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        radioBtnFechaDevolucion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioBtnFechaDevolucionActionPerformed(evt);
            }
        });

        check_solo_negativos.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        check_solo_negativos.setText("Mostrar solo faltantes");
        check_solo_negativos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        check_solo_negativos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                check_solo_negativosActionPerformed(evt);
            }
        });

        btnAddItem.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnAddItem.setMnemonic('a');
        btnAddItem.setText("Agregar");
        btnAddItem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton6.setMnemonic('q');
        jButton6.setText("Quitar");
        jButton6.setToolTipText("Eliminar elemento");
        jButton6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        btnShowAvailivity.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnShowAvailivity.setText("Mostrar disponibilidad");
        btnShowAvailivity.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnShowAvailivity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowAvailivityActionPerformed(evt);
            }
        });

        lblInfoConsultarDisponibilidad.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblInfoConsultarDisponibilidad.setForeground(new java.awt.Color(204, 51, 0));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(check_solo_negativos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDisponibilidadFechaInicial, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblInfoConsultarDisponibilidad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(txtDisponibilidadFechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioBtnTodos, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioBtnFechaEntrega, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioBtnFechaDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 163, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(btnAddItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnShowAvailivity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(1, 1, 1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDisponibilidadFechaFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDisponibilidadFechaInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(radioBtnTodos)
                        .addComponent(radioBtnFechaDevolucion)
                        .addComponent(radioBtnFechaEntrega)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(check_solo_negativos)
                    .addComponent(lblInfoConsultarDisponibilidad, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAddItem)
                        .addComponent(jButton6)
                        .addComponent(btnShowAvailivity)))
                .addGap(2, 13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 447, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabGeneral.addTab("Disponibilidad", jPanel2);

        jbtnSearch.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jbtnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/search-24.png"))); // NOI18N
        jbtnSearch.setToolTipText("Buscar");
        jbtnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText("Fecha de creación:");

        txtSearchInitialDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchInitialDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchInitialDateMouseClicked(evt);
            }
        });
        txtSearchInitialDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchInitialDateKeyPressed(evt);
            }
        });

        txtSearchEndDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchEndDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchEndDateMouseClicked(evt);
            }
        });
        txtSearchEndDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchEndDateKeyPressed(evt);
            }
        });

        txtSearchInitialEventDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchInitialEventDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchInitialEventDateMouseClicked(evt);
            }
        });
        txtSearchInitialEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchInitialEventDateKeyPressed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText("Fecha del evento:");

        txtSearchEndEventDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchEndEventDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSearchEndEventDateMouseClicked(evt);
            }
        });
        txtSearchEndEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchEndEventDateKeyPressed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText("Folio:");

        txtSearchFolioRenta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchFolioRenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchFolioRentaKeyPressed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel17.setText("Limitar resultados a:");

        cmbLimit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbStatus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel18.setText("Estado del evento:");

        cmbEventType.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbEventType.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText("Tipo de evento:");

        jButton7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/excel-24.png"))); // NOI18N
        jButton7.setToolTipText("Exportar Excel");
        jButton7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        txtSearchLikeItemDescription.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchLikeItemDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchLikeItemDescriptionKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText("Artículo:");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(144, 144, 144)
                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchLikeItemDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(419, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))))
                .addGap(7, 7, 7)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbEventType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtSearchLikeItemDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(7, 7, 7)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addGap(7, 7, 7)
                        .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jbtnSearch)
                            .addComponent(jButton7))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelTableItemsByFolioLayout = new javax.swing.GroupLayout(panelTableItemsByFolio);
        panelTableItemsByFolio.setLayout(panelTableItemsByFolioLayout);
        panelTableItemsByFolioLayout.setHorizontalGroup(
            panelTableItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1112, Short.MAX_VALUE)
        );
        panelTableItemsByFolioLayout.setVerticalGroup(
            panelTableItemsByFolioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 407, Short.MAX_VALUE)
        );

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTableItemsByFolio, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblInfoGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelTableItemsByFolio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Artículos por folio", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabGeneral)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabGeneral)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (txtSearch.getText().length() > 1000) {
            lblInfo.setText("Longitud de caracteres no validos");
            return;
        }
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER && tableIDetailtems.getRowCount() > 0) {
            editStock(0);
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN && tableIDetailtems.getRowCount() > 0) {
            tableIDetailtems.requestFocus();
            tableIDetailtems.changeSelection(0,0,false, false);
        } else {
            tableIDetailtems.format();
            List<Articulo> itemsFiltered = 
                UtilityCommon.applyFilterToItems(items,txtSearch.getText());
            fillTable(itemsFiltered);
        }
        
        
    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtDisponibilidadFechaInicialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaInicialMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaInicialMouseClicked

    private void txtDisponibilidadFechaInicialKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaInicialKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaInicialKeyPressed

    private void txtDisponibilidadFechaFinalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaFinalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaFinalMouseClicked

    private void txtDisponibilidadFechaFinalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDisponibilidadFechaFinalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDisponibilidadFechaFinalKeyPressed

    private void radioBtnTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnTodosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnTodosActionPerformed

    private void radioBtnFechaEntregaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnFechaEntregaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnFechaEntregaActionPerformed

    private void radioBtnFechaDevolucionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioBtnFechaDevolucionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioBtnFechaDevolucionActionPerformed

    private void check_solo_negativosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_check_solo_negativosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_check_solo_negativosActionPerformed

    private void btnAddItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddItemActionPerformed
        showAndSelectItems();
    }//GEN-LAST:event_btnAddItemActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        DefaultTableModel temp = (DefaultTableModel) tablaDisponibilidadArticulos.getModel();
        for( int i = temp.getRowCount() - 1; i >= 0; i-- ){
            if (Boolean.parseBoolean(tablaDisponibilidadArticulos.getValueAt(i, TableDisponibilidadArticulosShow.Column.BOOLEAN.getNumber()).toString())) {
                temp.removeRow(i);
            }
        }
        setLblInfoStatusChange();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void editStock (int selectedRow) {
        
        String itemDescription = 
                tableIDetailtems.getValueAt(selectedRow, TableIDetailtems.Column.DESCRIPTION.getNumber()).toString();
        String currentStock = 
                tableIDetailtems.getValueAt(selectedRow, TableIDetailtems.Column.STOCK.getNumber()).toString();
        StockItemFormDialog dialog = 
                new StockItemFormDialog(null, true, itemDescription,currentStock);        
        String stockUpdated = dialog.showDialog();

        if (stockUpdated == null || stockUpdated.isEmpty()) {
            return;
        }
            
        try {
            
            Integer itemId = 
                        Integer.parseInt(tableIDetailtems.getValueAt(
                                selectedRow, TableIDetailtems.Column.ID.getNumber()).toString());
            
            Articulo itemToUpdate = itemService.obtenerArticuloPorId(itemId);
            
            itemToUpdate.setCantidad(Float.parseFloat(stockUpdated));
            itemToUpdate.setFechaUltimaModificacion(new Timestamp(System.currentTimeMillis()));
            itemService.actualizarArticulo(itemToUpdate);
            tableIDetailtems.setValueAt(stockUpdated, selectedRow, TableIDetailtems.Column.STOCK.getNumber());
            lblInfo.setText("Se actualizó el stock con éxito.");
            UtilityCommon.setTimeout(() -> lblInfo.setText(""), 4000);
            
            items.stream()
                    .filter(t -> t.getArticuloId().equals(itemId))
                    .forEach(item -> {
                        item.setFechaUltimaModificacion(new Timestamp(System.currentTimeMillis()));
                        item.setCantidad(Float.parseFloat(stockUpdated));
                    });                    
            
        } catch (Exception exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }

        
    }
    
    
    private SearchItemByFolioParams getParametersToSearchItemsByFolio () throws InvalidDataException{
        
        SearchItemByFolioParams searchItemByFolioParams = new SearchItemByFolioParams();
         
        final String FORMAT_DATE = ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT; 
         
         searchItemByFolioParams.setInitCreatedAtEvent(
                 txtSearchInitialDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchInitialDate.getDate()) : null
         );
         searchItemByFolioParams.setEndCreatedAtEvent(
                 txtSearchEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchEndDate.getDate()) : null
         );
         searchItemByFolioParams.setInitialEventDate(
                txtSearchInitialEventDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchInitialEventDate.getDate()) : null
         );
         searchItemByFolioParams.setEndEventDate(
                 txtSearchEndEventDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtSearchEndEventDate.getDate()) : null
         );
         
         EstadoEvento estadoEvento = (EstadoEvento) cmbStatus.getModel().getSelectedItem();
         Tipo eventType = (Tipo) cmbEventType.getModel().getSelectedItem();
         
         searchItemByFolioParams.setEventStatusId(estadoEvento.getEstadoId());
         searchItemByFolioParams.setEventTypeId(eventType.getTipoId());
         searchItemByFolioParams.setLikeItemDescription(
                 UtilityCommon.removeAccents(txtSearchLikeItemDescription.getText().toLowerCase().trim()));
         
         searchItemByFolioParams.setLimit(Integer.parseInt(cmbLimit.getSelectedItem().toString()));
         try {
             if (!txtSearchFolioRenta.getText().isEmpty()){
                searchItemByFolioParams.setFolio(Long.parseLong(txtSearchFolioRenta.getText()));
             }
         } catch (NumberFormatException e) {
           throw new InvalidDataException("Folio no valido.");
         }
         
         return searchItemByFolioParams;
    
    }
    
    private void searchItemsByFolio () {
        try {
            SearchItemByFolioParams searchItemByFolioParams
                    = getParametersToSearchItemsByFolio();
            List<ItemByFolioResultQuery> itemByFolioResultQuerys = itemService.getItemsByFolio(searchItemByFolioParams);
            tableItemsByFolio.format();
            if (!itemByFolioResultQuerys.isEmpty()) {
                lblInfoGeneral.setText("Total: "+itemByFolioResultQuerys.size()+", Límite de resultados: "+cmbLimit.getSelectedItem());
                DefaultTableModel tableModel = (DefaultTableModel) tableItemsByFolio.getModel();
                
                    for(ItemByFolioResultQuery item : itemByFolioResultQuerys){
                        Object row[] = {
                            item.getEventId(),
                            item.getEventFolio(),
                            integerFormat.format(item.getItemAmount()),
                            item.getItemDescription(),
                            decimalFormat.format(item.getItemUnitPrice()),
                            item.getItemDiscountRate() > 0 ? integerFormat.format(item.getItemDiscountRate()) : ApplicationConstants.EMPTY_STRING,
                            item.getItemSubTotal() > 0 ? decimalFormat.format(item.getItemSubTotal()) : ApplicationConstants.EMPTY_STRING,
                            item.getEventDeliveryDate(),
                            item.getEventCreatedAtDate(),
                            item.getEventType(),
                            item.getEventStatus()
                        };
                    tableModel.addRow(row);
                }
            } else {
                lblInfoGeneral.setText("No se obtuvieron resultados.");
            }
        } catch (BusinessException | DataOriginException e) {
            log.error(e.getMessage(),e);
            JOptionPane.showMessageDialog(this, e.getMessage(), 
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }finally{
           Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public void mostrar_ver_disponibilidad_articulos() {
         // mostrara la ventana de disponibilidad de articulos
        String initDate = new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(txtDisponibilidadFechaInicial.getDate());
        String endDate = new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT).format(txtDisponibilidadFechaFinal.getDate());
        List<Long> itemsId = new ArrayList<>();
        for (int i = 0; i < tablaDisponibilidadArticulos.getRowCount(); i++) {
            itemsId.add(Long.parseLong(tablaDisponibilidadArticulos.getValueAt(i, TableDisponibilidadArticulosShow.Column.ID.getNumber()).toString()));
        }
        VerDisponibilidadArticulos win = new VerDisponibilidadArticulos(
                null,
                true,
                initDate,
                endDate,
                check_solo_negativos.isSelected(),
                radioBtnFechaEntrega.isSelected(),
                radioBtnFechaDevolucion.isSelected(),
                itemsId,
                null,
                null
        );
        win.setVisible(true);
        win.setLocationRelativeTo(null);
    }
    
    private void exportToExcelItemsByFolio () {
        utilityService = UtilityService.getInstance();
        utilityService.exportarExcel(tableItemsByFolio);
    }
    
    private void btnShowAvailivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAvailivityActionPerformed
        StringBuilder mensaje = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConstants.SIMPLE_DATE_FORMAT_SHORT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        int contador = 0;
        
        if ((txtDisponibilidadFechaInicial.getDate() == null
            || txtDisponibilidadFechaFinal.getDate() == null)) {
            mensaje.append(++contador).append(". Fecha inicial y final son requeridos.\n");
        }else{
            // 2018-12-04 verificamos que la fecha inicial sea menor a la fecha final
            LocalDate initDate = LocalDate.parse(sdf.format(txtDisponibilidadFechaInicial.getDate()),formatter);
            LocalDate endDate = LocalDate.parse(sdf.format(txtDisponibilidadFechaFinal.getDate()),formatter);

            if(initDate.isAfter(endDate))
            mensaje.append(++contador).append(". Fecha inicial debe ser menor a fecha final.\n");
        }

        if(!mensaje.toString().isEmpty())
        JOptionPane.showMessageDialog(null, mensaje.toString(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
        else
        this.mostrar_ver_disponibilidad_articulos();
    }//GEN-LAST:event_btnShowAvailivityActionPerformed

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        // TODO add your handling code here:
        this.searchItemsByFolio();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void txtSearchInitialDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateMouseClicked

    private void txtSearchInitialDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialDateKeyPressed

    private void txtSearchEndDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateMouseClicked

    private void txtSearchEndDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndDateKeyPressed

    private void txtSearchInitialEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateMouseClicked

    private void txtSearchInitialEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchInitialEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchInitialEventDateKeyPressed

    private void txtSearchEndEventDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateMouseClicked

    private void txtSearchEndEventDateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchEndEventDateKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchEndEventDateKeyPressed

    private void txtSearchFolioRentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchFolioRentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            this.searchItemsByFolio();
        }
    }//GEN-LAST:event_txtSearchFolioRentaKeyPressed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        exportToExcelItemsByFolio();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void txtSearchLikeItemDescriptionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchLikeItemDescriptionKeyPressed
        if (evt.getKeyCode() == 10 ) {
            this.searchItemsByFolio();
        }
    }//GEN-LAST:event_txtSearchLikeItemDescriptionKeyPressed

    private void btnUpdateStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStockActionPerformed
        if (tableIDetailtems.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, ApplicationConstants.SELECT_A_ROW_NECCESSARY,
                    ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);  
        } else {
            editStock(tableIDetailtems.getSelectedRow());
        }
        
        
    }//GEN-LAST:event_btnUpdateStockActionPerformed

    private void btnExportItemTableToExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportItemTableToExcelActionPerformed
        utilityService = UtilityService.getInstance();
        utilityService.exportarExcel(tableIDetailtems);
    }//GEN-LAST:event_btnExportItemTableToExcelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnExportItemTableToExcel;
    private javax.swing.JButton btnShowAvailivity;
    private javax.swing.JButton btnUpdateStock;
    private javax.swing.ButtonGroup buttonGroup1;
    public static javax.swing.JCheckBox check_solo_negativos;
    private javax.swing.JComboBox<Tipo> cmbEventType;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox<EstadoEvento> cmbStatus;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JLabel lblInfo;
    private static javax.swing.JLabel lblInfoConsultarDisponibilidad;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JPanel panelTableDetailItems;
    private javax.swing.JPanel panelTableItemsByFolio;
    public static javax.swing.JRadioButton radioBtnFechaDevolucion;
    public static javax.swing.JRadioButton radioBtnFechaEntrega;
    public static javax.swing.JRadioButton radioBtnTodos;
    private javax.swing.JTabbedPane tabGeneral;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaFinal;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaInicial;
    private javax.swing.JTextField txtSearch;
    private com.toedter.calendar.JDateChooser txtSearchEndDate;
    private com.toedter.calendar.JDateChooser txtSearchEndEventDate;
    private javax.swing.JTextField txtSearchFolioRenta;
    private com.toedter.calendar.JDateChooser txtSearchInitialDate;
    private com.toedter.calendar.JDateChooser txtSearchInitialEventDate;
    private javax.swing.JTextField txtSearchLikeItemDescription;
    // End of variables declaration//GEN-END:variables
}
