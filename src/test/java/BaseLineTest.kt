import egorov.Set
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.jetbrains.kotlinx.lincheck.verifier.linearizability.LinearizabilityVerifier

@StressCTest(verifier = LinearizabilityVerifier::class)
class BaseLineTest : VerifierState(), Set<Int> {
    private val set = mutableSetOf<Int>()

    @Operation
    override fun add(value: Int): Boolean = set.add(value)

    @Operation
    override fun remove(value: Int): Boolean = set.remove(value)

    @Operation
    override fun contains(value: Int): Boolean = set.contains(value)

    @Operation
    override fun isEmpty(): Boolean = set.isEmpty()

    @Operation
    override fun iterator(): Iterator<Int> = set.iterator()

    override fun extractState(): Any = set

}