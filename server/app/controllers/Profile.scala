package controllers

import javax.inject._

import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

class Profile @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) 
    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] { 

    val model = new ProfileModel(db)

    implicit val UserDeliveryDataWrites : Writes[UserDeliveryData] = Json.writes[UserDeliveryData]

    def withSessionUserId(f:String => Future[Result])(implicit request: Request[Any]) : Future[Result] = {
        request.session.get("userId").map(f).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
    }

    def prof = Action { implicit request =>
        request.session.get("userId").map{ _ =>
            Ok(views.html.profile())
        }.getOrElse(Redirect(routes.Login.login()))
    }

    def logout = Action { implicit request =>
        Redirect(routes.Login.login()).withNewSession
    }

    def getUserData() = Action.async { implicit request =>
        withSessionUserId{ id =>
            model.getUserData(id.toInt).map{result => 
                Ok(Json.toJson(result))
            }
        }
    }
}
