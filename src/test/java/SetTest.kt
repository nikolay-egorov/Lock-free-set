import egorov.SetImpl
import org.junit.Assert
import org.junit.jupiter.api.Test

class SetTest {
    @Test
    fun add() {
        val mySet = SetImpl<Int>()
        Assert.assertFalse(mySet.contains(0))
        Assert.assertFalse(mySet.contains(12))
        Assert.assertTrue(mySet.add(12))
        Assert.assertTrue(mySet.contains(12))
        Assert.assertFalse(mySet.add(12))
        Assert.assertTrue(mySet.contains(12))
        Assert.assertFalse(mySet.contains(0))
    }

    @Test
    fun testIsEmptyOnRemove() {
        val mySet = SetImpl<Int>()
        Assert.assertFalse(mySet.contains(0))
        for (i in 0..9) {
            Assert.assertFalse(mySet.contains(i))
            Assert.assertTrue(mySet.add(i))
            Assert.assertFalse(mySet.add(i))
        }
        Assert.assertFalse(mySet.isEmpty)
        Assert.assertTrue(mySet.remove(2))
        Assert.assertFalse(mySet.isEmpty)
        Assert.assertFalse(mySet.remove(2))
    }

    @Test
    fun testOnEmpty() {
        val mySet = SetImpl<Int>()
        Assert.assertFalse(mySet.contains(0))
        Assert.assertFalse(mySet.contains(12))
        Assert.assertFalse(mySet.remove(12))
        Assert.assertFalse(mySet.remove(11232))
        Assert.assertFalse(mySet.contains(12))
    }

    @Test
    fun testNegative() {
        val mySet = SetImpl<Int>()
        Assert.assertFalse(mySet.contains(0))
        Assert.assertFalse(mySet.contains(12))
        Assert.assertTrue(mySet.add(12))
        Assert.assertTrue(mySet.contains(12))
        Assert.assertTrue(mySet.add(-8))
        Assert.assertTrue(mySet.contains(-8))
        Assert.assertFalse(mySet.add(12))
        Assert.assertTrue(mySet.contains(-8))
        Assert.assertFalse(mySet.contains(0))
    }

    @Test
    fun remove() {
        val mySet = SetImpl<Int>()
        Assert.assertFalse(mySet.contains(0))
        Assert.assertFalse(mySet.contains(12))
        Assert.assertTrue(mySet.add(12))
        Assert.assertTrue(mySet.contains(12))
        Assert.assertFalse(mySet.isEmpty)
        Assert.assertFalse(mySet.add(12))
        Assert.assertFalse(mySet.contains(13))
        Assert.assertTrue(mySet.remove(12))
        Assert.assertFalse(mySet.contains(12))
        Assert.assertFalse(mySet.remove(12))
        Assert.assertFalse(mySet.contains(12))
    }

    @Test
    fun contains() {
        val mySet = SetImpl<Int>()
        Assert.assertFalse(mySet.contains(0))
        Assert.assertFalse(mySet.contains(12))
        for (i in 0..9) {
            Assert.assertFalse(mySet.contains(i))
            Assert.assertTrue(mySet.add(i))
            Assert.assertFalse(mySet.add(i))
            Assert.assertTrue(mySet.contains(i))
            Assert.assertFalse(mySet.contains(i + 1))
        }
        Assert.assertFalse(mySet.isEmpty)
    }

    @get:Test
    val isEmpty: Unit
        get() {
            val mySet = SetImpl<Int>()
            Assert.assertTrue(mySet.isEmpty)
            Assert.assertFalse(mySet.contains(1))
            for (i in 0..9) {
                Assert.assertTrue(mySet.add(i))
                Assert.assertFalse(mySet.isEmpty)
            }
            for (i in 0..9) {
                Assert.assertTrue(mySet.remove(i))
                Assert.assertFalse(mySet.contains(i))
                if (i != 9) {
                    Assert.assertFalse(mySet.isEmpty)
                } else {
                    Assert.assertTrue(mySet.isEmpty)
                }
            }
            Assert.assertTrue(mySet.isEmpty)
        }

    @Test
    operator fun iterator() {
        val mySet = SetImpl<Int>()
        Assert.assertTrue(mySet.isEmpty)
        Assert.assertFalse(mySet.contains(1))
        for (i in 0..19) {
            Assert.assertTrue(mySet.add(i))
            Assert.assertFalse(mySet.add(i))
            Assert.assertFalse(mySet.isEmpty)
        }
        var iter: Iterator<*> = mySet.iterator()
        Assert.assertTrue(iter.hasNext())
        for (i in 0..20) {
            if (i != 20) {
                Assert.assertTrue(iter.hasNext())
                Assert.assertEquals(i, iter.next())
            } else {
                Assert.assertFalse(iter.hasNext())
            }
        }
        for (i in 1..20) {
            if (i % 2 == 0) {
                mySet.remove(i)
            }
        }
        Assert.assertFalse(mySet.isEmpty)
        iter = mySet.iterator()
        for (i in 0..20) {
            if (i != 20) {
                Assert.assertTrue(iter.hasNext())
                if (i != 0 && i % 2 == 0) {
                    Assert.assertFalse(mySet.contains(i))
                } else {
                    Assert.assertTrue(mySet.contains(i))
                    Assert.assertEquals(i, iter.next())
                }
            } else {
                Assert.assertFalse(iter.hasNext())
                Assert.assertFalse(mySet.contains(i + 1))
            }
        }
    }
}