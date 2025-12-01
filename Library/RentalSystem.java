import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

public class RentalSystem {
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private static RentalSystem instance; //singleton implementation
    private enum type{RENT,RETURN};
    type recordType;

    private RentalSystem() {
    	loadData(); //call load data method
    }
    
    private void loadData() { //calls individual load methods
    	loadVehicles();
    	loadCustomers();
    	loadRecords();
    }
    
    
    public static RentalSystem getInstance() { //singleton implementation
    	if (instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }
    
    
    public boolean addVehicle(Vehicle vehicle) { 
    	if (findVehicleByPlate(vehicle.getLicensePlate()) == null) //checks if plate is already in system
    	{
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    	}
    	else {
    		System.out.println("License Plate is already in system."); //error output if duplicate is found
    		return false;
    	}
    }

    public boolean addCustomer(Customer customer) {
    	if (findCustomerById(customer.getCustomerId()) == null) { //checks for duplicate ID
        customers.add(customer);
        saveCustomer(customer);
        return true;
    	}
    	else {
    		System.out.println("Customer Id is already occupied."); //error message
    		return false;
    	}
    }

    
    private void saveVehicle(Vehicle vehicle) { 
    	
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", true))) { //try to create write
            writer.write(vehicle.toString()); //calls a overide to string method which formats for data loading
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }
    
    private void saveCustomer(Customer customer) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("Customers.txt", true))) { //try to create new write
            writer.write(customer.custToFile()); //cust to file formats data for easier data loading
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    	
    }
    
    public void SaveRecord(Vehicle vehicle, Customer customer, LocalDate date, double amount,type type) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("RentalRecords.txt", true))) {
            writer.write(vehicle.getLicensePlate()+","+customer.getCustomerId()+","+date.toString()+","+amount+","+type); //saves data in loadable method
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    	
    }
    
    
    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) { //method called to rent vehicle
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            recordType = type.RENT;
            SaveRecord(vehicle,customer,date,amount,recordType); //save data to txt
            
            return true; // for assert true / false methods
        }
        else {
            System.out.println("Vehicle is not available for renting.");
            
            return false; // for assert true / false methods
        }
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) { //return vehicle method
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            recordType = type.RETURN;
            SaveRecord(vehicle,customer,date,extraFees,recordType); //save data to txt
            
            return true; // for assert true / false methods
        }
        else {
            System.out.println("Vehicle is not rented.");
            
            return false; // for assert true / false methods
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) { //displays all availible vehicles
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    
    private void loadVehicles() { //method to load vehicles
    	try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;   

                String[] parts = line.split(",");      //this splits the line up by its commas

                String type   = parts[0];              //each part corresponds to a different peice of data
                String plate  = parts[1];
                String loadstatus = parts[2];
                String make   = parts[3];
                String model  = parts[4];
                int year      = Integer.parseInt(parts[5]);

                Vehicle v = null;

                switch (type) { //switch for the different types of cars
                    case "Car": {
                        int numSeats = Integer.parseInt(parts[6]); //extra info like seats is still picked up
                        v = new Car(make, model, year, numSeats);
                        break;
                    }
                    case "Minibus": {
                        boolean isAccessible = Boolean.parseBoolean(parts[6]);
                        v = new Minibus(make, model, year, isAccessible);
                        break;
                    }
                    case "PickupTruck": {
                        double cargoSize = Double.parseDouble(parts[6]);
                        boolean hasTrailer = Boolean.parseBoolean(parts[7]);
                        v = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                        break;
                    }
                    default:
                        System.out.println("Unknown vehicle type in file: " + type);
                }

                if (v != null) {
                    v.setLicensePlate(plate);
                    v.setStatus(Vehicle.VehicleStatus.valueOf(loadstatus));
                    vehicles.add(v);
                }
            }
        } catch (FileNotFoundException e) {
            
        
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage()); //error catch for loading issues
        }
    	
    }
    
    private void loadCustomers() {
    	try (BufferedReader reader = new BufferedReader(new FileReader("Customers.txt"))) { //loads from customers.txt
            String line;
            while ((line = reader.readLine()) != null) { 
                if (line.trim().isEmpty()) continue;   

                String[] parts = line.split(",");  //splits by commas
                
                int custId = Integer.parseInt(parts[0]); //each part is its own respective data
                String custName = parts[1];
                
                Customer cust = new Customer(custId,custName); //create new customers with said data
                
                customers.add(cust);
            }
            
            
            
            
            
    	} catch (FileNotFoundException e) {
            
            
        } catch (IOException e) {
            System.out.println("Error loading customer: " + e.getMessage()); //error handling
        }
    	
    	
    	
    }
    private void loadRecords() {
    	try (BufferedReader reader = new BufferedReader(new FileReader("RentalRecords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;   

                String[] parts = line.split(","); //splits input by commas
                
                Vehicle v = findVehicleByPlate(parts[0]); //find the vehicle that corresponds to the record, should already be there because load vehicles is called first
                Customer cust = findCustomerById(Integer.parseInt(parts[1])); //customers loaded first, so search for customer
                LocalDate date = LocalDate.parse(parts[2]); //parse back into a date
                double cost = Double.parseDouble(parts[3]); //string to double
                String recordType = parts[4];
                
                RentalRecord record = new RentalRecord(v,cust,date,cost,recordType); //create new record
                
                rentalHistory.addRecord(record); //adds to records
            }
    	} catch (FileNotFoundException e) {
            
            
        } catch (IOException e) {
            System.out.println("Error loading customer: " + e.getMessage()); //error handling
        }
    }
    
}