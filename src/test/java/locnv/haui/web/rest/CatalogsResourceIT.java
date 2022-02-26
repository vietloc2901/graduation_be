package locnv.haui.web.rest;

import static locnv.haui.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import locnv.haui.IntegrationTest;
import locnv.haui.domain.Catalogs;
import locnv.haui.repository.CatalogsRepository;
import locnv.haui.service.dto.CatalogsDTO;
import locnv.haui.service.mapper.CatalogsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CatalogsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CatalogsResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_SORT_ORDER = 1;
    private static final Integer UPDATED_SORT_ORDER = 2;

    private static final Long DEFAULT_PARENT_ID = 1L;
    private static final Long UPDATED_PARENT_ID = 2L;

    private static final ZonedDateTime DEFAULT_CREATE_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATE_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CREATE_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATE_BY = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_LAST_MODIFIED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_LAST_MODIFIED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/catalogs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CatalogsRepository catalogsRepository;

    @Autowired
    private CatalogsMapper catalogsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCatalogsMockMvc;

    private Catalogs catalogs;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Catalogs createEntity(EntityManager em) {
        Catalogs catalogs = new Catalogs()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .sortOrder(DEFAULT_SORT_ORDER)
            .parentId(DEFAULT_PARENT_ID)
            .createDate(DEFAULT_CREATE_DATE)
            .createBy(DEFAULT_CREATE_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        return catalogs;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Catalogs createUpdatedEntity(EntityManager em) {
        Catalogs catalogs = new Catalogs()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .sortOrder(UPDATED_SORT_ORDER)
            .parentId(UPDATED_PARENT_ID)
            .createDate(UPDATED_CREATE_DATE)
            .createBy(UPDATED_CREATE_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        return catalogs;
    }

    @BeforeEach
    public void initTest() {
        catalogs = createEntity(em);
    }

    @Test
    @Transactional
    void createCatalogs() throws Exception {
        int databaseSizeBeforeCreate = catalogsRepository.findAll().size();
        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);
        restCatalogsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(catalogsDTO)))
            .andExpect(status().isCreated());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeCreate + 1);
        Catalogs testCatalogs = catalogsList.get(catalogsList.size() - 1);
        assertThat(testCatalogs.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCatalogs.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCatalogs.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testCatalogs.getParentId()).isEqualTo(DEFAULT_PARENT_ID);
        assertThat(testCatalogs.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testCatalogs.getCreateBy()).isEqualTo(DEFAULT_CREATE_BY);
        assertThat(testCatalogs.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testCatalogs.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void createCatalogsWithExistingId() throws Exception {
        // Create the Catalogs with an existing ID
        catalogs.setId(1L);
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        int databaseSizeBeforeCreate = catalogsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCatalogsMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(catalogsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCatalogs() throws Exception {
        // Initialize the database
        catalogsRepository.saveAndFlush(catalogs);

        // Get all the catalogsList
        restCatalogsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(catalogs.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].sortOrder").value(hasItem(DEFAULT_SORT_ORDER)))
            .andExpect(jsonPath("$.[*].parentId").value(hasItem(DEFAULT_PARENT_ID.intValue())))
            .andExpect(jsonPath("$.[*].createDate").value(hasItem(sameInstant(DEFAULT_CREATE_DATE))))
            .andExpect(jsonPath("$.[*].createBy").value(hasItem(DEFAULT_CREATE_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(sameInstant(DEFAULT_LAST_MODIFIED_DATE))))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)));
    }

    @Test
    @Transactional
    void getCatalogs() throws Exception {
        // Initialize the database
        catalogsRepository.saveAndFlush(catalogs);

        // Get the catalogs
        restCatalogsMockMvc
            .perform(get(ENTITY_API_URL_ID, catalogs.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(catalogs.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.sortOrder").value(DEFAULT_SORT_ORDER))
            .andExpect(jsonPath("$.parentId").value(DEFAULT_PARENT_ID.intValue()))
            .andExpect(jsonPath("$.createDate").value(sameInstant(DEFAULT_CREATE_DATE)))
            .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(sameInstant(DEFAULT_LAST_MODIFIED_DATE)))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY));
    }

    @Test
    @Transactional
    void getNonExistingCatalogs() throws Exception {
        // Get the catalogs
        restCatalogsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCatalogs() throws Exception {
        // Initialize the database
        catalogsRepository.saveAndFlush(catalogs);

        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();

        // Update the catalogs
        Catalogs updatedCatalogs = catalogsRepository.findById(catalogs.getId()).get();
        // Disconnect from session so that the updates on updatedCatalogs are not directly saved in db
        em.detach(updatedCatalogs);
        updatedCatalogs
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .sortOrder(UPDATED_SORT_ORDER)
            .parentId(UPDATED_PARENT_ID)
            .createDate(UPDATED_CREATE_DATE)
            .createBy(UPDATED_CREATE_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(updatedCatalogs);

        restCatalogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, catalogsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(catalogsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
        Catalogs testCatalogs = catalogsList.get(catalogsList.size() - 1);
        assertThat(testCatalogs.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCatalogs.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCatalogs.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testCatalogs.getParentId()).isEqualTo(UPDATED_PARENT_ID);
        assertThat(testCatalogs.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testCatalogs.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
        assertThat(testCatalogs.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testCatalogs.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void putNonExistingCatalogs() throws Exception {
        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();
        catalogs.setId(count.incrementAndGet());

        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCatalogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, catalogsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(catalogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCatalogs() throws Exception {
        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();
        catalogs.setId(count.incrementAndGet());

        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(catalogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCatalogs() throws Exception {
        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();
        catalogs.setId(count.incrementAndGet());

        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogsMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(catalogsDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCatalogsWithPatch() throws Exception {
        // Initialize the database
        catalogsRepository.saveAndFlush(catalogs);

        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();

        // Update the catalogs using partial update
        Catalogs partialUpdatedCatalogs = new Catalogs();
        partialUpdatedCatalogs.setId(catalogs.getId());

        partialUpdatedCatalogs.parentId(UPDATED_PARENT_ID).createBy(UPDATED_CREATE_BY).lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restCatalogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCatalogs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogs))
            )
            .andExpect(status().isOk());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
        Catalogs testCatalogs = catalogsList.get(catalogsList.size() - 1);
        assertThat(testCatalogs.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testCatalogs.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCatalogs.getSortOrder()).isEqualTo(DEFAULT_SORT_ORDER);
        assertThat(testCatalogs.getParentId()).isEqualTo(UPDATED_PARENT_ID);
        assertThat(testCatalogs.getCreateDate()).isEqualTo(DEFAULT_CREATE_DATE);
        assertThat(testCatalogs.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
        assertThat(testCatalogs.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testCatalogs.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void fullUpdateCatalogsWithPatch() throws Exception {
        // Initialize the database
        catalogsRepository.saveAndFlush(catalogs);

        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();

        // Update the catalogs using partial update
        Catalogs partialUpdatedCatalogs = new Catalogs();
        partialUpdatedCatalogs.setId(catalogs.getId());

        partialUpdatedCatalogs
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .sortOrder(UPDATED_SORT_ORDER)
            .parentId(UPDATED_PARENT_ID)
            .createDate(UPDATED_CREATE_DATE)
            .createBy(UPDATED_CREATE_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY);

        restCatalogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCatalogs.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCatalogs))
            )
            .andExpect(status().isOk());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
        Catalogs testCatalogs = catalogsList.get(catalogsList.size() - 1);
        assertThat(testCatalogs.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testCatalogs.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCatalogs.getSortOrder()).isEqualTo(UPDATED_SORT_ORDER);
        assertThat(testCatalogs.getParentId()).isEqualTo(UPDATED_PARENT_ID);
        assertThat(testCatalogs.getCreateDate()).isEqualTo(UPDATED_CREATE_DATE);
        assertThat(testCatalogs.getCreateBy()).isEqualTo(UPDATED_CREATE_BY);
        assertThat(testCatalogs.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testCatalogs.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingCatalogs() throws Exception {
        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();
        catalogs.setId(count.incrementAndGet());

        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCatalogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, catalogsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(catalogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCatalogs() throws Exception {
        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();
        catalogs.setId(count.incrementAndGet());

        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(catalogsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCatalogs() throws Exception {
        int databaseSizeBeforeUpdate = catalogsRepository.findAll().size();
        catalogs.setId(count.incrementAndGet());

        // Create the Catalogs
        CatalogsDTO catalogsDTO = catalogsMapper.toDto(catalogs);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCatalogsMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(catalogsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Catalogs in the database
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCatalogs() throws Exception {
        // Initialize the database
        catalogsRepository.saveAndFlush(catalogs);

        int databaseSizeBeforeDelete = catalogsRepository.findAll().size();

        // Delete the catalogs
        restCatalogsMockMvc
            .perform(delete(ENTITY_API_URL_ID, catalogs.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Catalogs> catalogsList = catalogsRepository.findAll();
        assertThat(catalogsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
