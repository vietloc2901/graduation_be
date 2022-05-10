package locnv.haui.repository.impl;

import locnv.haui.repository.ProductsCustomRepository;
import locnv.haui.service.dto.ProductFullDataDTO;
import locnv.haui.service.dto.ProductsDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductsCustomRepositoryImpl implements ProductsCustomRepository {

    private final EntityManager entityManager;

    public ProductsCustomRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<ProductFullDataDTO> search(ProductsDTO productsDTO, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT c.name catalogName, p.id, p.code, p.name, p.create_date createDate, p.create_by createBy, ");
        sql.append("p.last_modified_date lastModifiedDate, p.last_modified_by lastModifiedBy, p.image, ");
        sql.append("p.brand, p.product_details productDetails, p.description_document descriptionDocument, p.video, p.catalog_id catalogId, ");
        sql.append("p.status, pp.price FROM products p LEFT JOIN catalogs c ON p.catalog_id = c.id LEFT JOIN ");
        sql.append(("(SELECT r1.product_id , r1.price, r1.apply_date FROM product_prices r1 RIGHT JOIN "));
        sql.append(" (SELECT id, product_id, max(apply_date) apply_date FROM product_prices GROUP BY product_id) as r2 ON ");
        sql.append("r1.product_id = r2.product_id GROUP BY r1.product_id) as pp ON p.id = pp.product_id where true ");
        if(Objects.nonNull(productsDTO.getId())){
            sql.append(" and p.id =:id ");
        }
        if(Objects.nonNull(productsDTO.getCatalogId())){
            sql.append(" and p.catalog_id =:catalogId ");
        }
        if(StringUtils.isNotBlank(productsDTO.getCode())){
            sql.append(" and upper(p.code) LIKE upper(:code) escape '&' ");
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            sql.append(" and upper(p.name) LIKE upper(:name) escape '&' ");
        }
        if(Objects.nonNull(productsDTO.getStatus())){
            sql.append(" and p.status =:status ");
        }
        sql.append(" order by p.catalog_id ,p.name, p.code ");
        if(page != 0 && pageSize != 0){
            int offset;
            if(page <= 1){
                offset = 0;
            }else{
                offset = (page -1) *pageSize;
            }
            sql.append(" limit ").append(offset).append(",").append(pageSize).append(" ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        query
            .addScalar("id", new LongType())
            .addScalar("catalogName", new StringType())
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("createBy", new StringType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .addScalar("image", new StringType())
            .addScalar("brand", new StringType())
            .addScalar("productDetails", new StringType())
            .addScalar("descriptionDocument", new StringType())
            .addScalar("catalogId", new LongType())
            .addScalar("status", new BooleanType())
            .addScalar("price", new BigDecimalType())
            .setResultTransformer(Transformers.aliasToBean(ProductFullDataDTO.class));
        if(Objects.nonNull(productsDTO.getId())){
            query.setParameter("id", productsDTO.getId());
        }
        if(Objects.nonNull(productsDTO.getCatalogId())){
            query.setParameter("catalogId", productsDTO.getCatalogId());
        }
        if(StringUtils.isNotBlank(productsDTO.getCode())){
            query.setParameter("code","%"+validateKeySearch(productsDTO.getCode()+"%"));
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            query.setParameter("name","%"+validateKeySearch(productsDTO.getName()+"%"));
        }
        if(Objects.nonNull(productsDTO.getStatus())){
            query.setParameter("status", productsDTO.getStatus());
        }
        List<ProductFullDataDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<ProductFullDataDTO> searchForViewProduct(ProductsDTO productsDTO, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT c.name catalogName, p.id, p.code, p.name, p.create_date createDate, p.create_by createBy, ");
        sql.append("p.last_modified_date lastModifiedDate, p.last_modified_by lastModifiedBy, p.image, ");
        sql.append("p.brand, p.product_details productDetails, p.description_document descriptionDocument, p.video, p.catalog_id catalogId, ");
        sql.append("p.status, pp.price FROM products p LEFT JOIN catalogs c ON p.catalog_id = c.id LEFT JOIN ");
        sql.append(("(SELECT r1.product_id , r1.price, r1.apply_date FROM product_prices r1 RIGHT JOIN "));
        sql.append(" (SELECT id, product_id, max(apply_date) apply_date FROM product_prices GROUP BY product_id) as r2 ON ");
        sql.append("r1.product_id = r2.product_id GROUP BY r1.product_id) as pp ON p.id = pp.product_id where true ");
        if(Objects.nonNull(productsDTO.getCatalogId())){
            sql.append(" and p.catalog_id =:catalogId ");
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            sql.append(" and upper(p.name) LIKE upper(:name) escape '&' ");
        }
        sql.append((" order by pp.price "));
        if(Objects.nonNull(productsDTO.getOrderType())){
            sql.append(productsDTO.getOrderType());
        }
        sql.append(" ,p.catalog_id ,p.name, p.code ");
        if(page != 0 && pageSize != 0){
            int offset;
            if(page <= 1){
                offset = 0;
            }else{
                offset = (page -1) *pageSize;
            }
            sql.append(" limit ").append(offset).append(",").append(pageSize).append(" ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        query
            .addScalar("id", new LongType())
            .addScalar("catalogName", new StringType())
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("createBy", new StringType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .addScalar("image", new StringType())
            .addScalar("brand", new StringType())
            .addScalar("productDetails", new StringType())
            .addScalar("descriptionDocument", new StringType())
            .addScalar("catalogId", new LongType())
            .addScalar("status", new BooleanType())
            .addScalar("price", new BigDecimalType())
            .setResultTransformer(Transformers.aliasToBean(ProductFullDataDTO.class));
        if(Objects.nonNull(productsDTO.getCatalogId())){
            query.setParameter("catalogId", productsDTO.getCatalogId());
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            query.setParameter("name","%"+validateKeySearch(productsDTO.getName()+"%"));
        }
        List<ProductFullDataDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<ProductFullDataDTO> searchForView(ProductsDTO productsDTO) {
        StringBuilder sql = new StringBuilder("SELECT c.name catalogName, p.id, p.code, p.name, p.create_date createDate, p.create_by createBy, ");
        sql.append("p.last_modified_date lastModifiedDate, p.last_modified_by lastModifiedBy, p.image, ");
        sql.append("p.brand, p.product_details productDetails, p.description_document descriptionDocument, p.video, p.catalog_id catalogId, ");
        sql.append("p.status, pp.price FROM products p LEFT JOIN catalogs c ON p.catalog_id = c.id LEFT JOIN ");
        sql.append(("(SELECT r1.product_id , r1.price, r1.apply_date FROM product_prices r1 RIGHT JOIN "));
        sql.append(" (SELECT id, product_id, max(apply_date) apply_date FROM product_prices GROUP BY product_id) as r2 ON ");
        sql.append("r1.product_id = r2.product_id GROUP BY r1.product_id) as pp ON p.id = pp.product_id where true ");
        if(Objects.nonNull(productsDTO.getCatalogId())){
            sql.append(" and p.catalog_id =:catalogId ");
        }
        sql.append(" order by pp.price ");

        if(Objects.nonNull(productsDTO.getOrderType())){
            sql.append(productsDTO.getOrderType());
        }

        sql.append(", p.create_date limit 0,8 ");

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        query
            .addScalar("id", new LongType())
            .addScalar("catalogName", new StringType())
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("createBy", new StringType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .addScalar("image", new StringType())
            .addScalar("brand", new StringType())
            .addScalar("productDetails", new StringType())
            .addScalar("descriptionDocument", new StringType())
            .addScalar("catalogId", new LongType())
            .addScalar("status", new BooleanType())
            .addScalar("price", new BigDecimalType())
            .setResultTransformer(Transformers.aliasToBean(ProductFullDataDTO.class));
        if(Objects.nonNull(productsDTO.getCatalogId())){
            query.setParameter("catalogId", productsDTO.getCatalogId());
        }
        List<ProductFullDataDTO> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public BigInteger totalRecord(ProductsDTO productsDTO) {
        StringBuilder sql = new StringBuilder("SELECT count(*) from products p where true ");
        if(Objects.nonNull(productsDTO.getCatalogId())){
            sql.append(" and p.catalog_id =:catalogId ");
        }
        if(StringUtils.isNotBlank(productsDTO.getCode())){
            sql.append(" and upper(p.code) LIKE upper(:code) escape '&' ");
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            sql.append(" and upper(p.name) LIKE upper(:name) escape '&' ");
        }
        if(Objects.nonNull(productsDTO.getStatus())){
            sql.append(" and p.status =:status ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        if(Objects.nonNull(productsDTO.getCatalogId())){
            query.setParameter("catalogId", productsDTO.getCatalogId());
        }
        if(StringUtils.isNotBlank(productsDTO.getCode())){
            query.setParameter("code","%"+validateKeySearch(productsDTO.getCode()+"%"));
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            query.setParameter("name","%"+validateKeySearch(productsDTO.getName()+"%"));
        }
        if(Objects.nonNull(productsDTO.getStatus())){
            query.setParameter("status", productsDTO.getStatus());
        }
        return (BigInteger) query.uniqueResult();
    }

    @Override
    public BigInteger totalRecordSearchForView(ProductsDTO productsDTO) {
        StringBuilder sql = new StringBuilder("SELECT count(*) from products p where true ");
        if(Objects.nonNull(productsDTO.getCatalogId())){
            sql.append(" and p.catalog_id =:catalogId ");
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            sql.append(" and upper(p.name) LIKE upper(:name) escape '&' ");
        }

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        if(Objects.nonNull(productsDTO.getCatalogId())){
            query.setParameter("catalogId", productsDTO.getCatalogId());
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            query.setParameter("name","%"+validateKeySearch(productsDTO.getName()+"%"));
        }
        return (BigInteger) query.uniqueResult();
    }

    @Override
    public List<ProductFullDataDTO> getDataExport(ProductsDTO productsDTO) {
        StringBuilder sql = new StringBuilder("SELECT c.name catalogName, p.id, p.code, p.name, p.create_date createDate, p.create_by createBy, ");
        sql.append("p.last_modified_date lastModifiedDate, p.last_modified_by lastModifiedBy, p.image, ");
        sql.append("p.brand, p.product_details productDetails, p.description_document descriptionDocument, p.video, p.catalog_id catalogId, ");
        sql.append("p.status, pp.price FROM products p LEFT JOIN catalogs c ON p.catalog_id = c.id LEFT JOIN ");
        sql.append(("(SELECT r1.product_id , r1.price, r1.apply_date FROM product_prices r1 RIGHT JOIN "));
        sql.append(" (SELECT id, product_id, max(apply_date) apply_date FROM product_prices GROUP BY product_id) as r2 ON ");
        sql.append("r1.product_id = r2.product_id GROUP BY r1.product_id) as pp ON p.id = pp.product_id where true ");
        if(Objects.nonNull(productsDTO.getId())){
            sql.append(" and p.id =:id ");
        }
        if(Objects.nonNull(productsDTO.getCatalogId())){
            sql.append(" and p.catalog_id =:catalogId ");
        }
        if(StringUtils.isNotBlank(productsDTO.getCode())){
            sql.append(" and upper(p.code) LIKE upper(:code) escape '&' ");
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            sql.append(" and upper(p.name) LIKE upper(:name) escape '&' ");
        }
        if(Objects.nonNull(productsDTO.getStatus())){
            sql.append(" and p.status =:status ");
        }
        sql.append(" order by p.catalog_id ,p.name, p.code ");

        NativeQuery query = ((Session) entityManager.getDelegate()).createNativeQuery(sql.toString());

        query
            .addScalar("id", new LongType())
            .addScalar("catalogName", new StringType())
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("createBy", new StringType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .addScalar("image", new StringType())
            .addScalar("brand", new StringType())
            .addScalar("productDetails", new StringType())
            .addScalar("descriptionDocument", new StringType())
            .addScalar("catalogId", new LongType())
            .addScalar("status", new BooleanType())
            .addScalar("price", new BigDecimalType())
            .setResultTransformer(Transformers.aliasToBean(ProductFullDataDTO.class));
        if(Objects.nonNull(productsDTO.getId())){
            query.setParameter("id", productsDTO.getId());
        }
        if(Objects.nonNull(productsDTO.getCatalogId())){
            query.setParameter("catalogId", productsDTO.getCatalogId());
        }
        if(StringUtils.isNotBlank(productsDTO.getCode())){
            query.setParameter("code","%"+validateKeySearch(productsDTO.getCode()+"%"));
        }
        if(StringUtils.isNotBlank(productsDTO.getName())){
            query.setParameter("name","%"+validateKeySearch(productsDTO.getName()+"%"));
        }
        if(Objects.nonNull(productsDTO.getStatus())){
            query.setParameter("status", productsDTO.getStatus());
        }
        List<ProductFullDataDTO> resultList = query.getResultList();
        return resultList;
    }

    public static String validateKeySearch(String str){
        return str.replaceAll("&", "&&").replaceAll("_", "&_");
    }
}
