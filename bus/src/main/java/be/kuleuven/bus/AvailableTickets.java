package be.kuleuven.bus;

import java.time.LocalDateTime;


public class AvailableTickets {
    protected String type;
    protected BoardingLocation boardingLocation;
    protected LocalDateTime dateTime;
    protected Boolean toFestival;
    protected Float price;
    protected Integer total;
    protected Integer sold;

    public AvailableTickets(String type, int total, BoardingLocation boardingLocation,LocalDateTime dateTime,Float price, Boolean toFestival){
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BoardingLocation getBoardingLocation() {
        return boardingLocation;
    }

    public void setBoardingLocation(BoardingLocation boardingLocation) {
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
