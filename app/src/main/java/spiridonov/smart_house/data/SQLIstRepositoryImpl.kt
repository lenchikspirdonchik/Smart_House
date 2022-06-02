package spiridonov.smart_house.data

import spiridonov.smart_house.domain.sqlItem.SQLIstRepository
import spiridonov.smart_house.domain.sqlItem.SQLItem

object SQLIstRepositoryImpl : SQLIstRepository {
    private val sqlList = mutableListOf<SQLItem>()
    override fun getSQList(): List<SQLItem> {
        //TODO("Here PostGreSQL need to be downloaded")
        return sqlList
    }
}