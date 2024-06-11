# DistributedProject
Github Repository for the Distributed Software course

# Suppliers:
Each supplier has his own folder -> go to root folder en run mvn spring-boot:run the bus should still be finished. The ip and port can be set in src/main/resources/application.properties

They are now set:

festival = 8090
camping = 8100
bus = 8110

# Deployed supppliers
Every supplier is currently hosted on an azure VM

### festival:
ssh: azureuser@dsgt2024team13.japaneast.cloudapp.azure.com
password: dsgt2024Japan
send request: dsgt2024team13.japaneast.cloudapp.azure.com:8090/request

### camping: 
ssh: azureuser@dsgt.canadacentral.cloudapp.azure.com 
password: dsgt2024Canada
send request: dsgt.canadacentral.cloudapp.azure.com:8100/request

### bus:
ssh: azureresource@dsgt.uksouth.cloudapp.azure.com
password dsgt2024UKSouth
send request: dsgt.uksouth.cloudapp.azure.com:8110/request
