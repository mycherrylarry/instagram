package models
import anorm._
import anorm.SqlParser._
import java.util.{Date}

import play.api.db._
import play.api.Play.current


case class User(id: Int, auth_id: String, name: String, email: String, desc: String)

object User {
    // -- Parsers

    val user = {
        get[Int]("id") ~
        get[String]("auth_id") ~
        get[String]("name") ~
        get[String]("email") ~
        get[String]("description") map {
            case id~auth_id~name~email~desc => User(id, auth_id, name, email, desc)
        }
    }

    def all(): List[User] = DB.withConnection {
        implicit c =>
        SQL("SELECT * FROM user").as(user *)
    }

    def create(auth_id: String, name: String = "", email: String = "", desc: String = "") {
        DB.withConnection {
            implicit c =>
            SQL(
                """
                INSERT INTO user (auth_id, name, email, description) 
                VALUES ({auth_id}, {name}, {email}, {desc})
                """
            ). on(
                'auth_id -> auth_id,
                'name    -> name,
                'email   -> email,
                'desc    -> desc
            ). executeUpdate()
        }
    }

    def update(id: Int, name: String, email: String, desc: String) {
        DB.withConnection {
            implicit c =>
            SQL(
                """
                UPDATE user SET name = {name}, email = {email}, description = {desc}
                WHERE id = {id}
                """
            ).on(
                'name -> name,
                'email -> email,
                'desc -> desc,
                'id -> id
            ).executeUpdate()
        }
    }

    def findByAuthId(auth_id: String) = DB.withConnection {
        implicit c =>
        SQL("SELECT * FROM user WHERE auth_id = {auth_id}").on(
            'auth_id -> auth_id
        ).as(user *)
    }

    def findById(id : Int) = DB.withConnection {
        implicit c =>
        SQL("SELECT * FROM user WHERE id = {id}").on(
            'id -> id
        ).as(user *)
    }
    
    def findFollowersOfUser(user_id : Int) = DB.withConnection{
        implicit c =>
        SQL(
            """
            SELECT user.id, user.auth_id, user.name, user.email, user.description
            FROM user, friendship WHERE friendship.followee_id = {user_id} AND friendship.follower_id = user.id
            """
        ).on(
        'user_id -> user_id
        ).as(user *)
    }
}
