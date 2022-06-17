/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.clementlevallois.umigon.model.Category;
import net.clementlevallois.umigon.model.Decision;
import net.clementlevallois.umigon.model.Document;
import net.clementlevallois.umigon.model.ResultOneHeuristics;

/**
 *
 * @author LEVALLOIS
 */
public class WhenTextContainsAModerator {

    Document document;
    Set<String> moderators;

    public WhenTextContainsAModerator(Document document, Set<String> moderators) {
        this.document = document;
        this.moderators = moderators;
    }
    
    

    public Document containsAModerator() {
        Map<Integer, ResultOneHeuristics> indexesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Map<Integer, ResultOneHeuristics> indexesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);

        if (indexesPos.isEmpty() || indexesNeg.isEmpty()) {
            return document;
        }
        int indexModerator;
        int indexPosFirst = Integer.MAX_VALUE;
        int indexNegFirst = Integer.MAX_VALUE;
        int indexPosLast = -1;
        int indexNegLast = -1;

        Set<String> termsInText = new HashSet();
        termsInText.addAll(Arrays.asList(document.getTextStripped().split(" ")));
        Iterator<Integer> iterator;

        iterator = indexesPos.keySet().iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next();
            if (currIndex < indexPosFirst) {
                indexPosFirst = currIndex;
            }
            if (currIndex > indexPosLast) {
                indexPosLast = currIndex;
            }
        }
        iterator = indexesNeg.keySet().iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next();
            if (currIndex < indexNegFirst) {
                indexNegFirst = currIndex;
            }
            if (currIndex > indexNegLast) {
                indexNegLast = currIndex;
            }
        }

        Decision decision;
        for (String moderator : moderators) {
            if (termsInText.contains(moderator)) {
                indexModerator = document.getTextStripped().toLowerCase().indexOf(moderator);
                if ((indexPosFirst < indexModerator)) {
                    decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.POSITIVE_TERM_THEN_MODERATOR);
                    decision.setHeuristicsImpacted(indexesPos.get(indexPosFirst));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTermInvolvedInDecision(moderator);
                    decision.setIndexOfTermInvolvedInDecision(indexModerator);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
                if ((indexNegFirst < indexModerator)) {
                    decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.NEGATIVE_TERM_THEN_MODERATOR);
                    decision.setHeuristicsImpacted(indexesNeg.get(indexNegFirst));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTermInvolvedInDecision(moderator);
                    decision.setIndexOfTermInvolvedInDecision(indexModerator);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
                if ((indexPosFirst > indexModerator & indexNegFirst < indexModerator)) {
                    decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.NEGATIVE_TERM_THEN_MODERATOR);
                    decision.setHeuristicsImpacted(indexesPos.get(indexNegFirst));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTermInvolvedInDecision(moderator);
                    decision.setIndexOfTermInvolvedInDecision(indexModerator);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
                if (indexNegFirst < indexModerator & indexNegLast < indexModerator) {
                    decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.TWO_NEGATIVE_TERMS_THEN_MODERATOR);
                    decision.setHeuristicsImpacted(indexesPos.get(indexNegFirst));
                    decision.setSecondHeuristicsImpacted(indexesPos.get(indexNegLast));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTermInvolvedInDecision(moderator);
                    decision.setIndexOfTermInvolvedInDecision(indexModerator);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
                if (indexPosFirst < indexModerator & indexPosLast < indexModerator) {
                    decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.TWO_POSITIVE_TERMS_THEN_MODERATOR);
                    decision.setHeuristicsImpacted(indexesPos.get(indexPosFirst));
                    decision.setSecondHeuristicsImpacted(indexesPos.get(indexPosLast));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTermInvolvedInDecision(moderator);
                    decision.setIndexOfTermInvolvedInDecision(indexModerator);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
            }
        }
        return document;
    }

}
