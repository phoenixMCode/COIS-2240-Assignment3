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
    private static RentalSystem instance;
    private enum type{RENT,RETURN};
    type recordType;

    private RentalSystem() {
    	loadData();
    }
    
    private void loadData() {
    	loadVehicles();
    	loadCustomers();
    	loadRecords();
    }
    
    
    public static RentalSystem getInstance() {
    	if (instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }
    
    
    public boolean addVehicle(Vehicle vehicle) {
    	if (findVehicleByPlate(vehicle.getLicensePlate()) == null)
    	{
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    	}
    	else {
    		System.out.println("License Plate is already in system.");
    		return false;
    	}
    }

    public boolean addCustomer(Customer customer) {
    	if (findCustomerById(customer.getCustomerId()) == null) {
        customers.add(customer);
        saveCustomer(customer);
        return true;
    	}
    	else {
    		System.out.println("Customer Id is already occupied.");
    		return false;
    	}
    }

    
    private void saveVehicle(Vehicle vehicle) {
    	
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", true))) {
            writer.write(vehicle.toString());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }
    
    private void saveCustomer(Customer customer) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("Customers.txt", true))) {
            writer.write(customer.custToFile());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    	
    }
    
    public void SaveRecord(Vehicle vehicle, Customer customer, LocalDate date, double amount,type type) {
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("RentalRecords.txt", true))) {
            writer.write(vehicle.getLicensePlate()+","+customer.getCustomerId()+","+date.toString()+","+amount+","+type);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    	
    }
    
    
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            recordType = type.RENT;
            SaveRecord(vehicle,customer,date,amount,recordType);
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            recordType = type.RETURN;
            SaveRecord(vehicle,customer,date,extraFees,recordType);
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
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
    
    private void loadVehicles() {
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

                switch (type) {
                    case "Car": {
                        int numSeats = Integer.parseInt(parts[6]);
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
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    	
    }
    
    private void loadCustomers() {
    	try (BufferedReader reader = new BufferedReader(new FileReader("Customers.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;   

                String[] parts = line.split(","); 
                
                int custId = Integer.parseInt(parts[0]);
                String custName = parts[1];
                
                Customer cust = new Customer(custId,custName);
                
                customers.add(cust);
            }
            
            
            
            
            
    	} catch (FileNotFoundException e) {
            
            
        } catch (IOException e) {
            System.out.println("Error loading customer: " + e.getMessage());
        }
    	
    	
    	
    }
    private void loadRecords() {
    	try (BufferedReader reader = new BufferedReader(new FileReader("RentalRecords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;   

                String[] parts = line.split(","); 
                
                Vehicle v = findVehicleByPlate(parts[0]);
                Customer cust = findCustomerById(Integer.parseInt(parts[1]));
                LocalDate date = LocalDate.parse(parts[2]);
                double cost = Double.parseDouble(parts[3]);
                String recordType = parts[4];
                
                RentalRecord record = new RentalRecord(v,cust,date,cost,recordType);
                
                rentalHistory.addRecord(record);
            }
    	} catch (FileNotFoundException e) {
            
            
        } catch (IOException e) {
            System.out.println("Error loading customer: " + e.getMessage());
        }
    }
    
}