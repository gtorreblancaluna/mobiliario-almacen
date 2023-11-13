package almacen.form.rentas;

import almacen.service.rentas.RentaService;
import almacen.model.rentas.NumberOfWeek;
import almacen.form.index.IndexForm;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.Renta;
import common.utilities.CheckBoxHeader;
import common.utilities.ItemListenerHeaderCheckbox;
import common.utilities.UtilityCommon;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class RentasForm extends javax.swing.JInternalFrame {

    private final RentaService rentaService = RentaService.getInstance();
    private static final Logger LOGGER = Logger.getLogger(RentasForm.class.getName());
   
    public RentasForm() {
        initComponents();
        init();
    }
    
    private void init () {
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            btnUpdateStatusRentaToEnRenta.setVisible(false);
        }
        this.cmbNumberOfWeeks.removeAllItems();
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (1,"Esta semana"));
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (2,"2 Semanas adelante"));
        
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (-1,"1 Semana atras"));
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (-2,"2 Semanas atras"));
        
        this.setClosable(true);
        this.setTitle("EVENTOS");
        this.getByNumberOfWeeks();
    }
    
    private void fillTable (List<Renta> rentas) {
        
        formatTable();
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        for (Renta renta : rentas) {
            
            Object row[] = {
                    false,
                    renta.getRentaId(),
                    renta.getFolio(),
                    renta.getDescripcion(),
                    renta.getCliente().getNombre() + " " + renta.getCliente().getApellidos(),
                    renta.getEstado().getDescripcion(),
                    renta.getFechaPedido(),
                    renta.getFechaEvento(),
                    renta.getFechaEntrega(),
                    renta.getTipo().getTipo(),
                    renta.getUsuario().getNombre() + " " + renta.getUsuario().getApellidos(),
                    renta.getChofer().getNombre() +" "+ renta.getChofer().getApellidos()
                };
                tableModel.addRow(row);
        }
        
        
    }
    
    private void getBetweenDates () {
        
        Map<String,Object> parameters = new HashMap<>();
        Integer userByCategoryId = null;
        final String FORMAT_DATE = "dd/MM/yyyy";
        
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            userByCategoryId = IndexForm.globalUser.getUsuarioId();
        }
        
        String initDate = txtInitDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtInitDate.getDate()) : null;
        String endDate = txtEndDate.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtEndDate.getDate()) : null;
        
        if (initDate == null || endDate == null) {
            JOptionPane.showMessageDialog(this, "Ingresa fecha inicial y final", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        parameters.put("userByCategoryId", userByCategoryId);
        parameters.put("initDate", initDate);
        parameters.put("endDate", endDate);
        parameters.put("statusId", Arrays.asList( 
                        ApplicationConstants.ESTADO_APARTADO,
                        ApplicationConstants.ESTADO_EN_RENTA
                    ));
        
        try {
            List<Renta> rentas = rentaService.getByParameters(parameters);
            lblInfo.setText("Total de eventos: "+rentas.size());
            fillTable(rentas);
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
        
    }
    
    private void search () {
        
        if (!txtSearchFolio.getText().isEmpty()) {
            getByFolio();
        } else {
            getBetweenDates();
        }
    
    }
    
    private void getByFolio () {
        
        if (txtSearchFolio.getText().isEmpty()) {
            return;
        }
        
        List<String> folios = Arrays.asList(txtSearchFolio.getText().trim().split(","));
        
        if (folios.isEmpty()) {
            return;
        }
        
        Map<String,Object> parameters = new HashMap<>();
        Integer userByCategoryId = null;
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            userByCategoryId = IndexForm.globalUser.getUsuarioId();
        }
        
        parameters.put("userByCategoryId", userByCategoryId);
        parameters.put("folio", folios);
        
        try {
            List<Renta> rentas = rentaService.getByParameters(parameters);
            lblInfo.setText("Total de eventos: "+rentas.size());
            fillTable(rentas);
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
    }    
    
 
    
    private void updateChofer () {
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_ACTION_DENIED, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try{
            UtilityCommon.validateSelectCheckboxInTable(table, Column.BOOLEAN.getNumber());
            List<String> idsSelected = UtilityCommon.getIdsSelected(table, Column.BOOLEAN.getNumber(), Column.ID.getNumber());
            List<String> folios = UtilityCommon.getIdsSelected(table, Column.BOOLEAN.getNumber(), Column.FOLIO.getNumber());
            
            if (idsSelected.isEmpty()) {
                throw new BusinessException("Selecciona al menos un folio.");
            }
            
            if (idsSelected.size() > 20) {
                throw new BusinessException("Límite de folios seleccionado excedido. [20]");
            }

            UpdateChoferDialogForm win = new UpdateChoferDialogForm(null,true,idsSelected,folios);
            win.setLocation(this.getWidth() / 2 - win.getWidth() / 2, this.getHeight() / 2 - win.getHeight() / 2 - 20);
            win.setVisible(true);
            
        } catch (BusinessException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.ERROR_MESSAGE);   
        }
    }    

    
    private void updateStatusFromApartadoToEnRenta () {
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_ACTION_DENIED, ApplicationConstants.MESSAGE_TITLE_ERROR, JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try{
            UtilityCommon.validateSelectCheckboxInTable(table, Column.BOOLEAN.getNumber());
            List<String> ids = UtilityCommon.getIdsSelected(table, Column.BOOLEAN.getNumber(), Column.ID.getNumber());
            int seleccion = JOptionPane.showOptionDialog(this, "ATENCIÓN. ESTA ACCIÓN NO SE PUEDE DESHACER.\n"
                    + "SE ACTUALIZARÁN A STATUS: "+ApplicationConstants.DS_ESTADO_EN_RENTA+".\n"
                    + "Folios seleccionados: "+ids.size()+". Marcar como: "+ApplicationConstants.DS_ESTADO_EN_RENTA+", ¿Deseas continuar?", "Mensaje", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si");
            if (seleccion != 0) {//presiono que no
                return;
            }
            Map<String,Object> parameters = new HashMap<>();
            parameters.put("ids", ids);
            rentaService.updateStatusFromApartadoToEnRenta(ids, IndexForm.globalUser);
            this.cmbNumberOfWeeks.setSelectedIndex(0);
            getByNumberOfWeeks();
        } catch (DataOriginException | BusinessException e) {
            LOGGER.error(e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);   
        }
    }
    
    private void getByNumberOfWeeks () {
        
        Integer userByCategoryId = null;
        Integer codeJobChofer = Integer.parseInt(ApplicationConstants.PUESTO_CHOFER+"");
        
        final NumberOfWeek numberOfWeek = (NumberOfWeek) this.cmbNumberOfWeeks.getModel().getSelectedItem();
        
        if (codeJobChofer.equals(IndexForm.globalUser.getPuesto().getPuestoId())) {
            JOptionPane.showMessageDialog(this, "Permisos insuficientes");
            this.dispose();
        }
        
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            userByCategoryId = IndexForm.globalUser.getUsuarioId();
        }
        
        try {
            List<Renta> rentas = rentaService.getEventsByNumbersOfWeeks(numberOfWeek.getNumber(), userByCategoryId);
            lblInfo.setText("Total de eventos: "+rentas.size());
            fillTable(rentas);
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }
    
    private void formatTable () {
       
        // customize column types
        DefaultTableModel tableModel = new DefaultTableModel(Column.getColumnNames(), 0){
                @Override
                public Class getColumnClass(int column) {
                    return Column.values()[column].getClazz();
                }

                @Override
                public boolean isCellEditable (int row, int column) {
                    return Column.values()[column].getIsEditable();
                }
        };
        
       table.setModel(tableModel);
       TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
       table.setRowSorter(ordenarTabla);       

       for (Column column : Column.values()) {
            table.getColumnModel()
                    .getColumn(column.getNumber())
                    .setPreferredWidth(column.getSize());
        }
       
       DefaultTableCellRenderer center = new DefaultTableCellRenderer();
       center.setHorizontalAlignment(SwingConstants.CENTER);
       
       table.getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(Column.ID.getNumber());
       
       // adding checkbox in header table
       TableColumn tc = table.getColumnModel().getColumn(Column.BOOLEAN.getNumber());
       tc.setCellEditor(table.getDefaultEditor(Boolean.class)); 
       tc.setHeaderRenderer(new CheckBoxHeader(new ItemListenerHeaderCheckbox(Column.BOOLEAN.getNumber(),table)));
      
    }
    
    private enum Column {
        
        BOOLEAN(0,"",20,Boolean.class, true),
        ID(1,"id",20,String.class, true),
        FOLIO(2,"Folio",20,String.class, false),
        ADDRESS(3,"Dirección",100,String.class, false),
        CLIENT(4,"Cliente",100,String.class, false),
        STATUS(5,"Estado",40,String.class, false),
        CREATED_AT(6,"Elaborado",40,String.class, false),
        EVENT_DATE(7,"Evento",40,String.class, false),
        DELIVERY_DATE(8,"Entrega",40,String.class, false),
        TYPE(9,"Tipo",40,String.class, false),
        USER(10,"Atendió",100,String.class, false),
        CHOFER(11,"Chofer",100,String.class, false);
        
        private final Integer number;
        private final String description;
        private final Integer size;
        private final Class clazz;
        private final Boolean isEditable;
        
        Column (Integer number, String description, Integer size, Class clazz, Boolean isEditable) {
            this.number = number;
            this.description = description;
            this.size = size;
            this.clazz = clazz;
            this.isEditable = isEditable;
        }
        
        public Boolean getIsEditable() {
            return isEditable;
        }
        
        public Class getClazz () {
            return clazz;
        }
        
        public Integer getNumber () {
            return this.number;
        }
        
        public String getDescription () {
            return this.description;
        }
        
        public Integer getSize () {
            return this.size;
        }
        
        public static String[] getColumnNames () {
            List<String> columnNames = new ArrayList<>();
            for (Column column : Column.values()) {
                columnNames.add(column.getDescription());
            }
            return columnNames.toArray(new String[0]);
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

        jPanel2 = new javax.swing.JPanel();
        cmbNumberOfWeeks = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        txtSearchFolio = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtInitDate = new com.toedter.calendar.JDateChooser();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        btnSearchByDates = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btnUpdateStatusRentaToEnRenta = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnUpdateStatusRentaToEnRenta1 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();

        cmbNumberOfWeeks.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbNumberOfWeeks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbNumberOfWeeksActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Número de semanas (fecha de entrega)");

        txtSearchFolio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchFolioKeyPressed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Buscar por folio");

        txtInitDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        txtEndDate.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        btnSearchByDates.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByDates.setText("Buscar");
        btnSearchByDates.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSearchByDates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchByDatesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Fecha inicial");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Fecha final");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons16/info-16.png"))); // NOI18N
        jLabel7.setToolTipText("Puedes ingresar varios folios, separados por coma. Ejemplo: (12,13,14)");
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jLabel7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jLabel7KeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addGap(0, 121, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearchFolio)))
                .addGap(12, 12, 12)
                .addComponent(btnSearchByDates)
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbNumberOfWeeks, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbNumberOfWeeks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchFolio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSearchByDates)))
                    .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnUpdateStatusRentaToEnRenta.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnUpdateStatusRentaToEnRenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/user-attend-24.png"))); // NOI18N
        btnUpdateStatusRentaToEnRenta.setMnemonic('a');
        btnUpdateStatusRentaToEnRenta.setToolTipText("[Alt+A] Marcar como atendido");
        btnUpdateStatusRentaToEnRenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateStatusRentaToEnRenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStatusRentaToEnRentaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText("Actualizar estado del folio");
        jLabel5.setToolTipText("[Alt+A] Marcar como atendido");
        jLabel5.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnUpdateStatusRentaToEnRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnUpdateStatusRentaToEnRenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnUpdateStatusRentaToEnRenta1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/truck-24.png"))); // NOI18N
        btnUpdateStatusRentaToEnRenta1.setMnemonic('b');
        btnUpdateStatusRentaToEnRenta1.setToolTipText("[Alt+X] Actualizar chofer.");
        btnUpdateStatusRentaToEnRenta1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateStatusRentaToEnRenta1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStatusRentaToEnRenta1ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText("Actualizar chofer");
        jLabel6.setToolTipText("[Alt+X] Actualizar chofer.");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(btnUpdateStatusRentaToEnRenta1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(btnUpdateStatusRentaToEnRenta1)
                        .addGap(9, 9, 9))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(368, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed

    }//GEN-LAST:event_tableKeyPressed

    private void cmbNumberOfWeeksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbNumberOfWeeksActionPerformed
        getByNumberOfWeeks();
    }//GEN-LAST:event_cmbNumberOfWeeksActionPerformed

    private void txtSearchFolioKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchFolioKeyPressed
        if (evt.getKeyCode() == 10){
            search();
        }
    }//GEN-LAST:event_txtSearchFolioKeyPressed

    private void btnSearchByDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchByDatesActionPerformed
        search();
    }//GEN-LAST:event_btnSearchByDatesActionPerformed

    private void btnUpdateStatusRentaToEnRentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusRentaToEnRentaActionPerformed
        updateStatusFromApartadoToEnRenta();
    }//GEN-LAST:event_btnUpdateStatusRentaToEnRentaActionPerformed

    private void btnUpdateStatusRentaToEnRenta1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusRentaToEnRenta1ActionPerformed
        updateChofer();
    }//GEN-LAST:event_btnUpdateStatusRentaToEnRenta1ActionPerformed

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        updateChofer();
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        updateStatusFromApartadoToEnRenta();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jLabel7KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel7KeyPressed

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        JOptionPane.showMessageDialog(this, "Puedes ingresar varios folios, separados por coma. Ejemplo: (12,13,14)", "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jLabel7MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearchByDates;
    private javax.swing.JButton btnUpdateStatusRentaToEnRenta;
    private javax.swing.JButton btnUpdateStatusRentaToEnRenta1;
    private javax.swing.JComboBox<NumberOfWeek> cmbNumberOfWeeks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtInitDate;
    private javax.swing.JTextField txtSearchFolio;
    // End of variables declaration//GEN-END:variables
}
