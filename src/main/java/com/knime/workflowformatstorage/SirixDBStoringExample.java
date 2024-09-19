package com.knime.workflowformatstorage;



import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sirix.access.DatabaseConfiguration;
import io.sirix.access.Databases;
import io.sirix.access.ResourceConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sirix.access.DatabaseConfiguration;
import io.sirix.access.Databases;
import io.sirix.access.ResourceConfiguration;
import io.sirix.api.json.JsonNodeTrx;
import io.sirix.api.json.JsonResourceSession;
import io.sirix.service.json.shredder.JsonShredder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Nicola Tesser, KNIME GmbH, Konstanz, Germany
 */
public class SirixDBStoringExample {
    public static void main(String[] args) throws Exception {
        // Path to SirixDB database
        Path databasePath = Paths.get("sirixdb", "mydatabase");


        // Create a new DatabaseConfiguration object
        var dbConfig = new DatabaseConfiguration(databasePath);

        // Create or open a SirixDB JSON database instance
        if (!Databases.existsDatabase(databasePath)) {
            Databases.createJsonDatabase(dbConfig);
        }

        // Open the database
        try (var database = Databases.openJsonDatabase(databasePath)) {

            // Create a new resource if it doesn't exist
            if (!database.existsResource("resource")) {
                database.createResource(ResourceConfiguration.newBuilder("resource").build());
            }

            // Open the resource manager for accessing the resource
            try (JsonResourceSession session = database.beginResourceSession("resource")) {

                final var wtx = session.beginNodeTrx();
                final var path = Paths.get("workflow1.json");
                final var fis = new FileInputStream("workflow1.json");

                    // Import a JSON-document.
                    wtx.insertSubtreeAsFirstChild(JsonShredder.createFileReader(path));

                    // Commit and persist the changes.
                    wtx.commit();


            }
        }
    }

    // Recursive method to insert a Jackson JsonNode into SirixDB
    private static void insertJsonNode(JsonNode node, JsonNodeTrx wtx) {
        if (node.isObject()) {
            // Insert an object node
            wtx.insertObjectAsFirstChild();
            wtx.moveToFirstChild();
            node.fieldNames().forEachRemaining(fieldName -> {
                JsonNode childNode = node.get(fieldName);
                // Insert the field name and handle the value
                wtx.setObjectKeyName(fieldName);
                insertJsonNode(childNode, wtx);
                wtx.moveToParent();  // Move back to the parent object
            });
        } else if (node.isArray()) {
            // Insert an array node
            wtx.insertArrayAsFirstChild();
            wtx.moveToFirstChild();
            for (JsonNode arrayElement : node) {
                insertJsonNode(arrayElement, wtx);
                wtx.moveToRightSibling();
            }
            wtx.moveToParent();  // Move back to the parent array
        } else if (node.isTextual()) {
            // Insert a string value
            wtx.insertStringValueAsFirstChild(node.textValue());
        } else if (node.isNumber()) {
            // Insert a number value
            wtx.insertNumberValueAsFirstChild(node.numberValue());
        } else if (node.isBoolean()) {
            // Insert a boolean value
            wtx.insertBooleanValueAsFirstChild(node.booleanValue());
        } else if (node.isNull()) {
            // Insert a null value
            wtx.insertNullValueAsFirstChild();
        }
    }
}



