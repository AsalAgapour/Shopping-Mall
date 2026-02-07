public class Product {
    private String id;
    private String name;
    private double price;
    private int stock;
    private String category;
    private String imagePath;

    public Product(String id, String name, double price, int stock, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.imagePath = imagePath;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCategory(String category) { this.category = category; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String toCsvLine() {
        return id + "," + name + "," + price + "," + stock + "," + category + "," + imagePath;
    }

    @Override
    public String toString() {
        return name + " - $" + price;
    }
}
