package controllers

import play.api._
import play.api.mvc._

import models.User
import models.Friendship
import models.Likeship

object Relationship extends Controller {
    def follow(follower: Int, followee: Int) = Action {
        Friendship.create(follower, followee)
        Ok("ok")
    }
    
    def unfollow(follower: Int, followee: Int) = Action {
        Friendship.delete(follower, followee)
        Ok("ok")
    }

    def like(user_id: Int, photo_id: Int) = Action {
        Likeship.create(user_id, photo_id)
        Ok("ok")
    }
    
    def unlike(user_id: Int, photo_id: Int) = Action {
        Likeship.delete(user_id, photo_id)
        Ok("ok")
    }
}
