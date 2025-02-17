package tennnisshop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tennnisshop.entity.Analytics;
import tennnisshop.repository.AnalyticsRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnalyticsService {

    private AnalyticsRepository analyticsRepository;


    @Autowired
    public void setOrderRepository(AnalyticsRepository analyticsRepository) {this.analyticsRepository = analyticsRepository;}


    public Optional<Analytics> getAnalyticsForProduct(Long productId) {
        return analyticsRepository.findByProductId(productId);
    }

    public List<Analytics> getAllAnalytics() {
        return analyticsRepository.findAll();
    }

    public void updateAnalyticsAfterSale(Long productId, Integer quantitySold, Double revenue) {
        Optional<Analytics> existingAnalytics = analyticsRepository.findByProductId(productId);

        if (existingAnalytics.isEmpty()) {
            analyticsRepository.createAnalytics(productId);
        }

        analyticsRepository.updateAnalytics(productId, quantitySold, revenue);
    }

}
