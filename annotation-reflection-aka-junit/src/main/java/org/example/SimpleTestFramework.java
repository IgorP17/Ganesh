package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     Домашнее задание.
 * Необходимо добавить поддержку задания имение теста в аннотации
 * Нужно сделать следующее:
 * 1. Добавить аннотацию @DisplayName, которую можно повесить на тестовый метод
 * 2. Внутри аннотация должна содержать строку(нужно разобраться как передавать значения в аннотацию)
 * 3. В TestFramework нужно будет получить это строку и использовать для отображения результатов теста.
 *
 * Пример:
 * Без аннотации:
 *         {@code @Test
 *         public void testSomething() {
 *             System.out.println("Выполняем тестовый метод");
 *         }}
 * В выводе отображается как:
 * Test: testSomething is success
 *
 * С аннотацией:
 *          {@code @Test
 *                 @DisplayName("otherName")
 *           public void testSomething() {
 *              System.out.println("Выполняем тестовый метод");
 *          }}
 * В выводе отобразиться как:
 * Test: otherName is success
 * </pre>
 */
public class SimpleTestFramework {

    public static void main(String[] args)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        new TestFramework().runTests(TestClassExample.class);
    }

    static class TestFramework {

        public void runTests(Class<?> testClass)
                throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            // Создали объект, считаем, что у него есть не приватный конструктор по умолчанию
            final Object testObject = testClass.getDeclaredConstructor().newInstance();

            // Из всех методов объекта нашли только тестовые
            List<Method> testMethods = new ArrayList<>();
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test.class)) {
                    testMethods.add(method);
                }
            }

            // Выполнили тестовые методы и записали результаты
            List<String> results = new ArrayList<>(testMethods.size());

            for (Method testMethod : testMethods) {
                try {
                    // Удаляем ограничение приватности (если нужно)
//                    testMethod.setAccessible(true);
                    testMethod.invoke(testObject);
                    results.add(String.format("Test: %s is success, %s",
                            testMethod.getName(),
                            getAnnotationParams(testMethod)));
                } catch (InvocationTargetException e) {
                    results.add(String.format("Test: %s is failed with %s, %s",
                            testMethod.getName(), e.getCause(),
                            getAnnotationParams(testMethod)));
                }
            }

            // Сделали простенький репорт
            for (String result : results) {
                System.out.println(result);
            }
        }

        private static String getAnnotationParams(Method method) {
            DisplayName displayName = method.getAnnotation(DisplayName.class);
            try {
                // Получаем приоритет из аннотации
                int priorityFromAnnotation = displayName.priority();
                // Получаем значение приоритета по умолчанию
                int defaultPriority = (int) DisplayName.class.getMethod("priority").getDefaultValue();
                return String.format("// Params of annotation: message = %s, priority = %d (%s)",
                        displayName.message(),
                        displayName.priority(),
                        (priorityFromAnnotation == defaultPriority ? "default" : "custom"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class TestClassExample {

        @Test
        @DisplayName(message = "This is DisplayName 1", priority = 2)
        public void testSomething() {
            System.out.println("Execute test1");
        }

        @Test
        @DisplayName(message = "This is DisplayName 2")
        public void testSomethingElse() {
            throw new RuntimeException("Execute test2 - it will fail (RuntimeException)");
        }
    }


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test {
    }

    // добавим пару параметров
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface DisplayName {
        String message();

        int priority() default 77;
    }
}
