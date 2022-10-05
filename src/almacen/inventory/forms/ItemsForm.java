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
        this.setClosable(true);
        getItemsAndFillTable();
    }
    
    private void fillTable (List<Articulo> items) {
        if (items == null) {
            return;
        }
        lblInfo.setText("Total articulos: "+decimalFormat.format(items.size()));
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

    private enum Columns {
        ID(0,"id"),
        CODE(1,"Código"),
        STOCK(2,"Stock"),
        RENT(3,"En renta"),
        MISSING(4,"Faltantes"),
        REPAIR(5,"Reparación"),
        WORK_ACCIDENT(6,"Accidente trabajo"),
        RETURN(7,"Devolución"),
        SHOPPING(8,"Compras"),
        UTILS(9,"Utiles"),
        CATEGORY(10,"Categoria"),
        DESCRIPTION(11,"Descripción"),
        COLOR(12,"Color");
        
        private Integer number;
        private String description;
        
        Columns (Integer number, String description) {
            this.number = number;
            this.description = description;
        }
        
        public Integer getNumber () {
            return this.number;
        }
        
        public String getDescription () {
            return this.description;
        }
                
    }
    
    private void formatTable () {
       Object[][] data = {{"","","","","","","","","","","","",""}};
        String[] columNames = {
            Columns.ID.getDescription(),
            Columns.CODE.getDescription(),
            Columns.STOCK.getDescription(),
            Columns.RENT.getDescription(),
            Columns.MISSING.getDescription(),
            Columns.REPAIR.getDescription(),
            Columns.WORK_ACCIDENT.getDescription(),
            Columns.RETURN.getDescription(),
            Columns.SHOPPING.getDescription(),
            Columns.UTILS.getDescription(),
            Columns.CATEGORY.getDescription(),
            Columns.DESCRIPTION.getDescription(),
            Columns.COLOR.getDescription()
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columNames);
        table.setModel(tableModel);
       TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<TableModel>(tableModel); 
       table.setRowSorter(ordenarTabla);
       
       int[] anchos = {20,20,20,20,20,20,20,20,20,20,20,100,100};

       for (int inn = 0; inn < table.getColumnCount(); inn++) {
           table.getColumnModel().getColumn(inn).setPreferredWidth(anchos[inn]);
       }
       
       DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
       centrar.setHorizontalAlignment(SwingConstants.CENTER);
       
       table.getColumnModel().getColumn(0).setMaxWidth(0);
       table.getColumnModel().getColumn(0).setMinWidth(0);
       table.getColumnModel().getColumn(0).setPreferredWidth(0);
        
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
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtSearch)
                    .addComponent(lblInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(454, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(45, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tableKeyPressed
        
    }//GEN-LAST:event_tableKeyPressed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        
        formatTable();
        List<Articulo> filterArticulos = items.stream()
                    .filter(articulo -> Objects.nonNull(articulo))
                    .filter(articulo -> Objects.nonNull(articulo.getDescripcion()))
                    .filter(articulo -> Objects.nonNull(articulo.getColor()))
                    .filter(articulo -> (articulo.getDescripcion().trim().toLowerCase() + " " + articulo.getColor().getColor().trim().toLowerCase()).contains(txtSearch.getText().toLowerCase().trim()))
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
