package com.chris.sd_assignment1.model.export;

import com.chris.sd_assignment1.model.entities.Item;
import java.io.FileWriter;
import java.util.List;

public class CsvExportStrategy implements ExportStrategy {
    @Override
    public void export(List<Item> items, String filePath) throws Exception {
        try (FileWriter writer = new FileWriter(filePath + ".csv")) {
            writer.append("ID,Name,Base Price,Stock Quantity\n");
            for (Item item : items) {
                writer.append(String.valueOf(item.getId())).append(",")
                        .append(item.getName().replace(",", "")).append(",")
                        .append(String.valueOf(item.getBasePrice())).append(",")
                        .append(String.valueOf(item.getStockQuantity())).append("\n");
            }
        }
    }
}