package com.yandex.app.http;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeToInstantTypeAdapter extends TypeAdapter<Instant> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.HH:mm");

    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        if (value != null) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(value, ZoneId.systemDefault());
            out.value(dateTime.format(formatter));
        } else {
            out.nullValue(); // Записываем null в JSON, если значение Instant отсутствует
        }
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        String dateString = in.nextString();
        if (dateString != null && !dateString.isEmpty()) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);
                return dateTime.atZone(ZoneId.systemDefault()).toInstant();
            } catch (DateTimeParseException e) {
                // Возвращаем null, если строка не соответствует формату LocalDateTime
                return null;
            }
        } else {
            return null; // Возвращаем null, если строка пуста или null
        }
    }
}
