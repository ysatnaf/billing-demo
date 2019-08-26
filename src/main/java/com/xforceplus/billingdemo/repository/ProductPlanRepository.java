package com.xforceplus.billingdemo.repository;

import com.xforceplus.billingdemo.domain.ProductPlan;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ProductPlan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductPlanRepository extends JpaRepository<ProductPlan, Long> {

}
