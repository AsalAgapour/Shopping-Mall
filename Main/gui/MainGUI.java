import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class MainGUI extends JFrame {
    private ArrayList<Product> allProducts;
    private DataStorage storage = new FileService();
    private User currentUser;
    private Cart myCart = new Cart();

    private JPanel mainPanel;
    private CardLayout cardLayout = new CardLayout();

    public MainGUI() {

        setTitle("Shopping Mall Project");
        setSize(900, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        allProducts = storage.load();


        if(allProducts.isEmpty()){
            allProducts.add(new Product("1", "Coffe", 3.5, 10, "Drink", "Coffe.jpg"));
            allProducts.add(new Product("2", "Tea", 3.0, 20, "Drink", "Tea.jpg"));
            allProducts.add(new Product("3", "Cake", 5.0, 5, "Food", "Cake.jpg"));
            storage.save(allProducts);
        }

        mainPanel = new JPanel(cardLayout);

        initLoginPage();

        add(mainPanel);
        setVisible(true);
    }

    private void initLoginPage() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.LIGHT_GRAY);

        JLabel title = new JLabel("Welcome to Shopping Mall");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(300, 100, 300, 30);

        JLabel userLbl = new JLabel("User:"); userLbl.setBounds(300, 200, 80, 30);
        JTextField userTxt = new JTextField(); userTxt.setBounds(380, 200, 150, 30);

        JLabel passLbl = new JLabel("Pass:"); passLbl.setBounds(300, 250, 80, 30);
        JPasswordField passTxt = new JPasswordField(); passTxt.setBounds(380, 250, 150, 30);

        JButton loginBtn = new JButton("Login"); loginBtn.setBounds(380, 310, 100, 40);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u = userTxt.getText();
                String p = new String(passTxt.getPassword());

                if(u.equals("admin") && p.equals("admin")) {
                    currentUser = new Admin(u, p);
                    initAdminPanel();
                } else if(u.equals("user") && p.equals("user")) {
                    currentUser = new Customer(u, p, 100.0);
                    initCustomerPanel();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Login! (Try: admin/admin or user/user)");
                }
            }
        });

        panel.add(title);
        panel.add(userLbl); panel.add(userTxt);
        panel.add(passLbl); panel.add(passTxt);
        panel.add(loginBtn);

        mainPanel.add(panel, "LOGIN");
    }

    private void initCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        Customer c = (Customer) currentUser;

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel header = new JLabel(" Welcome " + c.getUsername() + " | Balance: $" + c.getBalance());
        header.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel searchPanel = new JPanel();
        JTextField searchTxt = new JTextField(20);
        JButton searchBtn = new JButton("Search Product");
        searchPanel.add(new JLabel("Find:"));
        searchPanel.add(searchTxt);
        searchPanel.add(searchBtn);

        topPanel.add(header);
        topPanel.add(searchPanel);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(grid);
        panel.add(scrollPane, BorderLayout.CENTER);

        fillCustomerGrid(grid, "");

        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillCustomerGrid(grid, searchTxt.getText());
            }
        });

        JPanel bottomBtnPnl = new JPanel(new GridLayout(1, 2));
        JButton viewCartBtn = new JButton("View Cart & Checkout");
        viewCartBtn.setBackground(new Color(100, 200, 100));
        viewCartBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { initCartDetailPanel(); }
        });

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(200, 100, 100));
        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        });

        bottomBtnPnl.add(viewCartBtn);
        bottomBtnPnl.add(logoutBtn);
        panel.add(bottomBtnPnl, BorderLayout.SOUTH);

        mainPanel.add(panel, "CUSTOMER");
        cardLayout.show(mainPanel, "CUSTOMER");
    }

    private void fillCustomerGrid(JPanel gridPanel, String query) {
        gridPanel.removeAll();


        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);


            if (p.getName().toLowerCase().contains(query.toLowerCase())) {

                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(BorderFactory.createTitledBorder(p.getName()));

                String info = "<html><center>" + p.getName() + "<br>Price: $" + p.getPrice() + "<br>Stock: " + p.getStock() + "</center></html>";
                JLabel infoLbl = new JLabel(info, SwingConstants.CENTER);

                ImageIcon icon = resizeImage(p.getImagePath());
                if (icon != null) {
                    infoLbl.setIcon(icon);
                    infoLbl.setHorizontalTextPosition(JLabel.CENTER);
                    infoLbl.setVerticalTextPosition(JLabel.BOTTOM);
                }

                JButton addBtn = new JButton("Add to Cart");
                if (p.getStock() <= 0) {
                    addBtn.setEnabled(false);
                    addBtn.setText("Out of Stock");
                }

                addBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(p.getStock() > 0) {
                            myCart.addProduct(p);
                            JOptionPane.showMessageDialog(null, p.getName() + " added to cart!");
                        }
                    }
                });

                card.add(infoLbl, BorderLayout.CENTER);
                card.add(addBtn, BorderLayout.SOUTH);
                gridPanel.add(card);
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void initCartDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        ArrayList<Product> items = myCart.getItems();
        for (int i = 0; i < items.size(); i++) {
            listModel.addElement((i+1) + ". " + items.get(i).getName() + " - $" + items.get(i).getPrice());
        }

        JList<String> jList = new JList<>(listModel);
        panel.add(new JScrollPane(jList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));

        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { initCustomerPanel(); }
        });

        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = jList.getSelectedIndex();
                if (index != -1) {

                    myCart.getItems().remove(index);
                    initCartDetailPanel();
                }
            }
        });

        JButton payBtn = new JButton("Pay $" + myCart.calculateTotal());
        payBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });

        bottomPanel.add(backBtn);
        bottomPanel.add(removeBtn);
        bottomPanel.add(payBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "CART");
        cardLayout.show(mainPanel, "CART");
    }

    private void processPayment() {
        Customer c = (Customer) currentUser;
        double total = myCart.calculateTotal();

        if(c.getBalance() >= total) {
            c.deductBalance(total);

            ArrayList<Product> cartItems = myCart.getItems();


            for (int i = 0; i < cartItems.size(); i++) {
                Product pCart = cartItems.get(i);


                for(int j=0; j<allProducts.size(); j++) {
                    if(allProducts.get(j).getId().equals(pCart.getId())) {
                        int currentStock = allProducts.get(j).getStock();
                        allProducts.get(j).setStock(currentStock - 1);
                        break;
                    }
                }
            }

            storage.save(allProducts);
            myCart.getItems().clear();

            JOptionPane.showMessageDialog(null, "Purchase Successful!\nNew Balance: $" + c.getBalance());
            initCustomerPanel();
        } else {
            JOptionPane.showMessageDialog(null, "Insufficient Balance!");
        }
    }

    private void initAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] cols = {"ID", "Name", "Price", "Stock", "Category", "Image"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        fillAdminTable(model);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Manage Products"));

        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField();
        JTextField txtCat = new JTextField();

        JTextField txtImgName = new JTextField("no_image.jpg");
        txtImgName.setEditable(false);
        JButton btnSelectImg = new JButton("Select Image...");

        formPanel.add(new JLabel("ID:")); formPanel.add(txtId);
        formPanel.add(new JLabel("Name:")); formPanel.add(txtName);
        formPanel.add(new JLabel("Price:")); formPanel.add(txtPrice);
        formPanel.add(new JLabel("Stock:")); formPanel.add(txtStock);
        formPanel.add(new JLabel("Category:")); formPanel.add(txtCat);

        formPanel.add(new JLabel("Image:"));
        JPanel imgPanel = new JPanel(new BorderLayout());
        imgPanel.add(txtImgName, BorderLayout.CENTER);
        imgPanel.add(btnSelectImg, BorderLayout.EAST);
        formPanel.add(imgPanel);

        JButton addBtn = new JButton("Add New");
        JButton updateBtn = new JButton("Update Selected");
        JButton delBtn = new JButton("Delete Selected");
        JButton logoutBtn = new JButton("Logout");

        formPanel.add(addBtn);
        formPanel.add(updateBtn);
        formPanel.add(delBtn);
        formPanel.add(logoutBtn);

        panel.add(formPanel, BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if(row != -1) {
                    txtId.setText(model.getValueAt(row, 0).toString());
                    txtName.setText(model.getValueAt(row, 1).toString());
                    txtPrice.setText(model.getValueAt(row, 2).toString());
                    txtStock.setText(model.getValueAt(row, 3).toString());
                    txtCat.setText(model.getValueAt(row, 4).toString());
                    txtImgName.setText(model.getValueAt(row, 5).toString());
                    txtId.setEditable(false);
                }
            }
        });

        btnSelectImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    txtImgName.setText(selectedFile.getName());
                }
            }
        });

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    txtId.setEditable(true);
                    Product p = new Product(
                            txtId.getText(), txtName.getText(),
                            Double.parseDouble(txtPrice.getText()),
                            Integer.parseInt(txtStock.getText()),
                            txtCat.getText(), txtImgName.getText()
                    );
                    allProducts.add(p);
                    storage.save(allProducts);
                    fillAdminTable(model);
                    JOptionPane.showMessageDialog(null, "Added!");
                    clearFields(txtId, txtName, txtPrice, txtStock, txtCat, txtImgName);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error! Check inputs.");
                }
            }
        });

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String targetId = txtId.getText();
                    boolean found = false;

                    for(int i = 0; i < allProducts.size(); i++) {
                        Product p = allProducts.get(i);
                        if(p.getId().equals(targetId)) {
                            p.setName(txtName.getText());
                            p.setPrice(Double.parseDouble(txtPrice.getText()));
                            p.setStock(Integer.parseInt(txtStock.getText()));
                            p.setCategory(txtCat.getText());
                            p.setImagePath(txtImgName.getText());
                            found = true;
                            break;
                        }
                    }
                    if(found) {
                        storage.save(allProducts);
                        fillAdminTable(model);
                        JOptionPane.showMessageDialog(null, "Updated!");
                        clearFields(txtId, txtName, txtPrice, txtStock, txtCat, txtImgName);
                        txtId.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Product ID not found (Select row first)!");
                    }
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error updating!");
                }
            }
        });

        delBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String id = model.getValueAt(row, 0).toString();

                    for (int i = 0; i < allProducts.size(); i++) {
                        if (allProducts.get(i).getId().equals(id)) {
                            allProducts.remove(i);
                            break;
                        }
                    }
                    storage.save(allProducts);
                    fillAdminTable(model);
                    clearFields(txtId, txtName, txtPrice, txtStock, txtCat, txtImgName);
                    txtId.setEditable(true);
                    JOptionPane.showMessageDialog(null, "Deleted!");
                } else {
                    JOptionPane.showMessageDialog(null, "Select a row first!");
                }
            }
        });

        logoutBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainPanel, "LOGIN");
            }
        });

        mainPanel.add(panel, "ADMIN");
        cardLayout.show(mainPanel, "ADMIN");
    }

    private void fillAdminTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            model.addRow(new Object[]{
                    p.getId(), p.getName(), p.getPrice(), p.getStock(), p.getCategory(), p.getImagePath()
            });
        }
    }

    private void clearFields(JTextField... fields) {
        for(int i = 0; i < fields.length; i++) {
            fields[i].setText("");
        }
        fields[fields.length-1].setText("no_image.jpg");
    }

    private ImageIcon resizeImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) return null;
        File f = new File(imagePath);
        if(!f.exists()) return null;

        ImageIcon myIcon = new ImageIcon(imagePath);
        if (myIcon.getIconWidth() == -1) return null;

        Image img = myIcon.getImage();
        Image newImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }
}
