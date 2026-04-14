package main.java.com.ecom;

import com.ecom.model.Product;
import com.ecom.model.User;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner sc             = new Scanner(System.in);
    static UserService    userSvc = new UserService();
    static ProductService prodSvc = new ProductService();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    WELCOME TO E-COMMERCE SYSTEM        ");
        System.out.println("========================================");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. User Registration");
            System.out.println("2. View All Users");
            System.out.println("3. Add Product");
            System.out.println("4. View All Products");
            System.out.println("5. Search Product");
            System.out.println("6. Delete Product");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            int choice = Integer.parseInt(sc.nextLine().trim());

            try {
                switch (choice) {
                    case 1 -> registerUser();
                    case 2 -> viewUsers();
                    case 3 -> addProduct();
                    case 4 -> viewProducts();
                    case 5 -> searchProduct();
                    case 6 -> deleteProduct();
                    case 0 -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static void registerUser() throws Exception {
        System.out.println("\n-- Register User --");
        System.out.print("Name     : "); String name  = sc.nextLine();
        System.out.print("Email    : "); String email = sc.nextLine();
        System.out.print("Password : "); String pass  = sc.nextLine();
        System.out.print("Role (admin/customer): "); String role = sc.nextLine();
        userSvc.register(name, email, pass, role);
    }

    static void viewUsers() throws Exception {
        System.out.println("\n-- All Users --");
        List<User> users = userSvc.getAllUsers();
        if (users.isEmpty()) { System.out.println("No users found."); return; }
        users.forEach(u -> System.out.println(u.getDisplayInfo()));
    }

    static void addProduct() throws Exception {
        System.out.println("\n-- Add Product --");
        System.out.print("Name     : "); String name = sc.nextLine();
        System.out.print("Category : "); String cat  = sc.nextLine();
        System.out.print("Price    : "); double price = Double.parseDouble(sc.nextLine());
        System.out.print("Stock    : "); int stock    = Integer.parseInt(sc.nextLine());
        prodSvc.addProduct(name, cat, price, stock);
    }

    static void viewProducts() throws Exception {
        System.out.println("\n-- All Products --");
        List<Product> products = prodSvc.getAllProducts();
        if (products.isEmpty()) { System.out.println("No products found."); return; }
        products.forEach(p -> System.out.println(p.getDisplayInfo()));
    }

    static void searchProduct() throws Exception {
        System.out.print("\nSearch keyword: ");
        String kw = sc.nextLine();
        List<Product> results = prodSvc.searchProducts(kw);
        if (results.isEmpty()) { System.out.println("No results found."); return; }
        results.forEach(p -> System.out.println(p.getDisplayInfo()));
    }

    static void deleteProduct() throws Exception {
        System.out.print("\nEnter Product ID to delete: ");
        int id = Integer.parseInt(sc.nextLine());
        prodSvc.deleteProduct(id);
    }
}