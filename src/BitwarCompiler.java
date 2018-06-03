public class BitwarCompiler extends DynamicCompiler {
    private Strategy s1,s2;
    private int round;

    public BitwarCompiler(Strategy s1,Strategy s2,int round)
    {
        s1=s1;
        s2=s2;
        round=round;
    }



    @Override
    public Strategy getStrategy1() {
        return s1;
    }

    @Override
    public Strategy getStrategy2() {
        return s2;
    }

    @Override
    public int getRound() {
        return round;
    }

    @Override
    public String getClassName() {
        return "_TEMP_COMP";
    }

    @Override
    public String getMethodNameofStartGame() {
        return "start";
    }

    @Override
    public String getMethodNameofScore1() {
        return "getScore1";
    }

    @Override
    public String getMethodNameofScore2() {
        return "getScore2";
    }

    @Override
    public String getMethodNameofChoose1() {
        return "getChoose1";
    }

    @Override
    public String getMethodNameofCHoose2() {
        return "getChoose2";
    }
}
