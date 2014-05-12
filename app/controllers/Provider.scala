package controllers

import play.api._
import play.api.mvc._

import models.{User, Photo, Msgbox}

object Provider extends Controller {
    def photoAll(offset: Int = 0, limit: Int = 4) = Action {
        implicit request =>
        def photos = Photo.findAllSorted(offset, limit)
        Ok(views.html.provider.photoAll(photos))
    }

    def msgbox(auth_id: String) = Action{implicit request =>
        var user_id : Int  = User.findByAuthId(auth_id).head.id
        def msgboxes = Msgbox.findByUserId(user_id)
        Ok(views.html.provider.msgbox(msgboxes))
    }

    def msgboxDel(id: Int) = Action{implicit request =>
        def ret = Msgbox.delete(id)
        Ok(ret.toString)
    }
}
