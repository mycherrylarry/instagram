package models
import anorm._
import anorm.SqlParser._
import java.util.{Date}

import play.api.db._
import play.api.Play.current

case class Msgbox(id: Int, user_id : Int, followee_id: Int, photo_id: Int)

object Msgbox {
    val msgbox = {
        get[Int]("id") ~
        get[Int]("user_id") ~
        get[Int]("followee_id") ~
        get[Int]("photo_id") map {
            case id~user_id~followee_id~photo_id => Msgbox(id, user_id, followee_id, photo_id)
        }
    }

    def findByUserId (user_id: Int, offset: Int = 0, limit: Int = 10) : List[Msgbox] = DB.withConnection {
        implicit c =>
        var sql_query : String = "SELECT * FROM msgbox WHERE user_id = {user_id} ORDER BY c_time DESC LIMIT {limit} OFFSET {offset}"
        SQL(sql_query). on(
            'user_id -> user_id,
            'limit -> limit,
            'offset -> offset
        ).as(msgbox *)
    }

    def delete(id: Int) {
        DB.withConnection {
            implicit c =>
            SQL("DELETE FROM msgbox where id = {id}"). on(
                'id -> id
            ).executeUpdate()
        }
    }
}
