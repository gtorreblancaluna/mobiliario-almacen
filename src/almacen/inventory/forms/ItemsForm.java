package almacen.inventory.forms;

import common.model.Articulo;
import common.services.ItemService;
import java.awt.Toolkit;
import java.text.DecimalFormat;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        txtSearch = new javax.swing.JTextField();
        lblInfo = new javax.swing.JLabel();

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

        txtSearch.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        lblInfo.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 922, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(454, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(54, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblInfo;
    public static javax.swing.JTable table;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
