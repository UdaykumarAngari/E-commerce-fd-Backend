package in.udaykumar.foodApp.service;

import in.udaykumar.foodApp.IO.FoodRequest;
import in.udaykumar.foodApp.IO.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FoodService {
    String uploadFile(MultipartFile file);

    FoodResponse addFood(FoodRequest request, MultipartFile file);
}
