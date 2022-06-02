package spiridonov.smart_house.domain.sqlItem

class SQLItem(
    val hour: Int,
    val minute: Int,
    val temperature: Int,
    val humidity: Int
) {
    companion object {
        const val UNDEFINED_ID = -1
    }
}