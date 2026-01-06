package dev.rahulmg.tiny.repository;

import dev.rahulmg.tiny.model.UrlMapping;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing UrlMapping data in Cassandra.
 */
@Repository
public interface UrlMappingRepository extends CassandraRepository<UrlMapping, String> {
}