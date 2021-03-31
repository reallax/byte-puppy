package net.bytepuppy.dynamic.proxy;

import com.google.common.base.Joiner;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/31
 */
public class VehicleProxy {
    private IVehicle vehicle;

    public VehicleProxy(IVehicle vehical) {
        this.vehicle = vehical;
    }

    public IVehicle create(){
        final Class<?>[] interfaces = new Class[]{IVehicle.class};
        final VehicleInvocationHandler handler = new VehicleInvocationHandler(vehicle);

        return (IVehicle) Proxy.newProxyInstance(IVehicle.class.getClassLoader(), interfaces, handler);
    }

    public class VehicleInvocationHandler implements InvocationHandler {

        private final IVehicle vehicle;

        public VehicleInvocationHandler(IVehicle vehicle) {
            this.vehicle = vehicle;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {

            System.out.println("--before running...");
            System.out.println("print invoke parameters info: ");
            System.out.println("proxy: " + proxy.getClass().getName());
            System.out.println("method: " + method.getName());
            System.out.println("args: " + (args == null ? "{}" : Joiner.on(",").join(args)));
            Object ret = method.invoke(vehicle, args);
            System.out.println("--after running...");

            return ret;
        }

    }
}
