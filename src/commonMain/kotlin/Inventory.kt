class Inventory(
    val inventory: ArrayList<ArrayList<InventoryCell>>,
    val rows: Int,
    val cols: Int,
) {
    fun getFreeCellIndex() : Int {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (inventory[i][j].thing == null) {
                    println("$rows|$cols|$i|$j")
                    return i * cols + j
                }
            }
        }
        return -1
    }
}