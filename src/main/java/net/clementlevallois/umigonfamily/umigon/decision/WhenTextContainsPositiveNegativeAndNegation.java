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
public class WhenTextContainsPositiveNegativeAndNegation {

    Document document;
    Set<String> negations;

    public WhenTextContainsPositiveNegativeAndNegation(Document document, Set<String> negations) {
        this.document = document;
        this.negations = negations;
    }

    public Document containsANegationAndAPositiveAndNegativeSentiment() {
        Map<Integer, ResultOneHeuristics> indexesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Map<Integer, ResultOneHeuristics> indexesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);

        if (indexesPos.isEmpty() || indexesNeg.isEmpty()) {
            return document;
        }
        int indexNegation;
        int indexPos = 0;
        int indexNeg = 0;

        Set<String> termsInText = new HashSet();
        termsInText.addAll(Arrays.asList(document.getTextStripped().split(" ")));
        Iterator<Integer> iterator;

        iterator = indexesPos.keySet().iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next();
            if (indexPos < currIndex) {
                indexPos = currIndex;
            }
        }
        iterator = indexesNeg.keySet().iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next();
            if (indexNeg < currIndex) {
                indexNeg = currIndex;
            }
        }
        Decision decision;

        for (String negation : negations) {
            if (termsInText.contains(negation + " ")) {
                indexNegation = document.getText().indexOf(negation);

                // not sure this is a good rule!!
                // doesn't work for "The chocolate is excellent, it isn't bad"
                if ((indexPos < indexNegation & indexNeg > indexNegation)) {
                    document.getResultsOfHeuristics().remove(indexesPos.get(indexPos));
                    decision = new Decision();
                    decision.setHeuristicsImpacted(indexesPos.get(indexPos));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setOtherHeuristicsInvolvedInDecision(indexesNeg.get(indexNeg));
                    decision.setTermInvolvedInDecision(negation);
                    decision.setIndexOfTermInvolvedInDecision(indexNegation);
                    document.getSentimentDecisions().add(decision);
                    break;
                } else if ((indexPos > indexNegation & indexNeg < indexNegation)) {
                    document.getResultsOfHeuristics().remove(indexesNeg.get(indexNeg));
                    decision = new Decision();
                    decision.setHeuristicsImpacted(indexesNeg.get(indexNeg));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setOtherHeuristicsInvolvedInDecision(indexesPos.get(indexPos));
                    decision.setTermInvolvedInDecision(negation);
                    decision.setIndexOfTermInvolvedInDecision(indexNegation);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
                if ((indexNegation < indexPos & indexNegation < indexNeg & indexPos < indexNeg)) {
                    document.getResultsOfHeuristics().remove(indexesPos.get(indexPos));
                    document.getResultsOfHeuristics().remove(indexesNeg.get(indexNeg));
                    decision = new Decision();
                    decision.setHeuristicsImpacted(indexesPos.get(indexPos));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setOtherHeuristicsInvolvedInDecision(indexesNeg.get(indexNeg));
                    decision.setTermInvolvedInDecision(negation);
                    decision.setIndexOfTermInvolvedInDecision(indexNegation);
                    document.getSentimentDecisions().add(decision);
                    break;
                } else if ((indexNegation < indexPos & indexNegation < indexNeg & indexNeg < indexPos)) {
                    document.getResultsOfHeuristics().remove(indexesPos.get(indexPos));
                    document.getResultsOfHeuristics().remove(indexesNeg.get(indexNeg));
                    decision = new Decision();
                    decision.setHeuristicsImpacted(indexesNeg.get(indexNeg));
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setOtherHeuristicsInvolvedInDecision(indexesPos.get(indexPos));
                    decision.setTermInvolvedInDecision(negation);
                    decision.setIndexOfTermInvolvedInDecision(indexNegation);
                    document.getSentimentDecisions().add(decision);
                    break;
                }
            }
        }
        return document;
    }

}
