package com.etms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "office_location")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeLocation {
    @Id
    private String id;
    private double latitude;
    private double longitude;
    private double radius; // in meters
}
