package com.alibaba.csp.sentinel.adapter.hsf;

import com.taobao.hsf.domain.HSFRequest;
import com.taobao.hsf.invocation.Invocation;

/**
 * @author leyou
 * @since 2018/7/11
 */
class HsfResourceNameUtil {

    /**
     * Get resource name of the invocation.
     *
     * @param invocation
     * @return the resource name.
     */
    public static String getResourceName(Invocation invocation) {
        StringBuilder buf = new StringBuilder(64);
        String interfaceName = getInterfaceName(invocation);
        buf.append(interfaceName)
            .append(":")
            .append(invocation.getMethodName())
            .append("(");
        boolean isFirst = true;
        for (String argSig : invocation.getMethodArgSigs()) {
            if (!isFirst) {
                buf.append(",");
            }
            buf.append(argSig);
            isFirst = false;
        }
        buf.append(")");
        return buf.toString();
    }

    /**
     * Get interface name of the invocation.
     *
     * @param invocation
     * @return the interface name.
     */
    public static String getInterfaceName(Invocation invocation) {
        HSFRequest request = invocation.getHsfRequest();
        String uniqueName = request.getTargetServiceUniqueName();
        // uniqueName is like: hsf.provider.HelloService:1.0.0.22.daily
        if (uniqueName == null) {
            return "";
        }
        int idx = uniqueName.indexOf(":");
        if (idx == -1) {
            return uniqueName;
        }
        return uniqueName.substring(0, idx);
    }
}
