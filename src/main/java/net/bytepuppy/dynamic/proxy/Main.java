package net.bytepuppy.dynamic.proxy;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/31
 */
public class Main {

    public static void main(String[] args) {

        IVehicle car = new Car();
        VehicleProxy proxy = new VehicleProxy(car);

        IVehicle proxyObj = proxy.create();
        proxyObj.run(60);
    }
}
