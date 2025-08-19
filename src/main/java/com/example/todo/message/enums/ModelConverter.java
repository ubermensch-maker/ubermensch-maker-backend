package com.example.todo.message.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ModelConverter implements AttributeConverter<Model, String> {

  @Override
  public String convertToDatabaseColumn(Model model) {
    if (model == null) {
      return null;
    }
    return model.getValue();
  }

  @Override
  public Model convertToEntityAttribute(String value) {
    if (value == null) {
      return null;
    }
    return Model.fromValue(value);
  }
}
