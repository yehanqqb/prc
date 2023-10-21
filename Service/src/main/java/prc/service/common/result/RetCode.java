package prc.service.common.result;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RetCode {
    SUCCESS(200, "成功"),
    FAIL(400, "参数有误"),
    UNAUTHORIZED(401, "未登录"),
    NOT_FOUND(404, "不存在"),
    INTERNAL_SERVER_ERROR(500, "有误错误"),

    LOGIN_ERROR(10001,"登录失败！"),
    IMG_CODE_ERROR(10002,"获取验证码失败！"),
    GOOGLE_LOGIN_ERROR(10003,"登录失败，请输入正确的谷歌验证码！"),
    ;

    public int code;

    private String name;

    @JsonCreator
    RetCode(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
