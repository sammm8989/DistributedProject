package be.kuleuven.camping;

public class AvailableTickets {
    protected Pack type;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTickets(Pack type, int total) {
        this.type = type;
        this.total = total;
        this.sold = 0;
    }

    public synchronized boolean isAvailable(){
        return (sold < total);
    }

    public synchronized void sellCampingTicket() {
        if (sold < total) {
            sold++;
        }
    }

    public synchronized void restockCampingTicket(){
        if (sold >0){
            sold--;
        }
    }

    public Pack getType() {
        return type;
    }

    public void setType(Pack type) {
        this.type = type;
    }

    public Float getPrice(){return price;}

    public void setPrice(Float price) {this.price = price;}

    public Integer getTotal() {
        return total;
    }

    public synchronized void setTotal(Integer total) {
        this.total = total;
    }

    public synchronized Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }
}
