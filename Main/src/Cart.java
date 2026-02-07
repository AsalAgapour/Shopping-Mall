import java.util.ArrayList;

public class Cart {
    private ArrayList<Product> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addProduct(Product p) {
        items.add(p);
    }

    public double calculateTotal() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getPrice();
        }
        return total;
    }

    public ArrayList<Product> getItems() {
        return items;
    }
}
