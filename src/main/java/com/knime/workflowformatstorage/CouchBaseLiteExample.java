package com.knime.workflowformatstorage;

import com.couchbase.lite.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CouchBaseLiteExample {

    public static void main(String[] args) throws IOException {
        // Initialize the Couchbase Lite system
        CouchbaseLite.init();

        // Create or open a database
        DatabaseConfiguration config = new DatabaseConfiguration();
        try (Database database = new Database("mydb", config)) {


            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File("workflow1.json");// Original JSON
            JsonNode jsonNode = objectMapper.readTree(file);
            Map<String, Object> jsonDocument = jsonNodeToMap(jsonNode);


            // Create a new document
            MutableDocument document = new MutableDocument("user:124", jsonDocument);

            // Save the document to the database
            database.save(document);



            System.out.println("Document stored with ID: " + document.getId());

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    // Utility method to convert JsonNode to a Map
    private static Map<String, Object> jsonNodeToMap(JsonNode jsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }
}
