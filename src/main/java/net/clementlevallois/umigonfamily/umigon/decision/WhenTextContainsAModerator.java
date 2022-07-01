/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

import java.util.Arrays;
import java.util.HashMap;
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
        Map<Integer, ResultOneHeuristics> indexesPos = new HashMap();
        indexesPos.putAll(document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11));
        Map<Integer, ResultOneHeuristics> indexesNeg = new HashMap();
        indexesNeg.putAll(document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12));

        if (indexesPos.isEmpty() & indexesNeg.isEmpty()) {
            return document;
        }
        int indexModerator;
        int index = Integer.MAX_VALUE;
        int indexNegFirst = Integer.MAX_VALUE;
        int indexPosLast = -1;
        int indexNegLast = -1;

        Set<String> termsInText = new HashSet();
        termsInText.addAll(Arrays.asList(document.getTextStripped().split(" ")));
        Iterator<Integer> iterator;

        iterator = indexesPos.keySet().iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next();
            if (currIndex < index) {
                index = currIndex;
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
//                if (moderator.equals("but")){
//                    System.out.println("stop bc moderator is but");
//                }
                indexModerator = document.getTextStripped().toLowerCase().indexOf(moderator);
                Set<Map.Entry<Integer, ResultOneHeuristics>> entrySetPositiveHeuristics = indexesPos.entrySet();
                for (Map.Entry<Integer, ResultOneHeuristics> entry : entrySetPositiveHeuristics) {
                    int indexLoop = entry.getKey();
                    if ((indexLoop < indexModerator)) {
                        document.getResultsOfHeuristics().remove(indexesPos.get(indexLoop));
                        decision = new Decision();
                        decision.setDecisionMotive(Decision.DecisionMotive.POSITIVE_TERM_THEN_MODERATOR);
                        decision.setHeuristicsImpacted(indexesPos.get(indexLoop));
                        decision.setDecisionType(Decision.DecisionType.REMOVE);
                        decision.setTermInvolvedInDecision(moderator);
                        decision.setIndexOfTermInvolvedInDecision(indexModerator);
                        document.getSentimentDecisions().add(decision);
                    }
                }

                Set<Map.Entry<Integer, ResultOneHeuristics>> entrySetNegativeHeuristics = indexesNeg.entrySet();
                for (Map.Entry<Integer, ResultOneHeuristics> entry : entrySetNegativeHeuristics) {
                    int indexLoop = entry.getKey();
                    if ((indexLoop < indexModerator)) {
                        document.getResultsOfHeuristics().remove(indexesNeg.get(indexLoop));
                        decision = new Decision();
                        decision.setDecisionMotive(Decision.DecisionMotive.NEGATIVE_TERM_THEN_MODERATOR);
                        decision.setHeuristicsImpacted(indexesNeg.get(indexLoop));
                        decision.setDecisionType(Decision.DecisionType.REMOVE);
                        decision.setTermInvolvedInDecision(moderator);
                        decision.setIndexOfTermInvolvedInDecision(indexModerator);
                        document.getSentimentDecisions().add(decision);
                    }
                }
            }
        }
        return document;
    }

}
