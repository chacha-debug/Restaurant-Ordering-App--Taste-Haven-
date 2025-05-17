/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;

/**
 *
 * @author Admin
 */
public class OrderItem {
    
        String name;
        int quantity;
        double unitPrice;
        PreOrderPanel.MenuItem menuItem;
        

        public OrderItem(String name, int quantity, double unitPrice) {
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public OrderItem(PreOrderPanel.MenuItem menuItem, int quantity){
            this.menuItem = menuItem;
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            return quantity * unitPrice;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }
    
}
