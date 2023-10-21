package prc.service.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import prc.service.common.result.RetResponse;
import prc.service.common.result.RetResult;


@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public RetResult bizExceptionHandler(BizException e) {
        log.error("发生业务异常！原因是：{}", e);
        return RetResponse.makeErrRsp(e.getErrorCode(), e.getErrorMsg());
    }
}
