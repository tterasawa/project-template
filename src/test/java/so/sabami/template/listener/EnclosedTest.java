/*-
 * Copyright (c) 2004-2015 GMO-RESEARCH Inc. All rights reserved.
 * Redistribution in source and binary forms, with or without
 * modification, is limited by contract. 
 */
package so.sabami.template.listener;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * @author usr160056
 * @since 2015/03/04
 */
@RunWith(Enclosed.class)
public class EnclosedTest {

    public static class classOne {
        @Test
        public void test() {
            
        }
    }

    public static class classTwo {
        public void test() {
            
        }
    }

    static class classThree {
        
    }

}
