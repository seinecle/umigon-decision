/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

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
public class IsThereATraceOfSarcasm {

    Document document;
    Set<String> tracesOfIrony;

    public IsThereATraceOfSarcasm(Document document, Set<String> tracesOfIrony) {
        this.document = document;
        this.tracesOfIrony = tracesOfIrony;
    }

    public Document checkForIrony() {
        Map<Integer, ResultOneHeuristics> indicesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Map<Integer, ResultOneHeuristics> indicesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);
        if (!(indicesPos.isEmpty() & indicesNeg.isEmpty())) {
            for (String irony : tracesOfIrony) {
                if (document.getTextStripped().contains(irony)) {
                    int indexIronicTerm = document.getTextStripped().indexOf(irony);
                    Decision decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.TRACE_OF_IRONY);
                    decision.getListOfHeuristicsImpacted().addAll(indicesPos.values());
                    decision.getListOfHeuristicsImpacted().addAll(indicesNeg.values());
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTermInvolvedInDecision(irony);
                    decision.setIndexOfTermInvolvedInDecision(indexIronicTerm);
                    document.getSentimentDecisions().add(decision);
                }
            }
        }
        return document;
    }

}
