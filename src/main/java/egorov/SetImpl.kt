package egorov

import java.util.*
import java.util.concurrent.atomic.AtomicStampedReference


/**
 * This implementation is based on a linked-list.
 * Hence, expect O(N)
 */
class SetImpl<T : Comparable<T>> : Set<T> {
    inner class Node(val data: T?) {
        // (T, Int)
        var next = AtomicStampedReference<Node>(null, 0) // -1 for removed
        constructor(data: T?, next: AtomicStampedReference<Node>) : this(data) {
            this.next = next
        }

        val nextNode: Node?
            get() = next.reference

        val stamp: Int
            get() = next.stamp

        fun isLogicallyRemoved() = next.stamp == -1

        fun tryMark(toMark: Node?, stamp: Int = 0) = next.compareAndSet(toMark, toMark, stamp, -1)

        fun tryRemove(toRemove: Node, stamp: Int = 0) = next.compareAndSet(toRemove, toRemove.next.reference, stamp, stamp + 1)

        fun tryRemove(toRemove: Node?, nextVal: Node?, stamp: Int = 0) = next.compareAndSet(toRemove, nextVal, stamp, stamp + 1)
    }

    inner class StampedNode(val node: Node?, val stamp: Int)

    private val head = Node(null)

    /**
     * rhs.data < key <= lhs.data
     */
    private fun findSuitableWindow(key: T?): Pair<StampedNode, StampedNode?> {
        val stamped = IntArray(1)
        val stampedNext = IntArray(1)
        stampedNext[0] = 0
        outer@ while (true) {
            var current: Node? = head
            var next: Node? = current?.next?.get(stamped) ?: return Pair(StampedNode(current, stamped.first()), null)
            var nextNext: Node?
            while (true) {
                nextNext = next?.next?.get(stampedNext)
                // do actual remove
                while (stampedNext.first() == -1) {
                    if (current?.next?.compareAndSet(next, nextNext, stamped.first(), stamped.first() + 1) == true) {
                        next = nextNext
                        nextNext = next?.next?.get(stampedNext)
                    } else {
                        continue@outer
                    }
                }

                if (next?.data == null || next.data!! >= key!!) {
                    return Pair(StampedNode(current, stamped.first()), StampedNode(next, stampedNext.first()))
                }

                current = next
                next = nextNext
                stamped[0] = stampedNext[0]
            }
        }
    }

    override fun add(value: T): Boolean {
        while (true) {
            val (curr, next) = findSuitableWindow(value)
            if (next?.node?.data == value) {
                return false
            }

            val addedNode = Node(value, AtomicStampedReference<Node>(next?.node, 0))
            val st = curr.stamp
            if (st == -1) println("STAMP: -1 for ${curr.node?.data} in add: $value")
            if (curr.node?.next?.compareAndSet(next?.node, addedNode, st, st + 1 ) == true)  {
                return true
            }
        }
    }

    override fun remove(value: T): Boolean {
        while (true) {
            val (curr, next) = findSuitableWindow(value)
            if (next?.node?.data == null || next.node.data != value) {
                // println("<${curr.node?.data}, ${next?.node?.data}>")
                return false
            }
            val nextVersion = intArrayOf(next.stamp)
            val nextNext = next.node.next.get(nextVersion)
            if (nextVersion.first() == -1) return false

            if (next.node.next.compareAndSet(nextNext, nextNext, nextVersion.first(), -1)) {
                curr.node?.next?.compareAndSet(next.node, nextNext, curr.stamp, curr.stamp + 1)
                return true
            }
        }
    }

    override fun contains(value: T): Boolean {
        // return findSuitableWindow(value).second?.data == value
        val (curr, next) = completeTraversalRemove(false) { data!! < value }
        return next != null && next.data == value && !next.isLogicallyRemoved()
        // val stamp = intArrayOf(-1)
        // var curr = head.next[stamp]
        // while (curr?.data != null && curr.data != value) {
        //     curr = curr.nextNode
        // }
        // curr?.next?.get(stamp)
        // return curr?.data != null && curr.data == value && stamp[0] != -1
    }

    private fun completeTraversalRemove(withRetry: Boolean, condition: Node.() -> Boolean): Pair<Node, Node?> {
        var curr = head
        var next = curr.nextNode

        while (next != null && next.condition()) {
            if (next.isLogicallyRemoved()) {
                val wasRemoved = curr.tryRemove(next, curr.stamp)
                if (withRetry && !wasRemoved) {
                    curr = head
                    next = curr.nextNode
                    continue
                } else if (wasRemoved) {
                    next = next.nextNode
                    continue
                }
            }

            curr = next
            next = curr.nextNode
        }

        return Pair(curr, next)
    }


    override fun isEmpty(): Boolean {
        val curStamp = IntArray(1)
        val nextStamp = IntArray(1)
        nextStamp[0] = -1
        var curr: Node?
        var next: Node?
        var removedByMe = false
        var nextNext: Node?
        outer@ while (true) {
            curr = head
            next = curr.next.get(curStamp) ?: return true
            while (true) {
                nextNext = next?.next?.get(nextStamp)
                while (nextStamp.first() == -1) {
                    removedByMe = curr.tryRemove(next, nextNext, curStamp.first())
                    if (!removedByMe) {
                        continue@outer
                    }
                    next = nextNext
                    nextNext = next?.next?.get(nextStamp)
                }
                return next?.data == null
            }
        }

    }

    override fun iterator(): Iterator<T?> {
        return TreeSet<T>(createSnapshot()).iterator()
    }


    private fun createSnapshot(): Collection<T?> {
        while (true) {
            val firstTraverse = acquireNodes()
            val secondTraverse = acquireNodes()
            if (checkEquals(firstTraverse, secondTraverse)) return firstTraverse.map { it.data }
        }
    }

    private fun checkEquals(lhs: List<Node>, rhs: List<Node>): Boolean {
        if (lhs.size != rhs.size) return false
        lhs.forEachIndexed { index, node ->
            if (rhs[index].next != node.next) return false
        }

        return true
    }

    private fun acquireNodes(): List<Node> {
        val ans = mutableListOf<Node>()

        var current = head
        while (true) {
            val next = current.nextNode ?: break
            if (!next.isLogicallyRemoved()) {
                ans.add(next)
            }
            
            current = next
            if (current.next.reference == null) break
        }

        return ans
    }


}