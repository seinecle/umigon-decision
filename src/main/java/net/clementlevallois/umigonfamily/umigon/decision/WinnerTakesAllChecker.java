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
import net.clementlevallois.umigon.model.TypeOfToken;

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
        Map<Integer, ResultOneHeuristics> indexesPos = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._11);
        Map<Integer, ResultOneHeuristics> indexesNeg = document.getAllHeuristicsResultsForOneCategory(Category.CategoryEnum._12);

        int lastStrongNote = 0;
        ResultOneHeuristics finalNote = null;

        // detecting if we have such a "winner takes all" emotion in the text
        for (Map.Entry<Integer, ResultOneHeuristics> entry : indexesPos.entrySet()) {
            TypeOfToken.TypeOfTokenEnum typeOfToken = entry.getValue().getTypeOfToken();
            switch (typeOfToken) {
                case EMOJI, EMOTICON_IN_ASCII, HASHTAG, ONOMATOPAE, TEXTO_SPEAK:
                    if (entry.getValue().getIndexTokenInvestigated() > lastStrongNote) {
                        lastStrongNote = entry.getValue().getIndexTokenInvestigated();
                        finalNote = entry.getValue();
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
                    decision.setTermInvolvedInDecision(finalNote.getTokenInvestigated());
                    iteratorResultsHeuristics.remove();
                    document.getSentimentDecisions().add(decision);
                }
            }
        }

        return document;
    }

}
