package be.kuleuven.dsgt4.suppliers.Camping;

public class Camping {
        protected Integer id;
        protected Package camping_package;
        protected Boolean confirmed;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Package getCamping_package() {
            return camping_package;
        }

        public void setCamping_package(Package camping_package) {
            this.camping_package = camping_package;
        }

        public Boolean getConfirmed() {
            return confirmed;
        }

        public void setConfirmed(Boolean confirmed) {
            this.confirmed = confirmed;
        }
    }

