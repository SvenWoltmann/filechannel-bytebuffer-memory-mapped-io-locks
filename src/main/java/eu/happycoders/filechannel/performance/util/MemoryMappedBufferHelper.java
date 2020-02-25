package eu.happycoders.filechannel.performance.util;

import java.lang.reflect.*;
import java.nio.*;

public class MemoryMappedBufferHelper {

    public static void unsafeUnmap(MappedByteBuffer mappedByteBuffer) throws ReflectiveOperationException {
        // Doing this with reflection:
        // sun.misc.Unsafe.theUnsafe.invokeCleaner(mappedByteBuffer)
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Method invokeCleanerMethod = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
        invokeCleanerMethod.setAccessible(true);
        Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        Object theUnsafe = theUnsafeField.get(null);
        invokeCleanerMethod.invoke(theUnsafe, mappedByteBuffer);
    }

}
