package in.udaykumar.foodApp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.udaykumar.foodApp.IO.FoodRequest;
import in.udaykumar.foodApp.IO.FoodResponse;
import in.udaykumar.foodApp.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/foods")
@AllArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodResponse> addFood(
            @RequestPart("food") String foodString,
            @RequestPart("file") MultipartFile file) {

        ObjectMapper objectMapper = new ObjectMapper();
        FoodRequest request;

        try {
            request = objectMapper.readValue(foodString, FoodRequest.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().build();
        }

        FoodResponse response = foodService.addFood(request, file);
        return ResponseEntity.ok(response);
    }
}

