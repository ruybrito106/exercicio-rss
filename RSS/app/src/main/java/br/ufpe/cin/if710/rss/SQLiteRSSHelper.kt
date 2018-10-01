package br.ufpe.cin.if710.rss


import android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import android.content.ContentValues
import android.content.Context

import android.provider.BaseColumns
import org.jetbrains.anko.db.delete
import java.sql.SQLException

class SQLiteRSSHelper private constructor(internal var c: Context) : SQLiteOpenHelper(c, DATABASE_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        //Executa o comando de criação de tabela
        db.execSQL(CREATE_DB_COMMAND)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //estamos ignorando esta possibilidade no momento
        throw RuntimeException("nao se aplica")
    }

    //IMPLEMENTAR ABAIXO
    //Implemente a manipulação de dados nos métodos auxiliares para não ficar criando consultas manualmente
    fun insertItem(item: ItemRSS): Long {
        return insertItem(item.title, item.pubDate, item.description, item.link)
    }

    private fun insertItem(title: String, pubDate: String, description: String, link: String): Long {
        val values = ContentValues()
        values.put(ITEM_TITLE, title)
        values.put(ITEM_DATE, pubDate)
        values.put(ITEM_DESC, description)
        values.put(ITEM_LINK, link)
        values.put(ITEM_UNREAD, true)
        return writableDatabase.insertWithOnConflict(DATABASE_TABLE, null, values, CONFLICT_IGNORE)
    }

    @Throws(SQLException::class)
    fun getItemRSS(link: String): ItemRSS? {
        val query = String.format("SELECT * FROM %s WHERE %s.%s = %s",
                DATABASE_TABLE,
                DATABASE_TABLE,
                ITEM_LINK,
                link)
        val items = queryItems(query)
        return if (items.size > 0) items[0] else null
    }

    @Throws(SQLException::class)
    fun getItems(): List<ItemRSS> {
        val query = String.format("SELECT * FROM %s WHERE %s.%s = %d",
                DATABASE_TABLE,
                DATABASE_TABLE,
                ITEM_UNREAD,
                1)
        return queryItems(query)
    }

    private fun queryItems(query: String): List<ItemRSS> {
        readableDatabase.rawQuery(query, null).use { cursor ->
            val items = ArrayList<ItemRSS>()
            if (cursor.moveToFirst()) {
                do {
                    items.add(ItemRSS(
                            cursor.getString(cursor.getColumnIndex(ITEM_TITLE)),
                            cursor.getString(cursor.getColumnIndex(ITEM_LINK)),
                            cursor.getString(cursor.getColumnIndex(ITEM_DATE)),
                            cursor.getString(cursor.getColumnIndex(ITEM_DESC))
                    ))
                } while (cursor.moveToNext())
            }
            return items
        }
    }

    fun deleteAll(): Int {
        return writableDatabase.delete(
                DATABASE_TABLE,
                "1"
        )
    }

    fun markAsUnread(link: String): Boolean {
        val values = ContentValues()
        values.put(ITEM_UNREAD, true)
        return writableDatabase.update(
                DATABASE_TABLE,
                values,
                ITEM_LINK + " = ?",
                arrayOf(link)) > 0
    }

    fun markAsRead(link: String): Boolean {
        val values = ContentValues()
        values.put(ITEM_UNREAD, false)
        return writableDatabase.update(
                DATABASE_TABLE,
                values,
                ITEM_LINK + " = ?",
                arrayOf(link)) > 0
    }

    companion object {
        private val DATABASE_NAME = "rss"
        private val DATABASE_TABLE = "items"
        private val DB_VERSION = 1

        private var db: SQLiteRSSHelper? = null

        //Definindo Singleton
        fun getInstance(c: Context): SQLiteRSSHelper {
            return db ?: SQLiteRSSHelper(c.getApplicationContext())
        }

        //Definindo constantes que representam os campos do banco de dados
        val ITEM_ROWID = BaseColumns._ID
        val ITEM_TITLE = "title"
        val ITEM_DATE = "pubDate"
        val ITEM_DESC = "description"
        val ITEM_LINK = "guid"
        val ITEM_UNREAD = "unread"

        //Definindo constante que representa um array com todos os campos
        val columns = arrayOf<String>(ITEM_ROWID, ITEM_TITLE, ITEM_DATE, ITEM_DESC, ITEM_LINK, ITEM_UNREAD)

        //Definindo constante que representa o comando de criação da tabela no banco de dados
        private val CREATE_DB_COMMAND = "CREATE TABLE " + DATABASE_TABLE + " (" +
                ITEM_ROWID + " integer primary key autoincrement, " +
                ITEM_TITLE + " text not null unique, " +
                ITEM_DATE + " text not null, " +
                ITEM_DESC + " text not null, " +
                ITEM_LINK + " text not null, " +
                ITEM_UNREAD + " boolean not null);"
    }
}
