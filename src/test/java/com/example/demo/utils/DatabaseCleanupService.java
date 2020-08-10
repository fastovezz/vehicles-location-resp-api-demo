package com.example.demo.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.Transactional;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@Profile("test")
public class DatabaseCleanupService implements InitializingBean {
    private List<String> tableNames;
    private final EntityManager entityManager;

    public DatabaseCleanupService(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void afterPropertiesSet() {
        Metamodel metaModel = entityManager.getMetamodel();
        entityManager.getMetamodel();
        tableNames = metaModel.getManagedTypes()
                .stream()
                .filter(it ->
                        it.getJavaType().getAnnotation(Table.class) != null
                                || it.getJavaType().getAnnotation(Entity.class) != null
                )
                .map(it -> {
                    Annotation tableAnnotation = it.getJavaType().getAnnotation(Table.class);
                    String tableName = tableAnnotation == null ? it.getJavaType().getSimpleName() : ((Table) tableAnnotation).name();
                    return tableName.toLowerCase();
                })
                .collect(Collectors.toList());
    }

    /**
     * Utility method that truncates all identified tables
     */
    @Transactional
    public void truncate() {
        try {
            entityManager.flush();
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
            tableNames.forEach(tableName ->
                    entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate()
            );
        } finally {
            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
        }
    }
}
