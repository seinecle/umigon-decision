/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

import java.util.Iterator;
import java.util.Map;
import net.clementlevallois.umigon.model.Category;
import net.clementlevallois.umigon.model.Decision;
import net.clementlevallois.umigon.model.Document;
import net.clementlevallois.umigon.model.ResultOneHeuristics;

/**
 *
 * @author LEVALLOIS
 */
public class FinalDecisionMaker {

    Document document;

    public FinalDecisionMaker(Document document) {
        this.document = document;
    }

    public Document takeIt() {
        //what to do when a text contains both positive and negative markers?
        //classify it as negative

        Map<Integer, ResultOneHeuristics> indicesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Map<Integer, ResultOneHeuristics> indicesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);

        if (!indicesPos.isEmpty() && !indicesNeg.isEmpty()) {
            Iterator<ResultOneHeuristics> iteratorResultsHeuristics = document.getResultsOfHeuristics().iterator();
            while (iteratorResultsHeuristics.hasNext()) {
                ResultOneHeuristics nextHeuristics = iteratorResultsHeuristics.next();
                if (nextHeuristics.getCategoryEnum().equals(Category.CategoryEnum._11) || nextHeuristics.getCategoryEnum().equals(Category.CategoryEnum._111)) {
                    iteratorResultsHeuristics.remove();
                    Decision decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.FINAL_ADJUDICATION_NEGATIVE_SENTIMENT_PREVAILS);
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setHeuristicsImpacted(nextHeuristics);
                    document.getSentimentDecisions().add(decision);
                }
            }
        }

        return document;
    }

}
