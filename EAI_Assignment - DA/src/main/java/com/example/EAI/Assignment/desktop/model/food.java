package com.example.EAI.Assignment.desktop.model;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class food {
    private String idMeal;
    private String strMeal;
    private String strMealAlternate;
    private String strCategory;
    private String strArea;
    private String strInstructions;
    private String strMealThumb;
    private String strTags;
    private String strYoutube;
    private List<String> ingredients;
    private List<String> measures;
    private String strSource;
    private String strImageSource;
    private String strCreativeCommonsConfirmed;
    private String dateModified;
    private double price;

    // Default constructor
    public food() {
        this.ingredients = new ArrayList<>();
        this.measures = new ArrayList<>();
        this.price = 0.0;
    }

    // Constructor with parameters
    public food(String idMeal, String strMeal, String strCategory, String strArea, String strInstructions, String strMealThumb, double price) {
        this.idMeal = idMeal;
        this.strMeal = strMeal;
        this.strCategory = strCategory;
        this.strArea = strArea;
        this.strInstructions = strInstructions;
        this.strMealThumb = strMealThumb;
        this.price = price;
        this.ingredients = new ArrayList<>();
        this.measures = new ArrayList<>();
    }

    // Getter and setter for price (in case Lombok doesn't work)
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter for name (using strMeal)
    public String getName() {
        return strMeal;
    }

    @Override
    public String toString() {
        return "Food{" +
                "idMeal='" + idMeal + '\'' +
                ", strMeal='" + strMeal + '\'' +
                ", strCategory='" + strCategory + '\'' +
                ", strArea='" + strArea + '\'' +
                ", price='" + price + '\'' +
                ", ingredients=" + ingredients +
                ", measures=" + measures +
                '}';
    }
}
