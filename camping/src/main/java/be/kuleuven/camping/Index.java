package be.kuleuven.camping;

public class Index {
    private String name;
    private Integer number;

    public Index(){
        name = "Index Page";
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getString() {
        return name;
    }

    public void setString(String name) {
        name = name;
    }

}