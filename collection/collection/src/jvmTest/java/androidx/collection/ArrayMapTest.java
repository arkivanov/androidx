/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

@RunWith(JUnit4.class)
public class ArrayMapTest {
    ArrayMap<String, String> mMap = new ArrayMap<>();

    /**
     * Attempt to generate a ConcurrentModificationException in ArrayMap.
     * <p>
     * ArrayMap is explicitly documented to be non-thread-safe, yet it's easy to accidentally screw
     * this up; ArrayMap should (in the spirit of the core Java collection types) make an effort to
     * catch this and throw ConcurrentModificationException instead of crashing somewhere in its
     * internals.
     */
    @Test
    public void testConcurrentModificationException() throws Exception {
        final int testLenMs = 5000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (mMap != null) {
                    try {
                        mMap.put(String.format("key %d", i++), "B_DONT_DO_THAT");
                    } catch (ArrayIndexOutOfBoundsException e) {
                        fail(e.getMessage());
                    } catch (ClassCastException e) {
                        fail(e.getMessage());
                    } catch (ConcurrentModificationException e) {
                        System.out.println("[successfully caught CME at put #" + i
                                + " size=" + (mMap == null ? "??" : String.valueOf(mMap.size()))
                                + "]");
                    }
                }
            }
        }).start();
        for (int i = 0; i < (testLenMs / 100); i++) {
            try {
                Thread.sleep(100);
                mMap.clear();
            } catch (InterruptedException e) {
            } catch (ArrayIndexOutOfBoundsException e) {
                fail(e.getMessage());
            } catch (ClassCastException e) {
                fail(e.getMessage());
            } catch (ConcurrentModificationException e) {
                System.out.println(
                        "[successfully caught CME at clear #"
                                + i + " size=" + mMap.size() + "]");
            }
        }
        mMap = null; // will stop other thread
        System.out.println();
    }

    /**
     * Check to make sure the same operations behave as expected in a single thread.
     */
    @Test
    public void testNonConcurrentAccesses() throws Exception {
        for (int i = 0; i < 100000; i++) {
            try {
                mMap.put(String.format("key %d", i++), "B_DONT_DO_THAT");
                if (i % 200 == 0) {
                    System.out.print(".");
                }
                if (i % 500 == 0) {
                    mMap.clear();
                    System.out.print("X");
                }
            } catch (ConcurrentModificationException e) {
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void testIsSubclassOfSimpleArrayMap() {
        Object map = new ArrayMap<String, Integer>();
        assertTrue(map instanceof SimpleArrayMap);
    }

    /**
     * Regression test for ensure capacity: b/224971154
     */
    @Test
    public void putAll() {
        ArrayMap<String, String> map = new ArrayMap<>();
        Map<String, String> otherMap = new HashMap<>();
        otherMap.put("abc", "def");
        map.putAll(otherMap);
        assertEquals(map.size(), 1);
        assertEquals(map.keyAt(0), "abc");
        assertEquals(map.valueAt(0), "def");
    }

    @Test
    public void toArray() {
        Map<String, Integer> map = new ArrayMap<>();
        map.put("a", 1);
        String[] keys = map.keySet().toArray(new String[1]);
        Integer[] values = map.values().toArray(new Integer[1]);

        assertEquals(Arrays.toString(keys), "[a]");
        assertEquals(Arrays.toString(values), "[1]");

        // re-do again, it should re-use the same arrays
        String[] keys2 = map.keySet().toArray(keys);
        Integer[] values2 = map.values().toArray(values);

        assertSame(keys2, keys);
        assertSame(values2, values);
        assertEquals(Arrays.toString(keys2), "[a]");
        assertEquals(Arrays.toString(values2), "[1]");

        // add new items
        map.put("b", 2);
        map.put("c", 3);

        // now it shouldn't re-use arrays because arrays are too small
        String[] keys3 = map.keySet().toArray(keys);
        Integer[] values3 = map.values().toArray(values);

        assertNotSame(values3, values);
        assertNotSame(keys3, keys);
        assertEquals(Arrays.toString(keys3), "[a, b, c]");
        assertEquals(Arrays.toString(values3), "[1, 2, 3]");


        map.remove("b");
        map.remove("c");

        // arrays are big enough, re-use
        String[] keys4 = map.keySet().toArray(keys3);
        Integer[] values4 = map.values().toArray(values3);

        assertSame(values4, values3);
        assertSame(keys4, keys3);
        // https://docs.oracle.com/javase/8/docs/api/java/util/Set.html#toArray-T:A-
        // only the next index is nulled in the array, per documentation.
        assertEquals(Arrays.toString(keys4), "[a, null, c]");
        assertEquals(Arrays.toString(values4), "[1, null, 3]");
    }
}
