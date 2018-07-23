package com.alibaba.csp.sentinel.adapter.hsf.provider;

import com.alibaba.csp.sentinel.adapter.hsf.HelloService2;

public class HelloService2Impl implements HelloService2 {

    @Override
    public String sayHello2() {
        return "Sentinel test HelloService2.sayHello2()";
    }
}
