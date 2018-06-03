import jdk.internal.org.objectweb.asm.tree.analysis.AnalyzerException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analyzer {
    private static boolean running=false;

    public static void analyze(String code,OnAnalyzeListener onAnalyzeListener)
    {
        if(running)
        {
            return;
        }
        synchronized (Analyzer.class)
        {
            if(!running) {
                running = true;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WordAnalyze wordAnalyze=new WordAnalyze();
                        LanguageAnalyze languageAnalyze=new LanguageAnalyze();
                        // StrategyAnalyze strategyAnalyze=new StrategyAnalyze();

                        try {
                            List<Token> list=wordAnalyze.analyze(code);
                            StringBuffer sb=new StringBuffer();
                            sb.append("分析过程：\n");
                            for(Token token:list)
                            {
                                sb.append(token.toString()+"\n");
                            }
                            sb.append("\n");
                            languageAnalyze.analyze(list);
                            sb.append("分析结果： \n");
                            for(Strategy s:languageAnalyze.getStrategies())
                            {
                                sb.append(s+"\n");
                            }
                            onAnalyzeListener.onFinish(sb.toString());
                            onAnalyzeListener.progress("正在比赛...、");
                            // strategyAnalyze.anlyze(languageAnalyze.getStrategies());

                            Competition competition=languageAnalyze.getCompetition();
                            if(competition!=null)
                            {
                                Strategy s1=competition.getStrategy1(languageAnalyze.getStrategies());
                                if(competition.s1==null)
                                {
                                    //所有策略两两比较
                                    Map<String,Long> map=new HashMap<>();
                                    for(Strategy a:languageAnalyze.getStrategies()) {
                                        for (Strategy b : languageAnalyze.getStrategies()) {
                                            if (!a.name.equals(b.name)) {
                                                String result = combat(a, b, competition.round, map);
                                                sb.append(result);
                                            }
                                        }
                                    }
                                    sb.append("分数统计:\n");
                                    for(Map.Entry<String,Long>entry:map.entrySet())
                                    {
                                        sb.append(entry.getKey()+":"+entry.getValue()+"分\n");
                                    }
                                }else if (s1!=null){
                                    if(competition.s2==null){
                                        //s1和所有策略比赛
                                        for(Strategy s2:languageAnalyze.getStrategies())
                                        {
                                            if(!s2.name.equals(s1.name)){
                                                String result=combat(s1,s2,competition.round);
                                                sb.append(result);
                                            }
                                        }
                                    }else{
                                        //s1和s2比赛
                                        Strategy s2=competition.getStrategy2(languageAnalyze.getStrategies());
                                        if(s2==null){
                                            sb.append("\n不存在对应的比赛的策略："+competition.s2);
                                        }else{
                                            String result=combat(s1,s2,competition.round);
                                            sb.append(result);
                                        }
                                    }
                                }else{
                                    sb.append("\n不存在对应的比赛策略："+competition.s1);
                                }
                            }//end if(competion!=null)
                            onAnalyzeListener.onFinish(sb.toString());
                        } catch (AnalyzerException |InstantiationException |InvocationTargetException
                                |NoSuchMethodException|IllegalAccessException|ClassNotFoundException e) {
                            e.printStackTrace();
                            onAnalyzeListener.onError(e.toString());
                        } finally {
                            running=false;
                        }
                    }
                }).start();
            }else
            {
                onAnalyzeListener.onError("正在运行中");
            }

        }

    }
    private static String combat(Strategy s1,Strategy s2,int round)throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException, InstantiationException, AnalyzeException, ClassNotFoundException
    {
        return combat(s1,s2,round,null);
    }
    private static String combat(Strategy s1,Strategy s2,int round,Map<String,Long> record)throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException, AnalyzeException, ClassNotFoundException
    {
        StringBuffer sb=new StringBuffer();
        BitwarCompiler c=new BitwarCompiler(s1,s2,round);
        Class<?> A=c.compile();
        Object a=A.newInstance();
        A.getDeclaredMethod(c.getMethodNameofStartGame()).invoke(a);
        List<Integer> score1=(List<Integer>)A.getDeclaredMethod(c.getMethodNameofScore1()).invoke(a);
        List<Integer> score2=(List<Integer>)A.getDeclaredMethod(c.getMethodNameofScore2()).invoke(a);
        sb.append("比赛结果（"+s1.name+"-"+s2.name+"):\n");
        long sum1=0,sum2=0;
        for(int i=0;i<score1.size();i++)
        {
            sum1=sum1+score1.get(i);
            sum2=sum2+score2.get(i);
            sb.append(score1.get(i)+"-"+score2.get(i)+"\n");
        }
        if(record!=null)
        {
            Long sum=record.get(s1.name);
            if(sum==null)
            {
                sum=01L;
            }
            record.put(s1.name,sum1+sum);

            sum=record.get(s2.name);
            if(sum==null)
            {
                sum=01L;
            }
            record.put(s2.name,sum2+sum);
        }
        sb.append(s1.name+"总分："+sum1+"\n");
        sb.append(s2.name+"总分："+sum2+"\n\n");
        return sb.toString();
    }
    public interface OnAnalyzeListener {
        void onError(String error);

        void progress(String str);

        void onFinish(String result);
    }
}
