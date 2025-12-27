package in.udaykumar.foodApp.IO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponse {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Double price;
    private String category;
}
