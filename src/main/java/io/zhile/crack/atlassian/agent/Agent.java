package io.zhile.crack.atlassian.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author pengzhile
 * @version 1.0
 * @link https://zhile.io
 */
public class Agent {

    public static void premain(String args, Instrumentation inst) {

        System.out.println("================================================");
        System.out.println("=================Agent开始准备干活=================");
        System.out.println("================================================");

        System.out.println("================================================");
        System.out.println("=================其中传入的参数有：=================");
        System.out.println(args);
        System.out.println("================================================");

        try {
            inst.addTransformer(new KeyTransformer());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
