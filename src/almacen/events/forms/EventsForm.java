package almacen.events.forms;

import almacen.events.services.EventsService;
import almacen.events.vos.NumberOfWeek;
import almacen.index.forms.IndexForm;
import common.constants.ApplicationConstants;
import common.model.Renta;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class EventsForm extends javax.swing.JInternalFrame {

    private final EventsService eventsService = EventsService.getInstance();
   
    public EventsForm() {
        initComponents();
        init();
    }
    
    private void init () {
        
        this.cmbNumberOfWeeks.removeAllItems();
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (1,"1 Semana"));
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (2,"2 Semanas"));
        
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (-1,"1 Semana atras"));
        this.cmbNumberOfWeeks.addItem(new NumberOfWeek (-2,"2 Semanas atras"));
        
        this.setClosable(true);
        this.setTitle("EVENTOS");
        this.fillTable();
    }
    
    private void fillTable () {
        formatTable();
        List<Renta> rentas;
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
            rentas = eventsService.getEventsByNumbersOfWeeks(numberOfWeek.getNumber(), userByCategoryId);
            lblInfo.setText("Total de eventos: "+rentas.size());
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        for (Renta renta : rentas) {
            
            Object row[] = {
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
    
    private void formatTable () {
       
        Object[][] data = {{"","","","","","","","","","","","",""}};
        String[] columNames = {
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
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        table.setModel(tableModel);
       TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
       table.setRowSorter(ordenarTabla);
       
       int[] weights = {
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
       
       table.getColumnModel().getColumn(0).setMaxWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(0).setMinWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(0).setPreferredWidth(Column.ID.getNumber());
      
    }
    
    private enum Column {
        ID(0,"id",20),
        FOLIO(1,"Folio",20),
        ADDRESS(1,"Dirección",100),
        CLIENT(2,"Cliente",100),
        STATUS(3,"Estado",40),
        CREATED_AT(4,"Elaborado",40),
        EVENT_DATE(5,"Evento",40),
        DELIVERY_DATE(6,"Entrega",40),
        TYPE(7,"Tipo",40),
        USER(8,"Atendió",100),
        CHOFER(9,"Chofer",100);
        
        private final Integer number;
        private final String description;
        private final Integer weigth;
        
        Column (Integer number, String description, Integer weight) {
            this.number = number;
            this.description = description;
            this.weigth = weight;
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
        cmbNumberOfWeeks = new javax.swing.JComboBox<>();
        lblInfo = new javax.swing.JLabel();
        txtSearchFolio = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtEndDate = new com.toedter.calendar.JDateChooser();
        txtInitDate = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnSearchByDates = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

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

        cmbNumberOfWeeks.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        cmbNumberOfWeeks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbNumberOfWeeksActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText("Buscar por número de semanas");

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText("Buscar por folio");

        txtEndDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        txtInitDate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText("Fecha inicial");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText("Fecha final");

        btnSearchByDates.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        btnSearchByDates.setText("Enviar");
        btnSearchByDates.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 44, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtInitDate, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearchByDates)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 222, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSearchFolio)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                            .addComponent(cmbNumberOfWeeks, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 3, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtSearchFolio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbNumberOfWeeks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearchByDates))
                    .addComponent(txtEndDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtInitDate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed

    }//GEN-LAST:event_tableKeyPressed

    private void cmbNumberOfWeeksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbNumberOfWeeksActionPerformed
        fillTable();
    }//GEN-LAST:event_cmbNumberOfWeeksActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSearchByDates;
    private javax.swing.JComboBox<NumberOfWeek> cmbNumberOfWeeks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    private com.toedter.calendar.JDateChooser txtEndDate;
    private com.toedter.calendar.JDateChooser txtInitDate;
    private javax.swing.JTextField txtSearchFolio;
    // End of variables declaration//GEN-END:variables
}
