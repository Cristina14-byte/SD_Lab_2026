package com.chris.sd_assignment1.model.export;

import com.chris.sd_assignment1.model.entities.Item;
import java.util.List;

public interface ExportStrategy {
    void export(List<Item> items, String filePath) throws Exception;
}