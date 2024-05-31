package be.kuleuven.dsgt4.suppliers.Bus;

import com.google.type.DateTime;

public class AvailableTicketsBus {
    protected String ticket_type;
    protected BoardingLocation boardingLocation;
    protected DateTime dateTime;
    protected Boolean toFestival;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTicketsBus(String ticket_type, int total, BoardingLocation boardingLocation, DateTime dateTime,Float price, Boolean toFestival){
        this.ticket_type = ticket_type;
        this.boardingLocation = boardingLocation;
        this.dateTime = dateTime;
        this.toFestival  = toFestival;
        this.price = price;
        this.total = total;
        this.sold = 0;
    }

    public synchronized boolean isAvailable() {return  (sold < total);}
    public synchronized void sellBusTicket() {
        if (sold < total) {
            sold++;
        }
    }
    public synchronized void restockBusTicket(){
        if (sold >0){
            sold--;
        }
    }

    public String getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(String ticket_type) {
        this.ticket_type = ticket_type;
    }

    public BoardingLocation getBoardingLocation() {
        return boardingLocation;
    }

    public void setBoardingLocation(BoardingLocation boardingLocation) {
        this.boardingLocation = boardingLocation;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Boolean getToFestival() {
        return toFestival;
    }

    public void setToFestival(Boolean toFestival) {
        this.toFestival = toFestival;
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
