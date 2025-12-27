package in.udaykumar.foodApp.repo;

import in.udaykumar.foodApp.Entity.FoodEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FoodRepository extends MongoRepository<FoodEntity, String> {

}
