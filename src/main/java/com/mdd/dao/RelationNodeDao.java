package com.mdd.dao;

import com.mdd.entity.RelationNode;
import com.mdd.entity.Score;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationNodeDao extends Neo4jRepository<RelationNode, Long> {
//    @Query("MATCH (root:RelationNode)-[r1*]->(x) " +
//            "where root.sourceId = {0} and root.targetId = {1} " +
//            "OPTIONAL MATCH (y)-[r2]->(root) " +
//            "FOREACH(r IN r1 | DELETE r) " +
//            "DETACH DELETE r2, root, x, y;")
//    void deleteAllConnectedNodesBySourceIdAndTargetId(long sourceId, long targetId);


    @Query("MATCH (n:Score)-[r1*]->(root:RelationNode) " +
            "where root.sourceId = {0} and root.targetId = {1} " +
            "return n")
    Iterable<Score> getScoreLinkingToItById(long sourceId, long targetId);
}
