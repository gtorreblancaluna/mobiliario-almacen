
package almacen.form.providers;

import almacen.commons.utilities.Utility;
import almacen.form.index.IndexForm;
import almacen.service.SystemService;
import common.services.RentaService;
import common.tables.TableViewOrdersProviders;
import common.tables.TableViewOrdersProvidersDetail;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.exceptions.InvalidDataException;
import common.exceptions.NoDataFoundException;
import common.form.provider.OrderProviderCopyFormDialog;
import common.model.DatosGenerales;
import common.model.Renta;
import common.model.providers.OrdenProveedor;
import common.model.providers.OrderProviderCopyParameter;
import common.model.providers.ParameterOrderProvider;
import common.model.providers.queryresult.DetailOrderSupplierQueryResult;
import common.services.UtilityService;
import common.services.providers.OrderProviderService;
import common.utilities.JasperPrintUtility;
import common.utilities.UtilityCommon;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ViewOrdersProvidersForm extends javax.swing.JInternalFrame {

    private int indexTabPanelActive = 0;
    private final TableViewOrdersProviders tableViewOrdersProviders;
    private final TableViewOrdersProvidersDetail tableViewOrdersProvidersDetail;
    private final SystemService systemService = SystemService.getInstance();
    private final RentaService rentaService = RentaService.getInstance();
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0.00" );
    private final OrderProviderService orderService = OrderProviderService.getInstance();
    private static final org.apache.log4j.Logger LOGGER = 
            org.apache.log4j.Logger.getLogger(ViewOrdersProvidersForm.class.getName());
    private final UtilityService utilityService = UtilityService.getInstance();

    public ViewOrdersProvidersForm() {
        initComponents();
        tableViewOrdersProviders = new TableViewOrdersProviders();
        tableViewOrdersProvidersDetail = new TableViewOrdersProvidersDetail();
        UtilityCommon.addJtableToPane(950, 400, tabPanelGeneral, tableViewOrdersProviders);
        UtilityCommon.addJtableToPane(950, 400, tabPanelDetail, tableViewOrdersProvidersDetail);
        this.setClosable(true);
        initComboBox();
        eventListener();
        this.setMaximizable(true);
        this.setResizable(true);
    }
    
    private void eventListener () {
        tabGeneral.addMouseListener(new MouseAdapter(){
        @Override
        public void mousePressed(MouseEvent e) {
            Component c = tabGeneral.getComponentAt(new Point(e.getX(), e.getY()));
                //TODO Find the right label and print it! :-)
                indexTabPanelActive = tabGeneral.getSelectedIndex();            
            }
        });
        
        tableViewOrdersProviders.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editOrderProvider();
                }
            }
        });
    }
    
    private void search () {
              
       switch (IndexTabPanel.getEnum(indexTabPanelActive)) {
           case TAB_PANEL_GENERAL:
               fillTableTabPanelGeneral();
               break;
           case TAB_PANEL_DETAIL:
               fillTableTabPanelDetail();
               break;
           default:
               throw new AssertionError();
       }
   }
    
    private ParameterOrderProvider getParameters () {
        ParameterOrderProvider parameter = new ParameterOrderProvider();

         Integer folioRenta = null;
         Integer orderNumber = null;
         try{
             folioRenta = Integer.parseInt(this.txtSearchFolioRenta.getText());
         }catch(NumberFormatException e){
             System.out.println(e);
         }

         try{
             orderNumber = Integer.parseInt(this.txtSearchOrderNumber.getText());
         }catch(NumberFormatException e){
             System.out.println(e);
         }

         if(folioRenta != null && !folioRenta.toString().equals("")){
             parameter.setFolioRenta(folioRenta);
         }else if(orderNumber != null && !orderNumber.toString().equals("")){
             parameter.setOrderId(orderNumber);
         }else{
            if(!this.txtSearchByNameProvider.getText().equals("")){
                parameter.setNameProvider(this.txtSearchByNameProvider.getText());
            }
            if(this.txtSearchInitialDate.getDate() != null && this.txtSearchEndDate.getDate() != null){
                parameter.setInitDate(new Timestamp(txtSearchInitialDate.getDate().getTime()));
                parameter.setEndDate(new Timestamp(txtSearchEndDate.getDate().getTime()));
            }
            if(this.txtSearchInitialEventDate.getDate() != null && this.txtSearchEndEventDate.getDate() != null){
                try {
                    String formatDate = "dd/MM/yyyy";
                    parameter.setInitEventDate(UtilityCommon.getStringFromDate(txtSearchInitialEventDate.getDate(),formatDate));
                    parameter.setEndEventDate(UtilityCommon.getStringFromDate(txtSearchEndEventDate.getDate(),formatDate));
                } catch (ParseException e) {
                    LOGGER.error(e);
                    JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
                }
            }
            if(!this.cmbStatus.getModel().getSelectedItem().equals(ApplicationConstants.CMB_SELECCIONE)){
                parameter.setStatus(this.cmbStatus.getSelectedItem().toString());
            }
         }
         parameter.setLimit(Integer.parseInt(this.cmbLimit.getSelectedItem().toString()));
         return parameter;
   }
    
    private void initComboBox(){
       
        this.cmbLimit.removeAllItems();
        this.cmbStatus.removeAllItems();
        cmbLimit.addItem("100");
        cmbLimit.addItem("500");
        cmbLimit.addItem("1000");
        cmbLimit.addItem("10000");
        cmbStatus.addItem(ApplicationConstants.CMB_SELECCIONE);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_ORDER);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_PENDING);
        cmbStatus.addItem(ApplicationConstants.DS_STATUS_ORDER_PROVIDER_CANCELLED);     
   
   }
    
    private void fillTableTabPanelDetail () {
       ParameterOrderProvider parameter = getParameters();
       List<DetailOrderSupplierQueryResult> list;
       tableViewOrdersProvidersDetail.format();
       
        try{
            list = orderService.getDetailOrderSupplierCustomize(parameter);
        }catch(NoDataFoundException e){
            this.lblInfoGeneral.setText("No se han obtenido resultados :(");
            return;
        }catch(BusinessException e){
            LOGGER.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        return;
        }finally{
           Toolkit.getDefaultToolkit().beep();
        }

        this.lblInfoGeneral.setText("Registros: "+list.size()+". Límite: "+
                this.cmbLimit.getSelectedItem().toString());

       DefaultTableModel tableModel = (DefaultTableModel) tableViewOrdersProvidersDetail.getModel();

       for(DetailOrderSupplierQueryResult detail : list){
            Object fila[] = {                                          
                detail.getOrderSupplierId(),
                detail.getOrderSupplierDetailId(),
                detail.getRentaId(),
                detail.getFolio(),
                detail.getProduct(),
                detail.getAmount(),
                detail.getPrice() <= 0 ? "" : decimalFormat.format(detail.getPrice()),
                detail.getTotal() <= 0 ? "" : decimalFormat.format(detail.getTotal()),
                detail.getEventDate(),
                detail.getUser(),
                detail.getSupplier(),
                detail.getDetailComment(),
                detail.getOrderDetailType(),
                detail.getCreado()
              };
              tableModel.addRow(fila);

       }
       
   }
    
    private String getValueIdBySelectedRow (ColumnToGetValue columnToGetValue)throws BusinessException {
       
       JTable tableActive;
       int columnNumber=0;
       switch (IndexTabPanel.getEnum(indexTabPanelActive)) {
           case TAB_PANEL_GENERAL:
               tableActive = this.tableViewOrdersProviders;
               switch (columnToGetValue) {
                    case ORDER_ID:
                        columnNumber = TableViewOrdersProviders.Column.ORDER_NUM.getNumber();
                    break;
                    case RENTA_ID:
                        columnNumber = TableViewOrdersProviders.Column.RENTA_ID.getNumber();
                        break;
                    case FOLIO:
                        columnNumber = TableViewOrdersProviders.Column.FOLIO_RENTA.getNumber();
                        break;
                    default:
                        throw new AssertionError();
                }
               break;
           case TAB_PANEL_DETAIL:
               tableActive = this.tableViewOrdersProvidersDetail;
               switch (columnToGetValue) {
                    case ORDER_ID:
                        columnNumber = TableViewOrdersProvidersDetail.Column.ORDER_SUPPLIER_ID.getNumber();
                    break;
                    case RENTA_ID:
                        columnNumber = TableViewOrdersProvidersDetail.Column.RENTA_ID.getNumber();
                        break;
                    case FOLIO:
                        columnNumber = TableViewOrdersProvidersDetail.Column.FOLIO.getNumber();
                        break;
                    default:
                        throw new AssertionError();
                }
               break;
           default:
               throw new AssertionError();
       }
       
       if (tableActive.getSelectedRow() == - 1) {
           throw new BusinessException(ApplicationConstants.SELECT_A_ROW_NECCESSARY);
       }
       
       return tableActive.getValueAt(tableActive.getSelectedRow(), columnNumber).toString(); 
   }  

    
    public void showPaymentsProvidersForm() {
       String orderId;
       try {
            orderId = getValueIdBySelectedRow(ColumnToGetValue.ORDER_ID);
       } catch (BusinessException e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
           return;
       }
       
       try {
            IndexForm.showWindowDataUpdateSessionAndJobIdNotAccess(ApplicationConstants.PUESTO_CHOFER+"",true);
        } catch (InvalidDataException | DataOriginException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            return;
        }
               
        PaymentsProvidersForm form = new PaymentsProvidersForm(orderId);
        IndexForm.rootPanel.add(form);
        form.show();
        
    }
    
    private void showProviders() {
        ViewProviderForm win = new ViewProviderForm(null, true);
        win.setVisible(true);
        win.setLocationRelativeTo(null);

    }
    
    private void reportPDF(){
       
       try {
            String orderId = getValueIdBySelectedRow(ColumnToGetValue.ORDER_ID);
            DatosGenerales datosGenerales = systemService.getGeneralData();       
            JasperPrintUtility.generatePDFOrderProvider(orderId,datosGenerales, Utility.getPathLocation());
       } catch (Exception e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }       
       
     
     }
    
    private void fillTableTabPanelGeneral () {
       
     ParameterOrderProvider parameter = getParameters();
     List<OrdenProveedor> list;
     tableViewOrdersProviders.format();
     
     try{
        list = orderService.getOrdersByParameters(parameter);
     }catch(NoDataFoundException e){
         this.lblInfoGeneral.setText("No se han obtenido resultados :(");
         return;
     }catch(BusinessException e){
        LOGGER.error(e);
        JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        return;
     }finally {
        Toolkit.getDefaultToolkit().beep();
     }

     
        this.lblInfoGeneral.setText("Registros: "+list.size()+". Límite: "+
                this.cmbLimit.getSelectedItem().toString());

       DefaultTableModel tableModel = (DefaultTableModel) tableViewOrdersProviders.getModel();

       for(OrdenProveedor orden : list){      

            Object fila[] = {
                false,
                orden.getId(),
                orden.getRenta().getFolio(),
                orden.getUsuario().getNombre()+" "+orden.getUsuario().getApellidos(),
                orden.getProveedor().getNombre()+" "+orden.getProveedor().getApellidos(),
                orden.getStatusDescription(),
                orden.getCreado(),
                orden.getActualizado(),
                orden.getComentario(),
                orden.getRenta().getRentaId(),             
                decimalFormat.format(orden.getTotal()),
                orden.getAbonos() > 0 ? decimalFormat.format(orden.getAbonos()) : "",
                decimalFormat.format((orden.getTotal() - orden.getAbonos())),
                orden.getRenta().getFechaEvento()
              };
              tableModel.addRow(fila);

       }
     
   
   }
    
    private enum ColumnToGetValue {
        RENTA_ID,
        ORDER_ID,
        FOLIO;
    }
    
    private enum IndexTabPanel {
        
        TAB_PANEL_GENERAL(0),
        TAB_PANEL_DETAIL(1);
        
        private final int index;
        
        IndexTabPanel (int index) {
            this.index = index;
        }
        
        public int getIndex () {
            return this.index;
        }
        
        public static IndexTabPanel getEnum (int value) {
            for (IndexTabPanel indexTabPanel : IndexTabPanel.values()) {
                if ( (indexTabPanel.index+"").equals(value+"")) {
                    return indexTabPanel;
                }
            }
            return TAB_PANEL_GENERAL;
        }
    }
    
    private void exportToExcel () {
   
    switch (IndexTabPanel.getEnum(indexTabPanelActive)) {
           case TAB_PANEL_GENERAL:
               utilityService.exportarExcel(tableViewOrdersProviders);
               break;
           case TAB_PANEL_DETAIL:
               utilityService.exportarExcel(tableViewOrdersProvidersDetail);
               break;
           default:
               throw new AssertionError();
       }
   
   }
    
   private void addNewOrderProvider () {
        String folioID = JOptionPane.showInputDialog(this, "Folio de la renta para generar nueva orden.", "Nueva orden.", JOptionPane.INFORMATION_MESSAGE);
        if(folioID == null || folioID.isEmpty()){
             return;
        }
        System.out.println(folioID);

        try {
            Integer folioInt = Integer.parseInt(folioID);
            Renta renta = rentaService.getByFolio(folioInt);
            if (renta == null) {
                throw new BusinessException(String.format("Folio '%s' no encontrado en la base de datos.",folioInt));
            }
            OrderProviderForm form = new OrderProviderForm(renta.getFolio()+"", null, renta.getRentaId()+"");
            IndexForm.rootPanel.add(form);
            form.show();
        } catch (NumberFormatException numberFormatException) {
            JOptionPane.showMessageDialog(this, "Introduce un numero valido", ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException | DataOriginException dataOriginException) {
            JOptionPane.showMessageDialog(this, dataOriginException.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
        }
   }
    
   private void editOrderProvider () {       

       try {
            String rentaId = getValueIdBySelectedRow(ColumnToGetValue.RENTA_ID);
            String folio = getValueIdBySelectedRow(ColumnToGetValue.FOLIO);
            String orderId = getValueIdBySelectedRow(ColumnToGetValue.ORDER_ID);
            OrderProviderForm form = new OrderProviderForm(folio, orderId, rentaId);
            IndexForm.rootPanel.add(form);
            form.show();
       } catch (BusinessException businessException) {
           JOptionPane.showMessageDialog(this, businessException.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
       }
       
        
        
   }
   
   private void showBitacoraProveedores () {
        
        Frame frame = JOptionPane.getFrameForComponent(this);

        String rentaId;
        String folio;
       try {
            rentaId = getValueIdBySelectedRow(ColumnToGetValue.RENTA_ID);
            folio = getValueIdBySelectedRow(ColumnToGetValue.FOLIO);
       } catch (BusinessException e) {
           LOGGER.error(e);
           JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
           return;
       }

        ProviderStatusBitacoraDialog win =
        new ProviderStatusBitacoraDialog(frame,true,Long.parseLong(rentaId), folio);
        win.setLocationRelativeTo(this);
        win.setVisible(true);
   }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtSearchFolioRenta = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cmbLimit = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        txtSearchInitialDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndDate = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        jbtnSearch = new javax.swing.JButton();
        txtSearchByNameProvider = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtSearchOrderNumber = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        txtSearchInitialEventDate = new com.toedter.calendar.JDateChooser();
        txtSearchEndEventDate = new com.toedter.calendar.JDateChooser();
        jbtnAddOrder = new javax.swing.JButton();
        jbtnBitacoraProveedor = new javax.swing.JButton();
        btnCopyOrders = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblInfoGeneral = new javax.swing.JLabel();
        tabGeneral = new javax.swing.JTabbedPane();
        tabPanelGeneral = new javax.swing.JPanel();
        tabPanelDetail = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Busqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 12))); // NOI18N

        txtSearchFolioRenta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchFolioRenta.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchFolioRentaKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Nombre proveedor:");

        cmbStatus.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Por fecha de creación:");

        cmbLimit.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbLimit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Status orden:");

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

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Limitar resultados a:");

        jbtnSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/search-24.png"))); // NOI18N
        jbtnSearch.setToolTipText("Buscar");
        jbtnSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnSearchActionPerformed(evt);
            }
        });

        txtSearchByNameProvider.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchByNameProvider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchByNameProviderKeyPressed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Folio renta:");

        txtSearchOrderNumber.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearchOrderNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchOrderNumberKeyPressed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Número de orden:");

        jButton1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/beneficios-money-24.png"))); // NOI18N
        jButton1.setToolTipText("Pagos");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/excel-24.png"))); // NOI18N
        jButton2.setToolTipText("Exportar Excel");
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/searching-24.png"))); // NOI18N
        jButton3.setToolTipText("Detalle");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/truck-24.png"))); // NOI18N
        jButton4.setToolTipText("Proveedores");
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/pdf-24.png"))); // NOI18N
        jButton5.setToolTipText("Exportar PDF");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText("Por fecha del evento:");

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

        jbtnAddOrder.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnAddOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/agregar-24.png"))); // NOI18N
        jbtnAddOrder.setToolTipText("Agregar nueva orden");
        jbtnAddOrder.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnAddOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnAddOrderActionPerformed(evt);
            }
        });

        jbtnBitacoraProveedor.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jbtnBitacoraProveedor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/inventario-24.png"))); // NOI18N
        jbtnBitacoraProveedor.setToolTipText("Bitacora seguimiento proveedor");
        jbtnBitacoraProveedor.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnBitacoraProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnBitacoraProveedorActionPerformed(evt);
            }
        });

        btnCopyOrders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons/icon24/copy-24.png"))); // NOI18N
        btnCopyOrders.setToolTipText("Copiar ordenes a un nuevo folio");
        btnCopyOrders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCopyOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyOrdersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnAddOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnBitacoraProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCopyOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(cmbLimit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 7, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearchEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(6, 6, 6)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSearchEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSearchInitialDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSearchInitialEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSearchEndEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSearchByNameProvider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchFolioRenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchOrderNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jbtnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtnAddOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbtnBitacoraProveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCopyOrders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1, 1, 1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblInfoGeneral.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N

        tabGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabGeneralMouseClicked(evt);
            }
        });

        tabPanelGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabPanelGeneralMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout tabPanelGeneralLayout = new javax.swing.GroupLayout(tabPanelGeneral);
        tabPanelGeneral.setLayout(tabPanelGeneralLayout);
        tabPanelGeneralLayout.setHorizontalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 961, Short.MAX_VALUE)
        );
        tabPanelGeneralLayout.setVerticalGroup(
            tabPanelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );

        tabGeneral.addTab("General", tabPanelGeneral);

        javax.swing.GroupLayout tabPanelDetailLayout = new javax.swing.GroupLayout(tabPanelDetail);
        tabPanelDetail.setLayout(tabPanelDetailLayout);
        tabPanelDetailLayout.setHorizontalGroup(
            tabPanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 961, Short.MAX_VALUE)
        );
        tabPanelDetailLayout.setVerticalGroup(
            tabPanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 372, Short.MAX_VALUE)
        );

        tabGeneral.addTab("Detalle", tabPanelDetail);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabGeneral)
                    .addComponent(lblInfoGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblInfoGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabGeneral)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchFolioRentaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchFolioRentaKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            this.search();
        }
    }//GEN-LAST:event_txtSearchFolioRentaKeyPressed

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

    private void jbtnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnSearchActionPerformed
        // TODO add your handling code here:
        this.search();
    }//GEN-LAST:event_jbtnSearchActionPerformed

    private void txtSearchByNameProviderKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchByNameProviderKeyPressed
        if (evt.getKeyCode() == 10 ) {
            this.search();
        }
    }//GEN-LAST:event_txtSearchByNameProviderKeyPressed

    private void txtSearchOrderNumberKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchOrderNumberKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10 ) {
            this.search();
        }
    }//GEN-LAST:event_txtSearchOrderNumberKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showPaymentsProvidersForm();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        exportToExcel();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        editOrderProvider();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        showProviders();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        reportPDF();
    }//GEN-LAST:event_jButton5ActionPerformed

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

    private void tabPanelGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabPanelGeneralMouseClicked

    }//GEN-LAST:event_tabPanelGeneralMouseClicked

    private void tabGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabGeneralMouseClicked

    }//GEN-LAST:event_tabGeneralMouseClicked

    private void jbtnAddOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnAddOrderActionPerformed
        addNewOrderProvider();
    }//GEN-LAST:event_jbtnAddOrderActionPerformed

    private void jbtnBitacoraProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnBitacoraProveedorActionPerformed
        showBitacoraProveedores();
    }//GEN-LAST:event_jbtnBitacoraProveedorActionPerformed

    private void btnCopyOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyOrdersActionPerformed

        List<String> orders = new ArrayList<>();
        for (int i = 0; i < tableViewOrdersProviders.getRowCount(); i++) {
            if (Boolean.parseBoolean(tableViewOrdersProviders.
                getValueAt(i, TableViewOrdersProviders.Column.BOOLEAN.getNumber()).toString())) {
            orders.add(
                tableViewOrdersProviders.getValueAt(i, TableViewOrdersProviders.Column.ORDER_NUM.getNumber()).toString()
            );
        }
        }

        if (orders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una o mas ordenes al proveedor.");
            return;
        }

        OrderProviderCopyParameter orderProviderCopyParameter
        = new OrderProviderCopyParameter();

        orderProviderCopyParameter.setUsuarioId(IndexForm.globalUser.getUsuarioId());
        orderProviderCopyParameter.setOrders(orders);

        OrderProviderCopyFormDialog orderProviderCopyFormDialog =
        new OrderProviderCopyFormDialog(null, true, orderProviderCopyParameter);

        Boolean success = orderProviderCopyFormDialog.showDialog();

        if (success) {
            this.search();
        }
    }//GEN-LAST:event_btnCopyOrdersActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCopyOrders;
    private javax.swing.JComboBox cmbLimit;
    private javax.swing.JComboBox cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton jbtnAddOrder;
    private javax.swing.JButton jbtnBitacoraProveedor;
    private javax.swing.JButton jbtnSearch;
    private javax.swing.JLabel lblInfoGeneral;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JPanel tabPanelDetail;
    private javax.swing.JPanel tabPanelGeneral;
    private javax.swing.JTextField txtSearchByNameProvider;
    private com.toedter.calendar.JDateChooser txtSearchEndDate;
    private com.toedter.calendar.JDateChooser txtSearchEndEventDate;
    private javax.swing.JTextField txtSearchFolioRenta;
    private com.toedter.calendar.JDateChooser txtSearchInitialDate;
    private com.toedter.calendar.JDateChooser txtSearchInitialEventDate;
    private javax.swing.JTextField txtSearchOrderNumber;
    // End of variables declaration//GEN-END:variables
}
