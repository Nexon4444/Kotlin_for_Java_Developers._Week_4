package board

open class SquareBoardImpl(final override val width: Int) : SquareBoard {
    private val cells: List<List<Cell>>

    init {
        cells = (1..width).map { row -> (1..width).map { col -> Cell(row, col) } }
    }
    override fun getCellOrNull(i: Int, j: Int): Cell? {
        try {
            return getCell(i, j)
        } catch (e: IllegalArgumentException) {
            return null
        }
    }

    override fun getCell(i: Int, j: Int): Cell {
        try {
            return cells[i-1][j-1]
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException("Out of bounds")
        }
    }

    fun isInBounds(i: Int, j: Int): Boolean {
        return when {
            i < 1 || j < 1 || i > width || j > width -> false
            else -> true
        }
    }

    override fun getAllCells(): Collection<Cell> {
        return cells.flatten()
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val adjustedRange = jRange.filter { it in 0..width }
        return (adjustedRange).map { col -> getCell(i, col)}
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val adjustedRange = iRange.filter { it in 0..width }
        return (adjustedRange).map { row -> getCell(row, j) }
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        val (iPotentialNeighbour, jPotentialNeighbour) =
                when (direction) {
                    Direction.UP -> (i - 1 to j)
                    Direction.DOWN -> (i + 1 to j)
                    Direction.RIGHT -> (i to j + 1)
                    Direction.LEFT -> (i to j - 1)
                }
        return if (isInBounds(iPotentialNeighbour, jPotentialNeighbour)) getCell(iPotentialNeighbour, jPotentialNeighbour)
        else null

    }

}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)

class GameBoardImpl<T> (width: Int) : GameBoard<T>, SquareBoardImpl(width) {
//    l width: Int
    var mapCellsToValues: MutableMap<Cell, T?>
        init {
            mapCellsToValues = getAllCells().map { it to null }.toMap<Cell, T?>().toMutableMap()
        }

    override fun get(cell: Cell): T? {
        return mapCellsToValues.getOrDefault(cell, null)
    }

    override fun set(cell: Cell, value: T?) {
        mapCellsToValues.set(cell, value)
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return mapCellsToValues.filter{entry -> predicate(entry.value)}.keys.toList()
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        val foundKeysWithValues = mapCellsToValues.filterValues(predicate)//predicate(entry.value))}.keys.toList()
        return foundKeysWithValues.keys.first()
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return mapCellsToValues.values.any(predicate)
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return mapCellsToValues.values.all(predicate)
    }


}
fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)
fun main() {
    val b = createSquareBoard(2)
    println(b.getCellOrNull(1, 2))
    println(b.getAllCells())
}