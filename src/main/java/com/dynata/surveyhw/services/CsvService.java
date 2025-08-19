package com.dynata.surveyhw.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.List;

@Service
public class CsvService {

    public <T> Mono<List<T>> readFromCsv(FilePart filePart, Class<T> clazz) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    try (InputStream is = dataBuffer.asInputStream(true)) {
                        CsvMapper mapper = new CsvMapper();
                        CsvSchema schema = CsvSchema.emptySchema().withHeader();
                        MappingIterator<T> iterator = mapper.readerFor(clazz)
                                .with(schema)
                                .readValues(is);
                        return iterator.readAll();
                    } catch (Exception e) {
                        throw new RuntimeException("Error at reading " + clazz + " CSV file: " + e.getMessage(), e);
                    }
                });
    }
}
