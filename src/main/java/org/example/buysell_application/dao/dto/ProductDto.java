package org.example.buysell_application.dao.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private String title;
    private String description;
    private Integer price;
    private String city;
    private String author;
}


