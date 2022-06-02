package spiridonov.smart_house.domain.sqlItem

interface SQLIstRepository {
    fun getSQList(): List<SQLItem>
}