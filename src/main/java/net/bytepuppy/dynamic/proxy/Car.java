package net.bytepuppy.dynamic.proxy;

/**
 * @author liuh
 * @email hong.liu@dmall.com
 * @date 2021/3/31
 */
public class Car implements IVehicle {
    @Override
    public void run(int speed) {
        System.out.println("Car is running: " + speed);
    }
}
