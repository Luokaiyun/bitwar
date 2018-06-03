/*code => clazz { op [argv] [to] }
        clazz => 类名        //这里的类名只有两种 strategy和competition
        op=> .操作符         //操作符为保留字符串，表示特定的操作，如random、result等
        argv => (exp)
        to => ->value
        exp => 逻辑表达式|value  //
        value=> 值|变量名*/

import jdk.internal.org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.ArrayList;
import java.util.List;


public class WordAnalyze {
    //变量：id  值：number 浮点数：floa
        private static final int clazz=0,op=1,argv=2,to=3,exp=4,value=5,id=6,number=7,floa=8,opName=9;


        public List<Token>analyze(String code)throws AnalyzerException{
            List<Token> list=new ArrayList<>();

            int i=0,rowNum=1,colNum=0,state=clazz;
            String temp="";
            Token token;


            while(i<code.length())
            {
                char c=code.charAt(i);

                if(c=='\n')
                {
                    rowNum++;
                    colNum=0;
                }

                switch (state)
                {
                    //region case clazz
                    case clazz:
                    {
                        //是单词
                        if(Character.isAlphabetic(c))
                        {
                            colNum++;
                            i++;
                            temp=temp+c;
                        }
                        //是 .或 空格 ，则temp转token，转入op
                        else if(c=='.')
                        {
                          token=Token.getToken(temp,rowNum,colNum);
                          if(token==null)
                          {
                              throw new AnalyzeException(AnalyzeException.NO_SUCH_CLAZZ,rowNum,colNum,temp);
                          }
                          else
                          {
                              list.add(token);
                              temp="";
                          }
                          state=op;
                        }
                        //是空格且temp为空，无视
                        else if(Character.isWhitespace(c))
                        {
                            if(temp.equals(""))
                            {
                                i++;
                                colNum++;
                            }
                            else
                            {
                                token=Token.getToken(temp,rowNum,colNum);
                                if(token==null)
                                {
                                    throw new AnalyzeException(AnalyzeException.NO_SUCH_CLAZZ,rowNum,colNum,temp);
                                }
                                else
                                {
                                    list.add(token);
                                    temp="";
                                }
                                state=op;
                            }
                        }
                        else
                        {
                            //报错
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,c+"");
                        }
                        break;

                    }
                    //endregion

                    //region case op

                        //如果是 .，完成token，转为op_name
                        //如果是字母，形成操作符
                        //无视空格
                        //其他报错
                    case op:
                    {
                        if(c==',')
                        {
                            token=new Token(Token.POINT,Token.POINT,rowNum,colNum);
                            list.add(token);
                            i++;
                            colNum++;
                            state=opName;
                        }
                        else if(Character.isAlphabetic(c))
                        {
                            state=clazz;
                        }
                        else if(Character.isWhitespace(c))
                        {
                            i++;
                            colNum++;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,c+"");
                        }
                        break;

                    }
                    //endregion

                    //region case opName

                        //是字母的话形成操作符
                        //如果是空格前是 . 的话，无视
                        //如果是 . 回到状态op
                        //如果是“（”，转入argv状态
                        //如果是“-”,转入to状态
                        //其他报错
                    case opName:
                    {
                        if(Character.isAlphabetic(c))
                        {
                            temp=temp+c;
                            colNum++;
                            i++;
                        }
                        else if(Character.isWhitespace(c))
                        {
                            if(temp=="")
                            {
                                colNum++;
                                i++;
                            }
                            else
                            {
                                token=Token.getToken(temp,rowNum,colNum);
                                if(token==null)
                                {
                                    throw new AnalyzeException(AnalyzeException.NO_SUCH_OPERATOR,rowNum,colNum,temp);
                                }
                                else
                                {
                                    list.add(token);
                                    temp="";
                                }
                                state=argv;
                            }
                        }
                        else if(c=='.')
                        {
                          token=Token.getToken(temp,rowNum,colNum);
                          if(token==null)
                          {
                              throw new AnalyzeException(AnalyzeException.NO_SUCH_OPERATOR, rowNum,colNum,temp);
                          }
                          else
                          {
                              list.add(token);
                              temp="";
                          }
                          state=op;
                        }
                        else if(c=='(')
                        {
                            token=Token.getToken(temp,rowNum,colNum);
                            if(token==null)
                            {
                                throw new AnalyzeException(AnalyzeException.NO_SUCH_OPERATOR, rowNum,colNum,temp);

                            }
                            else
                            {
                                list.add(token);
                                temp="";
                            }
                            state=argv;
                        }
                        else if(c=='-')
                        {
                            token=Token.getToken(temp,rowNum,colNum);
                            if(token==null)
                            {
                                throw new AnalyzeException(AnalyzeException.NO_SUCH_OPERATOR,rowNum,colNum,temp);
                            }
                            else
                            {
                                list.add(token);
                                temp="";
                            }
                            state=to;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,c+"");
                        }
                        break;
                    }
                    //endregion

