import java.lang.reflect.*;
import java.util.*;

public class CopyUtils {
    private static final String TYPE_STRING = "String";
    private static final String TYPE_INT = "int";
    private static final String TYPE_BYTE = "byte";
    private static final String TYPE_CHAR = "char";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_SHORT = "short";
    private static final String TYPE_DOUBLE = "double";
    private static final String TYPE_BOOLEAN = "boolean";

    public static <T> T deepCopy(T sourceObj) {
        ByRefMap byRefMap = new ByRefMap();
        return deepCopyT(sourceObj, byRefMap);
    }

    public static <T> T deepCopyT(T sourceObj, ByRefMap byRefMap) {
        if (sourceObj == null)
            return null;

        if (sourceObj instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) sourceObj;
            return (T) copyCollection(col, byRefMap);
        }

        if (sourceObj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) sourceObj;
            return (T) copyMap(map, byRefMap);
        }

        if (sourceObj.getClass().isArray()) {
            return (T) copyArray(sourceObj, byRefMap);
        }

        if (byRefMap.containsKeyRef(sourceObj)) {
            return (T) byRefMap.getValueByKeyRef(sourceObj);
        }

        T copyObj = createNewT(sourceObj);
        byRefMap.refMap.put(sourceObj, copyObj);

        try {
            Class<?> classTObject = Class.forName(sourceObj.getClass().getName());

            Field[] fields = classTObject.getDeclaredFields();

            for (Field value : fields) {
                String fieldName = value.getName();
                Field field = copyObj.getClass().getDeclaredField(fieldName);

                if (Modifier.isTransient(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                field.setAccessible(true);

                if (field.getType().isPrimitive() || field.getType().getSimpleName().equals(TYPE_STRING)) {
                    field.set(copyObj, field.get(sourceObj));
                } else if (Collection.class.isAssignableFrom(field.getType())) {
                    field.set(copyObj, copyCollection((Collection<?>) field.get(sourceObj), byRefMap));
                } else if (Map.class.isAssignableFrom(field.getType())) {
                    field.set(copyObj, copyMap((Map<?, ?>) field.get(sourceObj), byRefMap));
                } else if (field.getType().isArray()) {
                    field.set(copyObj, copyArray(field.get(sourceObj), byRefMap));
                } else {
                    field.set(copyObj, deepCopyT(field.get(sourceObj), byRefMap));
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return copyObj;
    }

    // Возвращает копию массива с входного параметра sourceArray
    protected static Object copyArray(Object sourceArray, ByRefMap byRefMap) {
        if (sourceArray == null)
            return null;

        String nameArrayType = "";

        if (byRefMap.containsKeyRef(sourceArray)) {
            return byRefMap.getValueByKeyRef(sourceArray);
        }

        try {
            Class<?> classArr = Class.forName(sourceArray.getClass().getName());
            nameArrayType = classArr.getComponentType().getSimpleName();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        switch (nameArrayType) {
            case TYPE_INT:
                int[] intArr = (int[]) sourceArray;
                int[] copyIntArr = Arrays.copyOf(intArr, intArr.length);
                byRefMap.refMap.put(sourceArray, copyIntArr);
                return copyIntArr;
            case TYPE_BYTE:
                byte[] byteArr = (byte[]) sourceArray;
                byte[] copyByteArr = Arrays.copyOf(byteArr, byteArr.length);
                byRefMap.refMap.put(sourceArray, copyByteArr);
                return copyByteArr;
            case TYPE_CHAR:
                char[] charArr = (char[]) sourceArray;
                char[] copyCharArr = Arrays.copyOf(charArr, charArr.length);
                byRefMap.refMap.put(sourceArray, copyCharArr);
                return copyCharArr;
            case TYPE_LONG:
                long[] longArr = (long[]) sourceArray;
                long[] copyLongArr = Arrays.copyOf(longArr, longArr.length);
                byRefMap.refMap.put(sourceArray, copyLongArr);
                return copyLongArr;
            case TYPE_FLOAT:
                float[] floatArr = (float[]) sourceArray;
                float[] copyFloatArr = Arrays.copyOf(floatArr, floatArr.length);
                byRefMap.refMap.put(sourceArray, copyFloatArr);
                return copyFloatArr;
            case TYPE_SHORT:
                short[] shortArr = (short[]) sourceArray;
                short[] copyShortArr = Arrays.copyOf(shortArr, shortArr.length);
                byRefMap.refMap.put(sourceArray, copyShortArr);
                return copyShortArr;
            case TYPE_DOUBLE:
                double[] doubleArr = (double[]) sourceArray;
                double[] copyDoubleArr = Arrays.copyOf(doubleArr, doubleArr.length);
                byRefMap.refMap.put(sourceArray, copyDoubleArr);
                return copyDoubleArr;
            case TYPE_BOOLEAN:
                boolean[] booleanArr = (boolean[]) sourceArray;
                boolean[] copyBooleanArr = Arrays.copyOf(booleanArr, booleanArr.length);
                byRefMap.refMap.put(sourceArray, copyBooleanArr);
                return copyBooleanArr;
            default:
                Object[] copyObjArr = copyObjArray((Object[]) sourceArray, byRefMap);
                byRefMap.refMap.put(sourceArray, copyObjArr);
                return copyObjArr;
        }
    }

    // Возвращает копию массива ссылочных типов с входного параметра sourceArray
    private static <K> K[] copyObjArray(K[] sourceArray, ByRefMap byRefMap) {
        if (sourceArray == null)
            return null;

        K[] copyArr = null;

        try {
            Class<?> classArray = Class.forName(sourceArray.getClass().getName());
            copyArr = (K[]) Array.newInstance(classArray.getComponentType(), sourceArray.length);

            if (classArray.getComponentType().getSimpleName().equals(TYPE_STRING)) {
                copyArr = Arrays.copyOf(sourceArray, sourceArray.length);
            } else {
                for (int i = 0; i < sourceArray.length; i++) {
                    copyArr[i] = deepCopyT(sourceArray[i], byRefMap);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return copyArr;
    }

    // Возвращает копию словаря с входного параметра sourceMap
    private static <K, V> Map<K, V> copyMap(Map<K, V> sourceMap, ByRefMap byRefMap) {
        if (sourceMap == null)
            return null;

        if (byRefMap.containsKeyRef(sourceMap)) {
            return (Map<K, V>) byRefMap.getValueByKeyRef(sourceMap);
        }

        Map<K, V> map = createNewT(sourceMap);

        try {
            Class<?> classMap = Class.forName(sourceMap.getClass().getName());

            Method method = classMap.getDeclaredMethod("put", Object.class, Object.class);

            for (Map.Entry<K, V> item : sourceMap.entrySet()) {
                String className;
                K k;
                V v;

                className = item.getKey().getClass().getSimpleName();
                k = className.equals(TYPE_STRING) ? item.getKey() : deepCopyT(item.getKey(), byRefMap);

                className = item.getValue().getClass().getSimpleName();
                v = className.equals(TYPE_STRING) ? item.getValue() : deepCopyT(item.getValue(), byRefMap);

                method.invoke(map, k, v);
            }

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        byRefMap.refMap.put(sourceMap, map);

        return map;
    }

    // Возвращает копию коллекции с входного параметра sourceCol
    private static <E> Collection<E> copyCollection(Collection<E> sourceCol, ByRefMap byRefMap) {
        if (sourceCol == null)
            return null;

        if (byRefMap.containsKeyRef(sourceCol)) {
            return (Collection<E>) byRefMap.getValueByKeyRef(sourceCol);
        }

        Collection<E> col = createNewT(sourceCol);

        try {
            Class<?> classCollection = Class.forName(sourceCol.getClass().getName());

            Method method = classCollection.getDeclaredMethod("add", Object.class);

            for (Object o : sourceCol) {
                if (o.getClass().getSimpleName().equals(TYPE_STRING)) {
                    method.invoke(col, o);
                } else {
                    method.invoke(col, deepCopyT(o, byRefMap));
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        byRefMap.refMap.put(sourceCol, col);

        return col;
    }

    // Создает новый объект типа T
    private static <T> T createNewT(T sourceObject) {
        T newTObject = null;
        try {
            Class<?> classTObject = Class.forName(sourceObject.getClass().getName());
            Constructor<?>[] constructors  = classTObject.getConstructors();

            int constructorIndex = getConstructorIndex(constructors);

            if (constructorIndex >= 0) {
                newTObject = (T) classTObject.getConstructors()[constructorIndex].newInstance();
            } else {
                constructorIndex = 0;
                Class<?>[] parTypes = classTObject.getConstructors()[constructorIndex].getParameterTypes();
                Object[] params = new Object[parTypes.length];
                Arrays.fill(params, null);

                for (int i = 0; i < parTypes.length; i++) {
                    if (parTypes[i].isPrimitive()) {
                        if (parTypes[i].getSimpleName().equals(TYPE_BOOLEAN)) {
                            params[i] = false;
                        } else {
                            params[i] = 0;
                        }
                    }
                }
                newTObject = (T) classTObject.getConstructor(parTypes).newInstance(params);
            }
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newTObject;
    }

    // Возвращает индекс конструктора без параметров,
    // если конструктора без параметров нет,
    // то возвращает -1
    private static int getConstructorIndex(Constructor<?>[] constructors) {
        int constructorIndex = -1;

        for (int i = 0; i < constructors.length; i++) {
            if (constructors[i].getParameterCount() == 0) {
                constructorIndex = i;
                break;
            }
        }
        return constructorIndex;
    }
}
