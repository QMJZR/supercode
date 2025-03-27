package com.supercode.supercode.repository;

import com.supercode.supercode.po.Specification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface SpecificationRepository extends JpaRepository<Specification, Integer> {
    Set<Specification> findByProductId(Integer productId);
    @Transactional
    void deleteByProductId(Integer productId);
}
