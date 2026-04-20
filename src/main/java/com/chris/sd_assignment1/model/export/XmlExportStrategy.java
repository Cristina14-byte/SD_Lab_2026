package com.chris.sd_assignment1.model.export;

import com.chris.sd_assignment1.model.entities.Item;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.util.List;

public class XmlExportStrategy implements ExportStrategy {
    @Override
    public void export(List<Item> items, String filePath) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        xmlMapper.writeValue(new File(filePath + ".xml"), items);
    }
}