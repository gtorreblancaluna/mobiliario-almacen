package almacen.model.rentas;

import lombok.Data;

@Data
public class NumberOfWeek {
    
    private Integer number;
    private String description;
    
    public NumberOfWeek (Integer number, String description) {
        this.number = number;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
    
    
    
}
