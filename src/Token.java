import org.omg.CORBA.PUBLIC_MEMBER;

import java.lang.reflect.Field;

public class Token {
    public String key;//关键词:name、Strategy等
    public String value;//关键词的值
    public Integer rowNum;//行数
    public Integer colNum;//列数

    public static final String STRATEGY="Strategy";
    public static final String COMPETITION="Competition";
    public static final String NAME="name";
    public static final String BRANCH="branch";
    public static final String RESULT="result";
    //public static final String EXTEND="extend";
    public static final String SET="map";//map（value）->i 赋i value的值
    public static final String CUR="cur";//
    public static final String RANDOM="random";
    public static final String ENEMY="enemy";
    public static final String SELF="self";

    //
    public static final String COMBAT="combat";
    public static final String ROUND="round";
    //public static final String LOG="log";

    //
    public static final String EXP="exp";
    public static final String NUMBER="number";
    public static final String FLOAT="float";
    public static final String IDENTIFIED="id";
    public static final String POINT=".";
    public static final String ARROW="->";

    public Token(){}

    public Token(String key,String value,int rowNum,int colNum) {
        this.key = key;
        this.value = value;
        this.colNum = colNum;
        this.rowNum = rowNum;
    }
    public static Token getToken(String word,Integer rowNum,Integer colNum)
    {
        Token token=new Token();
        Class clazz=token.getClass();
        Field[] fields=clazz.getDeclaredFields();//获取clazz的所有字段
        for(Field field:fields) {
            try {
                Object obj = field.get(token);
                if(obj!=null && obj.equals(word))
                {
                    token.key=(String)obj;
                    token.value=(String)obj;
                    token.rowNum=rowNum;
                    token.colNum=colNum;
                    return token;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public String toString(){
        return key+":"+value+"in("+rowNum+","+colNum+")";
    }

}




