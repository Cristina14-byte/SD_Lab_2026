package com.chris.sd_assignment1.model.export;

import com.chris.sd_assignment1.model.entities.Item;
import com.google.gson.*;

import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonExportStrategy implements ExportStrategy {
    @Override
    public void export(List<Item> items, String filePath) throws Exception {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                    @Override
                    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                })
                .setPrettyPrinting()
                .create();

        try (FileWriter writer = new FileWriter(filePath + ".json")) {
            gson.toJson(items, writer);
        }
    }
}