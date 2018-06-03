

public class AnalyzeException extends RuntimeException {
    //错误所在列号
    public int colNum;
    //错误所在行号
    public int rowNum;
    //错误码
    public int errorCode;
    //错误出现时的词语
    public String extra;


    public AnalyzeException(int errorCode,int rowNum,int colNum,String extra)
    {
        super(getError(errorCode));
        this.rowNum=rowNum;
        this.colNum=colNum;
        this.errorCode=errorCode;
        this.extra=extra;

    }
    //错误的类名
    public static final int NO_SUCH_CLAZZ=-1;
    //错误的操作符
    public static final int NO_SUCH_OPERATOR=-2;
    //无法识别的token
    public static final int UNKNOWN_TOKEN=-3;
    //非法结束
    public static final int UNEXPECTED_END=-4;
    //非法token
    public static final int UNEXPECTED_TOKEN=-5;
    //已存在同名的策略
    public static final int EXIST_STRATEGY=-6;
    public static final int IO = -7;
    public static final int COMPILE = -8;



    private static String getError(int code) {
        return getError(code,"");
    }

    private static String getError(int code, String name) {
    switch (code)
    {
        case NO_SUCH_CLAZZ:
            return "非法类名"+name;
        case NO_SUCH_OPERATOR:
            return "非法操作符"+name;
        case UNKNOWN_TOKEN:
            return "未知符号"+name;
        case UNEXPECTED_END:
            return "非法结束"+name;
        case UNEXPECTED_TOKEN:
            return "非法出现的符号"+name;
        case EXIST_STRATEGY:
            return "存在同名的策略 " + name;
        case IO:
            return "文件读写出错：" + name;
        case COMPILE:
            return "编译错误：\n" + name;
            default:
                return "未知错误";
    }

    }
}
