import java.util.Set;

public class Competition {
    public String s1=null;
    public String s2=null;
    public int round=10;
    public void combat(String name1,String name2)
    {
        s1=name1;
        s2=name2;
    }
    public void combat(String name)
    {
        if(!name.equals(""))
            s1=name;
    }
    public void round(String number)throws NumberFormatException
    {
        /*int r = Integer.valueOf(number);
        if (r > 200)
        r = 200;
        round = r;*/
        round=Integer.valueOf(number);
    }
    public Strategy getStrategy1(Set<Strategy> set)
    {
        if(s1==null)
            return null;
        for(Strategy s:set)
        {
            if(s.name.equals(s1))
                return s;
        }
        return null;
    }
    public Strategy getStrategy2(Set<Strategy> set)
    {
        if (s2 == null)
            return null;
        for (Strategy s : set) {
            if (s.name.equals(s2))
                return s;
        }
        return null;
    }
    public int getRound()
    {
        return round;
    }



}
