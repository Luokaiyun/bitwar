import com.sun.corba.se.impl.oa.toa.TOA;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LanguageAnalyze {
    private Set<Strategy> strategies = new HashSet<>();
    private Competition competition;
    private List<Token> list;
    private int rowNum, colNum;
    private String extra;
    int size;


    public void analyze(List<Token> list) throws RuntimeException {
        this.list = list;
        this.strategies.clear();
        int i = 0;
        size = list.size();
        try {
            while (i < size) {
                Token token = list.get(i);
                //只能以 strategy或者competition开始
                if (token.key.equals(Token.STRATEGY)) {
                    i = getStrategy(i + 1);
                } else if (token.key.equals(Token.COMPETITION)) {
                    i = getCompetition(i + 1);
                } else {
                    throw new AnalyzeException(AnalyzeException.UNEXPECTED_TOKEN, token.rowNum, token.colNum, token.value);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new AnalyzeException(AnalyzeException.UNEXPECTED_TOKEN, rowNum, colNum, e.toString());
        } catch (IllegalAccessException e) {
            throw new AnalyzeException(AnalyzeException.UNEXPECTED_TOKEN, rowNum, colNum, e.toString());
        } catch (InvocationTargetException e) {
            throw new AnalyzeException(AnalyzeException.UNEXPECTED_TOKEN, rowNum, colNum, "方法的参数或值不合法：" + extra);
        }
    }

    private int getCompetition(int i) throws RuntimeException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Token token;
        competition = new Competition();
        while (i < size) {
            token = list.get(i);
            i++;
            if (token.key.equals(Token.POINT)) {
                token = list.get(i);
                i++;
                String methodName = token.key;
                if (i < size) {
                    token = list.get(i);
                }
                rowNum = token.rowNum;
                colNum = token.colNum;
                extra = token.value;

                if (token.key.equals(Token.EXP)) {
                    String exp = token.value;
                    i++;

                    if (i < size) {
                        token = list.get(i);
                    }
                    if (token.key.equals(Token.ARROW)) {
                        i++;
                        if (i >= size) {
                            throw new AnalyzeException(AnalyzeException.UNEXPECTED_END, token.rowNum, token.colNum, token.value);
                        }
                        token = list.get(i);
                        i++;
                        if (token.key.equals(Token.NUMBER)) {
                            Integer number = Integer.valueOf(token.value);
                            Method method = competition.getClass().getDeclaredMethod(methodName, String.class, Integer.class);
                            method.invoke(competition, exp, number);
                        } else if (token.key.equals(Token.FLOAT)) {
                            Double number = Double.valueOf(token.value);
                            Method method = competition.getClass().getDeclaredMethod(methodName, String.class, Double.class);
                            method.invoke(competition, exp, number);

                        } else if (token.key.equals(Token.IDENTIFIED)) {
                            String id = token.value;
                            Method method = competition.getClass().getDeclaredMethod(methodName, String.class, String.class);
                            method.invoke(competition, exp, id);
                        } else {
                            i--;
                        }
                    } else {
                        Method method = competition.getClass().getDeclaredMethod(methodName, String.class);
                        method.invoke(competition, exp);
                    }
                } else if (token.key.equals(Token.ARROW)) {
                    i++;
                    if (i >= size)
                        throw new AnalyzeException(AnalyzeException.UNEXPECTED_END, token.rowNum, token.colNum, token.value);
                    token = list.get(i++);

                    if (token.key.equals(Token.NUMBER)) {
                        Integer number = Integer.valueOf(token.value);
                        Method method = competition.getClass().getDeclaredMethod(methodName, String.class, Integer.class);
                        method.invoke(competition, null, number);
                    } else if (token.key.equals(Token.FLOAT)) {
                        Double number = Double.valueOf(token.value);
                        Method method = competition.getClass().getDeclaredMethod(methodName, String.class, Double.class);
                        method.invoke(competition, null, number);
                    } else if (token.key.equals(Token.IDENTIFIED)) {
                        String id = token.value;
                        Method method = competition.getClass().getDeclaredMethod(methodName, String.class, String.class);
                        method.invoke(competition, null, id);
                    } else {
                        i--;
                    }
                } else {
                    Method method = competition.getClass().getDeclaredMethod(methodName);
                    method.invoke(competition);
                }
            } else {
                i--;
                break;
            }

        }//end while
        return i;
    }

    private int getStrategy(int i) throws RuntimeException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Token token;
        Strategy strategy = new Strategy();
        Class clazz = strategy.getClass();

        while (i < size) {
            token = list.get(i);

            if (token.key.equals(Token.POINT)) {
                //region 逐个获取Strategy的操作符
                token = list.get(i);
                String methodName = token.key;
                i++;
                if (i < size) {
                    token = list.get(i);
                }
                rowNum = token.rowNum;
                colNum = token.colNum;
                extra = token.value;
                if (token.key.equals(Token.EXP)) {
                    String exp = token.value;
                    i++;
                    if (i < size) {
                        token = list.get(i);
                    }
                    if (token.key.equals(Token.ARROW)) {
                        i++;
                        //region Arrow

                        if (i >= size) {
                            throw new AnalyzeException(AnalyzeException.UNEXPECTED_END, token.rowNum, token.colNum, token.value);
                        }
                        token = list.get(i);
                        i++;
                        if (token.key.equals(Token.NUMBER)) {
                            Integer number = Integer.valueOf(token.value);
                            Method method = strategy.getClass().getDeclaredMethod(methodName, String.class, Integer.class);
                            method.invoke(strategy, exp, number);
                        } else if (token.key.equals(Token.FLOAT)) {
                            Double number = Double.valueOf(token.value);
                            Method method = strategy.getClass().getDeclaredMethod(methodName, String.class, Double.class);
                            method.invoke(strategy, exp, number);
                        } else if (token.key.equals(Token.IDENTIFIED)) {
                            String id = token.value;
                            Method method = strategy.getClass().getDeclaredMethod(methodName, String.class, String.class);
                            method.invoke(strategy, exp, id);
                        } else {
                            i--;
                        }
                        //endregion
                    } else {
                        Method method = strategy.getClass().getDeclaredMethod(methodName, String.class);
                        method.invoke(strategy, exp);
                    }
                } else if (token.key.equals(Token.ARROW)) {
                    i++;
                    if (i >= size) {
                        throw new AnalyzeException(AnalyzeException.UNEXPECTED_END, token.rowNum, token.colNum, token.value);

                    }
                    token = list.get(i);
                    //region Arrow
                    if (token.key.equals(Token.NUMBER)) {
                        Integer number = Integer.valueOf(token.value);
                        Method method = strategy.getClass().getDeclaredMethod(methodName, String.class, Integer.class);
                        method.invoke(strategy, null, number);
                    } else if (token.key.equals(Token.FLOAT)) {
                        Double number = Double.valueOf(token.value);
                        Method method = strategy.getClass().getDeclaredMethod(methodName, String.class, Double.class);
                        method.invoke(strategy, null, number);
                    } else if (token.key.equals(Token.IDENTIFIED)) {
                        String id = token.value;
                        Method method = strategy.getClass().getDeclaredMethod(methodName, String.class, String.class);
                        method.invoke(strategy, null, id);
                    } else {
                        i--;
                    }
                    //endregion

                } else {
                    Method method = strategy.getClass().getDeclaredMethod(methodName);
                    method.invoke(strategy);
                }


                //endregion

            } else {
                i--;
                break;
            }
        }//end while
        boolean notSame = strategies.add(strategy);
        if (!notSame) {
            throw new AnalyzeException(AnalyzeException.EXIST_STRATEGY, 0, 0, strategy.name);

        }
        return i;
    }


    public Set<Strategy> getStrategies() {
        return strategies;
    }

    public Competition getCompetition() {
        return competition;
    }
}
