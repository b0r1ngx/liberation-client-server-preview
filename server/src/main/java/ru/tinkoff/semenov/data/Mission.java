package ru.tinkoff.semenov.data;

import java.util.List;

public enum Mission {
    baseMobilisation(
            "Мобилизация базы",
            "Назовите три локации, одна из которых - ваша база. В свой следующий ход перед шагом 2 вы можете забрать карту базы в руку и положить любую новую базу."
    );

    private String name;
    private String description;
    private List<Object> cost;

    Mission(String name, String description) {
        this.name = name;
        this.description = description;
        this.cost = List.of();
    }
}
