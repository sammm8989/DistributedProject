package be.kuleuven.camping;

public class Camping {
        protected Integer id;
        protected Pack camping_package;
        protected Float price;
        protected Boolean confirmed;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Pack getCamping_package() {
            return camping_package;
        }

        public void setCamping_package(Pack camping_package) {
            this.camping_package = camping_package;
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

