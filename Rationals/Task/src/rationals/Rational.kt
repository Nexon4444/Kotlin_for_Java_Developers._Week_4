package rationals

import java.math.BigInteger

data class IntType(val num: BigInteger) : Number(), Comparable<IntType> {

    constructor(numP: Int) : this(numP.toBigInteger())
    constructor(numP: Long) : this(numP.toBigInteger())


    override operator fun compareTo(other: IntType): Int {
        return this.num.compareTo(other.num)
    }

    override fun toByte(): Byte {
        return num.toByte()
    }

    override fun toChar(): Char {
        return num.toChar()
    }

    override fun toDouble(): Double {
        return num.toDouble()
    }

    override fun toFloat(): Float {
        return num.toFloat()
    }

    override fun toInt(): Int {
        return num.toInt()
    }

    override fun toLong(): Long {
        return num.toLong()
    }

    override fun toShort(): Short {
        return num.toShort()
    }

    override fun toString(): String {
        return num.toString()
    }

    operator fun unaryMinus(): IntType {
        return IntType(-num)
    }

    infix operator fun plus(other: IntType): IntType {
        return IntType(num + other.num)
    }

    infix operator fun minus(other: IntType): IntType {
        return IntType(num - other.num)
    }

    infix operator fun div(other: IntType): IntType {
        return IntType(num / other.num)
    }

    infix operator fun times(other: IntType): IntType {
        return IntType(num * other.num)
    }

    fun abs(): IntType {
        return  IntType(num.abs())
    }

    operator fun rem(other: IntType) : IntType{
        return IntType(num % other.num)
    }
}

data class Rational(val nPar: IntType, val dPar: IntType): Comparable<Rational> {
    private val n: IntType
    private val d: IntType
    constructor(nPar: Int, dPar: Int) : this(IntType(nPar), IntType(dPar))
    constructor(nPar: Long, dPar: Long) : this(IntType(nPar), IntType(dPar))
    constructor(nPar: BigInteger, dPar: BigInteger) : this(IntType(nPar), IntType(dPar))

        init {
            val (n_arg, d_arg) = rationalMianDziel(nPar, dPar)
            when {
                d_arg < IntType(0) -> {n = -n_arg; d = -d_arg}
                else -> {n = n_arg; d = d_arg}
            }
//        d = IntType(dPar)
    }
    infix operator fun plus(other: Rational): Rational {
        val (licz_new, mian) = performSimpleOp(other, IntType::plus)
        return Rational(licz_new, mian)
    }

    private fun performSimpleOp(other: Rational, op: (IntType, IntType) -> IntType): Pair<IntType, IntType> {
        val (mian, licz_this, licz_other) = normalized(this, other)
        val licz_new = op(licz_this, licz_other)
        return rationalMianDziel(licz_new, mian)
    }

    private fun performCmplxOp(other: Rational, op: (IntType, IntType) -> IntType): Pair<IntType, IntType> {
        val (mian, licz_this, licz_other) = normalized(this, other)
        val licz_new = op(licz_this, licz_other)
        val mian_new = op(mian, mian)
        return rationalMianDziel(licz_new, mian_new)
    }

    infix operator fun minus(other: Rational): Rational {
        val (licz_new, mian) = performSimpleOp(other, IntType::minus)
        return Rational(licz_new, mian)
    }

    infix operator fun div(other: Rational): Rational {
        val (licz_new, mian) = performCmplxOp(other.inverse(), IntType::times)
        return Rational(licz_new, mian)
    }

    infix operator fun times(other: Rational): Rational {
        val (licz_new, mian) = performCmplxOp(other, IntType::times)
        return Rational(licz_new, mian)
    }

    operator fun unaryMinus(): Rational {
        return Rational(-n, d)
    }

    override operator fun compareTo(other: Rational): Int {
        val (_, nThis, nOther) = normalized(this, other)
        return nThis.compareTo(nOther)
    }

    fun inverse(): Rational {
        return Rational(d, n)
    }


    override fun toString(): String {
        return when (d) {
            IntType(1) -> n.toString()
            else -> "$n/$d"
        }
    }

    override fun hashCode(): Int {
        var result = n.hashCode()
        result = 31 * result + d.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (n != other.n) return false
        if (d != other.d) return false

        return true
    }

    companion object {
        fun zero(): Rational {
            return Rational(0, 0)
        }
        fun normalized(first: Rational, second: Rational): Triple<IntType, IntType, IntType> {
            val mian = first.d * second.d
            val liczFirst = first.n * second.d
            val liczSecond = second.n * first.d
            return Triple(mian, liczFirst, liczSecond)
        }


        tailrec fun NWD(a: IntType, b: IntType): IntType {
            return if (b != IntType(0))
                NWD(b, a % b)
            else
                a
        }
        fun rationalMianDziel(n: IntType, d: IntType): Pair<IntType, IntType>{
            return when (n) {
                IntType(0) -> IntType(0) to IntType(0)
                else -> n / NWD(n.abs(), d.abs()) to d / NWD(n.abs(), d.abs())
            }
        }

        fun NWW(aPar: IntType, bPar: IntType): IntType {
//        val a = IntType(aPar)
//        val b = IntType(bPar)
            val x = (aPar * bPar)
            return (aPar * bPar) / NWD(aPar.abs(), bPar.abs())

        }
    }
}

infix fun Int.divBy(d: Int): Rational {
    return Rational(this, d)
}

//
infix fun Long.divBy(d: Long): Rational {
    return Rational(this, d)
}

infix fun BigInteger.divBy(d: BigInteger): Rational {
    return Rational(this, d)
}

fun Long.toBigInteger() = BigInteger.valueOf(this)

fun Int.toBigInteger() = BigInteger.valueOf(toLong())

fun String.toRational(): Rational {
    val splitString = this.split('/')
    val n = splitString[0]
    val d = splitString.getOrElse(1) {"1"}
    return Rational(n.toBigInteger(), d.toBigInteger())
}

fun main() {


    println("20325830850349869048604856908".toRational()>"-9192901948302584358938698".toRational())

//    fun test4InRange0() = testInRange("0/68", "0/85", "1/100")
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
    val div: Rational = 5 divBy 6
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
    println(1 divBy 6 == difference)

    val product: Rational = half * third
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
//    println((-2 divBy 4).toString())
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L)
    println(1 divBy 2)
    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}