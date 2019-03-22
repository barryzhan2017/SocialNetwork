package com.mdd.dao;

import com.mdd.entity.TrustRelation;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface TrustRelationDao extends Neo4jRepository<TrustRelation, Long> {
}
