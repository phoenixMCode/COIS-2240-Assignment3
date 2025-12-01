import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;


public class VehicleRentalTest {

    private RentalSystem rentalSystem;
    private Vehicle v1;
    private Vehicle v2;
    private Customer customer;

    @Before
    public void setUp() {
        
        rentalSystem = RentalSystem.getInstance();

        // Add test vehicles
        v1 = new Car("Audi", "A5", 2009, 5);
        v1.setLicensePlate("ABC123");
        
        v2 = new Car("Honda", "CRV", 2014, 8);
        v2.setLicensePlate("XYZ999");
        
        rentalSystem.addVehicle(v1);
        rentalSystem.addVehicle(v2);
        
        customer = new Customer(1,"Liam Freake");
    }

  
    @Test // license plate validation tests
    public void testValidLicensePlate() {
    	Vehicle test = new Car("Ford", "F150", 2020, 4);

        try {
            test.setLicensePlate("AAA100");
            assertTrue(true);
        } catch (Exception e) {
            assertFalse(true);
        }

        try {
            test.setLicensePlate("ABC567");
            assertTrue(true);
        } catch (Exception e) {
            assertFalse(true);
        }

        try {
            test.setLicensePlate("ZZZ999");
            assertTrue(true);
        } catch (Exception e) {
            assertFalse(true);
        }       
    }

    @Test
    public void testInvalidLicensePlate() {
    	 Vehicle test = new Car("Ford", "F150", 2020, 4);

         try {
             test.setLicensePlate("");
             assertFalse(true); //should not reach
         } catch (IllegalArgumentException e) {
             assertTrue(true);
         }

         try {
             test.setLicensePlate(null);
             assertFalse(true);
         } catch (IllegalArgumentException e) {
             assertTrue(true);
         }

         try {
             test.setLicensePlate("AAA1000");
             assertFalse(true);
         } catch (IllegalArgumentException e) {
             assertTrue(true);
         }

         try {
             test.setLicensePlate("ZZZ99");
             assertFalse(true);
         } catch (IllegalArgumentException e) {
             assertTrue(true);
         }
    }

    
    @Test // rent vehicle tests
    public void testRentAndReturnVehicle() {

        assertTrue(v1.getStatus() == Vehicle.VehicleStatus.Available);

        // Rent success
        assertTrue(rentalSystem.rentVehicle(v1, customer, LocalDate.now(), 1));
        assertTrue(v1.getStatus() == Vehicle.VehicleStatus.Rented);

        // Rent fail
        assertFalse(rentalSystem.rentVehicle(v1, customer, LocalDate.now(), 1));

        // Return success
        assertTrue(rentalSystem.returnVehicle(v1, customer, LocalDate.now(), 1));
        assertTrue(v1.getStatus() == Vehicle.VehicleStatus.Available);

        // Return fail
        assertFalse(rentalSystem.returnVehicle(v1, customer, LocalDate.now(), 1));
    }

    

    @Test // Check for private constructor
    public void testSingletonConstructorIsPrivate() throws Exception {
    	Constructor<RentalSystem> constructor =
                RentalSystem.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers())); // must be private
    }

 
    @Test // Check if getInstance returns the same object
    public void testReturnsSameObject() {
        RentalSystem returnSame1 = RentalSystem.getInstance();
        RentalSystem returnSame2 = RentalSystem.getInstance();
        assertTrue(returnSame1 == returnSame2); //same reference
    }
}
