import sun.tools.java.ClassNotFound;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public abstract class DynamicCompiler {



    public Class<?> compile() throws AnalyzeException, ClassNotFoundException, IllegalArgumentException, SecurityException {
        Class<DynamicCompiler> c = DynamicCompiler.class;
        final String sourceCode = getCode();
        String dir=System.getProperty("user.dir")+ File.separatorChar+"tmp";
        File parent=new File(dir);
        parent.mkdirs();
        File file=new File(parent,getClassName()+".java");
        BufferedWriter bw=null;
        try
        {
            file.createNewFile();
            FileWriter fw=new FileWriter(file);
            bw=new BufferedWriter(fw);
            bw.write(sourceCode);
            bw.flush();

        } catch (IOException e) {
            throw new AnalyzeException(AnalyzeException.IO,0,0,e.toString());
        }
        finally {
            try
            {
                if(bw!=null)
                {
                    bw.close();
                }

            } catch (IOException e) {
                throw new AnalyzeException(AnalyzeException.IO,0,0,e.toString());
            }
        }
        try {
            //当前编译器
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            com.sun.tools.javac.Main.compile(new String[]{file.getAbsolutePath()}, pw);
            String error = baos.toString();
            if (!error.equals("")) {
                throw new AnalyzeException(AnalyzeException.COMPILE, 0, 0, error);
            }
            Class<?> target = new BitWarClassLoader(dir).findClass(getClassName());
            return target;
        }
        catch (ClassNotFoundException|IllegalArgumentException|SecurityException e)
        {
            throw e;
        }
    }
    private Map<String, Class<? extends Object>> ids = new HashMap<>();

    public String getCode() {
        Strategy s1=getStrategy1();
        Strategy s2=getStrategy2();
        int round=getRound();
        StringBuffer sb=new StringBuffer();
        // 策略s1得出的比赛得分列表 _r1
        sb.append("\tprivate List<Integer> _r1 = new ArrayList<>();\n");
        // 策略s2得出的比赛得分列表_r2
        sb.append("\tprivate List<Integer> _r2 = new ArrayList<>();\n");
        // 策略s1各回合的选择
        sb.append("\tprivate List<Integer> _c1 = new ArrayList<>();\n");
        // 策略s2各回合的选择
        sb.append("\tprivate List<Integer> _c2 = new ArrayList<>();\n\n");
        sb.append("\tpublic void " + getMethodNameofStartGame().trim() + "(){\n");// start
        sb.append("\t\t_c1.add(0);\n");
        sb.append("\t\t_c2.add(0);\n");
        sb.append("\t\tfor(int _i = 1;_i<=" + round + ";_i++){\n");// for(){
        sb.append("\t\t\tint _i1 = getResult1(_i);\n");
        sb.append("\t\t\tint _i2 = getResult2(_i);\n");
        sb.append("\t\t\t_c1.add(_i1);\n");
        sb.append("\t\t\t_c2.add(_i2);\n");
        sb.append("\t\t\tif(_i1 == 0 && _i2 == 0){\n");// if{
        sb.append("\t\t\t\t_r1.add(1);\n");
        sb.append("\t\t\t\t_r2.add(1);\n");
        sb.append("\t\t\t}\n");// if}
        sb.append("\t\t\telse if(_i1 == 1 && _i2 == 1){\n");// if{
        sb.append("\t\t\t\t_r1.add(3);\n");
        sb.append("\t\t\t\t_r2.add(3);\n");
        sb.append("\t\t\t}\n");// if}
        sb.append("\t\t\telse if(_i1 == 1 && _i2 == 0){\n");// if{
        sb.append("\t\t\t\t_r1.add(0);\n");
        sb.append("\t\t\t\t_r2.add(5);\n");
        sb.append("\t\t\t}\n");// if}
        sb.append("\t\t\telse if(_i1 == 0 && _i2 == 1){\n");// if{
        sb.append("\t\t\t\t_r1.add(5);\n");
        sb.append("\t\t\t\t_r2.add(0);\n");
        sb.append("\t\t\t}\n");// if}
        sb.append("\t\t\telse{\n");// if{
        sb.append("\t\t\t\t_r1.add(-1);\n");
        sb.append("\t\t\t\t_r2.add(-1);\n");
        sb.append("\t\t\t}\n");// if}
        sb.append("\t\t}\n");// for}
        sb.append("\t}\n\n");// start
        sb.append("\tprivate int getResult1(int _round) {\n");// getResult1
        ids.clear();
        sb.append("java.lang.Integer _tmp_step;\n");
        for(Condition c:s1.conditions)
        {
            //分支语句 if( expression ) return id/result;
            if(c instanceof Condition.Branch)
            {
                Condition.Branch e=(Condition.Branch)c;
                sb.append("\t\tif (" + e.expression + ")\n");
                sb.append("\t\t\treturn " + (e.result == null ? e.id : e.result) + ";\n");
            }
            //赋值语句 id=exp
            else if(c instanceof Condition.Assign)
            {
                Condition.Assign a=(Condition.Assign) c;
                if(!ids.containsKey(a.id))
                {
                    sb.append("\t\tjava.lang.Integer " + a.id + " = (" + a.exp + ");\n");
                    ids.put(a.id, Integer.class);
                }
                else
                {
                    sb.append("\t\t"+a.id+"=("+a.exp+");\n");
                }
            }
            //id=round;
            else if(c instanceof Condition.Round)
            {
                Condition.Round r=(Condition.Round) c;
                if(!ids.containsKey(r.id))
                {
                    sb.append("\t\tjava.lang.Integer " + r.id + " = " + round + ";\n");
                    ids.put(r.id, Integer.class);
                }
                else
                {
                    sb.append("\t\t" + r.id + " = " + round + ";\n");
                }
            }
            //id=random(i)
            else if(c instanceof Condition.Random)
            {
                Condition.Random r=(Condition.Random) c;
                if(!ids.containsKey(r.id))
                {
                    sb.append("\t\tjava.lang.Integer " + r.id + " = (int) (" + r.number + " * Math.random());\n");
                }
                else
                {
                    sb.append("\t\t" + r.id + " = (int) (" + r.number + " * Math.random());\n");
                }
            }
            //id=Current
            else if(c instanceof Condition.Current)
            {
                Condition.Current cur=(Condition.Current) c;
                if(!ids.containsKey(cur.id))
                {
                    sb.append("\t\tjava.lang.Integer " + cur.id + " = _round;\n");
                    ids.put(cur.id, Integer.class);
                }
                else
                {
                    sb.append("\t\t"+cur.id+"=_round;\n");
                }
            }
            //id=sum(_c2[start,end])
            else if(c instanceof Condition.Enemy)
            {
                Condition.Enemy e=(Condition.Enemy)c;
                if(!ids.containsKey(e.id))
                {
                    sb.append("\t\tjava.lang.Integer"+e.id+";\n");
                    ids.put(e.id,Integer.class);
                }
                sb.append("\t\t"+e.id+"=0;\n");
                sb.append("\t\t_tmp_step = " + "((" + e.end + ") > (" + e.start + ")) ? 1 : -1;\n");
                sb.append("\t\tfor(int _t_c2 = " + e.start + "; _t_c2 != (" + e.end
                        + ") + _tmp_step; _t_c2 += _tmp_step){\n");
                sb.append("\t\t\t" + e.id + " += (_c2.get(_t_c2).equals(1) ? 1 : 0);\n");
                sb.append("\t\t}\n");
            }
            //id=sum(_c1[start,end])
            else if(c instanceof Condition.Self)
            {
                Condition.Self s=(Condition.Self)c;
                if(!ids.containsKey(s.id))
                {
                    sb.append("\t\tjava.lang.Integer"+s.id+";\n");
                    ids.put(s.id,Integer.class);
                }
                sb.append("\t\t" + s.id + " = 0;\n");
                sb.append("\t\t_tmp_step = " + "((" + s.end + ") > (" + s.start + ")) ? 1 : -1;\n");
                sb.append("\t\tfor(int _t_c1 = " + s.start+ "; _t_c1 != (" + s.end
                        + ") + _tmp_step; _t_c1 += _tmp_step){\n");
                sb.append("\t\t\t" + s.id + " += (_c1.get(_t_c1).equals(1) ? 1 : 0);\n");
                sb.append("\t\t}\n");
            }
        }
        sb.append("\t\treturn " + s1.result + ";\n");
        sb.append("\t}\n\n");// getResult1
        sb.append("\tprivate int getResult2(int _round) {\n");// getResult1
        ids.clear();
        sb.append("java.lang.Integer _tmp_step;\n");
        for (Condition c : s2.conditions) {
            /**
             * 分支语句: if( expression ) return id/result;
             */
            if (c instanceof Condition.Branch) {
                Condition.Branch e = (Condition.Branch) c;
                sb.append("\t\tif (" + e.expression + ")\n");
                sb.append("\t\t\treturn " + (e.result == null ? e.id : e.result) + ";\n");
            }
            /**
             * 赋值语句：id = exp;
             */
            else if (c instanceof Condition.Assign) {
                Condition.Assign a = (Condition.Assign) c;
                if (!ids.containsKey(a.id)) {
                    sb.append("\t\tjava.lang.Integer " + a.id + " = (" + a.exp + ");\n");
                    ids.put(a.id, Integer.class);
                } else {
                    sb.append("\t\t" + a.id + " = (" + a.exp + ");\n");
                }
            }
            /**
             * 赋值语句: id = round;
             */
            else if (c instanceof Condition.Round) {
                Condition.Round r = (Condition.Round) c;
                if (!ids.containsKey(r.id)) {
                    sb.append("\t\tjava.lang.Integer " + r.id + " = " + round + ";\n");
                    ids.put(r.id, Integer.class);
                } else {
                    sb.append("\t\t" + r.id + " = " + round + ";\n");
                }
            }
            /**
             * 赋值语句： id = random(i);
             */
            else if (c instanceof Condition.Random) {
                Condition.Random r = (Condition.Random) c;
                if (!ids.containsKey(r.id)) {
                    sb.append("\t\tjava.lang.Integer " + r.id + " = (int) (" + r.number + " * Math.random());\n");
                } else {
                    sb.append("\t\t" + r.id + " = (int) (" + r.number + " * Math.random());\n");
                }
            }
            /**
             * 赋值语句： id = current;
             */
            else if (c instanceof Condition.Current) {
                Condition.Current cur = (Condition.Current) c;
                if (!ids.containsKey(cur.id)) {
                    sb.append("\t\tjava.lang.Integer " + cur.id + " = _round;\n");
                    ids.put(cur.id, Integer.class);
                } else {
                    sb.append("\t\t" + cur.id + " = _round;\n");
                }
            }
            /**
             * 赋值语句： id = sum( _c1[from...to] );
             */
            else if (c instanceof Condition.Enemy) {
                Condition.Enemy e = (Condition.Enemy) c;
                if (!ids.containsKey(e.id)) {
                    sb.append("\t\tjava.lang.Integer " + e.id + ";\n");
                    ids.put(e.id, Integer.class);
                }
                sb.append("\t\t" + e.id + " = 0;\n");
                sb.append("\t\t_tmp_step = " + "((" + e.end + ") > (" + e.start+ ")) ? 1 : -1;\n");
                sb.append("\t\tfor(int _t_c1 = " + e.start + "; _t_c1 != (" + e.end
                        + ") + _tmp_step; _t_c1 += _tmp_step){\n");
                sb.append("\t\t\t" + e.id + " += (_c1.get(_t_c1).equals(1) ? 1 : 0);\n");
                sb.append("\t\t}\n");
            }
            /**
             * 赋值语句： id = sum( _c2[from...to] );
             */
            else if (c instanceof Condition.Self) {
                Condition.Self s = (Condition.Self) c;
                if (!ids.containsKey(s.id)) {
                    sb.append("\t\tjava.lang.Integer " + s.id + ";\n");
                    ids.put(s.id, Integer.class);
                }
                sb.append("\t\t" + s.id + " = 0;\n");
                sb.append("\t\t_tmp_step = " + "((" + s.end + ") > (" + s.start + ")) ? 1 : -1;\n");
                sb.append("\t\tfor(int _t_c2 = " + s.start + "; _t_c2 != (" + s.end
                        + ") + _tmp_step; _t_c2 += _tmp_step){\n");
                sb.append("\t\t\t" + s.id + " += (_c2.get(_t_c2).equals(1) ? 1 : 0);\n");
                sb.append("\t\t}\n");
            }
        }
        sb.append("\t\treturn " + s2.result + ";\n");
        sb.append("\t}\n\n");// getResult1
        sb.append("\tpublic List<Integer> " + getMethodNameofChoose1().trim() + "(){\n");
        sb.append("\t\treturn _c1;\n");
        sb.append("\t}\n");
        sb.append("\tpublic List<Integer> " + getMethodNameofCHoose2().trim() + "(){\n");
        sb.append("\t\treturn _c2;\n");
        sb.append("\t}\n");
        sb.append("\tpublic List<Integer> " + getMethodNameofScore1().trim() + "(){\n");
        sb.append("\t\treturn _r1;\n");
        sb.append("\t}\n");
        sb.append("\tpublic List<Integer> " + getMethodNameofScore2().trim() + "(){\n");
        sb.append("\t\treturn _r2;\n");
        sb.append("\t}\n");
        sb.append("}\n");// class
        return sb.toString();
    }

    public abstract Strategy getStrategy1();
    public abstract Strategy getStrategy2();
    public abstract int getRound();
    public abstract String getClassName();
    public abstract String getMethodNameofStartGame();
    public abstract String getMethodNameofScore1();
    public abstract String getMethodNameofScore2();
    public abstract String getMethodNameofChoose1();
    public abstract String getMethodNameofCHoose2();

}
