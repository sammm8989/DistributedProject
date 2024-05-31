package be.kuleuven.dsgt4.suppliers.Bus;

import java.util.Date;

public class Bus {
    protected Integer id;
    protected String type;
    protected Date date;
    protected Boolean to_festival;
    protected BoardingLocation location;
    protected Boolean confirmed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getTo_festival() {
        return to_festival;
    }

    public void setTo_festival(Boolean to_festival) {
        this.to_festival = to_festival;
    }

    public BoardingLocation getLocation() {
        return location;
    }

    public void setLocation(BoardingLocation location) {
        this.location = location;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}
