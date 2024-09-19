package com.knime.workflowformatstorage;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;


public class ApplyPatch {


    public static void main(String[] args) {
        try {
            // Step 1: Initialize ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Step 2: Read the original JSON document from a file
            File jsonFile = new File("document.json");
            JsonNode originalJson = objectMapper.readTree(jsonFile);

            // Step 3: Read the JSON Patch from a file
            File patchFile = new File("patch.json");
            JsonPatch patch = objectMapper.readValue(patchFile, JsonPatch.class);

            // Step 4: Apply the JSON Patch to the original document
            JsonNode patchedJson = patch.apply(originalJson);

            // Step 5: Output the patched JSON document
            System.out.println("Patched JSON: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(patchedJson));

        } catch (IOException | JsonPatchException e) {
            e.printStackTrace();
        }
    }
}