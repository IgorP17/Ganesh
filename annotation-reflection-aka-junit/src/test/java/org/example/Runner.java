package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class Runner {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public static void main(String[] args) throws NoSuchMethodException {
        logger.info("Start program");
        // достанем список методов
        Class<TestClass> testClassClass = TestClass.class;
        final Method[] declaredMethods = testClassClass.getDeclaredMethods();
        logger.info("Got methods {}", Arrays.toString(declaredMethods));
        // теперь возьмем только с нужной аннотацией
        ArrayList<Method> methodsForRun = new ArrayList<>(); // почему интересно List<Method> не проканало
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(MyTestAnnotation.class)) {
                methodsForRun.add(declaredMethod);
            }
        }
        logger.info("Method for run {}", methodsForRun);
        // теперь нам нужен конструктор
        // а мы типа не знаем какие там есть
        final Constructor<?>[] declaredConstructors = testClassClass.getDeclaredConstructors();
        logger.info("Got constructors {}", Arrays.toString(declaredConstructors));


        //final Constructor<TestClass> constructor = testClassClass.getDeclaredConstructor(int.class, String.class);

        //
        final Constructor<TestClass> constructor = testClassClass.getDeclaredConstructor(int.class, String.class);
        try {
            TestClass instance = constructor.newInstance(10, "test");

            // Выполняем отобранные методы на нашем экземпляре
            for (Method method : methodsForRun) {
                boolean success = true;
                try {
                    // Удаляем ограничение приватности (если нужно)
                    method.setAccessible(true);
                    method.invoke(instance); // Попытка вызова метода
                } catch (InvocationTargetException ex) {
                    Throwable cause = ex.getCause();
                    if (cause instanceof AssertionError) {
                        logger.error("Test {} failed with: {}", method.getName(), cause.getMessage());
                        success = false;
                    } else {
                        throw ex; // Другие типы исключений выбрасываются наружу
                    }
                } catch (Throwable t) {
                    logger.error("Error invoke method {}: {}", method.getName(), t.getMessage());
                    success = false;
                }

                if (success) {
                    logger.info("Test {} passed", method.getName());
                }
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