                    //region case argv
                    case argv:
                    {
                        //是“（”的话，进入exp
                        //是“."的话，进入op
                        //是 空格的话无视
                        //是“-”的话，转to
                        if(c=='(')
                        {
                            state=exp;
                            colNum++;
                            i++;
                        }
                        else if(c=='.')
                        {
                            state=op;
                        }
                        else if(Character.isWhitespace(c))
                        {
                            colNum++;
                            i++;
                        }
                        else if(c=='-')
                        {
                            state=to;
                        }
                        break;

                    }
                    //endregion

                    //region case exp
                    case exp:
                    {
                        //如果是“）”，转入to
                        if(c==')')
                        {
                            token=new Token(Token.EXP,temp,rowNum,colNum);
                            list.add(token);

                            temp="";
                            state=to;
                        }
                        else
                        {
                            temp=temp+c;

                        }
                        colNum++;
                        i++;
                        break;
                    }
                    //endregion

                    //region case to
                    case to:
                    {
                        //是“.”的话，则进入op
                        if(c=='.')
                        {
                            state=op;
                        }
                        //如果是“-”，看下一位是不是“>”，不是则报错，是的话转value
                        else if(c=='-')
                        {
                           i++;
                           colNum++;
                           if(i>code.length())
                           {
                               throw new AnalyzeException(AnalyzeException.UNEXPECTED_END,rowNum,colNum,temp);
                           }
                           c=code.charAt(i);
                           if(c=='>')
                           {
                               list.add(new Token(Token.ARROW,Token.ARROW,rowNum,colNum));
                               state=value;
                               colNum++;
                               i++;
                           }
                           else
                           {
                               throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,"-"+c);
                           }
                        }
                        //无视空格
                        else if(Character.isWhitespace(c))
                        {
                            i++;
                            colNum++;
                        }
                        //字母
                        else if(Character.isAlphabetic(c))
                        {
                            state=clazz;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,c+"");
                        }
                        break;
                    }
                    //endregion

                    //region case value
                    case value:
                    {
                        //value=> 值|变量名
                        //判断是不是标识符的首字母，是的话转id
                        if(Character.isJavaIdentifierStart(c))
                        {
                            temp=temp+c;
                            i++;
                            colNum++;
                            state=id;
                        }
                        //是数字，则转number
                        if(Character.isDigit(c))
                        {
                            state=number;
                        }
                        //无视空格
                        if(Character.isWhitespace(c))
                        {
                            i++;
                            colNum++;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,temp);
                        }
                        break;
                    }
                    //endregion

                    //region case id
                    case id:
                    {
                        if(Character.isJavaIdentifierPart(c))
                        {
                            temp=temp+c;
                            i++;
                            colNum++;
                        }
                        //空格结束输入,转入op，如果是 . ，进入op
                        else if(Character.isWhitespace(c)||c=='.')
                        {
                            token=new Token(Token.IDENTIFIED,temp,rowNum,colNum);
                            list.add(token);
                            i++;
                            colNum++;
                            temp="";
                            state=op;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,c+"");
                        }

                        break;
                    }
                    //endregion

                    //region case number
                    case number:
                    {
                        //如果是数字，temp+c;
                        if(Character.isDigit(c))
                        {
                            temp=temp+c;
                            i++;
                            colNum++;
                        }

                        //如果是 . ，转到floa；
                        else if(c=='.')
                        {
                            temp=temp+c;
                            i++;
                            colNum++;
                            state=floa;
                        }
                        //空格结束输入，转入op；
                        else if (Character.isWhitespace(c))
                        {
                            token=new Token(Token.NUMBER,temp,rowNum,colNum);
                            list.add(token);
                            temp="";
                            i++;
                            colNum++;
                            state=op;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,temp+c);
                        }
                        break;

                    }
                    //endregion

                    //region case floa
                    case floa:
                    {

                        //如果是数字，存入temp
                        if(Character.isDigit(c))
                        {
                            temp=temp+c;
                            i++;
                            colNum++;
                        }
                        //空格结束输入，转入op；如果是.，转入op
                        if(Character.isWhitespace(c)||c=='.')
                        {
                            token=new Token(Token.FLOAT,temp,rowNum,colNum);
                            list.add(token);
                            temp="";
                            i++;
                            colNum++;
                            state=op;
                        }
                        else
                        {
                            throw new AnalyzeException(AnalyzeException.UNKNOWN_TOKEN,rowNum,colNum,temp+c);
                        }
                        break;
                    }
                    //endregion
                }
            }
            return list;

        }

}
