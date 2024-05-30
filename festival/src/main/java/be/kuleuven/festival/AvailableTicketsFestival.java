package be.kuleuven.festival;

public class AvailableTicketsFestival {
    protected TicketType ticketType;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTicketsFestival(TicketType ticketType, int total){
        this.ticketType = ticketType;
        this.total = total;
        this.sold = 0;
    }

    public synchronized boolean isAvailable(){
        return (sold < total);
    }

    public synchronized void sellFestivalTicket() {
        if (sold < total) {
            sold++;
        }
    }

    public synchronized void restockFestivalTicket(){
        if (sold >0){
            sold--;
        }
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }
}
