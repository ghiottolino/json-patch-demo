package com.knime.workflowformatstorage;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkflowStorageCouchbaseImplExample {

    public static void main(String[] args) throws IOException, CouchbaseLiteException {

        WorkflowStorageCouchbaseImpl workflowStorage = new WorkflowStorageCouchbaseImpl("knime");

        //Read file 1 first revision
        ObjectMapper objectMapper = new ObjectMapper();
        File file1 = new File("workflow1.json");// Original JSON
        JsonNode jsonNode1 = objectMapper.readTree(file1);
        workflowStorage.storeWorkflow("*123", objectMapper.convertValue(jsonNode1, Map.class));

        Map<String, Object> workflow = workflowStorage.getWorkflow("*123");
        System.out.println("Workflow stored with ID: *123 and workflow content: " + workflow);
    }
}
