package com.amalitech.demo.models;
//import com.amalitech.demo.utils.UniqueCategoryName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

//
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    private String name;
    public Category(){}
    public Category(String name){
        this.name = name;
    }
}
