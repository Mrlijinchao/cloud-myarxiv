package com.lijinchao.common;

public enum StatusCode {

    SUCCESS(200), ERROR(400), UNAUTHORIZED(401), PRECONDITION_FAILED(412), INTERNAL_SERVER_ERROR(500);

    private Integer code;

    private StatusCode(Integer code){
        this.code = code;
    }

    public Integer getCode(){
        return this.code;
    }


}
