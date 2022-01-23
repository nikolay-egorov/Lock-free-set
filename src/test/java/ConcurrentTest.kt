import egorov.SetImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread


class ConcurrentTest {

    @Test
    fun add() {
        val mySet = SetImpl<Int>()
        val countDownLatch = CountDownLatch(2)
        val t = thread(start = true) {
            assertTrue(mySet.add(2))
            countDownLatch.countDown()
            println("Latch is: ${countDownLatch.count} from ${Thread.currentThread().id}")
            countDownLatch.await()

            assertTrue(mySet.add(3))
        }

        val t2 = thread(start = true) {
            countDownLatch.countDown()
            println("Latch is: ${countDownLatch.count} from ${Thread.currentThread().id}")
            countDownLatch.await()

            assertTrue(mySet.remove(2))
        }
        t.join()
        t2.join()

        val it = mySet.iterator()
        assertFalse(mySet.isEmpty)
        assertTrue(it.hasNext())
        val n = it.next()
        assertEquals(n, 3)
        assertFalse(it.hasNext())
    }


    @Test
    fun differentOps() {
        val mySet = SetImpl<Int>()
        assertTrue(mySet.isEmpty)
        assertTrue(mySet.add(-5))
        assertTrue(mySet.add(8))
        assertFalse(mySet.isEmpty)


        val countDownLatch = CountDownLatch(2)
        val t = thread(start = true) {
            countDownLatch.countDown()
            println("Latch is: ${countDownLatch.count} from ${Thread.currentThread().id}")
            countDownLatch.await()

            assertFalse(mySet.contains(8))
            assertFalse(mySet.remove(-4))
            assertTrue(mySet.add(4))
            assertTrue(mySet.add(8))
        }

        val t2 = thread(start = true) {
            assertTrue(mySet.remove(8))
            countDownLatch.countDown()
            println("Latch is: ${countDownLatch.count} from ${Thread.currentThread().id}")
            countDownLatch.await()

            assertFalse(mySet.remove(7))
            assertTrue(mySet.add(9))
        }
        t.join()
        t2.join()

        assertFalse(mySet.isEmpty)
        assertTrue(mySet.contains(9))
    }


    @Test
    fun isEmpty() {
        val mySet = SetImpl<Int>()
        assertTrue(mySet.isEmpty)
        assertFalse(mySet.remove(1))
        assertTrue(mySet.add(4))
        assertFalse(mySet.isEmpty)


        val countDownLatch = CountDownLatch(2)
        val t = thread(start = true) {
            assertFalse(mySet.contains(5))
            countDownLatch.countDown()
            println("Latch is: ${countDownLatch.count} from ${Thread.currentThread().id}")
            countDownLatch.await()

            assertFalse(mySet.isEmpty)
        }

        val t2 = thread(start = true) {
            countDownLatch.countDown()
            println("Latch is: ${countDownLatch.count} from ${Thread.currentThread().id}")
            countDownLatch.await()

            assertTrue(mySet.remove(4))
        }

        val t3 = thread(start = true) {
            assertTrue(mySet.add(3))
        }
        t.join()
        t2.join()
        t3.join()

        assertFalse(mySet.isEmpty)
        assertFalse(mySet.contains(1))
        assertFalse(mySet.contains(5))
        assertTrue(mySet.add(5))
        assertFalse(mySet.contains(1))
        assertTrue(mySet.add(2))
    }


}