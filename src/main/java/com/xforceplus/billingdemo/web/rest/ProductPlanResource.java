package com.xforceplus.billingdemo.web.rest;

import com.xforceplus.billingdemo.domain.ProductPlan;
import com.xforceplus.billingdemo.repository.ProductPlanRepository;
import com.xforceplus.billingdemo.repository.search.ProductPlanSearchRepository;
import com.xforceplus.billingdemo.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.xforceplus.billingdemo.domain.ProductPlan}.
 */
@RestController
@RequestMapping("/api")
public class ProductPlanResource {

    private final Logger log = LoggerFactory.getLogger(ProductPlanResource.class);

    private static final String ENTITY_NAME = "productPlan";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductPlanRepository productPlanRepository;

    private final ProductPlanSearchRepository productPlanSearchRepository;

    public ProductPlanResource(ProductPlanRepository productPlanRepository, ProductPlanSearchRepository productPlanSearchRepository) {
        this.productPlanRepository = productPlanRepository;
        this.productPlanSearchRepository = productPlanSearchRepository;
    }

    /**
     * {@code POST  /product-plans} : Create a new productPlan.
     *
     * @param productPlan the productPlan to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productPlan, or with status {@code 400 (Bad Request)} if the productPlan has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-plans")
    public ResponseEntity<ProductPlan> createProductPlan(@RequestBody ProductPlan productPlan) throws URISyntaxException {
        log.debug("REST request to save ProductPlan : {}", productPlan);
        if (productPlan.getId() != null) {
            throw new BadRequestAlertException("A new productPlan cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ProductPlan result = productPlanRepository.save(productPlan);
        productPlanSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/product-plans/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /product-plans} : Updates an existing productPlan.
     *
     * @param productPlan the productPlan to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productPlan,
     * or with status {@code 400 (Bad Request)} if the productPlan is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productPlan couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-plans")
    public ResponseEntity<ProductPlan> updateProductPlan(@RequestBody ProductPlan productPlan) throws URISyntaxException {
        log.debug("REST request to update ProductPlan : {}", productPlan);
        if (productPlan.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ProductPlan result = productPlanRepository.save(productPlan);
        productPlanSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, productPlan.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /product-plans} : get all the productPlans.
     *

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productPlans in body.
     */
    @GetMapping("/product-plans")
    public List<ProductPlan> getAllProductPlans() {
        log.debug("REST request to get all ProductPlans");
        return productPlanRepository.findAll();
    }

    /**
     * {@code GET  /product-plans/:id} : get the "id" productPlan.
     *
     * @param id the id of the productPlan to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productPlan, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-plans/{id}")
    public ResponseEntity<ProductPlan> getProductPlan(@PathVariable Long id) {
        log.debug("REST request to get ProductPlan : {}", id);
        Optional<ProductPlan> productPlan = productPlanRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(productPlan);
    }

    /**
     * {@code DELETE  /product-plans/:id} : delete the "id" productPlan.
     *
     * @param id the id of the productPlan to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-plans/{id}")
    public ResponseEntity<Void> deleteProductPlan(@PathVariable Long id) {
        log.debug("REST request to delete ProductPlan : {}", id);
        productPlanRepository.deleteById(id);
        productPlanSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/product-plans?query=:query} : search for the productPlan corresponding
     * to the query.
     *
     * @param query the query of the productPlan search.
     * @return the result of the search.
     */
    @GetMapping("/_search/product-plans")
    public List<ProductPlan> searchProductPlans(@RequestParam String query) {
        log.debug("REST request to search ProductPlans for query {}", query);
        return StreamSupport
            .stream(productPlanSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
