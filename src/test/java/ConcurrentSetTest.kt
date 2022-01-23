import egorov.SetImpl
import org.jetbrains.kotlinx.lincheck.LinChecker
import org.jetbrains.kotlinx.lincheck.LoggingLevel
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test
import java.util.*


@StressCTest(sequentialSpecification = BaseLineTestState::class)
@Param(name = "value", gen = IntGen::class)
class ConcurrentSetTest {
    private val mySet = SetImpl<Int>()
    // private val mySet = LockFreeSetImpl<Int>()

    @Operation
    fun add(@Param(name = "value") value: Int): Boolean {
        return mySet.add(value)
    }

    @Operation
    fun remove(@Param(name = "value") value: Int): Boolean {
        return mySet.remove(value)
    }

    @Operation
    fun contains(@Param(name = "value") value: Int): Boolean {
        return mySet.contains(value)
    }

    @Operation
    fun isEmpty(): Boolean {
        return mySet.isEmpty()
    }

    // @Operation
    fun iterator(): Iterator<Int?> {
        return mySet.iterator()
    }

    @Test
    fun doTest() {
        val setUp = StressOptions()
            .iterations(1000).threads(3)
            .invocationsPerIteration(1)
            .logLevel(LoggingLevel.INFO)
        LinChecker.check(ConcurrentSetTest::class.java, setUp)


        // LinChecker.check(ConcurrentSetTest::class.java)
    }


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        if (!super.equals(o)) return false
        val that = o as ConcurrentSetTest
        return mySet == that.mySet
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), mySet)
    }

}