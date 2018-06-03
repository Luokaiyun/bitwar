import java.util.*;

public class StrategyAnalyze {
    public void anlyze(Set<Strategy>strategies)throws AnalyzeException
    {
        Map<String,Strategy>strategy=new HashMap<>();
        for(Strategy s:strategies)
        {
            strategy.put(s.name,s);
        }
        for(Strategy s:strategies)
        {
            List<Condition> newConditions=new ArrayList<>();
            for(int i=0;i<s.conditions.size();i++)
            {
                Condition c=s.conditions.get(i);
                newConditions.add(c);
            }
            s.conditions=newConditions;
        }

    }
}
