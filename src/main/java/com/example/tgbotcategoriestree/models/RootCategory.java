package com.example.tgbotcategoriestree.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class RootCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "root", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChildCategory> childCategories = new ArrayList<>();
}
