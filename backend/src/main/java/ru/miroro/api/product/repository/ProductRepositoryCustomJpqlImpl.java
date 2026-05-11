package ru.miroro.api.product.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import ru.miroro.api.product.model.Product;

@Repository
@Transactional
public class ProductRepositoryCustomJpqlImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Product> findAllWithDetails() {

        return em.createQuery("""
                select p
                from Product p
                left join fetch p.variants
                left join fetch p.images
                order by p.id
                """, Product.class).getResultList();
    }

    @Override
    public Product findByIdWithDetails(Integer id) {

        return em.createQuery("""
                select p
                from Product p
                left join fetch p.variants
                left join fetch p.images
                where p.id = :id
                """, Product.class).setParameter("id", id).getSingleResult();
    }
}
