package com.dynata.surveyhw.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ZeroOneBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();
        if ("1".equals(text)) {
            return true;
        }
        if ("0".equals(text)) {
            return false;
        }
        return null; // or throw an exception: throw new IOException("Invalid boolean value: " + text);
    }
}
