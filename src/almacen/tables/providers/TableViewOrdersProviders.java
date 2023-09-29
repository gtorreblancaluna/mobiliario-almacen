package almacen.tables.providers;

import java.awt.Font;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TableViewOrdersProviders extends JTable {

    public TableViewOrdersProviders() {
       
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
        
         this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMaxWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setMinWidth(0);
        this.getColumnModel().getColumn(Column.RENTA_ID.getNumber()).setPreferredWidth(0);
     
        this.getColumnModel().getColumn(Column.ORDER_NUM.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.FOLIO_RENTA.getNumber()).setCellRenderer(centrar);
        this.getColumnModel().getColumn(Column.SUB_TOTAL.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.PAYMENTS.getNumber()).setCellRenderer(right);
        this.getColumnModel().getColumn(Column.TOTAL.getNumber()).setCellRenderer(right);
    }
    
    private final String[] columnNames = {          
            Column.ORDER_NUM.getDescription(),
            Column.FOLIO_RENTA.getDescription(),
            Column.USER.getDescription(),
            Column.SUPPLIER.getDescription(),
            Column.STATUS.getDescription(),
            Column.CREATED.getDescription(),
            Column.UPDATED.getDescription(),            
            Column.COMMENT.getDescription(),
            Column.RENTA_ID.getDescription(),
            Column.SUB_TOTAL.getDescription(),
            Column.PAYMENTS.getDescription(),
            Column.TOTAL.getDescription(),
            Column.EVENT_DATE.getDescription()
        };
    
    private final int[] sizes = {
            Column.ORDER_NUM.getSize(),
            Column.FOLIO_RENTA.getSize(),
            Column.USER.getSize(),
            Column.SUPPLIER.getSize(),
            Column.STATUS.getSize(),
            Column.CREATED.getSize(),
            Column.UPDATED.getSize(),            
            Column.COMMENT.getSize(),
            Column.RENTA_ID.getSize(),
            Column.SUB_TOTAL.getSize(),
            Column.PAYMENTS.getSize(),
            Column.TOTAL.getSize(),
            Column.EVENT_DATE.getSize()
        };
    
    
    public enum Column{
        
       ORDER_NUM(0,"No orden",20),
       FOLIO_RENTA(1,"Folio renta",20),
       USER(2,"Usuario",80),
       SUPPLIER(3,"Proveedor",40),
       STATUS(4,"Status",40),
       CREATED(5,"Creado",80),
       UPDATED(6,"Actualizado",90),
       COMMENT(7,"Comentario",100),
       RENTA_ID(8,"id_renta",20),
       SUB_TOTAL(9,"Subtotal",80),
       PAYMENTS(10,"Pagos",80),
       TOTAL(11,"Total",80),
       EVENT_DATE(12,"Fecha Evento",80);
       
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
