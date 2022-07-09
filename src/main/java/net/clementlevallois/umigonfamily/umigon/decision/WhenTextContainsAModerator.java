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
public class WhenTextContainsAModerator {

    Document document;
    Set<String> moderators;

    public WhenTextContainsAModerator(Document document, Set<String> moderators) {
        this.document = document;
        this.moderators = moderators;
    }

    public Document containsAModerator() {
        List<NGram> ngrams = document.getNgrams();
        NGram moderator = null;

        for (NGram ngram : ngrams) {
            if (moderators.contains(ngram.getCleanedNgram().toLowerCase())) {
                moderator = ngram;
            }
        }
        if (moderator == null) {
            return document;
        }

        Set<ResultOneHeuristics> indexesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Set<ResultOneHeuristics> indexesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);

        if (indexesPos.isEmpty() & indexesNeg.isEmpty()) {
            return document;
        }
        int indexCardinalModerator;
        int index = Integer.MAX_VALUE;
        int indexNegFirst = Integer.MAX_VALUE;
        int indexPosLast = -1;
        int indexNegLast = -1;

        Iterator<ResultOneHeuristics> iterator;

        iterator = indexesPos.iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next().getTextFragmentInvestigated().getIndexCardinal();
            if (currIndex < index) {
                index = currIndex;
            }
            if (currIndex > indexPosLast) {
                indexPosLast = currIndex;
            }
        }
        iterator = indexesNeg.iterator();
        while (iterator.hasNext()) {
            Integer currIndex = iterator.next().getTextFragmentInvestigated().getIndexCardinal();
            if (currIndex < indexNegFirst) {
                indexNegFirst = currIndex;
            }
            if (currIndex > indexNegLast) {
                indexNegLast = currIndex;
            }
        }

        Decision decision;
        indexCardinalModerator = moderator.getIndexCardinal();
        for (ResultOneHeuristics entry : indexesPos) {
            int indexLoop = entry.getTextFragmentInvestigated().getIndexCardinal();
            if ((indexLoop < indexCardinalModerator)) {
                document.getResultsOfHeuristics().remove(entry);
                decision = new Decision();
                decision.setDecisionMotive(Decision.DecisionMotive.POSITIVE_TERM_THEN_MODERATOR);
                decision.setHeuristicsImpacted(entry);
                decision.setDecisionType(Decision.DecisionType.REMOVE);
                decision.setTextFragmentInvolvedInDecision(moderator);
                document.getSentimentDecisions().add(decision);
            }
        }

        for (ResultOneHeuristics entry : indexesNeg) {
            int indexLoop = entry.getTextFragmentInvestigated().getIndexCardinal();
            if ((indexLoop < indexCardinalModerator)) {
                document.getResultsOfHeuristics().remove(entry);
                decision = new Decision();
                decision.setDecisionMotive(Decision.DecisionMotive.NEGATIVE_TERM_THEN_MODERATOR);
                decision.setHeuristicsImpacted(entry);
                decision.setDecisionType(Decision.DecisionType.REMOVE);
                decision.setTextFragmentInvolvedInDecision(moderator);
                document.getSentimentDecisions().add(decision);
            }
        }
        return document;
    }
}
