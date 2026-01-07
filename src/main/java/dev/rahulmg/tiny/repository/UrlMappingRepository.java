package dev.rahulmg.tiny.repository;

import dev.rahulmg.tiny.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing UrlMapping data in the database.
 */
@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
}