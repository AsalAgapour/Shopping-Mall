import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileService implements DataStorage {
    private String fileName = "products.txt";

    @Override
    public void save(ArrayList<Product> list) {
        try {
            FileWriter writer = new FileWriter(fileName);

            for (int i = 0; i < list.size(); i++) {
                Product p = list.get(i);
                String line = p.getId() + "," + p.getName() + "," +
                        p.getPrice() + "," + p.getStock() + "," +
                        p.getCategory() + "," + p.getImagePath();
                writer.write(line + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Product> load() {
        ArrayList<Product> list = new ArrayList<>();
        try {
            File f = new File(fileName);
            if (!f.exists()) return list;


            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] d = line.split(",");

                if (d.length == 6) {
                    String id = d[0];
                    String name = d[1];
                    double price = Double.parseDouble(d[2]);
                    int stock = Integer.parseInt(d[3]);
                    String cat = d[4];
                    String img = d[5];

                    Product p = new Product(id, name, price, stock, cat, img);
                    list.add(p);
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
        return list;
    }
}
