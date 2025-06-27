package com.example.smoking.platform.service;

import com.example.smoking.platform.model.Rating;
import com.example.smoking.platform.model.User;
import com.example.smoking.platform.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public List<Rating> getAllRatings() {
        return ratingRepository.findAllByOrderBySubmittedAtDesc();
    }

    public boolean hasUserRated(User user) {
        return ratingRepository.findByUser(user).isPresent();
    }

    public boolean submitRating(User user, int stars, String comment) {
        if (hasUserRated(user)) return false;

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setStars(stars);
        rating.setComment(comment);
        ratingRepository.save(rating);
        return true;
    }

    public double calculateAverageStars() {
        List<Rating> ratings = ratingRepository.findAll();
        if (ratings.isEmpty()) return 0.0;

        double total = ratings.stream().mapToInt(Rating::getStars).sum();
        return total / ratings.size();
    }
}
