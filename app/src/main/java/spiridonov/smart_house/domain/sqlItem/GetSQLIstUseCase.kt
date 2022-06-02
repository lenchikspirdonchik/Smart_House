package spiridonov.smart_house.domain.sqlItem

class GetSQLIstUseCase(private val sqlIstRepository: SQLIstRepository) {
    fun getSQList(): List<SQLItem> = sqlIstRepository.getSQList()
}