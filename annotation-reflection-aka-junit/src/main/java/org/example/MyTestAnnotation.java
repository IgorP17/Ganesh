package org.example;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// типа видим в рантайме
@Retention(RetentionPolicy.RUNTIME)
public @interface MyTestAnnotation {
}
