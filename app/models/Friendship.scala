package models
import anorm._
import anorm.SqlParser._
import java.util.{Date}

import play.api.db._
import play.api.Play.current
import models.User


case class Friendship(id: Int, follower_id: Int, followee_id: Int)

object Friendship {
    // -- Parsers

    val friendship = {
        get[Int]("id") ~
        get[Int]("follower_id") ~
        get[Int]("followee_id") map {
            case id~follower_id~followee_id => Friendship(id, follower_id, followee_id)
        }
    }

    def create(follower_id: Int, followee_id: Int ){
        DB.withConnection {
            implicit c =>
            SQL(
                """
                INSERT INTO friendship (follower_id, followee_id)
                VALUES ({follower_id}, {followee_id})
                """
            ). on(
                'follower_id -> follower_id,
                'followee_id -> followee_id
            ). executeUpdate()
        }
    }

    def findExist(follower_id : Int, followee_id: Int) = {
        DB.withConnection{
            implicit c =>
            SQL(
                """
                SELECT * FROM friendship WHERE(follower_id = {follower_id} AND followee_id = {followee_id})
                """
            ) .on (
                'follower_id -> follower_id,
                'followee_id -> followee_id
            ).as (friendship *)
        }
    }

    def delete(follower_id: Int, followee_id: Int) {
        DB.withConnection {
            implicit c =>
            SQL("DELETE FROM friendship where follower_id = {follower_id} AND followee_id = {followee_id}"). on(
                'follower_id -> follower_id,
                'followee_id -> followee_id
            ).executeUpdate()
        }
    }
}
