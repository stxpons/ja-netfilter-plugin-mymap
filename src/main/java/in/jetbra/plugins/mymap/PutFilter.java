package in.jetbra.plugins.mymap;

import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.enums.RuleType;
import com.janetfilter.core.models.FilterRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PutFilter {
    private static Map<Object, Object> map;

    public static void setRules(List<FilterRule> rules) {
        map = new HashMap<>();

        for (FilterRule rule : rules) {
            if (rule.getType() != RuleType.EQUAL) {
                continue;
            }

            String[] sections = rule.getRule().split("->", 2);
            if (2 != sections.length) {
                DebugInfo.output("Invalid record: " + rule + ", skipped.");
                continue;
            }

            map.put(sections[0], sections[1]);
        }
    }

    public static Object testPut(Object k, Object v) {
        if (null == k) {
            return v;
        }

        if (map.containsKey(k)) {
            return map.get(k);
        }

        return v;
    }
}
