package com.cottage.reservation.config;

import com.cottage.reservation.entity.Room;
import com.cottage.reservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize rooms if they don't exist
        if (roomRepository.count() == 0) {
            Room bedroom1 = new Room(
                    "Bedroom 1",
                    "Cozy bedroom with queen bed and garden view. Perfect for couples.",
                    BigDecimal.ZERO,
                    2,
                    Room.RoomType.BEDROOM_1
            );

            Room bedroom2 = new Room(
                    "Bedroom 2", 
                    "Spacious bedroom with twin beds and lake view. Great for friends or family.",
                    BigDecimal.ZERO,
                    2,
                    Room.RoomType.BEDROOM_2
            );

            Room bedroom3 = new Room(
                    "Bedroom 3",
                    "Master bedroom with king bed, ensuite bathroom, and private balcony.",
                    BigDecimal.ZERO,
                    2,
                    Room.RoomType.BEDROOM_3
            );

            Room entireCottage = new Room(
                    "Entire Cottage",
                    "Reserve the entire 3-bedroom cottage with full kitchen, living room, and outdoor deck. Perfect for groups and families.",
                    BigDecimal.ZERO,
                    6,
                    Room.RoomType.ENTIRE_COTTAGE
            );

            roomRepository.save(bedroom1);
            roomRepository.save(bedroom2);
            roomRepository.save(bedroom3);
            roomRepository.save(entireCottage);

            System.out.println("Initial room data loaded successfully!");
        }
    }
}
