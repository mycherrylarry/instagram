package models
import anorm._
import anorm.SqlParser._
import java.util.{Date}

import play.api.db._
import play.api.Play.current

case class Photo(id: Int, contributer: Int, name: String, desc: String, like_num: Int, comment_num: Int, hash: String, suffix: String,  tag: String, c_time: Date)

object Photo {
    def sort_by(sort_type: Int): String = sort_type match {
        case 1 => "c_time"
        case 2 => "comment_num"
        case 3 => "like_num"
        case _ => "c_time"
    }

    val photo = {
        get[Int]("id") ~
        get[Int]("contributer") ~
        get[String]("name") ~
        get[String]("description") ~
        get[Int]("like_num") ~
        get[Int]("comment_num") ~
        get[String]("hash") ~
        get[String]("suffix") ~
        get[String]("tag") ~
        get[Date]("c_time")  map {
            case id~contributer~name~desc~like_num~comment_num~hash~suffix~tag~c_time => Photo(id, contributer, name, desc, like_num, comment_num, hash, suffix, tag, c_time)
        }
    }

    def all(): List[Photo] = DB.withConnection {implicit c =>
        SQL("SELECT * FROM photo").as(photo *)
    }

    def create(contributer: Int, hash: String, suffix: String, name: String = "") {
        DB.withConnection {implicit c =>
            SQL(
                """
                INSERT INTO photo (contributer, hash, suffix, name)
                VALUES ({contributer}, {hash}, {suffix}, {name})
                """
            ). on(
                'contributer -> contributer,
                'hash -> hash,
                'suffix -> suffix,
                'name -> name
            ).executeUpdate()
        }
    }

    def findById(id: Int) = DB.withConnection {implicit c =>
        SQL("SELECT * FROM photo WHERE id = {id}").on(
            'id -> id
        ).as(photo *)
    }

    def findByHash(hash: String) = DB.withConnection {implicit c =>
        SQL("SELECT * FROM photo WHERE hash = {hash}"). on(
            'hash -> hash
        ). as(photo *)
    }

    def findAllSorted(offset:Int = 0, limit: Int = 12, sort_type: Int = 1) : List[Photo] = DB.withConnection {
        implicit c =>
        var sql_query : String = "SELECT * FROM photo ORDER BY " + sort_by(sort_type) + " DESC LIMIT {limit} OFFSET {offset}"
        SQL(sql_query). on(
            'limit -> limit,
            'offset -> offset
        ).as(photo *)
    }

    def findByContributer (user_id: Int , offset: Int = 0, limit: Int = 6, sort_type: Int = 1) : List[Photo] = DB.withConnection {
        implicit c =>
        var sql_query : String = "SELECT * FROM photo WHERE contributer = {user_id} ORDER BY " + sort_by(sort_type) + " DESC LIMIT {limit} OFFSET {offset}"
        SQL(sql_query). on(
            'user_id -> user_id,
            'limit -> limit,
            'offset -> offset
        ).as(photo *)
    }
}
