import java.security.PublicKey;

public class Condition {
    public String strategyName="";
    private Condition(){}
    public static class Branch extends Condition
    {
        public String expression;
        public Integer result=null;
        public String id=null;

        public String toString()
        {
            return "branch:if"+expression+"=>"+(result==null?id:result+"");
        }
    }

    public static class Current extends Condition
    {
        public String id;

        public String toString()
        {
            return "map cur->"+id;
        }
    }
    public static class Round extends Condition
    {
        public String id;

        public String toString()
        {
            return "map cur->"+id;
        }
    }
    public static class Assign extends Condition {
        public String exp;
        public String id;


        public String toString() {
            return id + " = " + exp;
        }
    }
    public static class Random extends Condition
    {
        public String id;
        public String number;

        public String toString()
        {
            return id+"=random("+number+")";
        }

    }
    public static class Enemy extends Condition
    {
        public String id,start,end;

        public String toString()
        {
            return id+"=enemy["+start+","+end+"]";
        }
    }
    public static class Self extends Condition
    {
        public String id,start,end;

        public String toString()
        {
            return id+"self["+start+","+end+"]";
        }
    }




}
