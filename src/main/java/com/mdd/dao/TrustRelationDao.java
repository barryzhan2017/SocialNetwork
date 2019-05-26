package com.mdd.dao;


import com.mdd.entity.TrustRelation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface TrustRelationDao extends Neo4jRepository<TrustRelation, Long> {

    @Query("MATCH (n:Person)-[k:Trust]->(m:Person) where ID(n) = {0} and ID(m) = {1} return k")
    Iterable<TrustRelation> findBySourceIdAndTargetId(long sourceId, long targetId);


}
