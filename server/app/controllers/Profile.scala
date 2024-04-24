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

    implicit val CollectionDeliveryDataWrites : Writes[CollectionDeliveryData] = Json.writes[CollectionDeliveryData]

    implicit val collectionDataWrites : Writes[CollectionData] = Json.writes[CollectionData]

    implicit val collectionDataReads : Reads[CollectionData] = Json.reads[CollectionData]

    implicit val RecordDeliveryDataWrites : Writes[RecordDeliveryData] = Json.writes[RecordDeliveryData]

    implicit val RecordDeliveryDataReads : Reads[RecordDeliveryData] = Json.reads[RecordDeliveryData]

    implicit val RecordCollectionDataReads : Reads[RecordCollectionData] = Json.reads[RecordCollectionData]

    def withSessionUserId(f:String => Future[Result])(implicit request: Request[Any]) : Future[Result] = {
        request.session.get("userId").map(f).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
    }

    def withJsonBody[A](f: A => Future[Result])(implicit request:Request[AnyContent], reads: Reads[A]) :Future[Result] = {
        request.body.asJson.map{ body =>
            Json.fromJson[A](body) match {
                case JsSuccess(a,path) => f(a)
                case e @ JsError(_) => Future.successful(Redirect(routes.Login.login()))
            }
        }.getOrElse(Future.successful(Redirect(routes.Login.login())))
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

    def loadCollections() = Action.async { implicit request =>
        withSessionUserId{id =>
            model.loadCollections(id.toInt).map{result =>
                Ok(Json.toJson(result))
            }
        }   
    }

    def getCollection(collectionId:String) = Action.async {implicit request =>
        withSessionUserId{id => 
            model.getCollection(id.toInt,collectionId.toInt).map{result =>
                Ok(Json.toJson(result))
            }
        }
    }

    def removeRecord() = Action.async{implicit request =>
        withJsonBody[RecordCollectionData]{data =>
            model.removeRecord(data.record,data.collectionId).map{result =>
                Ok(Json.toJson(result))
            }
        }
    }
}
