package com.dynata.surveyhw.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Page record")
public class PageDto<T> {

    @Schema(description = "content")
    @JsonProperty("content")
    public List<T> content;

    @Schema(description = "pageNumber", example = "1")
    @JsonProperty("pageNumber")
    public int pageNumber;

    @Schema(description = "pageSize", example = "10")
    @JsonProperty("pageSize")
    public int pageSize;

    @Schema(description = "totalElements", example = "3")
    @JsonProperty("totalElements")
    public long totalElements;

    @Schema(description = "numberOfElements", example = "3")
    @JsonProperty("numberOfElements")
    public int numberOfElements;

    public PageDto(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber + 1;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.numberOfElements = content.size();
    }
}