package be.kuleuven.festival;

public class Festival {
    protected Integer id;
    protected TicketType type;
    protected Float price;
    protected Boolean confirmed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public Float getPrice() {return price;}

    public void setPrice(Float price) {this.price = price;}

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}
