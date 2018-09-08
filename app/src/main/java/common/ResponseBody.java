package common;

/**
 * 返回体基本信息
 */
public class ResponseBody {

    public static final String CODE_SUCCESS = "1";  //成功
    public static final String CODE_FAIL = "0";     //失败

    public String code;
    public String errCode;
    public String errMsg;
}
