package models
import anorm._
import anorm.SqlParser._
import java.util.{Date}

import play.api.db._
import play.api.Play.current


case class Likeship(id: Int, user_id: Int, photo_id: Int)

object Likeship {
    // -- Parsers

    val likeship = {
        get[Int]("id") ~
        get[Int]("user_id") ~
        get[Int]("photo_id") map {
            case id~user_id~photo_id => Likeship(id, user_id, photo_id)
        }
    }

    def create(user_id: Int, photo_id: Int ){
        DB.withConnection {
            implicit c =>
            SQL(
                """
                INSERT INTO likeship (user_id, photo_id)
                VALUES ({user_id}, {photo_id})
                """
            ). on(
                'user_id -> user_id,
                'photo_id -> photo_id
            ). executeUpdate()
        }
    }

    def findExist(user_id : Int, photo_id: Int) = {
        DB.withConnection{
            implicit c =>
            SQL(
                """
                SELECT * FROM likeship WHERE(user_id = {user_id} AND photo_id = {photo_id})
                """
            ) .on (
                'user_id -> user_id,
                'photo_id -> photo_id
            ).as (likeship *)
        }
    }

    def delete(user_id: Int, photo_id: Int) {
        DB.withConnection {
            implicit c =>
            SQL("DELETE FROM likeship where user_id = {user_id} AND photo_id = {photo_id}"). on(
                'user_id -> user_id,
                'photo_id -> photo_id
            ).executeUpdate()
        }
    }
}
