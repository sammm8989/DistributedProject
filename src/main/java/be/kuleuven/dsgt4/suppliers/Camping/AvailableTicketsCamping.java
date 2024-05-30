package be.kuleuven.dsgt4.suppliers.Camping;

public class AvailableTicketsCamping {
    protected Pack camping_package;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTicketsCamping(Pack camping_package, int total) {
        this.camping_package = camping_package;
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
        return camping_package;
    }

    public void setCampingPackage(Pack camping_package) {
        this.camping_package = camping_package;
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
