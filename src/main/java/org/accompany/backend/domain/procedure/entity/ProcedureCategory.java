package org.accompany.backend.domain.procedure.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "procedure_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcedureCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long procedureCategoryId;

    @Column(nullable = false, length = 100)
    private String categoryName;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "procedureCategory")
    private List<Procedure> procedures = new ArrayList<>();

    @Column
    private String color;

    @Column
    private String icon;

    @Builder
    private ProcedureCategory(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

}
