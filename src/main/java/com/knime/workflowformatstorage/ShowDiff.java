package com.knime.workflowformatstorage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.diff.JsonDiff;

import java.io.File;
import java.io.IOException;


public class ShowDiff {


    public static void main(String[] args) {
        try {
            // Step 1: Initialize ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Step 2: Read the two JSON files (before and after)
            File file1 = new File("workflow1.json");// Original JSON
            File file2 = new File("workflow2.json");  // Modified JSON

            JsonNode originalJson = objectMapper.readTree(file1);
            JsonNode modifiedJson = objectMapper.readTree(file2);

            // Step 3: Generate the JSON Patch (diff)
            JsonNode jsonPatch = JsonDiff.asJson(originalJson, modifiedJson);

            // Step 4: Output the patch to show the diffs
            System.out.println("JSON Patch (Diff): " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonPatch));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}