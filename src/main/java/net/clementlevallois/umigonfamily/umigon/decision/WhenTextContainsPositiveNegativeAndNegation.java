/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.clementlevallois.umigon.model.Category;
import net.clementlevallois.umigon.model.Decision;
import net.clementlevallois.umigon.model.Document;
import net.clementlevallois.umigon.model.NGram;
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
        Set<ResultOneHeuristics> indexesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Set<ResultOneHeuristics> indexesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);

        if (indexesPos.isEmpty() || indexesNeg.isEmpty()) {
            return document;
        }
        int indexPos = 0;
        int indexNeg = 0;

        ResultOneHeuristics targetPositiveHeuristics = null;
        ResultOneHeuristics targetNegativeHeuristics = null;

        Iterator<ResultOneHeuristics> iterator;

        iterator = indexesPos.iterator();
        while (iterator.hasNext()) {
            ResultOneHeuristics currHeuristics = iterator.next();
            if (indexPos < currHeuristics.getTextFragmentInvestigated().getIndexCardinal()) {
                indexPos = currHeuristics.getTextFragmentInvestigated().getIndexCardinal();
                targetPositiveHeuristics = currHeuristics;
            }
        }
        iterator = indexesNeg.iterator();
        while (iterator.hasNext()) {
            ResultOneHeuristics currHeuristics = iterator.next();
            if (indexNeg < currHeuristics.getTextFragmentInvestigated().getIndexCardinal()) {
                indexNeg = currHeuristics.getTextFragmentInvestigated().getIndexCardinal();
                targetNegativeHeuristics = currHeuristics;
            }
        }
        Decision decision;

        List<NGram> ngrams = document.getNgrams();
        
        NGram negation = null;

        int indexCardinalLastNegation = -1;
        for (NGram ngram : ngrams) {
            if (negations.contains(ngram.getCleanedNgram().toLowerCase())) {
                indexCardinalLastNegation = ngram.getIndexCardinal();
                negation = ngram;
            }
        }

        if (indexCardinalLastNegation == -1) {
            return document;
        }

        // not sure this is a good rule!!
        // doesn't work for "The chocolate is excellent, it isn't bad"
        if ((indexPos < indexCardinalLastNegation & indexNeg > indexCardinalLastNegation)) {
            document.getResultsOfHeuristics().remove(targetPositiveHeuristics);
            decision = new Decision();
            decision.setHeuristicsImpacted(targetPositiveHeuristics);
            decision.setDecisionType(Decision.DecisionType.REMOVE);
            decision.setOtherHeuristicsInvolvedInDecision(targetNegativeHeuristics);
            decision.setTextFragmentInvolvedInDecision(negation);
            document.getSentimentDecisions().add(decision);
        } else if ((indexPos > indexCardinalLastNegation & indexNeg < indexCardinalLastNegation)) {
            document.getResultsOfHeuristics().remove(targetNegativeHeuristics);
            decision = new Decision();
            decision.setHeuristicsImpacted(targetNegativeHeuristics);
            decision.setDecisionType(Decision.DecisionType.REMOVE);
            decision.setOtherHeuristicsInvolvedInDecision(targetPositiveHeuristics);
            decision.setTextFragmentInvolvedInDecision(negation);
            document.getSentimentDecisions().add(decision);
        }
        if ((indexCardinalLastNegation < indexPos & indexCardinalLastNegation < indexNeg & indexPos < indexNeg)) {
            document.getResultsOfHeuristics().remove(targetPositiveHeuristics);
            document.getResultsOfHeuristics().remove(targetNegativeHeuristics);
            decision = new Decision();
            decision.setHeuristicsImpacted(targetPositiveHeuristics);
            decision.setDecisionType(Decision.DecisionType.REMOVE);
            decision.setOtherHeuristicsInvolvedInDecision(targetNegativeHeuristics);
            decision.setTextFragmentInvolvedInDecision(negation);
            document.getSentimentDecisions().add(decision);
        } else if ((indexCardinalLastNegation < indexPos & indexCardinalLastNegation < indexNeg & indexNeg < indexPos)) {
            document.getResultsOfHeuristics().remove(targetPositiveHeuristics);
            document.getResultsOfHeuristics().remove(targetNegativeHeuristics);
            decision = new Decision();
            decision.setHeuristicsImpacted(targetNegativeHeuristics);
            decision.setDecisionType(Decision.DecisionType.REMOVE);
            decision.setOtherHeuristicsInvolvedInDecision(targetPositiveHeuristics);
            decision.setTextFragmentInvolvedInDecision(negation);
            document.getSentimentDecisions().add(decision);
        }
        return document;
    }
}
