package com.xforceplus.billingdemo.repository.search;

import com.xforceplus.billingdemo.domain.ProductPlan;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link ProductPlan} entity.
 */
public interface ProductPlanSearchRepository extends ElasticsearchRepository<ProductPlan, Long> {
}
