package be.kuleuven.bus;

import java.time.LocalDateTime;

public class Bus {
    protected Integer id;
    protected String type_to;
    protected String type_from;
    protected Float price;
    protected Boolean confirmed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType_to() {
        return type_to;
    }

    public void setType_to(String type_to) {
        this.type_to = type_to;
    }

    public String getType_from() {
        return type_from;
    }

    public void setType_from(String type_from) {
        this.type_from = type_from;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}