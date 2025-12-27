package in.udaykumar.foodApp.service;

import in.udaykumar.foodApp.Entity.FoodEntity;
import in.udaykumar.foodApp.IO.FoodRequest;
import in.udaykumar.foodApp.IO.FoodResponse;
import in.udaykumar.foodApp.repo.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService{

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    @Autowired
    private FoodRepository foodRepository;


    @Override
    public String uploadFile(MultipartFile file) {
        String fileNameExtension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")+1);
        String key = UUID.randomUUID().toString() + "." +  fileNameExtension;
        try{
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .acl("public-read")
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            if(response.sdkHttpResponse().isSuccessful()){
                return "https://" + bucketName + ".s3.amazonaws.com/"+key;
            }
            else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, " file upload Failed  ");
            }
        }catch (IOException e ){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occured while loading the file");
        }
    }

    @Override
    public FoodResponse addFood(FoodRequest request, MultipartFile file) {
        FoodEntity newFoodEntity = convertToEntity(request);
        String imageUrl = uploadFile(file);
        newFoodEntity.setImageUrl(imageUrl);
        FoodEntity foodEntity = foodRepository.save(newFoodEntity);
        return convertToResponse(foodEntity);
    }

    @Override
    public List<FoodResponse> readFoods() {
        List<FoodEntity> databaseEntries =  foodRepository.findAll();
        return databaseEntries.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public FoodResponse readFood(String id) {
       FoodEntity foodEntity = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food item not Found for the id : " + id));
       return convertToResponse(foodEntity);

    }

    @Override
    public boolean deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
        return true;
    }

    @Override
    public void deleteFood(String id) {
        FoodResponse foodResponse = readFood(id);

        //get the image url of image in s3 from database
        String imageUrl = foodResponse.getImageUrl();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/')+1);
        boolean isFileDeleted = deleteFile(fileName);

        if(isFileDeleted){
            foodRepository.deleteById(foodResponse.getId());
        }





    }

    private FoodEntity convertToEntity(FoodRequest request){
        return FoodEntity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
    }

    private FoodResponse convertToResponse(FoodEntity entity){
       return FoodResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrl())
                .build();
    }



}
