package namedEntities.heuristics;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcronymHeuristic implements Serializable {

    public List<String> extractCandidates(String text) {
        List<String> candidates = new ArrayList<>();

        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", "");
        Pattern pattern = Pattern.compile("\\b(?:[A-Z]\\.?){2,}\\b");

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            candidates.add(matcher.group());
        }
        return candidates;
    }
}
