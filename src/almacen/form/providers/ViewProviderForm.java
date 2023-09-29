package almacen.form.providers;

import common.constants.ApplicationConstants;
import common.utilities.UtilityCommon;
import common.exceptions.BusinessException;
import common.exceptions.InvalidDataException;
import java.awt.Toolkit;
import java.util.List;
import javax.mail.MessagingException;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import common.model.providers.Proveedor;
import common.services.providers.ProvidersService;

public class ViewProviderForm extends javax.swing.JDialog {

    private final ProvidersService providersService = ProvidersService.getInstance();
    private Long g_idProvider = null;
    
    public ViewProviderForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setTitle("Proveedores");
        fillTable();
        
       this.resetInputs();
    }
    
    public void resetInputs(){
        this.txtName.setText("");
        this.txtLastName.setText("");
        this.txtPhones.setText("");
        this.txtAdress.setText("");
        this.txtEmail.setText("");
        this.btnUpdate.setEnabled(false);
        
    }
    
    public void fillTableSearch(String data){
        List<Proveedor> proveedores;
        try{
            proveedores
                = providersService.searchByData(data);
        }catch(BusinessException e){
                JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
          }
        this.formatTable();
        if(proveedores == null || proveedores.size()<=0){
            return;
        }
       
        
        DefaultTableModel tabla = (DefaultTableModel) tableProviders.getModel();
        for(Proveedor proveedor : proveedores){
            String fila[] = {
                proveedor.getId()+"",
                proveedor.getNombre(),
                proveedor.getApellidos(),
                proveedor.getDireccion(),
                proveedor.getTelefonos(),
                proveedor.getEmail(),
                proveedor.getCreado()+"",
                proveedor.getActualizado()+""
            };         
            tabla.addRow(fila);
        } // end for
    
    
    }
    
        
    public void fillTable(){
        this.formatTable();
        List<Proveedor> proveedores;
                try{
                    proveedores= providersService.getAll();
                }catch(BusinessException e){
                    JOptionPane.showMessageDialog(this, e.getMessage()+"\n"+e.getCause(), "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
        if(proveedores == null || proveedores.size()<=0){
            return;
        }
                
        DefaultTableModel tabla = (DefaultTableModel) tableProviders.getModel();
        for(Proveedor proveedor : proveedores){
            String fila[] = {
                proveedor.getId()+"",
                proveedor.getNombre(),
                proveedor.getApellidos(),
                proveedor.getDireccion(),
                proveedor.getTelefonos(),
                proveedor.getEmail(),
                proveedor.getCreado()+"",
                proveedor.getActualizado()+""
            };         
            tabla.addRow(fila);
        } // end for
    
    }
    
     public void formatTable() {
        Object[][] data = {{"","","","","","","",""}};
        String[] columnNames = {
                        "id",
                        "Nombre", 
                        "Apellidos",
                        "Dirección", 
                        "Teléfonos",
                        "Email",
                        "Creado",
                        "Actualizado"
                        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        tableProviders.setModel(tableModel);
        
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
        tableProviders.setRowSorter(ordenarTabla);

        int[] anchos = {20,120,140,140,140,100,120,120};

        for (int inn = 0; inn < tableProviders.getColumnCount(); inn++) {
            tableProviders.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
        }

        try {
            DefaultTableModel temp = (DefaultTableModel) tableProviders.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);

        tableProviders.getColumnModel().getColumn(0).setMaxWidth(0);
        tableProviders.getColumnModel().getColumn(0).setMinWidth(0);
        tableProviders.getColumnModel().getColumn(0).setPreferredWidth(0);
      
        
    }
     
     public void deleteProvider(){
         
        if (this.tableProviders.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = this.tableProviders.getValueAt(tableProviders.getSelectedRow(), 0).toString();
        
         if(JOptionPane.showOptionDialog(this, "Se eliminará de la bd,  \u00BFContinuar? " ,"Confirme eliminacion", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Si", "No"}, "Si") != 0){
            return;
         }
        
        try{
            providersService.deleteById(Long.parseLong(id));
            this.fillTable();
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getCause()+"\n"+e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
     
     }
     
     public void editProvider(){
     
        if (this.tableProviders.getSelectedRow() == - 1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para continuar ", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String id = this.tableProviders.getValueAt(tableProviders.getSelectedRow(), 0).toString();
        Proveedor proveedor;
        try{
            proveedor = providersService.getById(Long.parseLong(id));
            this.btnUpdate.setEnabled(true);
            g_idProvider = proveedor.getId();
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getCause()+"\n"+e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        this.txtName.setText(proveedor.getNombre());
        this.txtLastName.setText(proveedor.getApellidos());
        this.txtAdress.setText(proveedor.getDireccion());
        this.txtPhones.setText(proveedor.getTelefonos());
        this.txtEmail.setText(proveedor.getEmail());
        
     
     }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableProviders = new javax.swing.JTable(){public boolean isCellEditable(int rowIndex,int colIndex){return false;}};
        txtSearchByName = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtAdress = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtPhones = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        tableProviders.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        tableProviders.setModel(new javax.swing.table.DefaultTableModel(
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
        tableProviders.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tableProviders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableProvidersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tableProviders);

        txtSearchByName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtSearchByName.setToolTipText("Buscar proveedor");
        txtSearchByName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchByNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchByNameKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtSearchByName, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(txtSearchByName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos proveedor", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 11))); // NOI18N

        txtName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtName.setToolTipText("Buscar proveedor");
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNameKeyReleased(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel1.setText("Nombre:");

        txtLastName.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtLastName.setToolTipText("Buscar proveedor");
        txtLastName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtLastNameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLastNameKeyReleased(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel2.setText("Apellidos:");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel3.setText("Dirección");

        txtAdress.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtAdress.setToolTipText("Buscar proveedor");
        txtAdress.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAdressKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAdressKeyReleased(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel4.setText("Teléfonos");

        txtPhones.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtPhones.setToolTipText("Buscar proveedor");
        txtPhones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPhonesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPhonesKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jLabel5.setText("Email");

        txtEmail.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        txtEmail.setToolTipText("Buscar proveedor");
        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEmailKeyReleased(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnAdd.setText("Agregar");
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnEdit.setText("Editar");
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnDelete.setText("Eliminar");
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        btnUpdate.setText("Actualizar");
        btnUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPhones, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtLastName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtAdress)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAdd)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(4, 4, 4)
                        .addComponent(txtAdress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(4, 4, 4)
                        .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(txtPhones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete)
                    .addComponent(btnUpdate)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableProvidersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableProvidersMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

         this.editProvider();

        }
    }//GEN-LAST:event_tableProvidersMouseClicked

    private void txtNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyPressed
        // TODO add your handling code here:
       
    }//GEN-LAST:event_txtNameKeyPressed

    private void txtNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyReleased
        // TODO add your handling code here:
        
      
        
    }//GEN-LAST:event_txtNameKeyReleased

    private void txtLastNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLastNameKeyPressed

    private void txtLastNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLastNameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLastNameKeyReleased

    private void txtAdressKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAdressKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAdressKeyPressed

    private void txtAdressKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAdressKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAdressKeyReleased

    private void txtPhonesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhonesKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhonesKeyPressed

    private void txtPhonesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPhonesKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhonesKeyReleased

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed
        // TODO add your handling code here:
//        if (evt.getKeyCode() == 10){
//            this.addProvider();
//        }
    }//GEN-LAST:event_txtEmailKeyPressed

    private void txtEmailKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailKeyReleased

    public void addProvider(){
        try{
            validForm();
        }catch(InvalidDataException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Proveedor proveedor = this.getDataFromInputs();
        
        if(proveedor == null){
            return;
        }
        try{
            providersService.save(proveedor);
            resetInputs();
            this.fillTable();
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getCause()+"\n"+e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        addProvider();
          
    }//GEN-LAST:event_btnAddActionPerformed

    public void validForm()throws InvalidDataException{
        
        StringBuilder message = new StringBuilder();
        
        if(this.txtName.getText().equals("")){
            message.append("Nombre es requerido\n");
        }
        if(this.txtPhones.getText().equals("")){
            message.append("Teléfono es requerido\n");
        }
        if(!this.txtEmail.getText().equals("")){
            try{
                UtilityCommon.isEmail(this.txtEmail.getText());
            }catch(MessagingException e){
                message.append("Email no válido\n");
            }
        }
        
        if(!message.toString().equals("")){
            throw new InvalidDataException(message.toString());
        }
    
    }
    public Proveedor getDataFromInputs(){
       
        
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(this.txtName.getText());
        proveedor.setApellidos(this.txtLastName.getText());
        proveedor.setDireccion(this.txtAdress.getText());
        proveedor.setTelefonos(this.txtPhones.getText());
        proveedor.setEmail(this.txtEmail.getText());
        proveedor.setFgActivo("1");
        
        return proveedor;
    
    }
    
    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        
        this.editProvider();
        
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.updateProvider();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void txtSearchByNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchByNameKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchByNameKeyPressed

    private void txtSearchByNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchByNameKeyReleased
        // TODO add your handling code here:
        String data = this.txtSearchByName.getText();
        System.out.println("Data keypressed: "+data);
       
            this.fillTableSearch(data);
    }//GEN-LAST:event_txtSearchByNameKeyReleased

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteProvider();
    }//GEN-LAST:event_btnDeleteActionPerformed

    public void updateProvider(){
        try{
            validForm();
        }catch(InvalidDataException e){
            JOptionPane.showMessageDialog(this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Proveedor proveedor = this.getDataFromInputs();
        
        if(proveedor == null){
            return;
        }
        proveedor.setId(g_idProvider);
        try{
            providersService.update(proveedor);
            Toolkit.getDefaultToolkit().beep();
            this.resetInputs();
            this.fillTable();
            JOptionPane.showMessageDialog(this, ApplicationConstants.MESSAGE_UPDATE_SUCCESSFUL,"EXITO" , JOptionPane.INFORMATION_MESSAGE);
        }catch(BusinessException e){
            JOptionPane.showMessageDialog(this, e.getCause()+"\n"+e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ViewProviderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ViewProviderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ViewProviderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ViewProviderForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ViewProviderForm dialog = new ViewProviderForm(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableProviders;
    private javax.swing.JTextField txtAdress;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPhones;
    private javax.swing.JTextField txtSearchByName;
    // End of variables declaration//GEN-END:variables
}
