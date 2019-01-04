package com.loading.neo4j.dao;

import com.loading.neo4j.entity.TrustRelation;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TrustRelationDao extends Neo4jRepository<TrustRelation, Long> {
}
