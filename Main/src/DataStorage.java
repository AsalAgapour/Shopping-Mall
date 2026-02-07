import java.util.ArrayList;

public interface DataStorage {
    void save(ArrayList<Product> list);
    ArrayList<Product> load();
}
