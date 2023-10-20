package almacen.form.item;

import common.form.items.AgregarArticuloDisponibilidadDialog;
import common.form.items.VerDisponibilidadArticulos;
import common.model.Articulo;
import common.services.ItemService;
import common.tables.TableDisponibilidadArticulosShow;
import common.utilities.UtilityCommon;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ItemsForm extends javax.swing.JInternalFrame {
    
    final ItemService itemService = ItemService.getInstance();
    List<Articulo> items;
    private static final DecimalFormat decimalFormat = new DecimalFormat( "#,###,###,##0" );
    private TableDisponibilidadArticulosShow tablaDisponibilidadArticulos;

    public ItemsForm() {
        initComponents();
        init();
    }
    
    private void init () {
        lblInfo.setText("Obteniendo articulos de la base de datos...");
        txtSearch.setEnabled(false);
        this.setTitle("INVENTARIO");
        this.setClosable(true);
        getItemsAndFillTable();
        
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
    }
    
    private void setLblInfoStatusChange () {
        
        final String FORMAT_DATE = "dd/MM/yy"; 
        int rowCount = tablaDisponibilidadArticulos.getRowCount();
        String initDate = txtDisponibilidadFechaInicial.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaInicial.getDate()) : null;
        String endDate = txtDisponibilidadFechaFinal.getDate() != null ? new SimpleDateFormat(FORMAT_DATE).format(txtDisponibilidadFechaFinal.getDate()) : null;
        
        String message = "";
        
        if (initDate != null && endDate != null) {
            if (rowCount > 0) {
                message = String.format("Articulos a mostrar %s entre el dia %s y %s",rowCount,initDate,endDate);
            } else {
                message = String.format("Mostrar todos los articulos entre el dia %s y %s", initDate,endDate);
            }
        }
        
        lblInfoConsultarDisponibilidad.setText(message);
        
    }
    
    private void fillTable (List<Articulo> items) {
        if (items == null) {
            return;
        }
        if (items.isEmpty()) {
            lblInfo.setText("No se encontraron articulos, puedes buscar por CODIGO, DESCRIPCION o COLOR");
        } else {
            lblInfo.setText("Total articulos: "+decimalFormat.format(items.size()));
        }
        
        for(Articulo articulo : items){
            
            DefaultTableModel temp = (DefaultTableModel) table.getModel();
            Object fila[] = {
                  articulo.getArticuloId()+"",
                  articulo.getCodigo(),
                  articulo.getCantidad() != 0 ? decimalFormat.format(articulo.getCantidad()) : "",
                  articulo.getRentados() != 0 ? decimalFormat.format(articulo.getRentados()) : "",
                  articulo.getFaltantes() != 0 ? decimalFormat.format(articulo.getFaltantes()) : "",
                  articulo.getReparacion() != 0 ? decimalFormat.format(articulo.getReparacion()) : "",
                  articulo.getAccidenteTrabajo() != 0 ? decimalFormat.format(articulo.getAccidenteTrabajo()) : "",
                  articulo.getDevolucion() != 0 ? decimalFormat.format(articulo.getDevolucion()) : "",
                  articulo.getTotalCompras() != 0 ? decimalFormat.format(articulo.getTotalCompras()) : "",
                  articulo.getUtiles() != 0 ? decimalFormat.format(articulo.getUtiles()) : "",
                  articulo.getCategoria().getDescripcion(),
                  articulo.getDescripcion(),
                  articulo.getColor().getColor()
               };
               temp.addRow(fila);
        }
    }
    
    private void showAndSelectItems () {
        
        AgregarArticuloDisponibilidadDialog dialog = new AgregarArticuloDisponibilidadDialog(null, true, items);
        String itemId = dialog.showDialog();

        Articulo item = itemService.obtenerArticuloPorId(Integer.parseInt(itemId));
        
        if(item == null)
            return;

        String dato = null;
         
         // verificamos que el elemento no se encuentre en la lista
        for (int i = 0; i < tablaDisponibilidadArticulos.getRowCount(); i++) {
            dato = tablaDisponibilidadArticulos.getValueAt(i, Column.ID.getNumber()).toString();
            System.out.println("dato seleccionado" + " " + " - " + dato + " - ");
            if (dato.equals(String.valueOf(item.getArticuloId()))) {
                 JOptionPane.showMessageDialog(null, "Ya se encuentra el elemento en la lista  ", "Error", JOptionPane.INFORMATION_MESSAGE);
                 return;
            }
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
    
    private void getItemsAndFillTable () {
        formatTable();
        
        try {
            items = itemService.obtenerArticulosBusquedaInventario(new HashMap<>());
            txtSearch.setEnabled(true);
            txtSearch.requestFocus();
            fillTable(items);
        } catch (Exception e) {
            Logger.getLogger(ItemsForm.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Ocurrio un inesperado\n "+e, "Error", JOptionPane.ERROR_MESSAGE);
            lblInfo.setText(e.getMessage());
        } finally {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private enum Column {
        ID(0,"id",20),
        CODE(1,"Código",20),
        STOCK(2,"Stock",20),
        RENT(3,"En renta",20),
        MISSING(4,"Faltantes",20),
        REPAIR(5,"Reparación",20),
        WORK_ACCIDENT(6,"Accidente trabajo",20),
        RETURN(7,"Devolución",20),
        SHOPPING(8,"Compras",20),
        UTILS(9,"Utiles",20),
        CATEGORY(10,"Categoria",90),
        DESCRIPTION(11,"Descripción",100),
        COLOR(12,"Color",100);
        
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
    
    private void formatTable () {
       Object[][] data = {{"","","","","","","","","","","","",""}};
        String[] columNames = {
            Column.ID.getDescription(),
            Column.CODE.getDescription(),
            Column.STOCK.getDescription(),
            Column.RENT.getDescription(),
            Column.MISSING.getDescription(),
            Column.REPAIR.getDescription(),
            Column.WORK_ACCIDENT.getDescription(),
            Column.RETURN.getDescription(),
            Column.SHOPPING.getDescription(),
            Column.UTILS.getDescription(),
            Column.CATEGORY.getDescription(),
            Column.DESCRIPTION.getDescription(),
            Column.COLOR.getDescription()
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        table.setModel(tableModel);
       TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
       table.setRowSorter(ordenarTabla);
       
       int[] weights = {
            Column.ID.getWeigt(),
            Column.CODE.getWeigt(),
            Column.STOCK.getWeigt(),
            Column.RENT.getWeigt(),
            Column.MISSING.getWeigt(),
            Column.REPAIR.getWeigt(),
            Column.WORK_ACCIDENT.getWeigt(),
            Column.RETURN.getWeigt(),
            Column.SHOPPING.getWeigt(),
            Column.UTILS.getWeigt(),
            Column.CATEGORY.getWeigt(),
            Column.DESCRIPTION.getWeigt(),
            Column.COLOR.getWeigt()
       };

       for (int inn = 0; inn < table.getColumnCount(); inn++) {
           table.getColumnModel().getColumn(inn).setPreferredWidth(weights[inn]);
       }
       
       DefaultTableCellRenderer center = new DefaultTableCellRenderer();
       center.setHorizontalAlignment(SwingConstants.CENTER);
       
       table.getColumnModel().getColumn(0).setMaxWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(0).setMinWidth(Column.ID.getNumber());
       table.getColumnModel().getColumn(0).setPreferredWidth(Column.ID.getNumber());
       
       table.getColumnModel().getColumn(Column.CODE.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.STOCK.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.RENT.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.MISSING.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.REPAIR.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.WORK_ACCIDENT.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.RETURN.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.SHOPPING.getNumber()).setCellRenderer(center);
       table.getColumnModel().getColumn(Column.UTILS.getNumber()).setCellRenderer(center);

    }
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        lblInfo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
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

        txtSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        lblInfo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 973, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
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
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Articulos", jPanel1);

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
        jLabel11.setText("Fecha Final");

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
        btnAddItem.setText("Agregar");
        btnAddItem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAddItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddItemActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jButton6.setText("Quitar");
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

        lblInfoConsultarDisponibilidad.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

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
                        .addComponent(radioBtnFechaDevolucion, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(btnAddItem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnShowAvailivity)))
                .addGap(0, 36, Short.MAX_VALUE))
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
            .addGap(0, 362, Short.MAX_VALUE)
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

        jTabbedPane1.addTab("Disponibilidad", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed
        
    }//GEN-LAST:event_tableKeyPressed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        if (txtSearch.getText().length() > 1000) {
            lblInfo.setText("Longitud de caracteres no validos");
            return;
        }
        formatTable();
        List<Articulo> filterArticulos = items.stream()
                    .filter(articulo -> Objects.nonNull(articulo))
                    .filter(articulo -> Objects.nonNull(articulo.getDescripcion()))
                    .filter(articulo -> Objects.nonNull(articulo.getColor()))
                    .filter(articulo -> Objects.nonNull(articulo.getCodigo()))
                    .filter(articulo -> (
                            articulo.getDescripcion().trim().toLowerCase() + " " + articulo.getColor().getColor().trim().toLowerCase()).contains(txtSearch.getText().toLowerCase().trim()) 
                            || articulo.getCodigo().trim().toLowerCase().contains(txtSearch.getText().toLowerCase().trim()))
                    .collect(Collectors.toList());
        fillTable(filterArticulos);
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

    public void mostrar_ver_disponibilidad_articulos() {
         // mostrara la ventana de disponibilidad de articulos
        String initDate = new SimpleDateFormat("dd/MM/yyyy").format(txtDisponibilidadFechaInicial.getDate());
        String endDate = new SimpleDateFormat("dd/MM/yyyy").format(txtDisponibilidadFechaFinal.getDate());
        List<Long> itemsId = new ArrayList<>();
        for (int i = 0; i < tablaDisponibilidadArticulos.getRowCount(); i++) {
            itemsId.add(Long.parseLong(tablaDisponibilidadArticulos.getValueAt(i, TableDisponibilidadArticulosShow.Column.ID.getNumber()).toString()));
        }
        VerDisponibilidadArticulos ventanaVerDisponibilidad = new VerDisponibilidadArticulos(
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
        ventanaVerDisponibilidad.setVisible(true);
        ventanaVerDisponibilidad.setLocationRelativeTo(null);
    }
    
    private void btnShowAvailivityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowAvailivityActionPerformed
        StringBuilder mensaje = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
        JOptionPane.showMessageDialog(null, mensaje.toString(), "Error", JOptionPane.INFORMATION_MESSAGE);
        else
        this.mostrar_ver_disponibilidad_articulos();
    }//GEN-LAST:event_btnShowAvailivityActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddItem;
    private javax.swing.JButton btnShowAvailivity;
    private javax.swing.ButtonGroup buttonGroup1;
    public static javax.swing.JCheckBox check_solo_negativos;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblInfo;
    private static javax.swing.JLabel lblInfoConsultarDisponibilidad;
    public static javax.swing.JRadioButton radioBtnFechaDevolucion;
    public static javax.swing.JRadioButton radioBtnFechaEntrega;
    public static javax.swing.JRadioButton radioBtnTodos;
    public static javax.swing.JTable table;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaFinal;
    private static com.toedter.calendar.JDateChooser txtDisponibilidadFechaInicial;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
