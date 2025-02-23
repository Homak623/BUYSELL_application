package buysell.dao.get;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetProductDto {
    private Long id;
    private String title;
    private String description;
    private Integer price;
    private String city;
    private String author;
}
