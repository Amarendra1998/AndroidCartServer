package com.example.computer.androidcartserver.Model;

public class Food {
    private String name,image,price, discount, description,menuId;

    public Food() {
    }

    public Food(String name, String image, String price, String discount, String description, String menuId) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.discount = discount;
        this.description = description;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
