package io.github.emukei555.domain.model.Equipment;

import java.util.Objects;

public record EquipmentCategory(String category) {

    public EquipmentCategory {
        Objects.requireNonNull(category);

        if (category.isBlank()) {
            throw new IllegalArgumentException("EquipmentCategory value cannot be blank");
        }
    }
}
