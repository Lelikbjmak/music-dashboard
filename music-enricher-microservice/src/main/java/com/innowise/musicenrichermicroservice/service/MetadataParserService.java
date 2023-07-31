package com.innowise.musicenrichermicroservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MetadataParserService<T> {

    T parse(String json) throws JsonProcessingException;

}
