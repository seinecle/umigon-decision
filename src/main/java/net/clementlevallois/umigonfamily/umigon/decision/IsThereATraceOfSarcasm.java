/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

import java.util.List;
import java.util.Map;
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
public class IsThereATraceOfSarcasm {

    Document document;
    Set<String> tracesOfIrony;

    public IsThereATraceOfSarcasm(Document document, Set<String> tracesOfIrony) {
        this.document = document;
        this.tracesOfIrony = tracesOfIrony;
    }

    public Document checkForIrony() {
        Set<ResultOneHeuristics> indicesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Set<ResultOneHeuristics> indicesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);
        if (!(indicesPos.isEmpty() & indicesNeg.isEmpty())) {
            List<NGram> ngrams = document.getNgrams();
            for (NGram ngram : ngrams) {
                if (tracesOfIrony.contains(ngram.getCleanedNgram().toLowerCase())) {
                    Decision decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.TRACE_OF_IRONY);
                    decision.getListOfHeuristicsImpacted().addAll(indicesPos);
                    decision.getListOfHeuristicsImpacted().addAll(indicesNeg);
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setTextFragmentInvolvedInDecision(ngram);
                    document.getSentimentDecisions().add(decision);
                    document.getResultsOfHeuristics().removeAll(document.getResultsOfHeuristics());
                }
            }
        }
        return document;
    }
}
