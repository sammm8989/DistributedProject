package be.kuleuven.Bus;

import java.time.LocalDateTime;

public class Bus {
    protected Integer id;
    protected String type_to;
    protected LocalDateTime to_festival;
    protected String type_from;
    protected LocalDateTime from_festival;
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

    public LocalDateTime getTo_festival() {
        return to_festival;
    }

    public void setTo_festival(LocalDateTime to_festival) {
        this.to_festival = to_festival;
    }

    public LocalDateTime getFrom_festival() {
        return from_festival;
    }

    public void setFrom_festival(LocalDateTime from_festival) {
        this.from_festival = from_festival;
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