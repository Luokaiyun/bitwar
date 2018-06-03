import java.util.ArrayList;

import java.util.List;

public class Strategy {
    //没有策略名，则随机分配名字，t1-t10
    public String name = "T" + (int) (Math.random() * 10+1);
    public List<Condition> conditions=new ArrayList<>();
    public String result="0";
    //默认选择为0（背叛）

    public void name(String s)
    {
        name=s.trim();
    }
    public void branch(String exp,Integer value)throws RuntimeException
    {
        int v=value;
        if(v!=0 && v!=1)
        {
            throw new RuntimeException();
        }
        exp=exp.replace("and","&&");
        exp=exp.replace("or","||");
        Condition.Branch c=new Condition.Branch();
        c.expression=exp;
        c.result=v;
        conditions.add(c);
    }
    public void branch(String exp,String id)
    {
        exp=exp.replace("and","&&");
        exp=exp.replace("or","||");
        Condition.Branch c=new Condition.Branch();
        c.expression=exp;
        c.id=id;
        conditions.add(c);
    }

    public void result(String exp,Integer value)throws RuntimeException
    {
        if(exp!=null)
        {
            throw new RuntimeException();
        }
        result=value+"";
    }

    public void result(String exp,String id)throws RuntimeException
    {
        if(exp!=null)
        {
            throw new RuntimeException();
        }
        result=id;
    }

    public void map(String exp,String id)
    {
        Condition.Assign a=new Condition.Assign();
        a.exp=exp;
        a.id=id;
        conditions.add(a);
    }
    public void cur(String exp,String id)
    {
        if(exp!=null)
        {
            throw new RuntimeException();
        }
        Condition.Current c=new Condition.Current();
        c.id=id;
        conditions.add(c);
    }

    public void round(String exp,String id)
    {
        if(exp!=null)
        {
            throw new RuntimeException();
        }
        Condition.Round r=new Condition.Round();
        r.id=id;
        conditions.add(r);
    }

    public void random(String id,String number)throws NumberFormatException
    {
        Condition.Random r=new Condition.Random();
        r.id=id;
        r.number=number;
        conditions.add(r);
    }

   public void enemy(String exp,String id)throws RuntimeException
   {
       String []numbers=exp.split(",");
       int len=numbers.length;
       if(len==1)
       {
           Condition.Enemy e=new Condition.Enemy();
           e.start=numbers[0];
           e.end=numbers[0];
           e.id=id;
           conditions.add(e);
       }
       else if(len==2)
       {
           Condition.Enemy e=new Condition.Enemy();
           e.start=numbers[0];
           e.end=numbers[1];
           e.id=id;
           conditions.add(e);
       }
       else
       {
           throw new RuntimeException();
       }
   }
   public void self(String exp,String id)throws RuntimeException
   {
       String []numbers=exp.split(",");
       int len=numbers.length;
       if(len==1)
       {
           Condition.Self s=new Condition.Self();
           s.start=numbers[0];
           s.end=numbers[0];
           s.id=id;
           conditions.add(s);
       }
       else if(len==2)
       {
           Condition.Self s=new Condition.Self();
           s.start=numbers[0];
           s.end=numbers[1];
           s.id=id;
           conditions.add(s);
       }
       else
       {
           throw new RuntimeException();
       }
   }


}
