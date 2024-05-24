package me.stella.core.tasks.scanners.data;

import me.stella.core.tasks.scanners.ScannerUtils;
import me.stella.reflection.ObjectCaster;
import me.stella.reflection.ObjectWrapper;

public interface ComparativeData {

    default Object getTypeValue(Number num, String typeMethod) {
        return new ObjectWrapper<>(num).invokeMethod(typeMethod, new Class<?>[]{}, new Object[]{}).getObject();
    }

    ObjectWrapper<?> getDataWrapper();

    String getOperator();

    default boolean compare(Number num1, Number num2) {
        String compareType = ScannerUtils.getByteSize(num1) <= ScannerUtils.getByteSize(num2) ?
                num2.getClass().getSimpleName() : num1.getClass().getSimpleName();
        String valueMethod = compareType.toLowerCase().concat("Value");
        Object[] params = new Object[] { getTypeValue(num1, valueMethod), getTypeValue(num2, valueMethod) };
        Class<?>[] types = new Class<?>[]{ params[0].getClass(), params[1].getClass() };
        return ObjectCaster.toBoolean(getDataWrapper().invokeMethod(getOperator(), types, params).getObject());
    }

    default boolean greater(byte num1, byte num2) {
        return num1 > num2;
    }

    default boolean greater(short num1, short num2) {
        return num1 > num2;
    }

    default boolean greater(int num1, int num2) {
        return num1 > num2;
    }

    default boolean greater(long num1, long num2) {
        return num1 > num2;
    }

    default boolean greater(float num1, float num2) {
        return num1 > num2;
    }

    default boolean greater(double num1, double num2) {
        return num1 > num2;
    }

    default boolean smaller(byte num1, byte num2) {
        return num1 < num2;
    }

    default boolean smaller(short num1, short num2) {
        return num1 < num2;
    }

    default boolean smaller(int num1, int num2) {
        return num1 < num2;
    }

    default boolean smaller(long num1, long num2) {
        return num1 < num2;
    }

    default boolean smaller(float num1, float num2) {
        return num1 < num2;
    }

    default boolean smaller(double num1, double num2) {
        return num1 < num2;
    }

    default boolean equal(byte num1, byte num2) {
        return num1 == num2;
    }

    default boolean equal(short num1, short num2) {
        return num1 == num2;
    }

    default boolean equal(int num1, int num2) {
        return num1 == num2;
    }

    default boolean equal(long num1, long num2) {
        return num1 == num2;
    }

    default boolean equal(float num1, float num2) {
        return num1 == num2;
    }

    default boolean equal(double num1, double num2) {
        return num1 == num2;
    }

    default boolean greaterOrEqual(byte num1, byte num2) {
        return num1 >= num2;
    }

    default boolean greaterOrEqual(short num1, short num2) {
        return num1 >= num2;
    }

    default boolean greaterOrEqual(int num1, int num2) {
        return num1 >= num2;
    }

    default boolean greaterOrEqual(long num1, long num2) {
        return num1 >= num2;
    }

    default boolean greaterOrEqual(float num1, float num2) {
        return num1 >= num2;
    }

    default boolean greaterOrEqual(double num1, double num2) {
        return num1 >= num2;
    }

    default boolean smallerOrEqual(byte num1, byte num2) {
        return num1 <= num2;
    }

    default boolean smallerOrEqual(short num1, short num2) {
        return num1 <= num2;
    }

    default boolean smallerOrEqual(int num1, int num2) {
        return num1 <= num2;
    }

    default boolean smallerOrEqual(long num1, long num2) {
        return num1 <= num2;
    }

    default boolean smallerOrEqual(float num1, float num2) {
        return num1 <= num2;
    }

    default boolean smallerOrEqual(double num1, double num2) {
        return num1 <= num2;
    }

}
