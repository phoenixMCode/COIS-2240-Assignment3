public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
    	if (make == null || make.isEmpty())
    		this.make = null;
    	else
    		this.make = capitalize(make);
    	
    	if (model == null || model.isEmpty())
    		this.model = null;
    	else
    		this.model = capitalize(model);
    	
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    private String capitalize(String string) {
    	return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
    
    private boolean isValidPlate(String plate) {
    	if (plate == null||plate.length()>6) {
    		return false;
    	}
    	return plate.matches("[A-Z]{3}[0-9]{3}");
    }
    
    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) {
        this.licensePlate = plate == null ? null : plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
    @Override
    public String toString() {
    	return getClass().getSimpleName()+","+getLicensePlate()+","+getStatus()+","+getMake()+","+getModel()+","+getYear();
    }

}
