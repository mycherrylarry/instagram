package controllers

import play.api._
import play.api.mvc._

import models.User

object Account extends Controller {
    def auth(auth_id: String) = Action {
        implicit request =>
        session.get("connected").map {
            user => Ok(user)
        }.getOrElse {
            var users : List[User]  = User.findByAuthId(auth_id)
            if (users.isEmpty) {
                User.create(auth_id)
                users  = User.findByAuthId(auth_id)
            }
            Ok(users.head.id.toString).withSession(
                session + ("connected" -> users.head.id.toString) + ("auth_id" -> users.head.auth_id)
            )
        }
    }

    def logout() = Action {
        implicit request =>
        Ok("logout").withNewSession
    }
}
