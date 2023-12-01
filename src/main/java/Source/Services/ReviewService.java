package Source.Services;

import Source.Models.Review;
import Source.Repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    public Review save(Review review){
        return reviewRepository.save(review);
    }

    public List<Review> findAll(){
        return reviewRepository.findAll();
    }

    public Review findById(int id){
        return reviewRepository.findById(id).orElse(null);
    }

    public void update(Review review){
        Review r = reviewRepository.findById(review.getReviewId())
                .orElse(null);
        if(r != null){
            r.setRating(review.getRating());
            r.setUser(review.getUser());
            r.setMovie(review.getMovie());

            reviewRepository.save(r);
        }
    }

    public void delete(Review review){
        reviewRepository.delete(review);
    }
}
