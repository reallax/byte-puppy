package net.bytepuppy.redefine.advice.simple;

/**
 * @author liuh
 * @date 2021/3/26
 */
public class Robot {
    private String name = "Casper";
    public String greetUser(String name ) {
        System.out.println("Inside greetUser method . . . ");
        return "Hello " + name + "! I am " + this.name;
    }
}
