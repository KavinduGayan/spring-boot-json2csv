package com.ezittech.json2csv.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

@RestController
public class MainController {

    @GetMapping("/test")
    public void test() {
        String json = "[ {\n" +
                "  \"item\" : \"No. 9 Sprockets\",\n" +
                "  \"quantity\" : 12,\n" +
                "  \"unitPrice\" : 1.23\n" +
                "}, {\n" +
                "  \"item\" : \"Widget (10mm)\",\n" +
                "  \"quantity\" : 4,\n" +
                "  \"unitPrice\" : 3.45\n" +
                "} ]";
        JsonNode jsonTree = null;
        try {
            jsonTree = new ObjectMapper().readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
        JsonNode firstObject = jsonTree.elements().next();
        firstObject.fieldNames().forEachRemaining(fieldName -> {
            csvSchemaBuilder.addColumn(fieldName);
        });
        CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        try {
            csvMapper.writerFor(JsonNode.class)
                    .with(csvSchema)
                    .writeValue(new File("src/main/resources/orderLines.csv"), jsonTree);
            /*File file = new File("src/main/resources/orderLines.csv");
            String zipFileName = file.getName().concat(".zip");

            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zos.putNextEntry(new ZipEntry(file.getName()));

            byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/"));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            zos.close();*/
            //helper.addAttachment("test.zip", new ByteArrayResource(bout.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
