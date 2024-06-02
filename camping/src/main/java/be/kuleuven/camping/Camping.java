package be.kuleuven.camping;

public class Camping {
        protected Integer id;
        protected Pack type;
        protected Float price;
        protected Boolean confirmed;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Pack getType() {
            return type;
        }

        public void setType(Pack type) {
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

