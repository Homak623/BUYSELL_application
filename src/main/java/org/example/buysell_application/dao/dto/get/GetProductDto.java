package org.example.buysell_application.dao.dto.get;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductDto {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private String city;
    private String author;
}
