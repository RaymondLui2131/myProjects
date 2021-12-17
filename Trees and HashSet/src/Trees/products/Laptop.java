package hw3.products;

import java.lang.Comparable;
/**
 * TODO: The 'Laptop' object should be comparable.
 *
 * @author Ritwik Banerjee , Raymond Lui
 */
public class Laptop implements Comparable<Laptop> {

    private String brand;
    private double processorSpeed;
    private int    ram;
    private int    price;
    private double screenSize;

    /**
     * Constructs a Laptop with a laptopBuilder's brand, processorSpeed,
     * ram,price, and screenSize
     * @param laptopBuilder the laptopBuilder creates a laptop with some brand,processorSpeed,ram,price, and screenSize.
     */
    private Laptop(LaptopBuilder laptopBuilder) {
        this.brand = laptopBuilder.brand;
        this.processorSpeed = laptopBuilder.processorSpeed;
        this.ram = laptopBuilder.ram;
        this.price = laptopBuilder.price;
        this.screenSize = laptopBuilder.screenSize;
    }

    /**
     * This method checks if two laptops variables are equal to each other
     * @param obj A laptop to be compared
     * @return true if all variables are equal and false if anyone one of the variables aren't equal.
     */
    @Override
    public boolean equals(Object obj){
        Laptop lap = (Laptop)obj;
        if (!this.brand.equals(lap.getBrand()))
            return false;
        if (this.processorSpeed != lap.getProcessorSpeed())
            return false;
        if (this.ram != lap.getRam())
            return false;
        if (this.price != lap.getPrice())
            return false;
        if (this.screenSize != lap.getScreenSize())
            return false;
        return true;
    }

    public String getBrand()          { return brand; }

    public double getProcessorSpeed() { return processorSpeed; }

    public int getRam()               { return ram; }

    public int getPrice()             { return price; }

    public double getScreenSize()     { return screenSize; }

    /**
     * This method compares two laptop prices
     * @param lap A given laptop to be compared
     * @return 1 if lap's price is greater than the other laptop's price, 0 if they're equal, and -1 if the other
     * laptop's price is greater than lap's price
     */
    public int compareTo(Laptop lap){
        if (price < lap.getPrice()){
            return 1;
        }
        else if (lap.getPrice() == price){
            return 0;
        }
        return -1;
    }

    private static class LaptopBuilder {
        private String brand;
        private double processorSpeed;
        private int    ram;
        private int    price;
        private double screenSize;

        LaptopBuilder withBrand(String brand) {
            this.brand = brand;
            return this;
        }

        LaptopBuilder withProcessorSpeed(double processorSpeed) {
            this.processorSpeed = processorSpeed;
            return this;
        }

        LaptopBuilder withMemory(int ram) {
            this.ram = ram;
            return this;
        }

        LaptopBuilder withPrice(int price) {
            this.price = price;
            return this;
        }

        LaptopBuilder withScreenSize(double screenSize) {
            this.screenSize = screenSize;
            return this;
        }

        Laptop build() {
            return new Laptop(this);
        }
    }

    public static Laptop fromString(String s) {
        if (s.isEmpty())
            return null;
        String[]      aspects = s.split(",");
        LaptopBuilder builder = new LaptopBuilder();

        try {
            builder.withBrand(aspects[0].toLowerCase());
            builder.withProcessorSpeed(Double.parseDouble(aspects[1]));
            builder.withMemory(Integer.parseInt(aspects[2]));
            builder.withPrice(Integer.parseInt(aspects[3]));
            builder.withScreenSize(Double.parseDouble(aspects[4]));
        } catch (Exception e) {
            System.out.println("Invalid input line.");
        }

        return builder.build();
    }

    // !!DO NOT MODIFY THIS CODE!!
    @Override
    public String toString() {
        return "products.Laptop{" + "brand='" + brand + '\'' +
                ", processorSpeed=" + processorSpeed +
                ", ram=" + ram +
                ", price=" + price +
                ", screenSize=" + screenSize +
                '}';
    }

    // !!DO NOT MODIFY THIS CODE!!
    @Override public int hashCode() {
        int  result;
        long temp;
        result = brand.hashCode();
        temp = Double.doubleToLongBits(processorSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + ram;
        result = 31 * result + price;
        temp = Double.doubleToLongBits(screenSize);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
