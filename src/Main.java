import java.io.*;

public class Main {
    public static void main(String arg[])
    {
        try{
            String filename="Stragedy.txt";
           // File file=new File(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

           // reader=new InputStreamReader(new FileInputStream(file));
            String code="";
            String temp;


            //int tempchar;
           while((temp=br.readLine())!=null)
           {
               code=code+temp;
               //System.out.print(code);
           }
            br.close();
            //System.out.println(code);
            Analyzer.analyze(code, new Analyzer.OnAnalyzeListener() {
                @Override
                public void onError(String error) {
                    System.out.println(error);
                }

                @Override
                public void progress(String str) {
                    System.out.println(str);
                }

                @Override
                public void onFinish(String result) {
                    System.out.println(result);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
