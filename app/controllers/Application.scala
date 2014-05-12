package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.tuple
import play.api.data.Forms.text

import java.util.{Date}
import java.awt.Image
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr._;


import sun.misc.BASE64Decoder;
import org.apache.commons.codec.binary.Base64;

import models._

object Application extends Controller {
    var uploadForm = Form(
        tuple(
            "image_name" -> text,
            "image_data" -> text
        )
    )
    
    var updateForm = Form(
        tuple(
            "name" -> text,
            "email" -> text,
            "desc" -> text
        )
    )
  
    def index = Action {
        def photo = Photo.findAllSorted(0,12)
        Ok(views.html.index("Your new application is ready.", photo))
    }

    def userList = Action { implicit request =>
        def users = User.all()
        Ok(views.html.userlist("userlist", users))
    }

    def profile(id: Int) = Action { implicit request =>
        val user = User.findById(id)
        if (user.isEmpty) {
            Ok("no user")
        } else {
            val photos : List[Photo] = Photo.findByContributer(id)
            var followed : Boolean = false
            val followers : List[User] = User.findFollowersOfUser(id)
            var cur_user_id : Int= -1;
            session.get("connected").map { cur_user_id_ =>
                cur_user_id = cur_user_id_.toInt
                val friendship = Friendship.findExist(cur_user_id, user.head.id)
                if (!friendship.isEmpty) {
                    followed = true
                }
            }
            Ok(views.html.profile("hello", user.head, photos, followed, followers, cur_user_id))
        }
    }

    def profileUpdate = Action(parse.tolerantFormUrlEncoded(maxLength = 1024 * 50)) {implicit request =>
        session.get("connected").map { user_id =>
            def values = updateForm.bindFromRequest.data
            var name :String = values("name")
            var email :String = values("email")
            var desc :String = values("desc")
            User.update(user_id.toInt, name, email, desc)
            Ok("success")
        }.getOrElse {
            Ok("please login")
        }
    }

    def management = Action { implicit c =>
        session.get("connected").map { user_id =>
            val user : User = User.findById(user_id.toInt).head
            val photos : List[Photo] = Photo.findByContributer(user_id.toInt)
            val followers : List[User] = User.findFollowersOfUser(user_id.toInt)
            Ok(views.html.management("hello", user, photos, followers))
        }.getOrElse {
            Ok("please login")
        }
    }
  
    def photo(id: Int) = Action { implicit request =>
        val photo = Photo.findById(id)
        if (photo.isEmpty) {
            Ok("no such poto")
        } else {
            val owner : User = User.findById(photo.head.contributer).head
            var liked : Boolean = false
            var cur_user_id : Int= -1;
            session.get("connected").map { cur_user_id_ =>
                cur_user_id = cur_user_id_.toInt
                val like = Likeship.findExist(cur_user_id, photo.head.id)
                if (!like.isEmpty) {
                    liked = true
                }
            }
            Ok(views.html.photo("hello", photo.head, owner, liked, cur_user_id))
        }
    }

    def upload = Action {implicit request =>
        session.get("connected").map { user_id =>
            Ok(views.html.upload("hello", user_id))
        }.getOrElse {
            Ok("please login")
        }
    }

    def uploadDo = Action(parse.tolerantFormUrlEncoded(maxLength = 1024 * 5000)) {implicit request =>
    //def uploadDo = Action {implicit request =>
        session.get("connected").map { user_id =>
            def values = uploadForm.bindFromRequest.data
            var image_data :String = values("image_data")
            var image_name :String = values("image_name")

            image_data = image_data.split(",").toList.last
            
            var suffix : String = "png"
            val now = new Date()
            val cDate = now.getTime.toString()
            var hash = user_id + cDate

            val file_folder = new java.io.File("public/resources/photo/" + user_id)
            if (!file_folder.exists()) {
                file_folder.mkdir()
            }

            val file_address = "public/resources/photo/" + user_id + "/" + hash + "." + suffix
            val file = new java.io.File(file_address)
            val decoder = new BASE64Decoder()
            val buffer = decoder.decodeBuffer(image_data)
            val bis : InputStream = new ByteArrayInputStream(buffer)
            val bi : BufferedImage = ImageIO.read(bis);
            val small_bi : BufferedImage = bi.getSubimage(0,0,bi.getWidth(),bi.getHeight())

            val small_file_address = "public/resources/photo/" + user_id + "/" + "small_" + hash + "." + suffix
            val small_file = new java.io.File(small_file_address)

            bis.close()
            ImageIO.write(bi, suffix, file)

            ImageIO.write(Scalr.resize(small_bi, Scalr.Mode.AUTOMATIC, 320, 240), suffix, small_file)

            // save file to public/resources/photo/{user_id}/{hash}.{suffix}
            Photo.create(user_id.toInt, hash, suffix, image_name)
            val photo : Photo = Photo.findByHash(hash).head
            Ok("name")
            //Ok(views.html.upload("hello"))
        }.getOrElse {
            Ok("please login")
        }
    }

}
