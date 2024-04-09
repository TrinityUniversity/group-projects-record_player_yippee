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

class Login @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) 
    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] { 
    
    val model = new LoginModel(db)

    implicit val userDataReads: Reads[UserData] = Json.reads[UserData]
    implicit val userCreationDataReads: Reads[UserCreationData] = Json.reads[UserCreationData]

    def withJsonBody[A](f: A => Future[Result])(implicit request:Request[AnyContent], reads: Reads[A]) :Future[Result] = {
        request.body.asJson.map{ body =>
            Json.fromJson[A](body) match {
                case JsSuccess(a,path) => f(a)
                case e @ JsError(_) => Future.successful(Redirect(routes.Login.login()))
            }
        }.getOrElse(Future.successful(Redirect(routes.Login.login())))
    }

    def login = Action { implicit request =>
        Ok(views.html.login())
    }

    def validateLogin = Action.async { implicit request => 
        withJsonBody[UserData]{ ud =>
            model.validateUser(ud.username,ud.password).map{ userExists =>
                if(userExists){
                    Ok(Json.toJson(true))
                        .withSession()
                }else{
                    Ok(Json.toJson(false))
                }
            }
        }
    }

    def createUser = Action.async { implicit request =>
        withJsonBody[UserData](ud => {
            model.createUser(ud.username,ud.password).map{userCreated =>
                if(userCreated){
                    Ok(Json.toJson(true))
                        .withSession()
                } else {
                    Ok(Json.toJson(false))
                }
            }
        })
    }
}