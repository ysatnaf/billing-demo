package com.xforceplus.billingdemo.web.rest;

import com.xforceplus.billingdemo.BillingDemoApp;
import com.xforceplus.billingdemo.domain.ProductPlan;
import com.xforceplus.billingdemo.repository.ProductPlanRepository;
import com.xforceplus.billingdemo.repository.search.ProductPlanSearchRepository;
import com.xforceplus.billingdemo.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static com.xforceplus.billingdemo.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ProductPlanResource} REST controller.
 */
@EmbeddedKafka
@SpringBootTest(classes = BillingDemoApp.class)
public class ProductPlanResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private ProductPlanRepository productPlanRepository;

    /**
     * This repository is mocked in the com.xforceplus.billingdemo.repository.search test package.
     *
     * @see com.xforceplus.billingdemo.repository.search.ProductPlanSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProductPlanSearchRepository mockProductPlanSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restProductPlanMockMvc;

    private ProductPlan productPlan;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProductPlanResource productPlanResource = new ProductPlanResource(productPlanRepository, mockProductPlanSearchRepository);
        this.restProductPlanMockMvc = MockMvcBuilders.standaloneSetup(productPlanResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductPlan createEntity(EntityManager em) {
        ProductPlan productPlan = new ProductPlan()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME);
        return productPlan;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductPlan createUpdatedEntity(EntityManager em) {
        ProductPlan productPlan = new ProductPlan()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME);
        return productPlan;
    }

    @BeforeEach
    public void initTest() {
        productPlan = createEntity(em);
    }

    @Test
    @Transactional
    public void createProductPlan() throws Exception {
        int databaseSizeBeforeCreate = productPlanRepository.findAll().size();

        // Create the ProductPlan
        restProductPlanMockMvc.perform(post("/api/product-plans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productPlan)))
            .andExpect(status().isCreated());

        // Validate the ProductPlan in the database
        List<ProductPlan> productPlanList = productPlanRepository.findAll();
        assertThat(productPlanList).hasSize(databaseSizeBeforeCreate + 1);
        ProductPlan testProductPlan = productPlanList.get(productPlanList.size() - 1);
        assertThat(testProductPlan.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testProductPlan.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the ProductPlan in Elasticsearch
        verify(mockProductPlanSearchRepository, times(1)).save(testProductPlan);
    }

    @Test
    @Transactional
    public void createProductPlanWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = productPlanRepository.findAll().size();

        // Create the ProductPlan with an existing ID
        productPlan.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductPlanMockMvc.perform(post("/api/product-plans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productPlan)))
            .andExpect(status().isBadRequest());

        // Validate the ProductPlan in the database
        List<ProductPlan> productPlanList = productPlanRepository.findAll();
        assertThat(productPlanList).hasSize(databaseSizeBeforeCreate);

        // Validate the ProductPlan in Elasticsearch
        verify(mockProductPlanSearchRepository, times(0)).save(productPlan);
    }


    @Test
    @Transactional
    public void getAllProductPlans() throws Exception {
        // Initialize the database
        productPlanRepository.saveAndFlush(productPlan);

        // Get all the productPlanList
        restProductPlanMockMvc.perform(get("/api/product-plans?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
    
    @Test
    @Transactional
    public void getProductPlan() throws Exception {
        // Initialize the database
        productPlanRepository.saveAndFlush(productPlan);

        // Get the productPlan
        restProductPlanMockMvc.perform(get("/api/product-plans/{id}", productPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(productPlan.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProductPlan() throws Exception {
        // Get the productPlan
        restProductPlanMockMvc.perform(get("/api/product-plans/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductPlan() throws Exception {
        // Initialize the database
        productPlanRepository.saveAndFlush(productPlan);

        int databaseSizeBeforeUpdate = productPlanRepository.findAll().size();

        // Update the productPlan
        ProductPlan updatedProductPlan = productPlanRepository.findById(productPlan.getId()).get();
        // Disconnect from session so that the updates on updatedProductPlan are not directly saved in db
        em.detach(updatedProductPlan);
        updatedProductPlan
            .code(UPDATED_CODE)
            .name(UPDATED_NAME);

        restProductPlanMockMvc.perform(put("/api/product-plans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProductPlan)))
            .andExpect(status().isOk());

        // Validate the ProductPlan in the database
        List<ProductPlan> productPlanList = productPlanRepository.findAll();
        assertThat(productPlanList).hasSize(databaseSizeBeforeUpdate);
        ProductPlan testProductPlan = productPlanList.get(productPlanList.size() - 1);
        assertThat(testProductPlan.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testProductPlan.getName()).isEqualTo(UPDATED_NAME);

        // Validate the ProductPlan in Elasticsearch
        verify(mockProductPlanSearchRepository, times(1)).save(testProductPlan);
    }

    @Test
    @Transactional
    public void updateNonExistingProductPlan() throws Exception {
        int databaseSizeBeforeUpdate = productPlanRepository.findAll().size();

        // Create the ProductPlan

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductPlanMockMvc.perform(put("/api/product-plans")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(productPlan)))
            .andExpect(status().isBadRequest());

        // Validate the ProductPlan in the database
        List<ProductPlan> productPlanList = productPlanRepository.findAll();
        assertThat(productPlanList).hasSize(databaseSizeBeforeUpdate);

        // Validate the ProductPlan in Elasticsearch
        verify(mockProductPlanSearchRepository, times(0)).save(productPlan);
    }

    @Test
    @Transactional
    public void deleteProductPlan() throws Exception {
        // Initialize the database
        productPlanRepository.saveAndFlush(productPlan);

        int databaseSizeBeforeDelete = productPlanRepository.findAll().size();

        // Delete the productPlan
        restProductPlanMockMvc.perform(delete("/api/product-plans/{id}", productPlan.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ProductPlan> productPlanList = productPlanRepository.findAll();
        assertThat(productPlanList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the ProductPlan in Elasticsearch
        verify(mockProductPlanSearchRepository, times(1)).deleteById(productPlan.getId());
    }

    @Test
    @Transactional
    public void searchProductPlan() throws Exception {
        // Initialize the database
        productPlanRepository.saveAndFlush(productPlan);
        when(mockProductPlanSearchRepository.search(queryStringQuery("id:" + productPlan.getId())))
            .thenReturn(Collections.singletonList(productPlan));
        // Search the productPlan
        restProductPlanMockMvc.perform(get("/api/_search/product-plans?query=id:" + productPlan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productPlan.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductPlan.class);
        ProductPlan productPlan1 = new ProductPlan();
        productPlan1.setId(1L);
        ProductPlan productPlan2 = new ProductPlan();
        productPlan2.setId(productPlan1.getId());
        assertThat(productPlan1).isEqualTo(productPlan2);
        productPlan2.setId(2L);
        assertThat(productPlan1).isNotEqualTo(productPlan2);
        productPlan1.setId(null);
        assertThat(productPlan1).isNotEqualTo(productPlan2);
    }
}
