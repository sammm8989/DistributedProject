package be.kuleuven.dsgt4.suppliers.Festival;

public class Festival {
    protected Integer id;
    protected TicketType ticket_type;
    protected Boolean confirmed;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TicketType getTicket_type() {
        return ticket_type;
    }

    public void setTicket_type(TicketType ticket_type) {
        this.ticket_type = ticket_type;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}
