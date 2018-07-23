package com.alibaba.csp.sentinel.adapter.hsf;

import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;

import com.taobao.hsf.annotation.Scope;
import com.taobao.hsf.annotation.Tag;
import com.taobao.hsf.domain.HSFResponse;
import com.taobao.hsf.invocation.AbstractInvocationHandlerInterceptor;
import com.taobao.hsf.invocation.Invocation;
import com.taobao.hsf.invocation.RPCResult;
import com.taobao.hsf.util.HSFConstants;
import com.taobao.hsf.util.concurrent.Futures;
import com.taobao.hsf.util.concurrent.ListenableFuture;
import com.taobao.hsf.util.concurrent.SettableFuture;

/**
 * <p>
 * Sentinel aspect for HSF Provider. This interceptor seems like Asynchronous, because its
 * {@link #invoke(Invocation)} method returns a {@link ListenableFuture}, but in fact,
 * whether the invocation is asynchronous or not depending on the Provider Bean's definition.
 * Here the asynchronous {@link #invoke(Invocation)} is just an asynchronous wrapper of the
 * real backward Provider's invocation.
 * </p>
 * <p>
 * So the Sentinel RT statistic will be shorter than the real provider's method ONLY when the
 * Provider is defined as asynchronous.
 * </p>
 *
 * see http://site.alibaba.net/middleware-container/hsf-guide/hsf-guide-book/chapter12-4.html
 *
 * @author leyou
 */
@Tag("server")
@Scope(Scope.Option.PROTOTYPE)
public class SentinelHsfProviderInterceptor extends AbstractInvocationHandlerInterceptor {

    public SentinelHsfProviderInterceptor() {
        RecordLog.info("Add SentinelHsfProviderInterceptor to HSF Provider handler chain");
    }

    @Override
    public ListenableFuture<RPCResult> invoke(Invocation invocation) throws Throwable {
        String resourceName = HsfResourceNameUtil.getResourceName(invocation);
        String interfaceName = HsfResourceNameUtil.getInterfaceName(invocation);
        String clientAppName = (String)invocation.getRequestProp(HSFConstants.CONSUMER_APP_NAME);
        if (clientAppName == null) {
            clientAppName = "";
        }
        Entry interfaceEntry = null;
        Entry methodEntry = null;
        try {
            ContextUtil.enter(resourceName);
            interfaceEntry = SphU.entry(interfaceName, EntryType.IN);
            methodEntry = SphU.entry(resourceName, EntryType.IN, 1, invocation.getMethodArgs());
            return delegate.invoke(invocation);
        } catch (BlockException e) {
            HSFResponse hsfResponse = new HSFResponse();
            /**
             * Use {@link BlockException.THROW_OUT_EXCEPTION} to represent all kinds of {@link BlockException}s
             */
            hsfResponse.setAppResponse(BlockException.THROW_OUT_EXCEPTION);

            RPCResult rpcResult = new RPCResult();
            rpcResult.setHsfResponse(hsfResponse);
            SettableFuture<RPCResult> defaultRPCFuture = Futures.createSettableFuture();
            defaultRPCFuture.set(rpcResult);
            return defaultRPCFuture;
        } catch (Throwable throwable) {
            Tracer.trace(throwable);
            throw throwable;
        } finally {
            if (methodEntry != null) {
                methodEntry.exit(1, invocation.getMethodArgs());
            }
            if (interfaceEntry != null) {
                interfaceEntry.exit();
            }
            ContextUtil.exit();
        }
    }
}
