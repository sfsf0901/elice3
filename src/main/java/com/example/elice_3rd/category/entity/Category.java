package com.example.elice_3rd.category.entity;

import com.example.elice_3rd.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private Boolean isDeleted;

    public static Category create(String name, String description) {
        Category category = new Category();
        category.name = name;
        category.description = description;
        return category;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void delete() {
        this.isDeleted = true;
    }
}

