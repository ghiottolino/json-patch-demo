package com.knime.workflowformatstorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkflowStorageCouchbaseImpl {

    private Database database;
    private ObjectMapper objectMapper = new ObjectMapper();


    public WorkflowStorageCouchbaseImpl(String databaseName) throws CouchbaseLiteException {
        CouchbaseLite.init();

        // Create or open a database
        DatabaseConfiguration config = new DatabaseConfiguration();
        this.database = new Database(databaseName, config);
    }

    public void storeWorkflow(String workflowId, Map<String,Object> workflowMap) throws IOException, CouchbaseLiteException {
        // Check if a document with the same ID already exists
        Document existingDocument = database.getDefaultCollection().getDocument(workflowId);
        if (existingDocument==null){
            // Create a new document
            MutableDocument document = new MutableDocument(workflowId,workflowMap);
            addRevisionHistoryInDocument(document);
            // Save the document to the database
            database.getDefaultCollection().save(document);
            System.out.println("Document stored with ID: " + document.getId() + " and document revision:"+document.getRevisionID());
        }
        else {
            // Load existing document
            MutableDocument document = existingDocument.toMutable();
            addRevisionHistoryInDocument(document);
            // Save the document to the database
            database.getDefaultCollection().save(document);
            System.out.println("Document updated with ID: " + document.getId() + " and document revision:"+document.getRevisionID());
        }
    }

    private void addRevisionHistoryInDocument(MutableDocument document){
        // Track revision history manually
        MutableArray history = document.getArray("revisionHistory");
        if (history == null) {
            history = new MutableArray();
        }
        history.addString(document.getRevisionID());  // Track revision ID
        document.setArray("revisionHistory", history);
    }

    public Map<String,Object> getWorkflow(String workflowId) throws CouchbaseLiteException {
        Document document = database.getDefaultCollection().getDocument(workflowId);
        if (document!=null){
            System.out.println("Getting document with ID: " + document.getId() + " and document revision:"+document.getRevisionID());
            return document.toMap();
        }
        else {
            System.out.println("Document with ID: " + workflowId + " not found.");
            return null;
        }
    }


}
