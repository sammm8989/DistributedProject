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

    public String getTypeTo() {
        return type_to;
    }

    public void setTypeTo(String type) {
        this.type_to = type;
    }

    public LocalDateTime getToFestival() {
        return to_festival;
    }

    public void setToFestival(LocalDateTime to_festival) {
        this.to_festival = to_festival;
    }

    public String getTypeFrom() {
        return type_from;
    }

    public void setTypeFrom(String type) {
        this.type_from = type;
    }

    public LocalDateTime getFromFestival() {
        return from_festival;
    }

    public void setFromFestival(LocalDateTime from_festival) {
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