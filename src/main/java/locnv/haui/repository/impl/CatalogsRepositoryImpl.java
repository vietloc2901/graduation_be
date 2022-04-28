package locnv.haui.repository.impl;

import locnv.haui.repository.CatalogsCustomRepository;
import locnv.haui.service.dto.CatalogsDTO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.ZonedDateTimeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

@Repository
public class CatalogsRepositoryImpl implements CatalogsCustomRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<CatalogsDTO> getCatalogsForTree(CatalogsDTO catalogsDTO) {
        StringBuilder sql = new StringBuilder("with recursive cte (id, code, name, parent_id) as (");
        sql.append(" select id, code, name, parent_id from catalogs ");
        sql.append(" where {0} ");
        sql.append((" union all "));
        sql.append((" select c.id, c.code, c.name, c.parent_id from catalogs c "));
        sql.append((" inner join cte on c.id = cte.parent_id )"));
        sql.append((" select DISTINCT c1.id, c1.code, c1.name, c1.sort_order sortOrder," +
            " c1.parent_id parentId, c1.create_date createDate, c1.create_by createBy, " +
            "c1.last_modified_date lastModifiedDate, c1.last_modified_by lastModifiedBy"));
        sql.append((" from cte join catalogs c1 where cte.id = c1.id "));
        if(Objects.nonNull(catalogsDTO.getId())){
            sql.append(" and c1.id != :id ");
        }
        String sqlStr = sql.toString();

        if(StringUtils.isBlank(catalogsDTO.getCode()) && StringUtils.isBlank(catalogsDTO.getName())){
            sqlStr = sqlStr.replace("{0}"," 1=1 ");
        }else{
            if(StringUtils.isNotBlank(catalogsDTO.getCode()) && StringUtils.isNotBlank(catalogsDTO.getName())){
                sqlStr = sqlStr.replace("{0}", " LOWER(code) like LOWER(CONCAT( '%',:code, '%' )) escape '&'" +
                    " and LOWER(name) like LOWER(CONCAT( '%',:name, '%' )) escape '&' ");
            }else if(StringUtils.isNotBlank(catalogsDTO.getCode())){
                sqlStr = sqlStr.replace("{0}"," LOWER(code) like LOWER(CONCAT( '%',:code, '%' )) escape '&' ");
            }else if(StringUtils.isNotBlank(catalogsDTO.getName())){
                sqlStr = sqlStr.replace("{0}"," LOWER(name) like LOWER(CONCAT( '%',:name, '%' )) escape '&' ");
            }
        }

        NativeQuery<CatalogsDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sqlStr);
        query
            .addScalar("id", new LongType())
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("sortOrder", new IntegerType())
            .addScalar("parentId", new LongType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("createBy", new StringType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .setResultTransformer(Transformers.aliasToBean(CatalogsDTO.class));
        if (StringUtils.isNotBlank(catalogsDTO.getCode())) {
            query.setParameter("code", "%" + validateKeySearch(catalogsDTO.getCode().trim()) + "%");
        }
        if (StringUtils.isNotBlank(catalogsDTO.getName())) {
            query.setParameter("name", "%" + validateKeySearch(catalogsDTO.getName().trim()) + "%");
        }
        if(Objects.nonNull(catalogsDTO.getId())){
            query.setParameter("id", catalogsDTO.getId());
        }
        return query.getResultList();
    }


    @Override
    public List<CatalogsDTO> getDataExport(CatalogsDTO catalogsDTO) {
        StringBuilder sql = new StringBuilder("with recursive cte (id, code, name, parent_id) as (");
        sql.append(" select id, code, name, parent_id from catalogs ");
        sql.append(" where {0} ");
        sql.append((" union all "));
        sql.append((" select c.id, c.code, c.name, c.parent_id from catalogs c "));
        sql.append((" inner join cte on c.id = cte.parent_id )"));
        sql.append((" select DISTINCT c1.id, c1.code, c1.name, c2.code parentCode, c2.name parentName ,c1.sort_order sortOrder," +
            " c1.parent_id parentId, c1.create_date createDate, c1.create_by createBy, " +
            "c1.last_modified_date lastModifiedDate, c1.last_modified_by lastModifiedBy"));
        sql.append((" from cte join catalogs c1 left join catalogs c2 on cte.parent_id = c2.id where cte.id = c1.id "));
        if(Objects.nonNull(catalogsDTO.getId())){
            sql.append(" and c1.id != :id ");
        }
        String sqlStr = sql.toString();

        if(StringUtils.isBlank(catalogsDTO.getCode()) && StringUtils.isBlank(catalogsDTO.getName())){
            sqlStr = sqlStr.replace("{0}"," 1=1 ");
        }else{
            if(StringUtils.isNotBlank(catalogsDTO.getCode()) && StringUtils.isNotBlank(catalogsDTO.getName())){
                sqlStr = sqlStr.replace("{0}", " LOWER(code) like LOWER(CONCAT( '%',:code, '%' )) escape '&'" +
                    " and LOWER(name) like LOWER(CONCAT( '%',:name, '%' )) escape '&' ");
            }else if(StringUtils.isNotBlank(catalogsDTO.getCode())){
                sqlStr = sqlStr.replace("{0}"," LOWER(code) like LOWER(CONCAT( '%',:code, '%' )) escape '&' ");
            }else if(StringUtils.isNotBlank(catalogsDTO.getName())){
                sqlStr = sqlStr.replace("{0}"," LOWER(name) like LOWER(CONCAT( '%',:name, '%' )) escape '&' ");
            }
        }

        NativeQuery<CatalogsDTO> query = ((Session) entityManager.getDelegate()).createNativeQuery(sqlStr);
        query
            .addScalar("id", new LongType())
            .addScalar("code", new StringType())
            .addScalar("name", new StringType())
            .addScalar("parentCode", new StringType())
            .addScalar("parentName", new StringType())
            .addScalar("sortOrder", new IntegerType())
            .addScalar("parentId", new LongType())
            .addScalar("createDate", new ZonedDateTimeType())
            .addScalar("createBy", new StringType())
            .addScalar("lastModifiedDate", new ZonedDateTimeType())
            .addScalar("lastModifiedBy", new StringType())
            .setResultTransformer(Transformers.aliasToBean(CatalogsDTO.class));
        if (StringUtils.isNotBlank(catalogsDTO.getCode())) {
            query.setParameter("code", "%" + validateKeySearch(catalogsDTO.getCode().trim()) + "%");
        }
        if (StringUtils.isNotBlank(catalogsDTO.getName())) {
            query.setParameter("name", "%" + validateKeySearch(catalogsDTO.getName().trim()) + "%");
        }
        if(Objects.nonNull(catalogsDTO.getId())){
            query.setParameter("id", catalogsDTO.getId());
        }
        return query.getResultList();
    }

    public static String validateKeySearch(String str) {
        return str.replace("&", "&&").replace("%", "&%").replace("_", "&_");
    }
}
