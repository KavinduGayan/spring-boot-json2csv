package com.ezittech.json2csv;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String fileName="abc3.csv";
        boolean a=uploadFile(fileName);
        if (a) {
            getFileAsInputStream(fileName);
        }
    }

    private static void getFileAsInputStream(String fileName) {
        try {
            Path path = Paths.get(fileName);
            InputStream fileStream = Files.newInputStream(path, StandardOpenOption.DELETE_ON_CLOSE);
            DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                    MediaType.TEXT_PLAIN_VALUE, true, fileName);
            try (OutputStream os = fileItem.getOutputStream()) {
                IOUtils.copy(fileStream, os);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid file: " + e, e);
            }
            finally {
                fileStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*InputStream resource = new ClassPathResource(
                "data/employees.dat").getInputStream();*/
    }

    private static boolean uploadFile(String fileName) {
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
        OutputStream out = null;
        try {
            jsonTree = new ObjectMapper().readTree(json);
            out = new FileOutputStream(fileName);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
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
                    .writeValue(out, jsonTree);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
