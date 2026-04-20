package com.chris.sd_assignment1.model.export;

import com.chris.sd_assignment1.model.entities.Item;
import java.util.List;

public class ExportContext {
    private ExportStrategy strategy;

    public void setStrategy(ExportStrategy strategy) {
        this.strategy = strategy;
    }

    public void executeExport(List<Item> items, String filePath) throws Exception {
        if (strategy == null) {
            throw new IllegalStateException("Export strategy not set");
        }
        strategy.export(items, filePath);
    }
}