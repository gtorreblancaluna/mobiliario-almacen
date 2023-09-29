package almacen.form.rentas;

import almacen.commons.utilities.Utility;
import almacen.service.rentas.RentaService;
import almacen.model.rentas.NumberOfWeek;
import almacen.form.index.IndexForm;
import common.constants.ApplicationConstants;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import common.model.Renta;
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
        }
        
    }
    
    private void getByFolio () {
        
        Integer folio;
        try {
            folio = Integer.parseInt(txtSearchFolio.getText());
            if (folio <= 0 || folio.toString().length() > 1000) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingresa un valor valido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Map<String,Object> parameters = new HashMap<>();
        Integer userByCategoryId = null;
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            userByCategoryId = IndexForm.globalUser.getUsuarioId();
        }
        
        parameters.put("userByCategoryId", userByCategoryId);
        parameters.put("folio", folio);
        
        try {
            List<Renta> rentas = rentaService.getByParameters(parameters);
            lblInfo.setText("Total de eventos: "+rentas.size());
            fillTable(rentas);
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private List<String> getIdsSelected () {
       List<String> ids = new ArrayList<>();
        
        for (int i = 0; i < table.getRowCount(); i++) {
            if (Boolean.parseBoolean(table.getValueAt(i, Column.BOOLEAN.getNumber()).toString())) {
                ids.add(
                        table.getValueAt(i, Column.ID.getNumber()).toString()
                );
            }
        }
        return ids;
   }
    
    private void updateStatusFromApartadoToEnRenta () {
        if (!IndexForm.globalUser.getAdministrador().equals("1")) {
            JOptionPane.showMessageDialog(this, "Acción denegada, solo un usuario con perfil administrador, puede realizar este proceso", "Error", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try{
            Utility.validateSelectCheckboxInTable(table, Column.BOOLEAN.getNumber());
            List<String> ids = getIdsSelected();
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
       
        String[] columnNames = {
            Column.BOOLEAN.getDescription(),
            Column.ID.getDescription(),
            Column.FOLIO.getDescription(),
            Column.ADDRESS.getDescription(),
            Column.CLIENT.getDescription(),
            Column.STATUS.getDescription(),
            Column.CREATED_AT.getDescription(),
            Column.EVENT_DATE.getDescription(),
            Column.DELIVERY_DATE.getDescription(),
            Column.TYPE.getDescription(),
            Column.USER.getDescription(),
            Column.CHOFER.getDescription()
        };
        
        
        Class[] types = {
            Column.BOOLEAN.getClazz(),
            Column.ID.getClazz(),
            Column.FOLIO.getClazz(),
            Column.ADDRESS.getClazz(),
            Column.CLIENT.getClazz(),
            Column.STATUS.getClazz(),
            Column.CREATED_AT.getClazz(),
            Column.EVENT_DATE.getClazz(),
            Column.DELIVERY_DATE.getClazz(),
            Column.TYPE.getClazz(),
            Column.USER.getClazz(),
            Column.CHOFER.getClazz()
        };
        
        boolean[] editable = {
            Column.BOOLEAN.getIsEditable(),
            Column.ID.getIsEditable(),
            Column.FOLIO.getIsEditable(),
            Column.ADDRESS.getIsEditable(),
            Column.CLIENT.getIsEditable(),
            Column.STATUS.getIsEditable(),
            Column.CREATED_AT.getIsEditable(),
            Column.EVENT_DATE.getIsEditable(),
            Column.DELIVERY_DATE.getIsEditable(),
            Column.TYPE.getIsEditable(),
            Column.USER.getIsEditable(),
            Column.CHOFER.getIsEditable()
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
       
       int[] weights = {
            Column.BOOLEAN.getWeigt(),
            Column.ID.getWeigt(),
            Column.FOLIO.getWeigt(),
            Column.ADDRESS.getWeigt(),
            Column.CLIENT.getWeigt(),
            Column.STATUS.getWeigt(),
            Column.CREATED_AT.getWeigt(),
            Column.EVENT_DATE.getWeigt(),
            Column.DELIVERY_DATE.getWeigt(),
            Column.TYPE.getWeigt(),
            Column.USER.getWeigt(),
            Column.CHOFER.getWeigt()
       };

       for (int inn = 0; inn < table.getColumnCount(); inn++) {
           table.getColumnModel().getColumn(inn).setPreferredWidth(weights[inn]);
       }
       
       DefaultTableCellRenderer center = new DefaultTableCellRenderer();
       center.setHorizontalAlignment(SwingConstants.CENTER);
       
       table.getColumnModel().getColumn(Column.ID.getNumber()).setMaxWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(Column.ID.getNumber()).setMinWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(Column.ID.getNumber()).setPreferredWidth(Column.ID.getNumber());
      
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
        private final Integer weigth;
        private final Class clazz;
        private final Boolean isEditable;
        
        Column (Integer number, String description, Integer weight, Class clazz, Boolean isEditable) {
            this.number = number;
            this.description = description;
            this.weigth = weight;
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
        
        public Integer getWeigt () {
            return this.weigth;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        lblInfo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnUpdateStatusRentaToEnRenta = new javax.swing.JButton();
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

        setPreferredSize(new java.awt.Dimension(1021, 524));

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

        btnUpdateStatusRentaToEnRenta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/almacen/icons24/user-attend-24.png"))); // NOI18N
        btnUpdateStatusRentaToEnRenta.setMnemonic('a');
        btnUpdateStatusRentaToEnRenta.setToolTipText("[Alt+A] Marcar como atendido");
        btnUpdateStatusRentaToEnRenta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdateStatusRentaToEnRenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateStatusRentaToEnRentaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnUpdateStatusRentaToEnRenta, javax.swing.GroupLayout.PREFERRED_SIZE, 33, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUpdateStatusRentaToEnRenta)
                .addContainerGap(451, Short.MAX_VALUE))
        );

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

        txtInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txtEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        btnSearchByDates.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByDates.setText("Enviar");
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchByDates)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchFolio, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbNumberOfWeeks, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbNumberOfWeeks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSearchFolio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchByDates))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            getByFolio();
        }
    }//GEN-LAST:event_txtSearchFolioKeyPressed

    private void btnSearchByDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchByDatesActionPerformed
        getBetweenDates();
    }//GEN-LAST:event_btnSearchByDatesActionPerformed

    private void btnUpdateStatusRentaToEnRentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateStatusRentaToEnRentaActionPerformed
        updateStatusFromApartadoToEnRenta();
    }//GEN-LAST:event_btnUpdateStatusRentaToEnRentaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearchByDates;
    private javax.swing.JButton btnUpdateStatusRentaToEnRenta;
    private javax.swing.JComboBox<NumberOfWeek> cmbNumberOfWeeks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtInitDate;
    private javax.swing.JTextField txtSearchFolio;
    // End of variables declaration//GEN-END:variables
}
