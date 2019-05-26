package com.mdd.service.impl;

import com.mdd.algorithm.*;
import com.mdd.dao.PersonDao;
import com.mdd.dao.RelationNodeDao;
import com.mdd.dao.ScoreDao;
import com.mdd.dao.TrustRelationDao;
import com.mdd.entity.Person;
import com.mdd.entity.RelationNode;
import com.mdd.entity.Score;
import com.mdd.entity.TrustRelation;
import com.mdd.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static com.mdd.common.CommonConstant.NO_AVAILABLE_RATING_NOW;
import static com.mdd.common.CommonConstant.NUMBER_OF_TRUST_LEVEL;

@Service
public class ScoreServiceImpl implements ScoreService {

    private ScoreDao scoreDao;

    private PersonDao personDao;

    private TrustRelationDao trustRelationDao;

    private RelationNodeDao relationNodeDao;

    private DynamicAdjustment dynamicAdjustment = new DynamicAdjustmentImpl();

    @Autowired
    public ScoreServiceImpl(ScoreDao scoreDao, PersonDao personDao, TrustRelationDao trustRelationDao,
                            RelationNodeDao relationNodeDao) {
        this.scoreDao = scoreDao;
        this.personDao = personDao;
        this.trustRelationDao = trustRelationDao;
        this.relationNodeDao = relationNodeDao;
    }

    /**
     * Get the score from a source person to the target in a specific trust level.
     * If the score is not stored, use mdd algorithm to create the mdd model and
     * store the score and its mdd model. Return the final score after evaluation.
     * @param sourceId Id of the source person
     * @param targetId Id of the target person
     * @param trustLevel Specific trust level the computation is on
     * @return The score from a source person to the target in a specific trust level
     */
    @Override
    public double getScore(long sourceId, long targetId, int trustLevel) {
        Score score = scoreDao.findBySourceIdAndTargetIdAndTrustLevel(sourceId, targetId, trustLevel);
        if (score != null) return score.getScore();
        else {
            MDDCreation mddCreation = new MDDCreation(NUMBER_OF_TRUST_LEVEL);
            MDD mdd = mddCreation.createMDD(sourceId, targetId, trustLevel);
            if (mdd == null) return NO_AVAILABLE_RATING_NOW;
            MDDEvaluation mddEvaluation = new MDDEvaluation();
            double probability = mddEvaluation.getProbability(mdd);
            Map<RelationshipNode, RelationNode> map = new HashMap<>();
            RelationNode relationNode = new RelationNode(mdd.getRootNode(), map);
            Score newScore = new Score(sourceId, targetId, probability, trustLevel, relationNode);
            scoreDao.save(newScore);
            return probability;
        }
    }

    /**
     * It's quite inefficient to get all connected nodes and update them.
     * So we just delete the changed mdd diagram and its score.
     * If the rating link is not established
     * @param sourceId Id of the source person
     * @param targetId Id of the target person
     * @param level Specific trust level the computation i
     */
    @Override
    @Transactional
    public void updateScore(long sourceId, long targetId, int level) {
        List<TrustRelation> trustRelationList =
                (List<TrustRelation>) trustRelationDao.findBySourceIdAndTargetId(sourceId,
                        targetId);

        double[] probability = new double[NUMBER_OF_TRUST_LEVEL];
        if (trustRelationList.isEmpty()) {
            Arrays.fill(probability, 1.0 / NUMBER_OF_TRUST_LEVEL);
            probability = dynamicAdjustment.adjust(probability, level, 1);
            Person sourcePerson = personDao.findById(sourceId).orElse(null);
            Person targetPerson = personDao.findById(targetId).orElse(null);
            if (sourcePerson == null || targetPerson == null)
                throw new NullPointerException("Cannot find Person with id " + sourceId + " or with id" + targetId);
            targetPerson.ratedById(sourceId);
            for (int i = 0; i < probability.length; i++)
                sourcePerson.trust(new TrustRelation(sourcePerson, targetPerson, i, probability[i]));
            personDao.save(sourcePerson);
            //Try without it
            personDao.save(targetPerson);
        }
        else {
            for (TrustRelation trustRelation : trustRelationList)
                probability[trustRelation.getTrustIndex()] = trustRelation.getProbability();
            trustRelationDao.save(trustRelationList, 0);
            Person targetPerson = trustRelationList.get(0).getTarget();
            targetPerson.ratedById(sourceId);

            probability = dynamicAdjustment.adjust(probability, level, targetPerson.getRatedTimesById(sourceId));
            for (TrustRelation trustRelation : trustRelationList)
                trustRelation.setProbability(probability[trustRelation.getTrustIndex()]);

            personDao.save(targetPerson);

            trustRelationDao.save(trustRelationList, 0);
            //Clear all the old scores
            for (Score score : relationNodeDao.getScoreLinkingToItById(sourceId, targetId))
                scoreDao.deleteAllConnectedNodesById(score.getId());
        }
    }
}
