package src.main.java.be.kuleuven.bus;

import com.google.type.DateTime;

import java.time.LocalDateTime;

public class AvailableTickets{
    protected String ticket_type;
    protected be.kuleuven.Bus.BoardingLocation boardingLocation;
    protected LocalDateTime dateTime;
    protected Boolean toFestival;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTickets(String ticket_type, int total, be.kuleuven.Bus.BoardingLocation boardingLocation, LocalDateTime dateTime, Float price, Boolean toFestival){
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

    public be.kuleuven.Bus.BoardingLocation getBoardingLocation() {
        return boardingLocation;
    }

    public void setBoardingLocation(be.kuleuven.Bus.BoardingLocation boardingLocation) {
        this.boardingLocation = boardingLocation;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
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