/*
 * author: Clément Levallois
 */
package net.clementlevallois.umigonfamily.umigon.decision;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.clementlevallois.umigon.model.Category;
import net.clementlevallois.umigon.model.Decision;
import net.clementlevallois.umigon.model.Document;
import net.clementlevallois.umigon.model.ResultOneHeuristics;
import net.clementlevallois.umigon.model.TypeOfTextFragment;

/**
 *
 * @author LEVALLOIS
 */
class WinnerTakesAllChecker {

    Document document;

    /*
    
    The idea here is that hashtags, emojis and onomatopaes
    are signs carrying an affective loads that take over
    all the other semantic markers of the text.
    
    Ex: I hate you :-)
    
    -> the emoji prevails and should lead us to not consider any previous semantic marker
    
     */
    public WinnerTakesAllChecker(Document document) {
        this.document = document;
    }

    public Document considerStrongSigns() {

        Set<ResultOneHeuristics> indexesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Set<ResultOneHeuristics> indexesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);
        
        Set<ResultOneHeuristics> posAndNegHeuristics = new HashSet();
        posAndNegHeuristics.addAll(indexesPos);
        posAndNegHeuristics.addAll(indexesNeg);

        int lastStrongNote = 0;
        ResultOneHeuristics finalNote = null;

        // detecting if we have such a "winner takes all" emotion in the text
        for (ResultOneHeuristics entry : posAndNegHeuristics) {
            TypeOfTextFragment.TypeOfTextFragmentEnum typeOfToken = entry.getTextFragmentInvestigated().getTypeOfTextFragment();
            int currCardinalIndex = entry.getTextFragmentInvestigated().getIndexCardinal();
            switch (typeOfToken) {
                case EMOJI, EMOTICON_IN_ASCII, HASHTAG, ONOMATOPAE, TEXTO_SPEAK:
                    if (currCardinalIndex > lastStrongNote) {
                        lastStrongNote = currCardinalIndex;
                        finalNote = entry;
                    }
                    ;
                    break;
            }
        }

        // if such a "winner takes all" emotion is detected, all the others should be deleted
        if (finalNote != null) {
            Iterator<ResultOneHeuristics> iteratorResultsHeuristics = document.getResultsOfHeuristics().iterator();
            while (iteratorResultsHeuristics.hasNext()) {
                ResultOneHeuristics nextHeuristics = iteratorResultsHeuristics.next();
                if (!nextHeuristics.equals(finalNote)) {
                    Decision decision = new Decision();
                    decision.setDecisionMotive(Decision.DecisionMotive.WINNER_TAKES_ALL);
                    decision.setDecisionType(Decision.DecisionType.REMOVE);
                    decision.setHeuristicsImpacted(nextHeuristics);
                    decision.setOtherHeuristicsInvolvedInDecision(finalNote);
                    decision.setTextFragmentInvolvedInDecision(finalNote.getTextFragmentInvestigated());
                    iteratorResultsHeuristics.remove();
                    document.getSentimentDecisions().add(decision);
                }
            }
        }

        return document;
    }

}
