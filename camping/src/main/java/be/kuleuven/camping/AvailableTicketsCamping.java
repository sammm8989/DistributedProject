package be.kuleuven.camping;

public class AvailableTicketsCamping {
    protected Pack type;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTicketsCamping(Pack type, int total) {
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

    public Pack getCampingPackage() {
        return type;
    }

    public void setCampingPackage(Pack camping_package) {
        this.type = camping_package;
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
