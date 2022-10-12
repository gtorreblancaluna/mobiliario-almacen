package almacen.rentas.vos;

public class NumberOfWeek {
    
    private Integer number;
    private String description;
    
    public NumberOfWeek (Integer number, String description) {
        this.number = number;
        this.description = description;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }
    
    
    
}
