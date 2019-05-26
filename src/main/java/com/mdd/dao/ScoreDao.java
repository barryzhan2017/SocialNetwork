package com.mdd.dao;
import com.mdd.entity.Score;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreDao extends Neo4jRepository<Score, Long> {
    Score findBySourceIdAndTargetIdAndTrustLevel(long sourceId, long targetId, int trustLevel);
    @Query("MATCH (root:Score)-[r1*]->(x) " +
            "where ID(root) = {0} " +
            "FOREACH(r IN r1| DELETE r) " +
            "DELETE root, x;")
    void deleteAllConnectedNodesById(long id);


}
