package prc.service.common.result;

public class RetResponse {
    private final static String SUCCESS = "成功";
    private final static String ERROR = "失败";

    public static <T> RetResult<T> makeOKRsp() {
        return new RetResult<T>().setCode(RetCode.SUCCESS).setMsg(SUCCESS);
    }

    public static <T> RetResult<T> makeOKRsp(T data) {
        return new RetResult<T>().setCode(RetCode.SUCCESS).setMsg(SUCCESS).setData(data);
    }

    public static <T> RetResult<T> makeErrRsp(String message) {
        return new RetResult<T>().setCode(RetCode.FAIL).setMsg(message);
    }

    public static <T> RetResult<T> makeErrRsp(int code, String message) {
        return new RetResult<T>().setCode(code).setMsg(message);
    }

    public static <T> RetResult<T> makeRsp(int code, String msg) {
        return new RetResult<T>().setCode(code).setMsg(msg);
    }

    public static <T> RetResult<T> makeRsp(int code, String msg, T data) {
        return new RetResult<T>().setCode(code).setMsg(msg).setData(data);
    }
}
