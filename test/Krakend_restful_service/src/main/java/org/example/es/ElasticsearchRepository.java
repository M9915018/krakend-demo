package org.example.es;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @param <T>
 * @param <ID>
 * @author Rizwan Idrees
 * @author Mohsin Husen
 * @author Sascha Woo
 * @author Murali Chevuri
 * @author Peter-Josef Meisch
 */
@NoRepositoryBean
public interface ElasticsearchRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

    /**
     * @deprecated since 4.0, use {@link #save(Object)} instead
     */
    @Deprecated
    default <S extends T> S index(S entity) {
        return save(entity);
    }

    /**
     * This method is intended to be used when many single inserts must be made that cannot be aggregated to be inserted
     * with {@link #saveAll(Iterable)}. This might lead to a temporary inconsistent state until {@link #refresh()} is
     * called.
     *
     * @deprecated since 4.0, use a custom repository implementation instead
     */
    @Deprecated
    <S extends T> S indexWithoutRefresh(S entity);

    /**
     * @deprecated since 4.0, use standard repository method naming or @{@link Query} annotated methods, or
     *             {@link org.springframework.data.elasticsearch.core.ElasticsearchOperations}.
     */
    Iterable<T> search(QueryBuilder query);

    /**
     * @deprecated since 4.0, use standard repository method naming or @{@link Query} annotated methods, or
     *             {@link org.springframework.data.elasticsearch.core.ElasticsearchOperations}.
     */
    Page<T> search(QueryBuilder query, Pageable pageable);

    /**
     * @deprecated since 4.0, use standard repository method naming or @{@link Query} annotated methods, or
     *             {@link org.springframework.data.elasticsearch.core.ElasticsearchOperations}.
     */
    Page<T> search(Query searchQuery);

    /**
     * Search for similar entities using a morelikethis query
     *
     * @param entity the entity for which similar documents should be searched, must not be {@literal null}
     * @param fields
     * @param pageable , must not be {@literal null}
     * @return
     */
    Page<T> searchSimilar(T entity, @Nullable String[] fields, Pageable pageable);

    /**
     * @deprecated since 4.0, use  instead. Repository methods should call refresh
     *             in their implementation.
     */
    @Deprecated
    void refresh();
}
