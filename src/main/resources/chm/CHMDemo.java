package chm;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap demo
 */
public class CHMDemo {

    static ConcurrentHashMap map = new ConcurrentHashMap(16);

    static {
        map.put("","");
    }
}
