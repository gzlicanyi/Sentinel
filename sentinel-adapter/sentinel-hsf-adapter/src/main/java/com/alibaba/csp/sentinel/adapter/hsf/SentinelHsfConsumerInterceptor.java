package com.alibaba.csp.sentinel.adapter.hsf;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.SentinelRpcException;

import com.taobao.hsf.annotation.Scope;
import com.taobao.hsf.annotation.Tag;
import com.taobao.hsf.invocation.Invocation;
import com.taobao.hsf.invocation.RPCResult;
import com.taobao.hsf.invocation.SyncInvocationHandler;
import com.taobao.hsf.invocation.SyncInvocationHandlerInterceptor;

/**
 * Synchronized Sentinel aspect for HSF Consumer.<br/>
 * see http://site.alibaba.net/middleware-container/hsf-guide/hsf-guide-book/chapter12-4.html
 *
 * @author leyou
 */
@Tag("client")
@Scope(Scope.Option.PROTOTYPE)
public class SentinelHsfConsumerInterceptor implements SyncInvocationHandlerInterceptor {
    private SyncInvocationHandler next;

    public SentinelHsfConsumerInterceptor() {
        RecordLog.info("Add SentinelHsfConsumerInterceptor to HSF Consumer handler chain");
    }

    @Override
    public void setSyncInvocationHandler(SyncInvocationHandler syncInvocationHandler) {
        this.next = syncInvocationHandler;
    }

    @Override
    public RPCResult invoke(Invocation invocation) throws Throwable {
        String resourceName = HsfResourceNameUtil.getResourceName(invocation);
        String interfaceName = HsfResourceNameUtil.getInterfaceName(invocation);

        Entry interfaceEntry = null;
        Entry methodEntry = null;
        try {
            ContextUtil.enter(resourceName);
            interfaceEntry = SphU.entry(interfaceName, EntryType.OUT);
            methodEntry = SphU.entry(resourceName, EntryType.OUT);
            return next.invoke(invocation);
        } catch (BlockException e) {
            throw new SentinelRpcException(e);
        } catch (Throwable throwable) {
            Tracer.trace(throwable);
            throw throwable;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit();
            }
            if (interfaceEntry != null) {
                interfaceEntry.exit();
            }
            ContextUtil.exit();
        }
    }
}
