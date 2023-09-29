package almacen.tables.providers;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TableViewOrdersProvidersDetail extends JTable {

    public TableViewOrdersProvidersDetail() {
        
        this.setFont(new Font( "Arial" , Font.PLAIN, 12 ));
        format();
        
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
    
    
    public void format () {
        
        setModel(
                new DefaultTableModel(columnNames,0)
        );
        
        
        for (int inn = 0; inn < this.getColumnCount(); inn++) {
            this.getColumnModel().getColumn(inn).setPreferredWidth(sizes[inn]);
        }
        
        try {
            DefaultTableModel temp = (DefaultTableModel) this.getModel();
            temp.removeRow(temp.getRowCount() - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            ;
        }
        
        DefaultTableCellRenderer centrar = new DefaultTableCellRenderer();
        centrar.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        
        this.getColumnModel().getColumn(Column.ORDER_SUPPLIER_DETAIL_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.ORDER_SUPPLIER_DETAIL_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.ORDER_SUPPLIER_DETAIL_ID.getNumber()).setPreferredWidth(0);
     
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
        
        this.getColumnModel().getColumn(Column.FOLIO.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.AMOUNT.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.PRICE.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.IMPORT.getNumber()).setCellRenderer(right);
    }
    
    private final String[] columnNames = {          
            Column.ORDER_SUPPLIER_ID.getDescription(),
            Column.ORDER_SUPPLIER_DETAIL_ID.getDescription(),
            Column.RENTA_ID.getDescription(),
            Column.FOLIO.getDescription(),
            Column.PRODUCT.getDescription(),
            Column.AMOUNT.getDescription(),
            Column.PRICE.getDescription(),            
            Column.IMPORT.getDescription(),
            Column.EVENT_DATE.getDescription(),
            Column.USER.getDescription(),
            Column.SUPPLIER.getDescription(),
            Column.ORDER_COMMENT.getDescription(),
            Column.ORDER_DETAIL_TYPE.getDescription(),
            Column.CREATED_AT.getDescription()
        };
    
    private final int[] sizes = {
            Column.ORDER_SUPPLIER_ID.getSize(),
            Column.ORDER_SUPPLIER_DETAIL_ID.getSize(),
            Column.RENTA_ID.getSize(),
            Column.FOLIO.getSize(),
            Column.PRODUCT.getSize(),
            Column.AMOUNT.getSize(),
            Column.PRICE.getSize(),            
            Column.IMPORT.getSize(),
            Column.EVENT_DATE.getSize(),
            Column.USER.getSize(),
            Column.SUPPLIER.getSize(),
            Column.ORDER_COMMENT.getSize(),
            Column.ORDER_DETAIL_TYPE.getSize(),
            Column.CREATED_AT.getSize()
        };
    
    
    public enum Column{
        
       ORDER_SUPPLIER_ID(0,"No Orden",10),
       ORDER_SUPPLIER_DETAIL_ID(1,"detalle_orden_proveedor_id",10),
       RENTA_ID(2,"renta_id",10),
       FOLIO(3,"Folio",20),
       PRODUCT(4,"Articulo",60),
       AMOUNT(5,"Cantidad",40),
       PRICE(6,"Precio",40),
       IMPORT(7,"Importe",40),
       EVENT_DATE(8,"Fecha evento",60),
       USER(9,"Usuario",80),
       SUPPLIER(10,"Proveedor",80),
       ORDER_COMMENT(11,"Comentario orden",80),
       ORDER_DETAIL_TYPE(12,"Tipo",50),
       CREATED_AT(13,"Creado",60);
       
       private final int number;
       private final String description;
       private final int size;

        Column(int number, String description, int size) {
            this.number = number;
            this.description = description;
            this.size = size;
        }
        public int getSize () {
            return this.size;
        }
        public int getNumber () {
            return this.number;
        }
        
        public String getDescription () {
            return this.description;
        }
        
    }
    
    
}
