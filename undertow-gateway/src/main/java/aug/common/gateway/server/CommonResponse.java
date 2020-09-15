package aug.common.gateway.server;

/**
 * @author guoxiaoyong
 * @date 2020/9/11
 */
public class CommonResponse<T> {
    public final int code;
    public final String msg;
    public final T data;

    private CommonResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> CommonResponse<T> makeResponse(int code, String msg, T data) {
        return new CommonResponse<T>(code,msg,data);
    }
}
